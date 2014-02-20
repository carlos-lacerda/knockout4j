package io.github.tehsenaus.knockout4j.bindings.swing;

import javax.swing.*;

public abstract class VisibleBinding extends SwingBinding<Boolean> {
    public VisibleBinding(final JComponent component) {
        super(new Setter<Boolean>() {
            @Override
            public void run() {
                component.setVisible(value);
            }
        });
    }
}
