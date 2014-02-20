package io.github.tehsenaus.knockout4j.bindings.swing;

import javax.swing.*;

public abstract class TextBinding extends SwingBinding<String> {
    public TextBinding(final JLabel label) {
        super(new Setter<String>() {
            @Override
            public void run() {
                label.setText(value);
            }
        });
    }
    public TextBinding(final AbstractButton btn) {
        super(new Setter<String>() {
            @Override
            public void run() {
                btn.setText(value);
            }
        });
    }
}
