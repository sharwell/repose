package com.rackspace.papi.service.event.common;

public interface EventListener<T extends Enum<T>, P> {

    void onEvent(Event<T, P> e);
}
