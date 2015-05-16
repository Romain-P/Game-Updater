package org.heatup.utils;

import org.heatup.core.Main;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.io.InputStream;
import java.text.DecimalFormat;

/**
 * Created by romain on 08/05/2015.
 */
public class FileUtils {
    public static String getReadableSize(long bytes) {
        if(bytes <= 0) return "0";

        final String[] units = new String[] { "B", "kiB", "MiB", "GiB", "TiB" };
        int digitGroups = (int) (Math.log10(bytes)/Math.log10(1024));

        return new DecimalFormat("#,##0.#").format(bytes/Math.pow(1024, digitGroups)) + " " + units[digitGroups];
    }

    public static Image getImage(String name) {
        InputStream input = Main.class.getClassLoader().getResourceAsStream(name);
        try {
            return ImageIO.read(input);
        } catch (IOException e) { return null; }
    }

    public static JButton createPressedAndOverButton(String normal, String over, int x, int y) {
        ImageIcon defaultButton = new ImageIcon(getImage(normal));
        JButton button = new JButton(defaultButton);
        button.setPressedIcon(defaultButton);
        button.setRolloverIcon(new ImageIcon(getImage(over)));
        button.setFocusPainted(false);
        button.setBounds(x, y, defaultButton.getIconWidth(), defaultButton.getIconHeight());

        button.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));

        return button;
    }
}
