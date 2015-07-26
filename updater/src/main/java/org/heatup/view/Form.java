package org.heatup.view;

import org.heatup.api.UI.UserInterface;
import org.heatup.core.UpdateManager;
import org.heatup.utils.FileUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

public class Form extends JFrame implements UserInterface{
    private final UpdateManager manager;
    private final FormContent content;
    private Point mousePointMover;

    public Form(UpdateManager manager) {
        this.manager = manager;
        this.content = new FormContent(manager);
    }

    public void initialize() {
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setContentPane(content.initialize());
        setUndecorated(true);
        setResizable(false);
        setBackground(new Color(0, 0, 0, 0));

        ImageIcon image = new ImageIcon(FileUtils.getImage("background.png"));
        JLabel background = new JLabel(image);

        background.setBounds(0, 0, image.getIconWidth(), image.getIconHeight());
        add(background);
        setBounds(background.getBounds().x, background.getBounds().y, background.getBounds().width, background.getBounds().height);


        setVisible(true);
        deployMoving();
    }

    public void updateCurrentPercentage(int percentage, int step, int steps) {
        content.getFirstLine().setText(String.format("Downloading package %d/%d (%d %%)", step, steps, percentage));
    }

    public void updateCompressPercentage(int percentage, int step, int steps) {
        content.getFirstLine().setText(String.format("Unzipping package %d/%d (%d %%)", step, steps, percentage));
    }

    public void updateTotalPercentage(int percentage, String remainingTime, String speed) {
        content.getSecondLine().setText(remainingTime + " remaining (" + percentage + "% at " + speed + ")");
    }

    public void alreadyUpdated() {
        content.getFirstLine().setText("");
        content.getSecondLine().setText("Your client is already up-to-date!");
        content.getPlayButton().setEnabled(true);
    }

    public void updateFinished() {
        if(content.getFirstLine().getText().isEmpty())
            return;

        content.getFirstLine().setText("");
        content.getSecondLine().setText("Download finished successfully!");
        content.getPlayButton().setEnabled(true);
    }

    @Override
    public void setVisible(boolean bool) {
        super.setVisible(bool);
    }

    @Override
    public void dispose() {
        super.dispose();
    }

    private void deployMoving() {
        addMouseListener(new MouseListener() {
            public void mouseReleased(MouseEvent e) {
                setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
                mousePointMover = null;
            }

            public void mousePressed(MouseEvent e) {
                setCursor(new Cursor(Cursor.MOVE_CURSOR));
                mousePointMover = e.getPoint();
            }

            public void mouseExited(MouseEvent e) {
            }

            public void mouseEntered(MouseEvent e) {
            }

            public void mouseClicked(MouseEvent e) {
            }
        });

        addMouseMotionListener(new MouseMotionListener() {
            public void mouseDragged(MouseEvent e) {
                if (mousePointMover == null) return;
                Point point = e.getLocationOnScreen();
                setLocation(point.x - mousePointMover.x, point.y - mousePointMover.y);
            }

            public void mouseMoved(MouseEvent e) {
            }
        });
    }
}
