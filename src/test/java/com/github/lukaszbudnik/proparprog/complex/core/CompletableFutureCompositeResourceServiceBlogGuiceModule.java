package com.github.lukaszbudnik.proparprog.complex.core;

import com.github.lukaszbudnik.proparprog.complex.blog.BlogService;
import com.github.lukaszbudnik.proparprog.complex.blog.SimpleBlogService;
import com.github.lukaszbudnik.proparprog.complex.data.DataService;
import com.github.lukaszbudnik.proparprog.complex.data.SimpleDataService;
import com.github.lukaszbudnik.proparprog.complex.resource.CompletableFutureCompositeResourceService;
import com.github.lukaszbudnik.proparprog.complex.resource.ResourceService;
import com.github.lukaszbudnik.proparprog.complex.resource.SlowAndFaultyResourceService;
import com.github.lukaszbudnik.proparprog.complex.blog.BlogService;import com.github.lukaszbudnik.proparprog.complex.blog.SimpleBlogService;import com.github.lukaszbudnik.proparprog.complex.data.DataService;import com.github.lukaszbudnik.proparprog.complex.data.SimpleDataService;import com.github.lukaszbudnik.proparprog.complex.resource.CompletableFutureCompositeResourceService;import com.github.lukaszbudnik.proparprog.complex.resource.ResourceService;import com.github.lukaszbudnik.proparprog.complex.resource.SlowAndFaultyResourceService;import com.google.inject.Binder;

public class CompletableFutureCompositeResourceServiceBlogGuiceModule extends AbstractBlogModule {

    public CompletableFutureCompositeResourceServiceBlogGuiceModule(boolean faulty) {
        super(faulty);
    }

    @Override
    public void configure(Binder binder) {
        binder.bind(BlogService.class).to(SimpleBlogService.class);
        binder.bind(ResourceService.class).to(CompletableFutureCompositeResourceService.class);
        binder.bind(DataService.class).to(SimpleDataService.class);
        binder.bind(SlowAndFaultyResourceService.class).toInstance(new SlowAndFaultyResourceService(faulty));
    }

}
