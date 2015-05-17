package org.heatup.view;

import lombok.Getter;
import org.heatup.core.UpdateManager;
import org.heatup.utils.FileUtils;

import javax.swing.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class FormContent extends JPanel{
    private final UpdateManager manager;
    @Getter private JButton closeButton;
    @Getter private JButton playButton;
    @Getter private final JLabel firstLine, secondLine;

    public FormContent(UpdateManager manager) {
        this.manager = manager;
        this.firstLine = new JLabel();
        this.secondLine = new JLabel();
    }

    public FormContent initialize() {
        setLayout(null);
        add((closeButton = FileUtils.createPressedAndOverButton("close-normal.png", "close-hover.png", 923, 185)));
        add((playButton = FileUtils.createPressedAndOverButton("play-normal.png", "play-hover.png", 298, 379)));

        firstLine.setBounds(169, 591, 500, 13);
        secondLine.setBounds(169, 605, 500, 13);
        add(firstLine);
        add(secondLine);

        playButton.setDisabledIcon(new ImageIcon(FileUtils.getImage("play-inactive.png")));
        playButton.setEnabled(false);

        deployListeners();
        return this;
    }

    private void deployListeners() {
        closeButton.addMouseListener(new MouseAdapter() {
            public void mouseReleased(MouseEvent event) {
                manager.end(true);
            }
        });
    }
}
