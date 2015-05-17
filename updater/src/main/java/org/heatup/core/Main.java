package org.heatup.core;

import org.heatup.api.UI.AppManager;
import org.heatup.controllers.DownloadController;
import org.heatup.controllers.ReleaseController;
import org.heatup.utils.AppUtils;

import java.awt.*;

/**
 * Created by romain on 16/05/2015.
 */
public class Main {
    public static final String SERVER = "";

    public static void main(String[] args) {
        AppUtils.deployingSystemLook();

        //if(AppUtils.requestRights()) return;

        final AppManager manager = new UpdateManager();

        EventQueue.invokeLater(new Runnable() {
            public void run() {
                ReleaseController releases;

                manager.start(
                        (releases = new ReleaseController(manager)),
                        new DownloadController(manager, releases)
                );
            }
        });

        Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
            public void run() {
                manager.end(true);
            }
        }));
    }
}
