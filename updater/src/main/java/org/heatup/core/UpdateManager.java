package org.heatup.core;

import org.heatup.api.controllers.Controller;
import org.heatup.api.controllers.ControllerManager;
import org.heatup.view.Form;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Created by romain on 16/05/2015.
 */
public class UpdateManager implements ControllerManager {
    private final List<Controller> controllers;
    private final Form form;
    private boolean isEnd;

    public UpdateManager(Controller... controllers) {
        this.controllers = Collections.unmodifiableList(Arrays.asList(controllers));
        this.form = new Form(this);
    }

    @Override
    public void start() {
        form.initialize();

        for(Controller controller: controllers)
            controller.start();
    }

    @Override
    public void end(boolean dispose) {
        if(isEnd) return;

        for(Controller controller: controllers)
            controller.end();

        if(dispose) form.dispose();
        isEnd = true;
    }
}
