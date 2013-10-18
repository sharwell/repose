package com.rackspace.papi.commons.config.manager;

import com.rackspace.papi.commons.config.parser.common.ConfigurationParser;
import com.rackspace.papi.commons.config.resource.ConfigurationResource;
import com.rackspace.papi.commons.util.Destroyable;

public interface ConfigurationUpdateManager extends Destroyable {

     <T> void registerListener(UpdateListener<? super T> listener, ConfigurationResource resource, ConfigurationParser<T> parser, String filterName);

     void unregisterListener(UpdateListener<?> listener, ConfigurationResource resource);
     
}
