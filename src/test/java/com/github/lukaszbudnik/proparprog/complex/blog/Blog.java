package com.github.lukaszbudnik.proparprog.complex.blog;

import java.util.List;

public class Blog {

    private String name;
    private List<String> lines;
    private List<String> files;

    public List<String> getLines() {
        return lines;
    }

    public void setLines(List<String> lines) {
        this.lines = lines;
    }

    public List<String> getFiles() {
        return files;
    }

    public void setFiles(List<String> files) {
        this.files = files;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
