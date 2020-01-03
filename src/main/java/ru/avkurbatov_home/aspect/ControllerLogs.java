package ru.avkurbatov_home.aspect;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Indicates controllers that should have detailed logs.
 * See more {@link Target ru.avkurbatov_home.aspect.ControllerLogsAspect}
 * */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ControllerLogs {
}

