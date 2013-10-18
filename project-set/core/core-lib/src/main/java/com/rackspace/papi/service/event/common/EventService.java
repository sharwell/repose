package com.rackspace.papi.service.event.common;


public interface EventService {

    <T extends Enum<T>, P> void newEvent(T e, P payload);

    EventDispatcher nextDispatcher() throws InterruptedException;

     <T extends Enum<T>> void listen(EventListener<T, ?> el, Class<T> events);

     <T extends Enum<T>> void listen(EventListener<T, ?> el, T... events);

     <T extends Enum<T>> void squelch(EventListener<T, ?> el, Class<T> events);

     <T extends Enum<T>> void squelch(EventListener<T, ?> el, T... events);
}
