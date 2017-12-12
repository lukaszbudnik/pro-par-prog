package com.github.lukaszbudnik.proparprog.complex.resource;

import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandGroupKey;
import com.netflix.hystrix.HystrixCommandKey;

import javax.inject.Inject;

public class SlowAndFaultyResourceServiceSaveCommand extends HystrixCommand<Integer> {

    private String resource;

    private final int retries;
    private static int MAX_RETRIES = 10;

    private SlowAndFaultyResourceService slowAndFaultyResourceService;

    @Inject
    protected SlowAndFaultyResourceServiceSaveCommand(SlowAndFaultyResourceService slowAndFaultyResourceService) {
        this(slowAndFaultyResourceService, 0);
    }

    public SlowAndFaultyResourceServiceSaveCommand(SlowAndFaultyResourceService slowAndFaultyResourceService, int retries) {
        super(Setter.withGroupKey(HystrixCommandGroupKey.Factory.asKey("SlowAndFaultyResourceService"))
                .andCommandKey(HystrixCommandKey.Factory.asKey("SlowAndFaultyResourceServiceSaveCommand")));
        this.slowAndFaultyResourceService = slowAndFaultyResourceService;
        this.retries = retries;
    }

    @Override
    protected Integer run() throws Exception {
        return slowAndFaultyResourceService.save(resource);
    }

    @Override
    protected Integer getFallback() {
        System.out.println("Resource ==> " + resource + " number of retries ==> " + retries);
        if (retries < MAX_RETRIES) {
            SlowAndFaultyResourceServiceSaveCommand fallback = new SlowAndFaultyResourceServiceSaveCommand(slowAndFaultyResourceService, retries + 1);
            fallback.setResource(resource);
            return fallback.execute();
        } else {
            System.out.println("Resource ==> " + resource + " exhausted all retries...");
            throw new RuntimeException("Did have enough luck");
        }
    }

    public void setResource(String resource) {
        this.resource = resource;
    }
}
