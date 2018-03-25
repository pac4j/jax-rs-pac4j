package org.pac4j.jax.rs.helpers;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.security.AccessController;
import java.security.PrivilegedAction;

/**
 * @author Victor Noel - Linagora
 * @since 1.0.1
 */
public class AnnotationsHelper {

    private AnnotationsHelper() {
    }

    public static <A extends Annotation> A getClassLevelAnnotation(Class<?> clazz, Class<A> annotationClass) {
        A foundOnInterface = null;
        Class<?> cls = clazz;
        do {
            A a = cls.getAnnotation(annotationClass);
            if (a != null) {
                return a;
            }

            // if we found it on an interface before (i.e. in a subclass),
            // we don't want to check for interfaces on a superclass then
            if (foundOnInterface == null) {
                for (Class<?> itf : cls.getInterfaces()) {
                    a = itf.getAnnotation(annotationClass);
                    if (a != null) {
                        // if we find it on an interface, we still need to check all the superclass
                        // (but not their interfaces) because superclass takes precedence
                        foundOnInterface = a;
                        break;
                    }
                }
            }
        } while ((cls = cls.getSuperclass()) != null);

        // if we didn't return before, then it is either on one of the interface, or it is not found
        return foundOnInterface;
    }

    public static <A extends Annotation> A getMethodLevelAnnotation(Method m, Class<A> annotationClass) {
        return getMethodLevelAnnotation(m, m.getDeclaringClass(), annotationClass);
    }

    private static <A extends Annotation> A getMethodLevelAnnotation(Method m, Class<?> c, Class<A> annotationClass) {

        Method rm = findMethod(m, c);

        if (rm == null) {
            return null;
        }

        A a = rm.getAnnotation(annotationClass);
        if (a != null) {
            return a;
        }

        // super classes before interfaces
        final Class<?> sc = c.getSuperclass();
        if (sc != null && sc != Object.class) {
            a = getMethodLevelAnnotation(m, sc, annotationClass);
            if (a != null) {
                return a;
            }
        }

        for (Class<?> i : c.getInterfaces()) {
            a = getMethodLevelAnnotation(m, i, annotationClass);
            if (a != null) {
                return a;
            }
        }

        return null;
    }

    private static Method findMethod(Method m, Class<?> c) {
        // that's how they do it in Jersey, most certainly
        // because getMethod can throw a SecurityException
        return AccessController.doPrivileged(new PrivilegedAction<Method>() {
            @Override
            public Method run() {
                try {
                    return c.getMethod(m.getName(), m.getParameterTypes());
                } catch (NoSuchMethodException e) {
                    // TODO for now we support only exactly matching parameters!
                    return null;
                }
            }
        });
    }
}
