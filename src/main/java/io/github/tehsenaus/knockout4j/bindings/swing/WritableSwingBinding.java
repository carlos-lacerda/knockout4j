package io.github.tehsenaus.knockout4j.bindings.swing;

import io.github.tehsenaus.knockout4j.KO;


public abstract class WritableSwingBinding<T> extends SwingBinding<T> {
    final KO.Readable<T> readable;
    final KO.Writable<T> writable;
    protected boolean writing = false;

    public <OT extends KO.Readable<T> & KO.Writable<T>> WritableSwingBinding(Setter<T> setter, OT observable) {
        super(setter, KO.Computed.DEFER_EVAL);
        this.readable = observable;
        this.writable = observable;
        this.updater.get();
    }

    protected void write(T value) {
        writing = true;
        try {
            writable.set(value);
        } finally {
            writing = false;
        }
    }

    @Override
    protected T evaluate() {
        return readable.get();
    }

    @Override
    final protected void updateBinding(T value) {
        if (!writing) {
            super.updateBinding(value);
        }
    }
}
