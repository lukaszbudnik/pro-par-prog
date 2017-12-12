package com.github.lukaszbudnik.proparprog.complex;

import com.github.lukaszbudnik.proparprog.complex.blog.Blog;
import com.github.lukaszbudnik.proparprog.complex.blog.BlogService;
import com.github.lukaszbudnik.proparprog.complex.core.FullyParallelCompletableFutureDedicatedExecutorHystrixBlogGuiceModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.netflix.config.ConfigurationManager;
import com.netflix.hystrix.HystrixCommandKey;
import com.netflix.hystrix.HystrixCommandMetrics;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;

public class HystrixTest {

    private Blog blog1;
    private Blog blog2;

    @Before
    public void setup() {
        blog1 = new Blog();
        blog1.setName("First");
        blog1.setFiles(Arrays.asList("a", "b", "c", "d", "e", "f", "g", "h"));
        blog1.setLines(Arrays.asList("1", "2", "3", "4", "5", "6"));

        blog2 = new Blog();
        blog2.setName("Second");
        blog2.setFiles(Arrays.asList("s", "t", "u", "w", "x", "y", "z", "ź", "ż"));
        blog2.setLines(Arrays.asList("-128", "-127", "-126", "-125", "-124", "-123"));
    }

    @Test
    public void s6_all_operations_using_parallel_streams_and_completable_futures_with_hystrix() throws Exception {
        ConfigurationManager.getConfigInstance().setProperty("hystrix.command.default.requestLog.enabled", true);
        ConfigurationManager.getConfigInstance().setProperty("hystrix.command.default.execution.isolation.thread.timeoutInMilliseconds", 20 * 1000);
        ConfigurationManager.getConfigInstance().setProperty("hystrix.threadpool.default.coreSize", 2 * 10);
        ConfigurationManager.getConfigInstance().setProperty("hystrix.command.default.circuitBreaker.requestVolumeThreshold", 2 * 20);

        Injector injector = Guice.createInjector(new FullyParallelCompletableFutureDedicatedExecutorHystrixBlogGuiceModule(true));
        BlogService blogService = injector.getInstance(BlogService.class);

        long start = System.currentTimeMillis();
        try {
            blogService.save(blog1);
            blogService.save(blog2);
        } catch (Exception e) {
            e.printStackTrace();
        }
        long end = System.currentTimeMillis();

        long time = end - start;

        System.out.println("Execution took ==> " + time / 1000d);

        HystrixCommandKey commandKey = HystrixCommandKey.Factory.asKey("SlowAndFaultyResourceServiceSaveCommand");
        long totalRequests = HystrixCommandMetrics.getInstance(commandKey).getHealthCounts().getTotalRequests();
        long totalErrors = HystrixCommandMetrics.getInstance(commandKey).getHealthCounts().getErrorCount();
        long errorPercentage = HystrixCommandMetrics.getInstance(commandKey).getHealthCounts().getErrorPercentage();
        System.out.println("Command ==> " + commandKey.name() + " total requests ==> " + totalRequests + " total errors ==> " + totalErrors + " error percentage ==> " + errorPercentage);

    }
    
}
