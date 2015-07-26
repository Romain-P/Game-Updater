package org.heatup.api.UI;

/**
 * Created by romain on 17/05/2015.
 */
public interface UserInterface {
    void updateCurrentPercentage(int percent, int step, int steps);
    void updateCompressPercentage(int percent, int step, int steps);
    void updateTotalPercentage(int percent, String remainingTime, String speed);
    void alreadyUpdated();
    void updateFinished();
    void setVisible(boolean bool);

    void initialize();
    void dispose();
}
