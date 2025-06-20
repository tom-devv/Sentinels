package dev.tom.sentinels.launchable.items;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.RECORD_COMPONENT)
public @interface FieldInfo {
    String unit() default "";
    String name() default "";
    boolean ignore() default false;
}
