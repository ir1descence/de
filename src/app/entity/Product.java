package app.entity;

import app.annotation.ColumnType;
import lombok.Data;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.util.regex.Pattern;

@Data
public class Product {
    @ColumnType("Идентификатор")
    private int id;
    @ColumnType("Наименование продукции")
    private String title;
    @ColumnType("Тип продукции")
    private String productType;
    @ColumnType("Артикул")
    private String articleNumber;
    @ColumnType("Описание")
    private String description;
    @ColumnType("Изображение")
    private ImageIcon image;
    @ColumnType("Количество человек для производства")
    private int productionPersonCount;
    @ColumnType("Номер цеха для производства")
    private int productionWorkshopNumber;
    @ColumnType("Минимальная стоимость для агента")
    private float minCostForAgent;

    public Product(int id, String title, String productType, String articleNumber, String description, String image, int productionPersonCount, int productionWorkshopNumber, float minCostForAgent) {
        this.id = id;
        this.title = title;
        this.productType = productType;
        this.articleNumber = articleNumber;
        this.description = description;
        this.productionPersonCount = productionPersonCount;
        this.productionWorkshopNumber = productionWorkshopNumber;
        this.minCostForAgent = minCostForAgent;
        //image = image.replaceAll(Pattern.quote("\\"), "/");
        //System.out.println(image);
        try {

            this.image = new ImageIcon(ImageIO.read
                            (Product.class.getClassLoader().getResource(image))
                    .getScaledInstance(50, 50, Image.SCALE_DEFAULT));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
