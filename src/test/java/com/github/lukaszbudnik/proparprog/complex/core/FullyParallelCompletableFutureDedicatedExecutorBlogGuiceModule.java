package com.github.lukaszbudnik.proparprog.complex.core;

import com.github.lukaszbudnik.proparprog.complex.blog.BlogService;
import com.github.lukaszbudnik.proparprog.complex.blog.FullyParallelBlogService;
import com.github.lukaszbudnik.proparprog.complex.data.DataService;
import com.github.lukaszbudnik.proparprog.complex.data.ParallelDataService;
import com.github.lukaszbudnik.proparprog.complex.resource.ParallelDedicatedExecutorCompositeResourceService;
import com.github.lukaszbudnik.proparprog.complex.resource.ResourceService;
import com.github.lukaszbudnik.proparprog.complex.resource.SlowAndFaultyResourceService;
import com.github.lukaszbudnik.proparprog.complex.data.DataService;import com.github.lukaszbudnik.proparprog.complex.resource.ParallelDedicatedExecutorCompositeResourceService;import com.google.inject.Binder;

public class FullyParallelCompletableFutureDedicatedExecutorBlogGuiceModule extends AbstractBlogModule {

    public FullyParallelCompletableFutureDedicatedExecutorBlogGuiceModule(boolean faulty) {
        super(faulty);
    }

    @Override
    public void configure(Binder binder) {
        binder.bind(BlogService.class).to(FullyParallelBlogService.class);
        binder.bind(ResourceService.class).to(ParallelDedicatedExecutorCompositeResourceService.class);
        binder.bind(DataService.class).to(ParallelDataService.class);
        binder.bind(SlowAndFaultyResourceService.class).toInstance(new SlowAndFaultyResourceService(faulty));
    }

}
