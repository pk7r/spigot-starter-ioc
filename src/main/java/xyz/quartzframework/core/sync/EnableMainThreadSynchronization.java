package xyz.quartzframework.core.sync;

import java.lang.annotation.*;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface EnableMainThreadSynchronization {

}