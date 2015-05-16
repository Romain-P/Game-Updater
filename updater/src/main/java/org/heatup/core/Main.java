package org.heatup.core;

import org.heatup.api.controllers.ControllerManager;
import org.heatup.utils.AppUtils;

import java.awt.*;

/**
 * Created by romain on 16/05/2015.
 */
public class Main {
    public static void main(String[] args) {
        AppUtils.requestRights();

        if(AppUtils.requestRights()) return;

        final ControllerManager manager = new UpdateManager();

        EventQueue.invokeLater(new Runnable() {
            public void run() {
                manager.start();
            }
        });

        Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
            public void run() {
                manager.end();
            }
        }));

    }
}
