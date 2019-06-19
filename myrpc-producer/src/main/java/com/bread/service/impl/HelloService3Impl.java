package com.bread.service.impl;

import com.bread.service.HelloService;
import com.bread.service.HelloService3;
import com.bread.utils.RpcService;

/**
 * @author zhiwj
 * @date 2019/6/12
 */
@RpcService(HelloService.class)
public class HelloService3Impl implements HelloService3 {

    @Override
    public String hello3(String msg) {
        return "hello3333333333 : " + msg;
    }
}
