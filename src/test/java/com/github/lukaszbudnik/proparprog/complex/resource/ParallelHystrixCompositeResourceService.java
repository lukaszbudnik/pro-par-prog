package com.github.lukaszbudnik.proparprog.complex.resource;

import com.google.inject.Injector;
import com.netflix.hystrix.HystrixRequestLog;
import com.netflix.hystrix.strategy.concurrency.HystrixRequestContext;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Singleton
public class ParallelHystrixCompositeResourceService implements ResourceService, AutoCloseable {

    @Inject
    private FastResourceService fastResourceService;

    @Inject
    private Injector injector;

    private ExecutorService es = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

    @Override
    public int save(String resource) {

        HystrixRequestContext ctx = HystrixRequestContext.initializeContext();

        SlowAndFaultyResourceServiceSaveCommand slowAndFaultyResourceServiceSaveCommand = injector.getInstance(SlowAndFaultyResourceServiceSaveCommand.class);
        slowAndFaultyResourceServiceSaveCommand.setResource(resource);

        CompletableFuture<Integer> slow = CompletableFuture.supplyAsync(() ->
                slowAndFaultyResourceServiceSaveCommand.execute(),
                es);

        CompletableFuture<Integer> fast = CompletableFuture.supplyAsync(() ->
                fastResourceService.save(resource),
                es);

        try {
            return fast.thenCombine(slow, (f, s) -> f).get();
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            System.out.println("Resource ==> " + resource + " executed commands ==> " + HystrixRequestLog.getCurrentRequest().getExecutedCommandsAsString());
            ctx.shutdown();
        }

    }

    @Override
    public void close() throws Exception {
        es.shutdown();
    }
}