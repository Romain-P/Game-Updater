package org.heatup.controllers;

import lombok.Getter;
import org.heatup.api.UI.AppManager;
import org.heatup.api.controllers.Controller;
import org.heatup.api.serialized.SerializedObject;
import org.heatup.api.serialized.SerializedReleases;
import org.heatup.api.serialized.implementations.SerializedObjectImpl;
import org.heatup.core.Main;
import org.heatup.core.UpdateManager;
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
    private final SerializedObject<SerializedReleases> serializedReleases;
    @Getter private final SerializedObject<Integer> serializedRelease;
    @Getter private long updateLength;

    public ReleaseController(AppManager manager) {
        this.manager = manager;
        this.files = new LinkedBlockingDeque<>();
        this.serializedReleases = SerializedObjectImpl.create(UpdateManager.serverHttp, true, null);
        this.serializedRelease = SerializedObjectImpl.create(FileUtils.path("swtour", "release.int"), false, 0);
    }

    /**
     * TODO: checking local files
     */
    @Override
    public void start() {
        this.future = manager.getWorker().submit(new Runnable() {
            @Override
            public void run() {
                int release = serializedRelease.get();
                int serverRelease = serializedReleases.get().lastRelease(AppUtils.OS);
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
                        e.printStackTrace();
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
