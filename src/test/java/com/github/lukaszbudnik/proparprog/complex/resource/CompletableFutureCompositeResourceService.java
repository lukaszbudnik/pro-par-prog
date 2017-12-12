package com.github.lukaszbudnik.proparprog.complex.resource;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.concurrent.CompletableFuture;

@Singleton
public class CompletableFutureCompositeResourceService implements ResourceService {

    @Inject
    FastResourceService fastResourceService;

    @Inject
    SlowAndFaultyResourceService slowAndFaultyResourceService;

    @Override
    public int save(String resource) {
        CompletableFuture<Integer> slow = CompletableFuture.supplyAsync(() ->
                slowAndFaultyResourceService.save(resource));

        CompletableFuture<Integer> fast = CompletableFuture.supplyAsync(() ->
                        fastResourceService.save(resource)
        );

        try {
            return fast.thenCombine(slow, (f,s) -> f).get();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

}