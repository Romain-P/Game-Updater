package org.heater.api.utils;

import java.util.Locale;

public final class OsCheck {
    public enum OSType {
        WINDOWS, MAC, LINUX, Other, ALL;

        public String toString() {
            return this.name().toLowerCase();
        }
    }

    protected static OSType detectedOS;

    public static OSType getOperatingSystemType() {
        if (detectedOS == null) {
            String os = System.getProperty("os.name", "generic").toLowerCase(Locale.ENGLISH);
            if ((os.indexOf("mac") >= 0) || (os.indexOf("darwin") >= 0)) {
                detectedOS = OSType.MAC;
            } else if (os.indexOf("win") >= 0) {
                detectedOS = OSType.WINDOWS;
            } else if (os.indexOf("nux") >= 0) {
                detectedOS = OSType.LINUX;
            } else {
                detectedOS = OSType.Other;
            }
        }
        return detectedOS;
    }
}