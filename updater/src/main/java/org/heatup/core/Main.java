package org.heatup.core;

import jwrapper.jwutils.JWService;
import jwrapper.jwutils.JWSockIPC;
import org.heatup.api.UI.AppManager;
import org.heatup.controllers.DownloadController;
import org.heatup.controllers.ReleaseController;
import org.heatup.utils.AppUtils;

import java.awt.*;

/**
 * Created by romain on 16/05/2015.
 */
public class Main {
    public static void main(String[] args) {
        String arg = args.length > 0 ? "" : args[0];

        AppUtils.deployingSystemLook();
        AppUtils.createShortcuts();

        final AppManager manager = new UpdateManager();

        if(!arg.equals("network")) {
            ProcessNetwork network = new ProcessNetwork(manager);

            if (!network.isAlreadyLaunched())
                network.start();

            return;
        }

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
