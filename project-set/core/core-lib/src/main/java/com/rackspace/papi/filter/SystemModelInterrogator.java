package com.rackspace.papi.filter;

import com.rackspace.papi.commons.util.net.StaticNetworkNameResolver;
import com.rackspace.papi.commons.util.net.NetworkInterfaceProvider;
import com.rackspace.papi.commons.util.net.NetworkNameResolver;
import com.rackspace.papi.commons.util.net.StaticNetworkInterfaceProvider;
import com.rackspace.papi.domain.Port;
import com.rackspace.papi.model.Destination;
import com.rackspace.papi.model.Node;
import com.rackspace.papi.model.ReposeCluster;
import com.rackspace.papi.model.SystemModel;

import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author franshua
 */
public class SystemModelInterrogator {

   private static final Logger LOG = LoggerFactory.getLogger(SystemModelInterrogator.class);
   private final NetworkInterfaceProvider networkInterfaceProvider;
   private final NetworkNameResolver nameResolver;
   private final SystemModel systemModel;
   private final List<Port> ports;

   public SystemModelInterrogator(SystemModel powerProxy, List<Port> ports) {
      this(StaticNetworkNameResolver.getInstance(), StaticNetworkInterfaceProvider.getInstance(), powerProxy, ports);
   }

   public SystemModelInterrogator(NetworkNameResolver nameResolver, NetworkInterfaceProvider nip, SystemModel systemModel, List<Port> ports) {
      this.nameResolver = nameResolver;
      this.networkInterfaceProvider = nip;
      this.systemModel = systemModel;
      this.ports = ports;
   }

   public final class DomainNodeWrapper {

      private final Node node;

      private DomainNodeWrapper(Node node) {
         if (node == null) {
            throw new IllegalArgumentException("Node cannot be null");
         }
         this.node = node;
      }

      public boolean hasLocalInterface() {
         boolean result = false;

         try {
            final InetAddress hostAddress = nameResolver.lookupName(node.getHostname());
            result = networkInterfaceProvider.hasInterfaceFor(hostAddress);
         } catch (UnknownHostException uhe) {
            LOG.error("Unable to look up network host name. Reason: " + uhe.getMessage(), uhe);
         } catch (SocketException socketException) {
            LOG.error(socketException.getMessage(), socketException);
         }

         return result;
      }

      private List<Port> getPortsList() {
         List<Port> ports = new ArrayList<Port>();
         
         
         // TODO Model: use constants or enum for possible protocols
         if (node.getHttpPort() > 0) {
            ports.add(new Port("http", node.getHttpPort()));
         }
         
         if (node.getHttpsPort() > 0) {
            ports.add(new Port("https", node.getHttpsPort()));
         }
         
         return ports;
      }

   }

   public final class ServiceDomainWrapper {

      private final ReposeCluster domain;

      private ServiceDomainWrapper(ReposeCluster domain) {
         if (domain == null) {
            throw new IllegalArgumentException("Domain cannot be null");
         }
         this.domain = domain;
      }

      public boolean containsLocalNodeForPorts(List<Port> ports) {
         return getLocalNodeForPorts(ports) != null;
      }
      
      public Node getLocalNodeForPorts(List<Port> ports) {

         Node localhost = null;
         
         if (ports.isEmpty()) {
            return localhost;
         }

         for (Node host : domain.getNodes().getNode()) {
            DomainNodeWrapper wrapper = new DomainNodeWrapper(host);
            List<Port> hostPorts = wrapper.getPortsList();
            
            if (hostPorts.equals(ports) && wrapper.hasLocalInterface()) {
               localhost = host;
               break;

            }
         }

         return localhost;
      }

      public Destination getDefaultDestination() {
         Destination dest = null;

         List<Destination> destinations = new ArrayList<Destination>();

         destinations.addAll(domain.getDestinations().getEndpoint());
         destinations.addAll(domain.getDestinations().getTarget());

         for (Destination destination : destinations) {
            if (destination.isDefault()) {
               dest = destination;
               break;
            }
         }

         return dest;
      }
   }

   public ReposeCluster getLocalServiceDomain() {
      ReposeCluster domain = null;

      for (ReposeCluster possibleDomain : systemModel.getReposeCluster()) {
         if (new ServiceDomainWrapper(possibleDomain).containsLocalNodeForPorts(ports)) {
            domain = possibleDomain;
            break;
         }
      }

      return domain;
   }

   public Node getLocalHost() {
      Node localHost = null;

      for (ReposeCluster domain : systemModel.getReposeCluster()) {
         Node node = new ServiceDomainWrapper(domain).getLocalNodeForPorts(ports);

         if (node != null) {
            localHost = node;
            break;
         }
      }

      return localHost;
   }

   public Destination getDefaultDestination() {
      ServiceDomainWrapper domain = new ServiceDomainWrapper(getLocalServiceDomain());

      return domain.getDefaultDestination();
   }
}