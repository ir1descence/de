package app.ui;

import app.annotation.ColumnType;
import app.annotation.SQLString;
import app.entity.Product;
import app.manager.ProductManager;
import app.util.BaseForm;
import app.util.DialogUtil;
import app.util.TableModel;
import lombok.SneakyThrows;

import javax.swing.*;
import java.awt.*;
import java.lang.reflect.Field;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ProductForm extends BaseForm {
    private JPanel mainPanel;
    private JButton addButton;
    private JTable table;
    private JPanel panel;
    private JButton saveButton;
    private JButton deleteButton;
    private JButton cancelButton;
    private JPanel panelTable;
    public static final int INSERT = -1;
    private int id;
    private TableModel model;


    public ProductForm(int id) {
        super(800, 500);
        this.id = id;
        setContentPane(mainPanel);
        panel.setLayout(new GridLayout(Product.class.getDeclaredFields().length, 2, 3, 3));
        initFields();
        initButton();
        setVisible(true);
    }


    private void initFields() {
        Field[] fields = Product.class.getDeclaredFields();
        for (int i = (id ==  INSERT? 1 : 0); i < fields.length; i++) {
            panel.add(new JLabel(fields[i].getAnnotation(ColumnType.class).value()), BorderLayout.CENTER);
            panel.add(new JTextField(""), BorderLayout.CENTER);
        }
        if (id != INSERT) {
            panel.getComponent(1).setEnabled(false);
            fillFields(fields);

        } else {
            deleteButton.setVisible(false);
            panelTable.setVisible(false);
        }
    }

    private void fillFields(Field[] fields) {
        try {
            List<String> product = ProductManager.selectById(id);
            for (int i = 0; i < fields.length; i++) {
                fields[i].setAccessible(true);
                ((JTextField) panel.getComponent(i*2+1)).setText(product.get(i));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initButton() {
        saveButton.addActionListener(e -> {
            checkData();
        });
        deleteButton.addActionListener(e -> {
           if (JOptionPane.showConfirmDialog(this, "?", "Удаление", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
               try {
                   ProductManager.delete(id);
               } catch (SQLException ex) {
                   ex.printStackTrace();
               }
           }
        });
        cancelButton.addActionListener(e -> {
            dispose();
            new TableForm();
        });
    }

    private void checkData() {
        List<String> list = new ArrayList<>();
        for (Component component : panel.getComponents()) {
            if (component.getClass().equals(JTextField.class)) {
                if (Product.class.getDeclaredFields()[list.size()-1].getAnnotation(SQLString.class) != null) {
                    int len = Product.class.getDeclaredFields()[list.size()-1].getAnnotation(SQLString.class).value();
                    if (list.get(list.size() - 1).length() < 1 && list.get(list.size() - 1).length() > len) {
                        DialogUtil.showError(this, "Поле " +
                                Product.class.getDeclaredFields()[list.size()-1].getAnnotation(ColumnType.class).value()
                                + " введено неккоректно");
                        return;
                    }
                }
                list.add(((JTextField) component).getText());
            }
        }
            try {
                if (id == INSERT) {
                    ProductManager.insert(list);
                } else {
                    ProductManager.update(list);
                }
                DialogUtil.showInfo(this, "Данные успешно сохранены");
            } catch (Exception e) {
                DialogUtil.showError(this, "Ошибка");
                e.printStackTrace();
            }
    }
}
