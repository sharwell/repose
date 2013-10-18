package com.rackspace.papi.service.naming;

import javax.naming.Context;
import javax.naming.NamingException;
import javax.naming.spi.InitialContextFactory;
import java.util.Hashtable;

@SuppressWarnings("UseOfObsoleteCollectionType")
public class InitialServiceContextFactory implements InitialContextFactory {

   public Context getInitialContext() throws NamingException {
      return getInitialContext(new Hashtable<Object, Object>());
   }

   @SuppressWarnings("PMD.ReplaceHashtableWithMap")
   @Override
   public Context getInitialContext(Hashtable<?, ?> environment) throws NamingException {
      return new PowerApiNamingContext("", (Hashtable<? extends String, ? extends Object>)environment);
   }
}
