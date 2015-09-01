package com.github.lukaszbudnik.proparprog.complex.blog;

import com.github.lukaszbudnik.proparprog.complex.data.DataService;
import com.github.lukaszbudnik.proparprog.complex.resource.ResourceService;
import com.google.inject.Singleton;

import javax.inject.Inject;

@Singleton
public class SimpleBlogService implements BlogService {

    @Inject
    private DataService dataService;

    @Inject
    private ResourceService resourceService;

    @Override
    public void save(Blog blog) throws Exception {
        blog.getFiles().forEach(resourceService::save);
        dataService.save(blog.getLines());
    }

}
