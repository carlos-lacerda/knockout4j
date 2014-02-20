package io.github.tehsenaus.knockout4j.bindings.swing;

import javax.swing.*;

public abstract class EnabledBinding extends SwingBinding<Boolean> {
    public EnabledBinding(final JComponent component) {
        super(new Setter<Boolean>() {
            @Override
            public void run() {
                component.setEnabled(value);
            }
        });
    }
}
