package org.heatup.controllers;

import org.heatup.api.UI.AppManager;
import org.heatup.api.controllers.Controller;

import java.net.URL;
import java.net.URLConnection;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.TimeUnit;

/**
 * Created by romain on 16/05/2015.
 */
public class DownloadController implements Controller{
    private final AppManager manager;
    private final ReleaseController releases;
    private Future<?> future;

    public DownloadController(AppManager manager, ReleaseController releases) {
        this.manager = manager;
        this.releases = releases;
    }

    @Override
    public void start() {
        this.future = manager.getWorker().submit(new Runnable() {
            @Override
            public void run() {
                LinkedBlockingDeque<URL> files = releases.getFiles();

                while(true) {
                    try {
                        URL url = files.poll(1, TimeUnit.SECONDS);

                        if(url == null && files.isEmpty()) {
                            manager.getForm().updateFinished();
                            break;
                        }

                        if(url == null) continue;

                        URLConnection connection = url.openConnection();


                    } catch(Exception e) {
                        System.out.println(e.getMessage());
                    }
                }
            }
        });
    }

    @Override
    public void end() {
        if(future != null && !future.isCancelled())
            future.cancel(true);
    }
}
