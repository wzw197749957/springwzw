package com.lagou.edu.anno;

import java.lang.annotation.*;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface WzwAutowired {
    String value() default "";
}
