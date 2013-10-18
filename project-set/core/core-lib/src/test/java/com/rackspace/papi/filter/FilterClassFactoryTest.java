package com.rackspace.papi.filter;

import com.oracle.javaee6.FilterType;
import com.oracle.javaee6.FullyQualifiedClassType;
import com.rackspace.papi.servlet.PowerApiContextException;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;
import org.springframework.context.ApplicationContext;

import javax.servlet.Filter;
import java.net.MalformedURLException;
import java.net.URL;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;


/**
 * @author fran
 */
@RunWith(Enclosed.class)
public class FilterClassFactoryTest {
    public static class WhenUsingFilterClassFactory {
        @Test
        @SuppressWarnings("unchecked")
        public void shouldInstantiate() throws ClassNotFoundException {
            ClassLoader mockedClassLoader = mock(ClassLoader.class);
            FilterType mockedFilterType = mock(FilterType.class);
            FullyQualifiedClassType mockedClassType = mock(FullyQualifiedClassType.class);
            

            when((Class<Object>)mockedClassLoader.loadClass(any(String.class))).thenReturn(Object.class);

            FilterClassFactory filterClassFactory = new FilterClassFactory(mockedFilterType, mockedClassLoader);

            assertNotNull(filterClassFactory);
        }        

        @Test
        @SuppressWarnings("unchecked")
        public void shouldGetClassLoader() throws ClassNotFoundException, MalformedURLException {
            URL url = new URL("http://fake url");
            ClassLoader mockedClassLoader = mock(ClassLoader.class);
            FilterType mockedFilterType = mock(FilterType.class);
            FullyQualifiedClassType mockedClassType = mock(FullyQualifiedClassType.class);

            when((Class<Object>)mockedClassLoader.loadClass(any(String.class))).thenReturn(Object.class);
            when(mockedClassLoader.getResource(any(String.class))).thenReturn(url);

            FilterClassFactory filterClassFactory = new FilterClassFactory(mockedFilterType, mockedClassLoader);

            assertTrue(url.getPath().equalsIgnoreCase(filterClassFactory.getClassLoader().getResource("").getPath()));
        }

        @Test
        @SuppressWarnings("unchecked")
        public void shouldToString() throws ClassNotFoundException {
            String className = "Fake class name";
            ClassLoader mockedClassLoader = mock(ClassLoader.class);
            FilterType mockedFilterType = mock(FilterType.class);
            FullyQualifiedClassType mockedClassType = mock(FullyQualifiedClassType.class);

            when((Class<Object>)mockedClassLoader.loadClass(any(String.class))).thenReturn(Object.class);
            when(mockedFilterType.getFilterClass()).thenReturn(mockedClassType);
            when(mockedClassType.getValue()).thenReturn(className);

            FilterClassFactory filterClassFactory = new FilterClassFactory(mockedFilterType, mockedClassLoader);

            assertTrue(className.equalsIgnoreCase(filterClassFactory.toString()));
        }
    }

    public static class WhenValidatingFilterClass {
        @Test
        @SuppressWarnings("unchecked")
        public void shouldValidate() throws ClassNotFoundException {
            ClassLoader mockedClassLoader = mock(ClassLoader.class);
            FilterType mockedFilterType = mock(FilterType.class);
            FullyQualifiedClassType mockedClassType = mock(FullyQualifiedClassType.class);

            when((Class<Filter>)mockedClassLoader.loadClass(any(String.class))).thenReturn(Filter.class);
            when(mockedFilterType.getFilterClass()).thenReturn(mockedClassType);
            when(mockedClassType.getValue()).thenReturn("Fake class name");

            FilterClassFactory filterClassFactory = new FilterClassFactory(mockedFilterType, mockedClassLoader);

            filterClassFactory.validate(Filter.class);
        }

