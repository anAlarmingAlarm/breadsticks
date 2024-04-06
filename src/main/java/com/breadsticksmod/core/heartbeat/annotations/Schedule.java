package com.breadsticksmod.core.heartbeat.annotations;

import com.breadsticksmod.core.time.ChronoUnit;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface Schedule {
    int rate();
    ChronoUnit unit();

    boolean parallel() default false;
}