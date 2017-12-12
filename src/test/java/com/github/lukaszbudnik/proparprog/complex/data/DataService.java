package com.github.lukaszbudnik.proparprog.complex.data;

import java.util.List;

public interface DataService {

    void save(List<String> data);

    List<String> get(int id);

}
