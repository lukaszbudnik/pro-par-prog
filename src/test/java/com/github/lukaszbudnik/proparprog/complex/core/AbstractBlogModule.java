package com.github.lukaszbudnik.proparprog.complex.core;

import com.google.inject.Module;

public abstract class AbstractBlogModule implements Module {
    protected final boolean faulty;

    public AbstractBlogModule(boolean faulty) {
        this.faulty = faulty;
    }
}
