package org.heatup.api.utils;

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
}
