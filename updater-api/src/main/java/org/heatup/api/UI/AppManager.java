package org.heatup.api.UI;

import org.heatup.api.controllers.ControllerManager;

/**
 * Created by romain on 17/05/2015.
 */
public abstract class AppManager implements ControllerManager {
    public abstract UserInterface getForm();
}
