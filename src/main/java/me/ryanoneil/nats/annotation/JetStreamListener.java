package me.ryanoneil.nats.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface JetStreamListener {
    String subject();

    String queue() default "";

    String stream() default "";

    int threads() default 1;
}
