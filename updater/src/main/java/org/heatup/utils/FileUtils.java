package org.heatup.utils;

import org.heatup.api.serialized.SerializedReleases;
import org.heatup.core.Main;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.net.URL;
import java.text.DecimalFormat;

/**
 * Created by romain on 08/05/2015.
 */
public class FileUtils {
    public static String path(String... files) {
        String separator = File.separator;
        StringBuilder builder = new StringBuilder();

        for(String file: files)
            builder.append(separator).append(file);

        return builder.toString().substring(1);
    }

    public static Integer getLocalRelease(String localPath) {
        try {
            return (Integer) getObject(new FileInputStream(localPath));
        } catch(Exception e) {
            return 0;
        }
    }

    public static SerializedReleases getReleases(String url) {
        try {
            return (SerializedReleases) getObject(new URL(url).openStream());
        } catch(Exception e) {
            return null;
        }
    }

    private static Object getObject(InputStream stream) {
            ObjectInputStream object = null;

            try {
                object = new ObjectInputStream(stream);
                return object.readObject();
            } catch (Exception e) {
                return null;
            } finally {
                try {
                    if(object != null) object.close();
                } catch (IOException e) {}
            }
    }

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
