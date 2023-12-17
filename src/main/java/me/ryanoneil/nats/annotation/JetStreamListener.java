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

    String stream();

    int threads() default 1;

    boolean bind() default false;

    String durable() default "";

    String name() default "";

    boolean ordered() default false;

}
