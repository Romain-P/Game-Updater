package org.heatup.builder;

import lombok.RequiredArgsConstructor;

import java.util.Scanner;

/**
 * Created by romain on 07/05/2015.
 */
@RequiredArgsConstructor
public class ConsoleAnalyzer {
    private final Builder builder;
    private final Scanner console;

    Runnable runnable = new Runnable() {
        public void run() {
            String command;

            System.out.println("Write the 'build' command to analyze & rebuild releases");

            while((command = console.next()) != null) {
                try {
                    if(command.equalsIgnoreCase("build"))
                        builder.build();
                } catch(Exception e){}
            }
        }
    };

    public void start() {
        Thread thread = new Thread(runnable);
        thread.setDaemon(true);
        thread.start();
    }
}
