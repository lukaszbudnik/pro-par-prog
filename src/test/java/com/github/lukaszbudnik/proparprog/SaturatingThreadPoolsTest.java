/**
 * Copyright (C) 2015 Łukasz Budnik <lukasz.budnik@gmail.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
 */
package com.github.lukaszbudnik.proparprog;

import com.codahale.metrics.ConsoleReporter;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.jvm.ThreadStatesGaugeSet;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.*;

public class SaturatingThreadPoolsTest {

    private int slowBlockingIOSimulation() {
        try {
            Thread.sleep(700);
            return 123;
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private int cpuConsumingOperationSimulation() {
        for (int i = 0; i < 500000; i++) {
            UUID.randomUUID().toString();
        }
        return -123;
    }

    @Test
    public void slowBlockingIOSimulationMatchingProcessors() throws ExecutionException, InterruptedException {
        long start = System.currentTimeMillis();

        int numberOfThreads = Runtime.getRuntime().availableProcessors();
        int numberOfOperations = Runtime.getRuntime().availableProcessors();
        executeSlowBlockingIOSimulation(numberOfThreads, numberOfOperations);

        long end = System.currentTimeMillis();
        long duration = end - start;

        System.out.println("saturatingThreadPoolWithSlowBlockingIOSimulation ==> " + duration / (float) 1000);
    }

    @Test
    public void slowBlockingIOSimulationNotYetSaturated1() throws ExecutionException, InterruptedException {
        long start = System.currentTimeMillis();

        int numberOfThreads = Runtime.getRuntime().availableProcessors() * 2;
        int numberOfOperations = Runtime.getRuntime().availableProcessors() * 2;
        executeSlowBlockingIOSimulation(numberOfThreads, numberOfOperations);

        long end = System.currentTimeMillis();
        long duration = end - start;

        System.out.println("saturatingThreadPoolWithSlowBlockingIOSimulation ==> " + duration / (float) 1000);
    }

    @Test
    public void slowBlockingIOSimulationNotYetSaturated2() throws ExecutionException, InterruptedException {
        long start = System.currentTimeMillis();

        int numberOfThreads = Runtime.getRuntime().availableProcessors() * 4;
        int numberOfOperations = Runtime.getRuntime().availableProcessors() * 4;
        executeSlowBlockingIOSimulation(numberOfThreads, numberOfOperations);

        long end = System.currentTimeMillis();
        long duration = end - start;

        System.out.println("saturatingThreadPoolWithSlowBlockingIOSimulation ==> " + duration / (float) 1000);
    }

    @Test
    public void slowBlockingIOSimulationNotYetSaturated3() throws ExecutionException, InterruptedException {
        long start = System.currentTimeMillis();

        int numberOfThreads = Runtime.getRuntime().availableProcessors() * 64;
        int numberOfOperations = Runtime.getRuntime().availableProcessors() * 64;
        executeSlowBlockingIOSimulation(numberOfThreads, numberOfOperations);

        long end = System.currentTimeMillis();
        long duration = end - start;

        System.out.println("saturatingThreadPoolWithSlowBlockingIOSimulation ==> " + duration / (float) 1000);
    }

    @Test
    public void slowBlockingIOSimulationSaturated1() throws ExecutionException, InterruptedException {
        long start = System.currentTimeMillis();

        int numberOfThreads = Runtime.getRuntime().availableProcessors() * 196; // 8 * 196
        int numberOfOperations = Runtime.getRuntime().availableProcessors() * 196; // 8 * 196
        executeSlowBlockingIOSimulation(numberOfThreads, numberOfOperations);

        long end = System.currentTimeMillis();
        long duration = end - start;

        System.out.println("saturatingThreadPoolWithSlowBlockingIOSimulation ==> " + duration / (float) 1000);
    }

    @Test
    public void slowBlockingIOSimulationSaturated2() throws ExecutionException, InterruptedException {
        long start = System.currentTimeMillis();

        int numberOfThreads = Runtime.getRuntime().availableProcessors();
        int numberOfOperations = Runtime.getRuntime().availableProcessors() * 2;
        executeSlowBlockingIOSimulation(numberOfThreads, numberOfOperations);

        long end = System.currentTimeMillis();
        long duration = end - start;

        System.out.println("saturatingThreadPoolWithSlowBlockingIOSimulation ==> " + duration / (float) 1000);
    }

    @Test
    public void cpuConsumingOperationSimulationMatchingProcessors() throws ExecutionException, InterruptedException {
        long start = System.currentTimeMillis();

        int numberOfThreads = Runtime.getRuntime().availableProcessors();
        int numberOfOperations = Runtime.getRuntime().availableProcessors();
        executeCpuConsumingOperationSimulation(numberOfThreads, numberOfOperations);

        long end = System.currentTimeMillis();
        long duration = end - start;

        System.out.println("cpuConsumingOperationSimulationMatchingProcessors ==> " + duration / (float) 1000);
    }


    @Test
    public void cpuConsumingOperationSimulationSaturated1() throws ExecutionException, InterruptedException {
        long start = System.currentTimeMillis();

        int numberOfThreads = Runtime.getRuntime().availableProcessors() * 2;
        int numberOfOperations = Runtime.getRuntime().availableProcessors() * 2;
        executeCpuConsumingOperationSimulation(numberOfThreads, numberOfOperations);

        long end = System.currentTimeMillis();
        long duration = end - start;

        System.out.println("cpuConsumingOperationSimulationSaturated1 ==> " + duration / (float) 1000);
    }

    @Test
    public void cpuConsumingOperationSimulationSaturated2() throws ExecutionException, InterruptedException {
        long start = System.currentTimeMillis();

        int numberOfThreads = Runtime.getRuntime().availableProcessors();
        int numberOfOperations = Runtime.getRuntime().availableProcessors() * 2;
        executeCpuConsumingOperationSimulation(numberOfThreads, numberOfOperations);

        long end = System.currentTimeMillis();
        long duration = end - start;

        System.out.println("cpuConsumingOperationSimulationSaturated2 ==> " + duration / (float) 1000);
    }

    private static ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

    @BeforeClass
    public static void before() {
        MetricRegistry registry = new MetricRegistry();
        registry.register("threads", new ThreadStatesGaugeSet());
        // checkout other reporters! this is just a sample
        ConsoleReporter reporter = ConsoleReporter.forRegistry(registry)
                .outputTo(new PrintStream(outputStream))
                .convertRatesTo(TimeUnit.SECONDS)
                .convertDurationsTo(TimeUnit.MILLISECONDS)
                .build();
        reporter.start(5, TimeUnit.SECONDS);
    }

    @AfterClass
    public static void after() {
        // print as a summary so that tests output is not clattered
        System.out.println(outputStream.toString());
    }

    private void executeSlowBlockingIOSimulation(int numberOfThreads, int numberOfOperations) throws InterruptedException, ExecutionException {
        ThreadFactory threadFactory = new ThreadFactoryBuilder().setNameFormat("slowBlockingIOSimulation-thread-%d").build();

        ExecutorService executorService = Executors.newFixedThreadPool(numberOfThreads, threadFactory);

        List<CompletableFuture<Integer>> futures = new ArrayList<>();
        for (int i = 0; i < numberOfOperations; i++) {
            CompletableFuture<Integer> future = CompletableFuture.supplyAsync(() -> slowBlockingIOSimulation(), executorService);
            futures.add(future);
        }

        CompletableFuture<Void> all = CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));
        all.get();

        executorService.shutdown();
    }

    private void executeCpuConsumingOperationSimulation(int numberOfThreads, int numberOfOperations) throws InterruptedException, ExecutionException {
        ThreadFactory threadFactory = new ThreadFactoryBuilder().setNameFormat("cpuConsumingOperationSimulation-thread-%d").build();

        ExecutorService executorService = Executors.newFixedThreadPool(numberOfThreads, threadFactory);

        List<CompletableFuture<Integer>> futures = new ArrayList<>();
        for (int i = 0; i < numberOfOperations; i++) {
            CompletableFuture<Integer> future = CompletableFuture.supplyAsync(() -> cpuConsumingOperationSimulation(), executorService);
            futures.add(future);
        }

        CompletableFuture<Void> all = CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));
        all.get();

        executorService.shutdown();
    }



}
