package me.ryanoneil.nats.util;

import static org.springframework.test.util.AssertionErrors.assertEquals;
import static org.springframework.test.util.AssertionErrors.assertNotNull;

import java.lang.invoke.MethodHandle;
import java.lang.reflect.Method;
import org.junit.jupiter.api.Test;

class MethodUtilTest {

    @Test
    void getMethodHandler() throws NoSuchMethodException, IllegalAccessException {
        Object object = Object.class;
        Method method = object.getClass().getMethods()[0];

        MethodHandle methodHandle = MethodUtil.getMethodHandler(method);

        assertNotNull("Method handle", methodHandle);
    }

    @Test
    void getParameterType() {
        Method method = MethodUtil.class.getMethods()[0];
        Class<?> classType = MethodUtil.getParameterType(method, 0);

        assertEquals("Parameter class type", classType, Method.class);
    }

}
