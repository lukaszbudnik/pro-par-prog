package com.github.lukaszbudnik.proparprog.complex.blog;

import com.github.lukaszbudnik.proparprog.complex.data.DataService;
import com.github.lukaszbudnik.proparprog.complex.resource.ResourceService;
import com.google.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import javax.inject.Inject;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Singleton
public class FullyParallelBlogService implements BlogService {

    private static Logger logger = LoggerFactory.getLogger(FullyParallelBlogService.class);

    @Inject
    private DataService dataService;

    @Inject
    private ResourceService resourceService;

    @Override
    public void save(Blog blog) throws Exception {
        ExecutorService es = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

        MDC.put("blogName", blog.getName());

        logger.debug("About to save blog");

        CompletableFuture<Void> files = CompletableFuture.runAsync(() ->
                blog.getFiles().parallelStream().forEach(f -> resourceService.save(f)), es);

        CompletableFuture<Void> data = CompletableFuture.runAsync(() ->
                dataService.save(blog.getLines()), es);

        files.thenAcceptBoth(data, (f, d) -> {
        }).get();

        logger.debug("Blog saved");

        MDC.clear();

        es.shutdown();
    }

}
