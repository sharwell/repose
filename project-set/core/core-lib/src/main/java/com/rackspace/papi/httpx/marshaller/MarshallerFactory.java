package com.rackspace.papi.httpx.marshaller;

import com.rackspace.httpx.MessageEnvelope;

/**
 * @author fran
 */
public final class MarshallerFactory {
    
    private MarshallerFactory(){
    }
    
    public static Marshaller<MessageEnvelope> newInstance() {
        return new MessageEnvelopeMarshaller();
    }
}
