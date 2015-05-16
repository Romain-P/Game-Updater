package org.heatup.view;

import org.heatup.core.UpdateManager;
import org.heatup.utils.FileUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

public class Form extends JFrame {
    private final UpdateManager manager;
    private final FormContent content;
    private Point mousePointMover;

    public Form(UpdateManager manager) {
        this.manager = manager;
        this.content = new FormContent(manager, this);
    }

    public Form initialize() {
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
        return this;
    }

    public void updateFileProgress(String fileName, int percentage) {
        content.getFirstLine().setText("Downloading " + fileName + " (" + percentage + "%)");
    }

    public void updateTotalPercentage(int percentage, String remainingTime, String speed) {
        content.getSecondLine().setText(remainingTime + " remaining (" + percentage + "% at " + speed + ")");
    }

    public void updateFinished() {
        content.getFirstLine().setText("");
        String tosend = true//todo
                ? "Your client is already up-to-date!" : "Download finished successfully!";
        content.getSecondLine().setText(tosend);
        content.getPlayButton().setEnabled(true);
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
