package org.heatup.utils;

import org.heatup.api.utils.OsCheck;
import org.heatup.core.Main;

import javax.swing.*;
import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.util.prefs.Preferences;

/**
 * Created by romain on 16/05/2015.
 */
public class AppUtils {

    public static void deployingSystemLook() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
        }
    }

    public static boolean requestRights() {
        if(!hasRights()) {
            OsCheck.OSType type = OsCheck.getOperatingSystemType();

            switch (type) {
                case WINDOWS: {
                    try {
                        File file = File.createTempFile("Elevate", ".dat");

                        FileChannel channel = new FileOutputStream(file).getChannel();
                        channel.transferFrom(Channels.newChannel(Main.class.getClassLoader().getResourceAsStream("Elevator")), 0, 199552);
                        channel.close();

                        Runtime.getRuntime().exec(String.format("%s javaw -jar %s",
                                file.getPath(),
                                Main.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath().substring(1)));
                    } catch (Exception e) {}
                    break;
                }

                case MAC: {
                    try {
                        File executor = File.createTempFile("Executor",".sh");
                        PrintWriter writer = new PrintWriter(executor, "UTF-8");

                        writer.println("#!/bin/bash");
                        writer.println();
                        writer.println("java $*");
                        writer.close();
                        executor.setExecutable(true);

                        File elevator = File.createTempFile("Elevator",".sh");
                        writer = new PrintWriter(elevator, "UTF-8");
                        writer.println("#!/bin/bash");
                        writer.println();
                        writer.println(String.format("osascript -e \"do shell script \\\"%s $*\\\" with administrator privileges\"",
                                executor.getPath()));
                        writer.close();
                        elevator.setExecutable(true);

                        Runtime.getRuntime().exec(String.format("%s -cp %s Main param",
                                elevator.getPath(),
                                Main.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath()));
                    } catch (Exception e) {}
                    break;
                }

                case LINUX: {
                    try {
                        Runtime.getRuntime().exec(String.format("gksudo java -jar %s",
                                Main.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath().substring(1)));
                    } catch (Exception e) {}
                    break;
                }

                case ALL: return false;
            }
            return true;
        }
        return false;
    }

    private static boolean hasRights() {
        try{
            Preferences prefs = Preferences.systemRoot();
            prefs.put("foo", "bar");
            prefs.remove("foo");
            prefs.flush();
            return true;
        }catch(Exception e){
            return false;
        }
    }
}
