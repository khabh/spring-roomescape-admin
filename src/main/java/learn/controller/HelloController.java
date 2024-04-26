package learn.controller;

import learn.service.HelloService;

public class HelloController {
    private HelloService helloService;

    public HelloService getHelloService() {
        return helloService;
    }

    public void setHelloService(final HelloService helloService) {
        this.helloService = helloService;
    }
}
