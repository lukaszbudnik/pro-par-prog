package com.github.lukaszbudnik.proparprog.complex.resource;


import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class CompositeResourceService implements ResourceService {

    @Inject
    FastResourceService fastResourceService;

    @Inject
    SlowAndFaultyResourceService slowAndFaultyResourceService;

    @Override
    public int save(String resource) {
        slowAndFaultyResourceService.save(resource);
        fastResourceService.save(resource);
        return 123;
    }

}
