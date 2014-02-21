import io.github.tehsenaus.knockout4j.KO;
import io.github.tehsenaus.knockout4j.bindings.swing.EnabledBinding;
import io.github.tehsenaus.knockout4j.bindings.swing.ForeachBindingTableModel;
import io.github.tehsenaus.knockout4j.bindings.swing.SwingBinding;
import io.github.tehsenaus.knockout4j.bindings.swing.TextValueBinding;

import java.awt.*;
import java.awt.event.*;
import java.lang.Object;
import java.lang.Override;
import java.lang.String;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import javax.swing.*;

public class SimpleList extends JPanel {
    final KO.ObservableList<String> items = new KO.ObservableList<String>(new ArrayList<String>());
    final KO.Observable<String> itemToAdd = new KO.Observable<String>("");

    final Collection<SwingBinding> swingBindings = new ArrayList<SwingBinding>();

    public SimpleList() {
        initComponents();

        items.add("Alpha");
        items.add("Beta");
        items.add("Gamma");

        table1.setModel(new ForeachBindingTableModel<String>() {
            @Override
            protected Object getColumnValue(String row, int columnIndex) {
                return row;
            }

            @Override
            protected List<String> evaluate() {
                return items.get();
            }

            @Override
            public int getColumnCount() {
                return 1;
            }
        });

        swingBindings.add(new TextValueBinding(itemToAdd, textField1));
        swingBindings.add(new EnabledBinding(button1) {
            @Override
            protected Boolean evaluate() {
                return !itemToAdd.get().isEmpty();
            }
        });
    }

    private void button1ActionPerformed(ActionEvent e) {
        items.add(itemToAdd.get());
        itemToAdd.set("");
    }

    public static void main(String [] args) {
        JDialog dialog = new JDialog();
        dialog.getContentPane().add(new SimpleList());
        dialog.pack();
        dialog.show();
        dialog.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                System.exit(0);
            }
        });
    }

    private void removeButtonActionPerformed(ActionEvent e) {
        items.remove(table1.getSelectedRow());
    }

    private void sortButtonActionPerformed(ActionEvent e) {
        Collections.sort(items);
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        scrollPane1 = new JScrollPane();
        table1 = new JTable();
        panel1 = new JPanel();
        textField1 = new JTextField();
        button1 = new JButton();
        panel2 = new JPanel();
        removeButton = new JButton();
        sortButton = new JButton();

        //======== this ========
        setLayout(new BorderLayout());

        //======== scrollPane1 ========
        {
            scrollPane1.setViewportView(table1);
        }
        add(scrollPane1, BorderLayout.CENTER);

        //======== panel1 ========
        {
            panel1.setLayout(new FlowLayout());

            //---- textField1 ----
            textField1.setColumns(20);
            panel1.add(textField1);

            //---- button1 ----
            button1.setText("Add");
            button1.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    button1ActionPerformed(e);
                }
            });
            panel1.add(button1);
        }
        add(panel1, BorderLayout.PAGE_START);

        //======== panel2 ========
        {
            panel2.setLayout(new FlowLayout());

            //---- removeButton ----
            removeButton.setText("Remove");
            removeButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    removeButtonActionPerformed(e);
                }
            });
            panel2.add(removeButton);

            //---- sortButton ----
            sortButton.setText("Sort");
            sortButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    sortButtonActionPerformed(e);
                }
            });
            panel2.add(sortButton);
        }
        add(panel2, BorderLayout.PAGE_END);
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    private JScrollPane scrollPane1;
    private JTable table1;
    private JPanel panel1;
    private JTextField textField1;
    private JButton button1;
    private JPanel panel2;
    private JButton removeButton;
    private JButton sortButton;
    // JFormDesigner - End of variables declaration  //GEN-END:variables
}
