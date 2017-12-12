package com.github.lukaszbudnik.proparprog.complex.resource;


import javax.inject.Singleton;
import java.util.Random;

@Singleton
public class FastResourceService implements ResourceService {
    @Override
    public int save(String resource) {
        try {
            Thread.sleep(500 + new Random().nextInt(100));
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        return 123;
    }

}
