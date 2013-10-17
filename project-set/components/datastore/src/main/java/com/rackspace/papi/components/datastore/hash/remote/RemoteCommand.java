package com.rackspace.papi.components.datastore.hash.remote;

import com.rackspace.papi.commons.util.http.ServiceClientResponse;
import com.rackspace.papi.components.datastore.common.RemoteBehavior;
import com.rackspace.papi.commons.util.proxy.RequestProxyService;
import java.io.IOException;

/**
 *
 * @author zinic
 */
public interface RemoteCommand {

   <E> ServiceClientResponse<E> execute(RequestProxyService proxyService, RemoteBehavior remoteBehavior);
   Object handleResponse(ServiceClientResponse<?> response) throws IOException;
   void setHostKey(String hostKey);
}
