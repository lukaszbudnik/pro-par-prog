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

import com.google.common.base.Stopwatch;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.hamcrest.core.IsEqual.equalTo;
import static org.hamcrest.number.OrderingComparison.lessThan;
import static org.junit.Assert.assertThat;

public class ParallelStreamsTest {

    @Test
    public void processList() {
        Stopwatch stopwatch = Stopwatch.createStarted();

        List<String> strings = Arrays.asList("first", "second", "third");

        List<String> processed = strings.parallelStream().unordered().map((s) -> {
            long sleep = 0;
            switch (s) {
                case "first": sleep = 500; break;
                case "second": sleep = 1000; break;
                case "third": sleep = 100; break;
            }

            try {
                Thread.sleep(sleep);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

            return s;
        }).map((s) -> {
            System.out.println(s);
            return s;
        }).collect(Collectors.toList());

        long duration = stopwatch.elapsed(TimeUnit.MILLISECONDS);

        System.out.println("processList ==> " + duration / (float) 1000);

        assertThat(processed, equalTo(strings));
        assertThat(duration, lessThan(1000l + 100));
    }

    @Test
    public void customForkJoinPool() throws ExecutionException, InterruptedException {
        Stopwatch stopwatch1 = Stopwatch.createStarted();
        Random random = new Random();
        List<String> uuids1 = IntStream.range(0, 1_000).parallel().mapToObj((i) -> {
            try {
                Thread.sleep(random.nextInt(100));
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            return UUID.randomUUID().toString();
        }).collect(Collectors.toList());

        long duration1 = stopwatch1.elapsed(TimeUnit.MILLISECONDS);

        Stopwatch stopwatch2 = Stopwatch.createStarted();

        ForkJoinPool forkJoinPool = new ForkJoinPool(Runtime.getRuntime().availableProcessors() * 2);

        List<String> uuids2 = forkJoinPool.submit(new Callable<List<String>>() {
            public List<String> call() {
                return IntStream.range(0, 1_000).parallel().mapToObj((i) -> {
                    try {
                        Thread.sleep(random.nextInt(100));
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    return UUID.randomUUID().toString();
                }).collect(Collectors.toList());
            }
        }).get();

        long duration2 = stopwatch2.elapsed(TimeUnit.MILLISECONDS);

        System.out.println("createParallelStream 1 ==> " + duration1 / (float) 1000);
        System.out.println("createParallelStream 2 ==> " + duration2 / (float) 1000);
    }

}
