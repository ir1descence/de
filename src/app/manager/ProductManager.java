package app.manager;

import app.App;
import app.entity.Product;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ProductManager {
    private static Product createProduct(ResultSet resultSet) throws SQLException {
        return new Product(
                resultSet.getInt(1),
                resultSet.getString(2),
                resultSet.getString(3),
                resultSet.getString(4),
                resultSet.getString(5),
                resultSet.getString(6),
                resultSet.getInt(7),
                resultSet.getInt(8),
                resultSet.getFloat(9)
        );
    }

    private static PreparedStatement fillPrepareStatement(List<String> list, PreparedStatement preparedStatement) throws Exception {
        preparedStatement.setString(1, list.get(0));
        preparedStatement.setString(2, list.get(1));
        preparedStatement.setString(3, list.get(2));
        preparedStatement.setString(4, list.get(3));
        preparedStatement.setString(5, list.get(4));
        preparedStatement.setInt(6, Integer.parseInt(list.get(5)));
        preparedStatement.setInt(7, Integer.parseInt(list.get(6)));
        preparedStatement.setFloat(8, Float.parseFloat(list.get(7)));
        return preparedStatement;
    }


    public static List<Product> selectAll() throws SQLException {
        try(Connection connection = App.getConnection()){
            ResultSet resultSet = connection.createStatement().executeQuery("SELECT * FROM product");
            List<Product> products = new ArrayList<>();
            while (resultSet.next()) {
                products.add(createProduct(resultSet));
            }
            return products;
        }
    }
    public static List<String> selectById(int id) throws SQLException {
        try(Connection connection = App.getConnection()){
            ResultSet resultSet = connection.createStatement().executeQuery("SELECT * FROM product WHERE ID = " + id);
            List<String> list = new ArrayList<>();
            if (resultSet.next()) {
                list.add(String.valueOf(resultSet.getInt(1)));
                list.add(resultSet.getString(2));
                list.add(resultSet.getString(3));
                list.add(resultSet.getString(4));
                list.add(resultSet.getString(5));
                list.add(resultSet.getString(6));
                list.add(String.valueOf(resultSet.getInt(7)));
                list.add(String.valueOf(resultSet.getInt(8)));
                list.add(String.valueOf(resultSet.getFloat(9)));
            } return list;
        }
    }

    public static void insert(List<String> list) throws Exception {
        try(Connection connection = App.getConnection()){
            PreparedStatement preparedStatement = connection.
                    prepareStatement("INSERT INTO product VALUES (default, ?,?,?,?,?,?,?,?)");
            fillPrepareStatement(list, preparedStatement).executeUpdate();
        }
    }
    public static void update(List<String> list) throws Exception {
        try(Connection connection = App.getConnection()){
            PreparedStatement preparedStatement = connection.
                    prepareStatement("UPDATE product SET Title=?, ProductType=?, ArticleNumber=?, Description=?, Image=?, ProductionPersonCount=?, ProductionWorkshopNumber=?, MinCostForAgent=? WHERE ID=?");
            preparedStatement = fillPrepareStatement(list.subList(1, list.size()), preparedStatement);
            preparedStatement.setInt(9, Integer.parseInt(list.get(0)));
            preparedStatement.executeUpdate();
        }
    }
    public static void delete(int id) throws SQLException {
        try(Connection connection = App.getConnection()){
            connection.createStatement().executeUpdate("DELETE FROM product WHERE ID =" + id);
        }
    }
}
