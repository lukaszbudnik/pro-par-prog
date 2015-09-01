package com.github.lukaszbudnik.proparprog.complex.core;

import com.github.lukaszbudnik.proparprog.complex.blog.BlogService;
import com.github.lukaszbudnik.proparprog.complex.blog.ParallelBlogService;
import com.github.lukaszbudnik.proparprog.complex.data.DataService;
import com.github.lukaszbudnik.proparprog.complex.data.ParallelDataService;
import com.github.lukaszbudnik.proparprog.complex.resource.CompositeResourceService;
import com.github.lukaszbudnik.proparprog.complex.resource.ResourceService;
import com.github.lukaszbudnik.proparprog.complex.resource.SlowAndFaultyResourceService;
import com.github.lukaszbudnik.proparprog.complex.blog.ParallelBlogService;import com.github.lukaszbudnik.proparprog.complex.data.DataService;import com.github.lukaszbudnik.proparprog.complex.resource.CompositeResourceService;import com.google.inject.Binder;

public class ParallelBlogGuiceModule extends AbstractBlogModule {

    public ParallelBlogGuiceModule(boolean faulty) {
        super(faulty);
    }

    @Override
    public void configure(Binder binder) {
        binder.bind(BlogService.class).to(ParallelBlogService.class);
        binder.bind(ResourceService.class).to(CompositeResourceService.class);
        binder.bind(DataService.class).to(ParallelDataService.class);
        binder.bind(SlowAndFaultyResourceService.class).toInstance(new SlowAndFaultyResourceService(faulty));
    }

}
