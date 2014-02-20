package io.github.tehsenaus.knockout4j.bindings.swing;

import io.github.tehsenaus.knockout4j.KO;

import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.JTextComponent;

public class TextValueBinding extends WritableSwingBinding<String> {
    public <OT extends KO.Readable<String> & KO.Writable<String>> TextValueBinding(OT observable, final JTextComponent textComponent) {
        super(new Setter<String>() {
            @Override
            public void run() {
                textComponent.setText(value);
            }
        }, observable);

        textComponent.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                update();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                update();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                update();
            }

            private void update() {
                try {
                    write(textComponent.getDocument().getText(0, textComponent.getDocument().getLength()));
                } catch (BadLocationException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }

    public <OT extends KO.Readable<String> & KO.Writable<String>> TextValueBinding(OT observable, final Document document) {
        super(new Setter<String>() {
            @Override
            public void run() {
                try {
                    document.remove(0, document.getLength());
                    document.insertString(0, value, null);
                } catch (BadLocationException e) {
                    throw new RuntimeException(e);
                }
            }
        }, observable);

        document.addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                update();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                update();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                update();
            }

            private void update() {
                try {
                    write(document.getText(0, document.getLength()));
                } catch (BadLocationException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }
}
