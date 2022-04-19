package app.ui;

import app.entity.Product;
import app.manager.ProductManager;
import app.util.BaseForm;
import app.util.TableModel;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.sql.SQLException;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Predicate;

public class TableForm extends BaseForm {
    private JPanel mainPanel;
    private JTable table;
    private JButton devButton;
    private JButton helpButton;
    private JTextField searchField;
    private JComboBox comboBox1;
    private JComboBox comboBox2;
    private JButton sortButton;
    private JButton clearButton;
    private JButton addButton;
    private JComboBox comboBox3;
    private JButton backButton;
    private JButton nextButton;
    private JLabel pages;
    private JLabel rows;
    private TableModel<Product> model;
    private boolean sort = false;


    public TableForm() throws HeadlessException {
        super(800, 600);
        setContentPane(mainPanel);
        initTable();
        initFilters();
        initButtons();
        setVisible(true);
    }

    private void initTable(){
        table.getTableHeader().setReorderingAllowed(false);
        table.setRowHeight(50);
        model = new TableModel<>(Product.class) {
            @Override
            public void onUpdate() {
                pages.setText(model.getCurrentPage() + "/" + model.getPages());
                rows.setText(model.getFilteredRowsOnPage().size() + "/" + model.getAllRows().size());
            }
        };
        try {
            model.setAllRows(ProductManager.selectAll());
            table.setModel(model);
        } catch (SQLException e) {
            e.printStackTrace();
        }


        model.getFilters()[0] = new Predicate<Product>() {
            @Override
            public boolean test(Product product) {
                if (searchField.getText().isEmpty() || searchField.getText().length() == 0) return true;
                return product.getTitle().toLowerCase().startsWith(searchField.getText().toLowerCase());
            }
        };

        model.getFilters()[1] = new Predicate<Product>() {
            @Override
            public boolean test(Product product) {
                if (comboBox1.getSelectedIndex() == 0) return true;
                return product.getProductType().equals(comboBox1.getSelectedItem());
            }
        };

        model.getFilters()[2] = new Predicate<Product>() {
            @Override
            public boolean test(Product product) {
                if (comboBox2.getSelectedIndex() == 0) return  true;
                return (product.getMinCostForAgent() > Integer.parseInt(comboBox2.getSelectedItem().toString().split("-")[0]))
                        && (product.getMinCostForAgent() < Integer.parseInt(comboBox2.getSelectedItem().toString().split("-")[1]));
            }
        };
    }

    private void initFilters(){
        comboBox1.addItem("все");
        comboBox2.addItem("все");
        Set<String> types = new HashSet<>();
        int maxcost = 0;
        for (Product product : model.getAllRows()) {
            types.add(product.getProductType());
            if (maxcost < product.getMinCostForAgent()) {
                maxcost = (int) Math.ceil(product.getMinCostForAgent());
            }
        }
        maxcost = (int) (Math.ceil((float) maxcost / 10000) * 10000);
        for (String type : types) {
            comboBox1.addItem(type);
        }
        for (int i = 0; i<maxcost; i+=10000) {
            comboBox2.addItem(i + "-" + (i+10000));
        }

        comboBox1.addItemListener(e -> {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                model.updateRows();
            }
        });
        comboBox2.addItemListener(e -> {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                model.updateRows();
            }
        });
        comboBox3.addItemListener(e -> {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                if (comboBox3.getSelectedIndex() == 0) {
                    model.setRowsOnPage(0);

                } else {
                    model.setRowsOnPage(Integer.parseInt(comboBox3.getSelectedItem().toString()));
                }
                initPages();
            }
        });

        searchField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                model.updateRows();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                model.updateRows();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                model.updateRows();
            }
        });
    }

    private void initButtons(){
        addButton.addActionListener(e -> {
            dispose();
            new ProductForm(ProductForm.INSERT);
        });
        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    dispose();
                    System.out.println(table.rowAtPoint(e.getPoint()));
                    System.out.println(model.getFilteredRowsOnPage().get(table.rowAtPoint(e.getPoint())).getId());
                    new ProductForm(model.getFilteredRowsOnPage().get(table.rowAtPoint(e.getPoint())).getId());
                }
            }
        });
        clearButton.addActionListener(e -> {
            comboBox1.setSelectedIndex(0);
            comboBox2.setSelectedIndex(0);
            comboBox3.setSelectedIndex(0);
            searchField.setText("");
            model.setSorter(new Comparator<Product>() {
                @Override
                public int compare(Product o1, Product o2) {
                    if (o1.getId() < o1.getId()) {
                        return -1;
                    } else {
                        return 1;
                    }
                }
            });
        });
        nextButton.addActionListener(e ->  {
            model.setCurrentPage(model.getCurrentPage()+1);
            initPages();
        });
        backButton.addActionListener(e ->  {
            model.setCurrentPage(model.getCurrentPage()-1);
            initPages();
        });
        sortButton.addActionListener(e -> {
            model.setSorter(new Comparator<Product>() {
                @Override
                public int compare(Product o1, Product o2) {
                    if (o1.getMinCostForAgent() < o2.getMinCostForAgent()) {
                        return (sort? -1 : 1);
                    } else {
                        return (sort? 1 : -1);
                    }
                }
            });
            sort= !sort;
        });
    }

    private void initPages(){
        if (model.getCurrentPage() <= 1) {
            backButton.setEnabled(false);
        } else backButton.setEnabled(true);
        if (model.getCurrentPage() == model.getPages()) {
            nextButton.setEnabled(false);
        } else nextButton.setEnabled(true);
    }
}
