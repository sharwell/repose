package com.rackspace.papi.service.event;

import com.rackspace.papi.service.event.common.Event;
import com.rackspace.papi.service.event.common.EventDispatcher;
import com.rackspace.papi.service.event.common.EventListener;
import com.rackspace.papi.service.event.common.EventService;
import com.rackspace.papi.service.event.common.impl.EventDispatcherImpl;
import com.rackspace.papi.service.event.common.impl.EventListenerDescriptor;
import com.rackspace.papi.service.event.impl.SimpleEvent;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@Component("eventManager")
public class PowerProxyEventManager implements EventService {

    private final Map<ComparableClassWrapper<? extends Enum<?>>, Set<EventListenerDescriptor<?>>> listenerMap;
    private final Queue<Event<?, ?>> eventQueue;
    private final Lock eventQueueLock;
    private final Condition queueNotEmpty;

    public PowerProxyEventManager() {
        listenerMap = new TreeMap<ComparableClassWrapper<? extends Enum<?>>, Set<EventListenerDescriptor<?>>>();
        eventQueue = new LinkedList<Event<?, ?>>();

        eventQueueLock = new ReentrantLock();
        queueNotEmpty = eventQueueLock.newCondition();
    }

    @Override
    public synchronized EventDispatcher nextDispatcher() throws InterruptedException {
        final Event<?, ?> e = nextEvent();

        return new EventDispatcherImpl(e, Collections.unmodifiableSet(getOrCreateListenerSet(e.type().getClass())));
    }

    private Event<?, ?> nextEvent() throws InterruptedException {
        eventQueueLock.lock();

        try {
            while (eventQueue.size() == 0) {
                queueNotEmpty.await();
            }

            return eventQueue.poll();
        } catch (InterruptedException ie) {
            Thread.currentThread().interrupt();
            throw ie;
        } finally {
            eventQueueLock.unlock();
        }
    }

    @Override
    public <T extends Enum<T>, P> void newEvent(T e, P payload) {
        eventQueueLock.lock();

        try {
            eventQueue.add(new SimpleEvent<T, P>(e, payload, this));

            if (eventQueue.size() == 1) {
                queueNotEmpty.signalAll();
            }
        } finally {
            eventQueueLock.unlock();
        }
    }

    @Override
    public <T extends Enum<T>> void listen(EventListener<T, ?> el, Class<T> events) {
        regsiterListener(el, events, EnumSet.allOf(events));
    }

    @Override
    public <T extends Enum<T>> void listen(EventListener<T, ?> el, T... events) {
        if (events == null || events.length == 0) {
            throw new IllegalArgumentException("Must subscribe to at least one event type");
        }

        for (T event : events) {
            if (event != null) {
                regsiterListener(el, (Class<T>) event.getClass(), Arrays.asList(events));
                break;
            }
        }
    }

    private <T extends Enum<T>> void regsiterListener(EventListener<T, ?> el, Class<T> enumClass, Collection<T> events) {
        boolean found = false;

        final Set<EventListenerDescriptor<?>> descriptorSet = getOrCreateListenerSet(enumClass);

        for (EventListenerDescriptor<?> descriptor : descriptorSet) {
            if (descriptor.getListener() == el) {
                EventListenerDescriptor<T> typedDescriptor = (EventListenerDescriptor<T>)descriptor;
                typedDescriptor.listenFor(events);
                found = true;

                break;
            }
        }

        if (!found) {
            descriptorSet.add(new EventListenerDescriptor<T>(el, events));
        }
    }

    @Override
    public <T extends Enum<T>> void squelch(EventListener<T, ?> el, Class<T> events) {
        final Set<EventListenerDescriptor<?>> listenerSet = listenerMap.get(createComparableClassWrapper(events));

        if (listenerSet != null) {
            final Iterator<EventListenerDescriptor<?>> itr = listenerSet.iterator();

            while (itr.hasNext()) {
                final EventListenerDescriptor<?> elw = itr.next();

                if (elw.getListener() == el) {
                    itr.remove();
                    break;
                }
            }
        }
    }

    @Override
    public <T extends Enum<T>> void squelch(EventListener<T, ?> el, T... events) {
        if (events == null || events.length == 0) {
            throw new IllegalArgumentException("Must unsubscribe from at least one event type");
        }

        final Set<EventListenerDescriptor<?>> listenerSet = listenerMap.get(createComparableClassWrapper(events[0].getClass()));

        if (listenerSet != null) {
            final Iterator<EventListenerDescriptor<?>> itr = listenerSet.iterator();

            while (itr.hasNext()) {
                final EventListenerDescriptor<?> elw = itr.next();

                if (elw.getListener() == el) {
                    EventListenerDescriptor<T> descriptor = (EventListenerDescriptor<T>)elw;
                    if (descriptor.silence(Arrays.asList(events))) {
                        itr.remove();
                    }

                    break;
                }
            }
        }
    }

    private <T extends Enum<T>> Set<EventListenerDescriptor<?>> getOrCreateListenerSet(Class<T> e) {
        final ComparableClassWrapper<? extends Enum<?>> classWrapper = createComparableClassWrapper(e);
        Set<EventListenerDescriptor<?>> listenerSet = listenerMap.get(classWrapper);

        if (listenerSet == null) {
            listenerSet = new HashSet<EventListenerDescriptor<?>>();
            listenerMap.put(classWrapper, listenerSet);
        }

        return listenerSet;
    }

    private <T extends Enum<T>> ComparableClassWrapper<T> createComparableClassWrapper(Class<T> e) {
        return new ComparableClassWrapper<T>(e);
    }
}