        @Test(expected=PowerApiContextException.class)
        public void shouldThrowServletContextInitExceptionForNullClassOnValidate() throws ClassNotFoundException {
            ClassLoader mockedClassLoader = mock(ClassLoader.class);
            FilterType mockedFilterType = mock(FilterType.class);
            FullyQualifiedClassType mockedClassType = mock(FullyQualifiedClassType.class);

            when(mockedClassLoader.loadClass(any(String.class))).thenReturn(null);
            when(mockedFilterType.getFilterClass()).thenReturn(mockedClassType);
            when(mockedClassType.getValue()).thenReturn("Fake class name");

            FilterClassFactory filterClassFactory = new FilterClassFactory(mockedFilterType, mockedClassLoader);

            filterClassFactory.validate(null);
        }

        @Test(expected=PowerApiContextException.class)
        @SuppressWarnings("unchecked")
        public void shouldThrowServletContextInitExceptionForNonFilterClassOnValidate() throws ClassNotFoundException {
            ClassLoader mockedClassLoader = mock(ClassLoader.class);
            FilterType mockedFilterType = mock(FilterType.class);
            FullyQualifiedClassType mockedClassType = mock(FullyQualifiedClassType.class);

            when((Class<Object>)mockedClassLoader.loadClass(any(String.class))).thenReturn(Object.class);
            when(mockedFilterType.getFilterClass()).thenReturn(mockedClassType);
            when(mockedClassType.getValue()).thenReturn("Fake class name");

            FilterClassFactory filterClassFactory = new FilterClassFactory(mockedFilterType, mockedClassLoader);

            filterClassFactory.validate(null);
        }
    }

    public static class WhenCreatingNewClassInstance {
        @Test
        @SuppressWarnings("unchecked")
        public void shouldCreateNewInstance() throws ClassNotFoundException {
            ClassLoader mockedClassLoader = mock(ClassLoader.class);
            FilterType mockedFilterType = mock(FilterType.class);
            FullyQualifiedClassType mockedClassType = mock(FullyQualifiedClassType.class);

            when((Class<FakeFilterClass>)mockedClassLoader.loadClass(any(String.class))).thenReturn(FakeFilterClass.class);
            when(mockedFilterType.getFilterClass()).thenReturn(mockedClassType);
            when(mockedClassType.getValue()).thenReturn("Fake class name");

            FilterClassFactory filterClassFactory = new FilterClassFactory(mockedFilterType, mockedClassLoader);

            Filter newFilter = filterClassFactory.newInstance(mock(ApplicationContext.class));

            assertNotNull(newFilter);
        }

        @Test(expected= FilterClassException.class)
        @SuppressWarnings("unchecked")
        public void shouldThrowFilterClassExceptionOnInstantiationException() throws ClassNotFoundException {
            ClassLoader mockedClassLoader = mock(ClassLoader.class);
            FilterType mockedFilterType = mock(FilterType.class);
            FullyQualifiedClassType mockedClassType = mock(FullyQualifiedClassType.class);

            when((Class<Filter>)mockedClassLoader.loadClass(any(String.class))).thenReturn(Filter.class);
            when(mockedFilterType.getFilterClass()).thenReturn(mockedClassType);
            when(mockedClassType.getValue()).thenReturn("Fake class name");

            FilterClassFactory filterClassFactory = new FilterClassFactory(mockedFilterType, mockedClassLoader);

            filterClassFactory.newInstance(mock(ApplicationContext.class));
        }

        @Test(expected= FilterClassException.class)
        @SuppressWarnings("unchecked")
        public void shouldThrowFilterClassExceptionOnIllegalAccessException() throws ClassNotFoundException {
            ClassLoader mockedClassLoader = mock(ClassLoader.class);
            FilterType mockedFilterType = mock(FilterType.class);
            FullyQualifiedClassType mockedClassType = mock(FullyQualifiedClassType.class);

            when((Class<FakeFilterClassWithPrivateConstructor>)mockedClassLoader.loadClass(any(String.class))).thenReturn(FakeFilterClassWithPrivateConstructor.class);
            when(mockedFilterType.getFilterClass()).thenReturn(mockedClassType);
            when(mockedClassType.getValue()).thenReturn("Fake class name");

            FilterClassFactory filterClassFactory = new FilterClassFactory(mockedFilterType, mockedClassLoader);

            filterClassFactory.newInstance(mock(ApplicationContext.class));
        }        
    }
}
