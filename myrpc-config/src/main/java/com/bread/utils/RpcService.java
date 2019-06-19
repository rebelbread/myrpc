package com.bread.utils;

import org.springframework.stereotype.Component;

import java.lang.annotation.*;

/**
 * @author zhiwj
 * @date 2019/6/12
 */

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Component
public @interface RpcService {

    Class<?> value();

}
