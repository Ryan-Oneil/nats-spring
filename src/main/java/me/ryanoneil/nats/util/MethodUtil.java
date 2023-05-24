package me.ryanoneil.nats.util;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

public class MethodUtil {

    private MethodUtil() {}

    public static MethodHandle getMethodHandler(Method method) throws NoSuchMethodException, IllegalAccessException {
        var lookup = MethodHandles.lookup();
        var methodType = MethodType.methodType(method.getReturnType(), method.getParameterTypes());

        return lookup.findVirtual(method.getDeclaringClass(), method.getName(), methodType);
    }

    public static Class<?> getParameterType(Method method, int parameterIndex) {
        Parameter[] parameters =  method.getParameters();


        return parameters[parameterIndex].getType();
    }

}
