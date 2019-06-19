package com.bread.service.impl;

import com.bread.service.HelloService;
import com.bread.utils.RpcService;

/**
 * @author zhiwj
 * @date 2019/6/12
 */
@RpcService(HelloService.class)
public class HelloServiceImpl implements HelloService {

    @Override
    public String hello(String msg) {
        return "hello : " + msg;
    }

    @Override
    public String hello2(String msg) {
        return "hello2222222222222222222222222 : " + msg;
    }
}
