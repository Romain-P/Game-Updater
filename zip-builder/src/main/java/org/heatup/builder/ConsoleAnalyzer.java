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

    public void start() {
        String command;
        System.out.println("Write the 'build' command to analyze & rebuild releases");

        while((command = console.next()) != null) {
            try {
                if(command.equalsIgnoreCase("build")) {
                    builder.build();
                    break;
                }
            } catch(Exception e){}
        }
    }
}
