package org.heater.api.utils;

import java.util.Locale;

public final class OsCheck {
    public enum OSType {
        Windows("windows"), MacOS("mac"), Linux("linux"), Other("other"), All("all");

        private String name;
        private OSType(String name) {
            this.name = name;
        }

        public final String toString() {
            return name;
        }
    }

    protected static OSType detectedOS;

    public static OSType getOperatingSystemType() {
        if (detectedOS == null) {
            String os = System.getProperty("os.name", "generic").toLowerCase(Locale.ENGLISH);
            if ((os.indexOf("mac") >= 0) || (os.indexOf("darwin") >= 0)) {
                detectedOS = OSType.MacOS;
            } else if (os.indexOf("win") >= 0) {
                detectedOS = OSType.Windows;
            } else if (os.indexOf("nux") >= 0) {
                detectedOS = OSType.Linux;
            } else {
                detectedOS = OSType.Other;
            }
        }
        return detectedOS;
    }
}