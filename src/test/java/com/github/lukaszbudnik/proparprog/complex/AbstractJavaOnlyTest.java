package com.github.lukaszbudnik.proparprog.complex;

import com.github.lukaszbudnik.proparprog.complex.blog.Blog;
import com.github.lukaszbudnik.proparprog.complex.blog.BlogService;
import com.github.lukaszbudnik.proparprog.complex.core.*;import com.google.inject.Guice;
import com.google.inject.Injector;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;

public abstract class AbstractJavaOnlyTest {

    private final boolean faulty;

    private Blog blog;

    public AbstractJavaOnlyTest(boolean faulty) {
        this.faulty = faulty;
    }

    @Before
    public void setup() {
        blog = new Blog();
        blog.setFiles(Arrays.asList("a", "b", "c", "d", "e", "f", "g", "h"));
        blog.setLines(Arrays.asList("1", "2", "3", "4", "5", "6"));
    }


    @Test
    public void s1_all_operations_sequential() throws Exception {
        Injector injector = Guice.createInjector(new SimpleBlogGuiceModule(faulty));
        BlogService blogService = injector.getInstance(BlogService.class);

        long start = System.currentTimeMillis();
        blogService.save(blog);
        long end = System.currentTimeMillis();

        long time = end - start;

        System.out.println("Execution took ==> " + time / 1000d);
    }

    @Test
    public void s2_completable_future_composite_resource_service() throws Exception {
        Injector injector = Guice.createInjector(new CompletableFutureCompositeResourceServiceBlogGuiceModule(faulty));
        BlogService blogService = injector.getInstance(BlogService.class);

        long start = System.currentTimeMillis();
        blogService.save(blog);
        long end = System.currentTimeMillis();

        long time = end - start;

        System.out.println("Execution took ==> " + time / 1000d);
    }

    @Test
    public void s3_parallel_saving_files_and_lines() throws Exception {
        Injector injector = Guice.createInjector(new ParallelBlogGuiceModule(faulty));
        BlogService blogService = injector.getInstance(BlogService.class);

        long start = System.currentTimeMillis();
        blogService.save(blog);
        long end = System.currentTimeMillis();

        long time = end - start;

        System.out.println("Execution took ==> " + time / 1000d);
    }

    @Test
    public void s4_completable_future_and_parallel_saving() throws Exception {
        Injector injector = Guice.createInjector(new ParallelCompletableFutureBlogGuiceModule(faulty));
        BlogService blogService = injector.getInstance(BlogService.class);

        long start = System.currentTimeMillis();
        blogService.save(blog);
        long end = System.currentTimeMillis();

        long time = end - start;

        System.out.println("Execution took ==> " + time / 1000d);
    }

    @Test
    public void s5_completable_future_and_parallel_saving_with_dedicated_executor() throws Exception {
        Injector injector = Guice.createInjector(new ParallelCompletableFutureDedicatedExecutorBlogGuiceModule(faulty));
        BlogService blogService = injector.getInstance(BlogService.class);

        long start = System.currentTimeMillis();
        blogService.save(blog);
        long end = System.currentTimeMillis();

        long time = end - start;

        System.out.println("Execution took ==> " + time / 1000d);
    }

    @Test
    public void s6_all_operations_using_parallel_streams_and_completable_futures() throws Exception {
        Injector injector = Guice.createInjector(new FullyParallelCompletableFutureDedicatedExecutorBlogGuiceModule(faulty));
        BlogService blogService = injector.getInstance(BlogService.class);

        long start = System.currentTimeMillis();
        blogService.save(blog);
        long end = System.currentTimeMillis();

        long time = end - start;

        System.out.println("Execution took ==> " + time / 1000d);
    }
}
