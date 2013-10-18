package com.rackspace.cloud.valve.server;

import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.spi.LoggingEvent;

import java.util.ArrayList;
import java.util.List;

public class AppenderForTesting extends AppenderSkeleton {
    private static List<String> messages = new ArrayList<String>();

    protected void append(LoggingEvent event) {
        messages.add(event.getRenderedMessage());
    }

    public void close() {
    }

    public boolean requiresLayout() {
        return false;
    }

    public static String[] getMessages() {
        return messages.toArray(new String[messages.size()]);
    }

    public static void clear() {
        messages.clear();
    }
}
