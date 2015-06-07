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
    private boolean isEnd;

    public UpdateManager() {
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
    }

    @Override
    public void end(boolean dispose) {
        if(isEnd) return;

        for(Controller controller: controllers)
            controller.end();

        worker.shutdown();

        if(dispose) form.dispose();
        isEnd = true;
    }
}
