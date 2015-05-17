package org.heatup.api.controllers;

import java.util.concurrent.ExecutorService;

/**
 * Created by romain on 16/05/2015.
 */
public interface ControllerManager {
    void start(Controller... controllers);
    void end(boolean dispose);
    ExecutorService getWorker();
}
