package org.heatup.core;

import lombok.Getter;
import org.heatup.api.UI.AppManager;
import org.heatup.api.UI.UserInterface;
import org.heatup.api.controllers.Controller;
import org.heatup.view.Form;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by romain on 16/05/2015.
 */
public class UpdateManager extends AppManager {
    private final List<Controller> controllers;
    @Getter private final ExecutorService worker;
    @Getter private final UserInterface form;
    private final ProcessManager processManager;
    private boolean isEnd;

    public static final String SERVER = "http://tpe-audition.net";
    public static final String RELEASE = SERVER+"/releases/releases.dat";

    public UpdateManager(ProcessManager manager) {
        this.processManager = manager;
        this.controllers = new ArrayList<>();
        this.form = new Form(this);
        this.worker = Executors.newCachedThreadPool();
    }

    @Override
    public void start(Controller... controllers) {
        form.initialize();

        this.controllers.addAll(Arrays.asList(controllers));

        for(Controller controller: controllers)
            controller.start();

        processManager.hide();
        form.setVisible(false);
    }

    @Override
    public void end(boolean dispose) {
        if(isEnd) return;
        processManager.stop();

        for(Controller controller: controllers)
            controller.end();

        worker.shutdown();

        if(dispose) form.dispose();
        isEnd = true;
    }
}
