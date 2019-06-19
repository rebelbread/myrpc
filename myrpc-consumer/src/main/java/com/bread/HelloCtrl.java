package com.bread;

import com.bread.client.RpcProxy;
import com.bread.service.HelloService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author zhiwj
 * @date 2019/6/17
 */
@RestController
public class HelloCtrl {

    @Autowired
    private HelloService helloService;
//
//    @RpcReference
//    private HelloService3 helloService3;

    @Autowired
    private RpcProxy rpcProxy;

    @RequestMapping("/hello")
    public String hello() {
        HelloService helloServiceProxy = rpcProxy.get(HelloService.class);


        System.out.println(helloServiceProxy.hello("qwe"));
        System.out.println(helloService.hello("que"));

        return "";
    }

    @RequestMapping("/")
    public String index() {
        return "index";
    }

}
