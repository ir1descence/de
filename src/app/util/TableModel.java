package app.util;

import app.annotation.ColumnType;
import lombok.Getter;

import javax.swing.table.AbstractTableModel;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.function.Predicate;


@Getter
public class TableModel<T> extends AbstractTableModel {
    private Class<T> cls;
    private List<T> allRows;
    private List<T> filteredRows;
    private List<T> filteredRowsOnPage;
    private Predicate<T>[] filters = new Predicate[5];
    private Comparator<T> sorter;
    private int currentPage = 1;
    private int pages  = 1;
    private int rowsOnPage = 0;

    public TableModel(Class<T> cls) {
        this.cls = cls;
    }

    public void updateRows() {
        currentPage = 1;
        pages = 1;
        rowsOnPage = 0;
        filteredRows = new ArrayList<>(allRows);
        for (Predicate<T> filter : filters) {
            if (filter != null) {
                filteredRows.removeIf(e -> !filter.test(e));
            }
        }
        if (sorter != null) {
            Collections.sort(filteredRows, sorter);
        }
        filteredRowsOnPage = new ArrayList<>(filteredRows);
        fireTableDataChanged();
        onUpdate();
    }
    public void updatePage() {
        if (rowsOnPage != 0) {
            pages = (int) Math.ceil((float) filteredRows.size() / rowsOnPage);
            int maxIndex = (currentPage - 1) * rowsOnPage + rowsOnPage;
            System.out.println(currentPage);
            filteredRowsOnPage = filteredRows.subList((currentPage - 1) * rowsOnPage, Math.min(maxIndex, filteredRows.size()));
            fireTableDataChanged();
            onUpdate();
        }
    }

    public void onUpdate() {}


    @Override
    public int getRowCount() {
        return filteredRowsOnPage.size();
    }

    @Override
    public int getColumnCount() {
        return cls.getDeclaredFields().length;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        Field field = cls.getDeclaredFields()[columnIndex];
        field.setAccessible(true);
        try {
            return field.get(filteredRowsOnPage.get(rowIndex));
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return "ERROR";
    }

    @Override
    public String getColumnName(int column) {
        return cls.getDeclaredFields()[column].getAnnotation(ColumnType.class).value();
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        return cls.getDeclaredFields()[columnIndex].getType();
    }

    public void setAllRows(List<T> allRows) {
        this.allRows = allRows;
        updateRows();
    }

    public void setSorter(Comparator<T> sorter) {
        this.sorter = sorter;
        updateRows();
    }

    public void setCurrentPage(int currentPage) {
        this.currentPage = currentPage;
        updatePage();
    }

    public void setRowsOnPage(int rowsOnPage) {
        this.rowsOnPage = rowsOnPage;
        currentPage = 1;
        updatePage();
    }
}
