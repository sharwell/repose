package com.rackspace.papi.service.event.common;

public interface Event <T extends Enum<T>, P> {

    T type();

    P payload();
    
    EventService eventManager();
}
