package org.heatup.core;

import lombok.Setter;
import lombok.SneakyThrows;
import org.heatup.api.UI.AppManager;
import org.heatup.api.serialized.SerializedObject;
import org.heatup.api.serialized.implementations.SerializedObjectImpl;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * Created by Romain on 27/07/2015.
 */
public class ProcessManager implements Runnable {
    private final SerializedObject<Request> request;
    private final Thread thread;
    @Setter private AppManager manager;
    private boolean stopped;

    public ProcessManager() {
        this.thread = new Thread(this, "checking-task");
        this.thread.setDaemon(true);
        this.request = SerializedObjectImpl.create("updater.req", false, Request.SHOW);
    }

    public void start() {
        File file = new File("created");
        File exit = new File("exit");

        if(!exit.exists()) {
            if(file.exists()) file.delete();
            request.setObject(Request.SHOW);
        }

        if(!file.exists()) {
            try {
                file.createNewFile();
                request.write();
                Files.deleteIfExists(Paths.get("exit"));
            } catch (Exception e) {}

            Main.start(this);
        }
        thread.start();
    }

    @Override
    public void run() {
        while(!stopped) {
            switch(request.get())
            {
                case SHOW:
                    if(manager != null)
                        manager.getForm().setVisible(true);
                    break;
                case HIDE:
                    if(manager != null) {
                        request.set("updater.req", false, Request.HIDE);
                        break;
                    }

                    request.setObject(Request.SHOW).write();
                    stop();
                    break;
            }

            try {
                Thread.sleep(1000);
            } catch(Exception e) {}
        }
    }

    @SneakyThrows
    public void stop() {
        this.stopped = true;

        if(manager != null) {
            Files.deleteIfExists(Paths.get("created"));
            Files.deleteIfExists(Paths.get("updater.req"));
            new File("exit").createNewFile();
        }
    }

    public void hide() {
        request.setObject(Request.HIDE).write();
    }

    private enum Request {
        SHOW,
        HIDE
    }
}
