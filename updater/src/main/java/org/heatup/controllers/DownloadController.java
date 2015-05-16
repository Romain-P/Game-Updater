package org.heatup.controllers;

import org.heatup.api.controllers.Controller;
import org.heatup.core.UpdateManager;

/**
 * Created by romain on 16/05/2015.
 */
public class DownloadController implements Controller{
    private final UpdateManager manager;

    public DownloadController(UpdateManager manager) {
        this.manager = manager;
    }

    @Override
    public void start() {

    }

    @Override
    public void end() {

    }
}
