package org.heatup.utils;

import org.heatup.core.Main;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
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

    public static String getReadableSize(long bytes) {
        if(bytes <= 0) return "0";

        final String[] units = new String[] { "B/s", "KiB/s", "MiB/s", "GiB/s", "TiB/s" };
        int digitGroups = (int) (Math.log10(bytes)/Math.log10(1024));

        return new DecimalFormat("#,##0.#").format(bytes/Math.pow(1024, digitGroups)) + " " + units[digitGroups];
    }

    public static String getTimeAsString(long milliseconds) {
        double days, hours, minutes, seconds;

        final double msPerSecond = 1000;
        final double msPerMinute = 60 * msPerSecond;
        final double msPerHour = 60 * msPerMinute;
        final double msPerDay =  24 * msPerHour;

        days = milliseconds / msPerDay;
        milliseconds %= msPerDay;
        hours = milliseconds / msPerHour;
        milliseconds %= msPerHour;
        minutes = milliseconds / msPerMinute;
        milliseconds %= msPerMinute;
        seconds = milliseconds / msPerSecond;

        String asString;
        if (days >= 1)
            asString = ((int) days + 1) + " days";
        else if (hours >= 1)
            asString = ((int) hours + 1) + " hours";
        else if(minutes >= 1)
            asString = ((int) minutes + 1) + " minutes";
        else
            asString = ((int) seconds + 1) + " seconds";

        return asString;
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
