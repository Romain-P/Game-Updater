package org.heatup.builder;

import java.util.Scanner;

/**
 * Created by romain on 07/05/2015.
 */
public class Main {
    public static void main(String[] args) {
        new ConsoleAnalyzer(new Builder(), new Scanner(System.in));
    }
}
