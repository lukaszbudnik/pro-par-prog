package com.github.lukaszbudnik.proparprog.complex.data;

import javax.inject.Singleton;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Singleton
public class SimpleDataService implements DataService {
    @Override
    public void save(List<String> data) {
        Random r = new Random();
        for (String d : data) {
            try {
                Thread.sleep(100 + r.nextInt(20));
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    public List<String> get(int limit) {
        Random r = new Random();
        List<String> data = new ArrayList<>(limit);
        for (int i = 0; i < limit; i++) {
            try {
                Thread.sleep(100 + r.nextInt(20));
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            data.add("data-" + i);
        }
        return data;
    }
}
