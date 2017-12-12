/**
 * Copyright (C) 2015 Łukasz Budnik <lukasz.budnik@gmail.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
 */
package com.github.lukaszbudnik.proparprog.simple;

import com.google.common.util.concurrent.*;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

import static org.hamcrest.core.IsEqual.equalTo;
import static org.hamcrest.number.OrderingComparison.greaterThan;
import static org.hamcrest.number.OrderingComparison.lessThan;
import static org.junit.Assert.assertThat;

public class FuturesTest {

    @Test
    public void sequential() throws InterruptedException {
        long start = System.currentTimeMillis();
        int res1 = slow1();
        String res2 = slow2();
        long res3 = slow3();
        long end = System.currentTimeMillis();
        long duration = end - start;
        assertThat(res1, equalTo(123));
        assertThat(res2, equalTo("456"));
        assertThat(res3, equalTo(789l));
        assertThat(duration, greaterThan(1500l));
        System.out.println("sequentialTest ==> " + duration / (float) 1000);
    }

    @Test
    public void futures() throws InterruptedException, ExecutionException {
        long start = System.currentTimeMillis();

        // for Java 5 verbose anonymous implementation of Callable interface
        Callable<Integer> callable1 = new Callable<Integer>() {
            @Override
            public Integer call() throws Exception {
                return slow1();
            }
        };
        // for Java 8 lambdas
        Callable<String> callable2 = () -> slow2();
        Callable<Long> callable3 = () -> slow3();

        // always name your threads
        ThreadFactory threadFactory = new ThreadFactoryBuilder().setNameFormat("futuresTest-thread-%d").build();

        ExecutorService executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors(), threadFactory);
        Future<Integer> futureSlow1 = executorService.submit(callable1);
        Future<String> futureSlow2 = executorService.submit(callable2);
        Future<Long> futureSlow3 = executorService.submit(callable3);
        Integer res1 = futureSlow1.get();
        String res2 = futureSlow2.get();
        Long res3 = futureSlow3.get();

        // always shutdown executor!
        executorService.shutdown();

        long end = System.currentTimeMillis();
        long duration = end - start;

        assertThat(res1, equalTo(123));
        assertThat(res2, equalTo("456"));
        assertThat(res3, equalTo(789l));
        assertThat(duration, lessThan(1500l));

        System.out.println("futuresTest ==> " + duration / (float) 1000);
    }

    @Test
    public void completableFutures() throws InterruptedException, ExecutionException {
        long start = System.currentTimeMillis();

        ThreadFactory threadFactory = new ThreadFactoryBuilder().setNameFormat("completableFuturesTest-thread-%d").build();

        ExecutorService executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors(), threadFactory);

        // supplier doesn't throw exceptions, thus wrapped methods
        CompletableFuture<Integer> futureSlow1 = CompletableFuture.supplyAsync(() -> slow1Wrapped(), executorService);
        CompletableFuture<String> futureSlow2 = CompletableFuture.supplyAsync(() -> slow2Wrapped(), executorService);
        CompletableFuture<Long> futureSlow3 = CompletableFuture.supplyAsync(() -> slow3Wrapped(), executorService);

        // what? Void? see next test
        CompletableFuture<Void> allFutures = CompletableFuture.allOf(futureSlow1, futureSlow2, futureSlow3);
        Void res = allFutures.get();

        // always shutdown executor!
        executorService.shutdown();

        long end = System.currentTimeMillis();
        long duration = end - start;

        assertThat(duration, lessThan(1500l));

        System.out.println("completableFuturesTest ==> " + duration / (float) 1000);
    }

    @Test
    public void completableFuturesAllOfTest() throws InterruptedException, ExecutionException {
        long start = System.currentTimeMillis();

        ThreadFactory threadFactory = new ThreadFactoryBuilder().setNameFormat("completableFuturesTest-thread-%d").build();

        ExecutorService executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors(), threadFactory);

        Map<String, Object> res = new ConcurrentHashMap<>();
        CompletableFuture<Integer> futureSlow1 = CompletableFuture.supplyAsync(() -> {
            int res1 = slow1Wrapped();
            res.put("res1", res1);
            return res1;
        }, executorService);
        CompletableFuture<String> futureSlow2 = CompletableFuture.supplyAsync(() -> {
            String res2 = slow2Wrapped();
            res.put("res2", res2);
            return res2;
        }, executorService);
        CompletableFuture<Long> futureSlow3 = CompletableFuture.supplyAsync(() -> {
            long res3 = slow3Wrapped();
            res.put("res3", res3);
            return res3;
        }, executorService);

        // what? Void
        CompletableFuture<Void> allFutures = CompletableFuture.allOf(futureSlow1, futureSlow2, futureSlow3);
        Void all = allFutures.get();

        // always shutdown executor!
        executorService.shutdown();

        long end = System.currentTimeMillis();
        long duration = end - start;

        assertThat(res.get("res1"), equalTo(123));
        assertThat(res.get("res2"), equalTo("456"));
        assertThat(res.get("res3"), equalTo(789l));
        assertThat(duration, lessThan(1500l));

        System.out.println("completableFuturesAllOfTest ==> " + duration / (float) 1000);
    }

    @Test
    public void listenableFuturesTest() throws InterruptedException, ExecutionException {
        long start = System.currentTimeMillis();

        Callable<Integer> callable1 = () -> slow1();

        ThreadFactory threadFactory = new ThreadFactoryBuilder().setNameFormat("listenableFuturesTest-thread-%d").build();

        ListeningExecutorService executorService = MoreExecutors.listeningDecorator(Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors(), threadFactory));
        ListenableFuture<Integer> futureSlow1 = executorService.submit(callable1);
        ListenableFuture<Integer> futureSlow2 = executorService.submit(callable1);
        ListenableFuture<Integer> futureSlow3 = executorService.submit(callable1);
        ListenableFuture<List<Integer>> allFutures = Futures.allAsList(futureSlow1, futureSlow2, futureSlow3);
        List<Integer> all = allFutures.get();
        executorService.shutdown();
        long end = System.currentTimeMillis();
        long duration = end - start;

        assertThat(all, equalTo(Arrays.asList(123, 123, 123)));
        assertThat(duration, lessThan(1500l));

        System.out.println("listenableFuturesTest ==> " + duration / (float) 1000);
    }

    private int slow1() throws InterruptedException {
        Thread.sleep(300);
        return 123;
    }

    private String slow2() throws InterruptedException {
        Thread.sleep(700);
        return "456";
    }

    private long slow3() throws InterruptedException {
        Thread.sleep(500);
        return 789l;
    }

    private int slow1Wrapped() {
        try {
            return slow1();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private String slow2Wrapped() {
        try {
            return slow2();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private long slow3Wrapped() {
        try {
            return slow3();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

}
