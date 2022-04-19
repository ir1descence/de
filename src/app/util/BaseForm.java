package app.util;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.IOException;

public class BaseForm extends JFrame {
    public BaseForm(int w, int h) {
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(w, h));
        setLocation(
                Toolkit.getDefaultToolkit().getScreenSize().width / 2 - w / 2,
                Toolkit.getDefaultToolkit().getScreenSize().height / 2 - h / 2
        );
        try {
            setIconImage(ImageIO.read(BaseForm.class.getClassLoader().getResource("icon.png")));
        } catch (Exception e) {

        }

    }
}
