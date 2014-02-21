import io.github.tehsenaus.knockout4j.KO;
import io.github.tehsenaus.knockout4j.bindings.swing.SwingBinding;
import io.github.tehsenaus.knockout4j.bindings.swing.TextBinding;
import io.github.tehsenaus.knockout4j.bindings.swing.TextValueBinding;

import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.Collection;
import javax.swing.*;

public class HelloWorld extends JPanel {
    final KO.Observable<String> firstName = new KO.Observable<String>("Planet");
    final KO.Observable<String> lastName = new KO.Observable<String>("Earth");
    final KO.Computed<String> fullName = new KO.Computed<String>() {
        @Override
        protected String evaluate() {
            return firstName.get() + " " + lastName.get();
        }
    };

    final Collection<SwingBinding> swingBindings = new ArrayList<SwingBinding>();

    public HelloWorld() {
        initComponents();

        swingBindings.add(new TextValueBinding(firstName, textField1));
        swingBindings.add(new TextValueBinding(lastName, textField2));
        swingBindings.add(new TextBinding(label1) {
            @Override
            protected String evaluate() {
                return "Hello, " + fullName.get() + "!";
            }
        });
    }

    public static void main(String [] args) {
        JDialog dialog = new JDialog();
        dialog.getContentPane().add(new HelloWorld());
        dialog.pack();
        dialog.show();
        dialog.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                System.exit(0);
            }
        });
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        panel1 = new JPanel();
        textField1 = new JTextField();
        textField2 = new JTextField();
        panel2 = new JPanel();
        label1 = new JLabel();

        //======== this ========
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        //======== panel1 ========
        {
            panel1.setLayout(new FlowLayout(FlowLayout.LEFT));

            //---- textField1 ----
            textField1.setColumns(20);
            panel1.add(textField1);

            //---- textField2 ----
            textField2.setColumns(20);
            panel1.add(textField2);
        }
        add(panel1);

        //======== panel2 ========
        {
            panel2.setLayout(new FlowLayout(FlowLayout.LEFT));

            //---- label1 ----
            label1.setText("text");
            label1.setFont(label1.getFont().deriveFont(label1.getFont().getSize() + 10f));
            panel2.add(label1);
        }
        add(panel2);
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    private JPanel panel1;
    private JTextField textField1;
    private JTextField textField2;
    private JPanel panel2;
    private JLabel label1;
    // JFormDesigner - End of variables declaration  //GEN-END:variables
}
