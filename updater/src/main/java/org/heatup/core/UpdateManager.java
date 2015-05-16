package org.heatup.core;

import org.heatup.api.controllers.Controller;
import org.heatup.api.controllers.ControllerManager;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Created by romain on 16/05/2015.
 */
public class UpdateManager implements ControllerManager {
    private final List<Controller> controllers;

    public UpdateManager(Controller... controllers) {
        this.controllers = Collections.unmodifiableList(Arrays.asList(controllers));
    }

    @Override
    public void start() {
        for(Controller controller: controllers)
            controller.start();
    }

    @Override
    public void end() {
        for(Controller controller: controllers)
            controller.end();
    }
}
