package io.github.tehsenaus.knockout4j.bindings.swing;

import io.github.tehsenaus.knockout4j.KO;

import javax.swing.*;

public abstract class SwingBinding<T> {
    protected final KO.Computed<T> updater;
    protected final Setter<T> setter;

    public SwingBinding(Setter<T> setter) {
        this(setter, 0);
    }
    public SwingBinding(Setter<T> setter, int flags) {
        this.setter = setter;
        this.updater = new KO.Computed<T>(flags) {
            @Override
            protected T evaluate() {
                T value = SwingBinding.this.evaluate();
                updateBinding(value);
                return value;
            }
        };
    }

    protected void updateBinding(T value) {
        setter.value = value;
        SwingUtilities.invokeLater(setter);
    }

    protected abstract T evaluate();

    abstract static class Setter<T> implements Runnable {
        T value;
    }
}
