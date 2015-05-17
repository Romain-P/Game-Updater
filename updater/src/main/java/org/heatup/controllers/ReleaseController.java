package org.heatup.controllers;

import lombok.Getter;
import org.heatup.api.UI.AppManager;
import org.heatup.api.controllers.Controller;
import org.heatup.core.Main;
import org.heatup.utils.AppUtils;
import org.heatup.utils.FileUtils;

import java.net.URL;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingDeque;

/**
 * Created by romain on 17/05/2015.
 */
public class ReleaseController implements Controller {
    private final AppManager manager;
    @Getter private final LinkedBlockingDeque<URL> files;
    private Future<?> future;

    public ReleaseController(AppManager manager) {
        this.manager = manager;
        this.files = new LinkedBlockingDeque<>();
    }

    @Override
    public void start() {
        this.future = manager.getWorker().submit(new Runnable() {
            @Override
            public void run() {
                int serverRelease = FileUtils.getReleases(
                        FileUtils.path(Main.SERVER, "releases", "releases.dat")).lastRelease(AppUtils.OS);
                int release = FileUtils.getLocalRelease("release.int");

                int result = serverRelease - release;

                if(result == 0 || result < 0) {
                    manager.getForm().alreadyUpdated();
                    return;
                }

                for(int i=release+1;i<serverRelease;i++) {
                    try {
                        files.addLast(new URL(
                                FileUtils.path(Main.SERVER, "releases", AppUtils.OS.toString(), i + ".zip")));
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
