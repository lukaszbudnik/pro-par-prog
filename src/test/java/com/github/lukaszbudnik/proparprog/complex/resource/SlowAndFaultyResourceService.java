package com.github.lukaszbudnik.proparprog.complex.resource;

import javax.inject.Singleton;
import java.util.Random;

@Singleton
public class SlowAndFaultyResourceService implements ResourceService {

    private boolean faulty;

    public SlowAndFaultyResourceService(boolean faulty) {
        this.faulty = faulty;
    }

    @Override
    public int save(String resource) {
        Random r = new Random();
        try {
            Thread.sleep(600 + new Random().nextInt(100));
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
//        if (faulty && r.nextInt(3) == 2) {
        if (faulty && r.nextBoolean()) {
            try {
                Thread.sleep(300);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            throw new RuntimeException("boom, there was 33% probability of failure, I know it's high!");
        }
        return 123;
    }

}
