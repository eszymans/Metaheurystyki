package org.example;

import javax.imageio.IIOException;
import javax.swing.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Main {
    public static void main(String[] args) {

        String filePath = "data/input/A-n80-k10.txt";
        int ants = 20;
        int iterations = 500;
        double alpha = 1.0;
        double beta = 5.0;
        double rho = 0.5;
        double pRandom = 0.01;

        try{
            System.out.println("Reading file: " + filePath);
            List<Point> points = DataLoader.loadData(filePath);

            System.out.println("Starting ACO algorithm...");
            long startTime = System.currentTimeMillis();

            ACO aco = new ACO(points, ants, iterations, alpha, beta, rho, pRandom);
            aco.solve();

            long endTime = System.currentTimeMillis();

            System.out.println("--- WYNIKI ---");
            System.out.println("Czas obliczeń: " + (endTime - startTime) + " ms");
            System.out.printf("Najkrótsza trasa: %.2f\n", aco.getBestTourLength());
            System.out.println("Kolejność: " + Arrays.toString(aco.getBestTourOrder()));

            SwingUtilities.invokeLater(() -> {
               new RoutePlotter(points, aco.getBestTourOrder(), aco.getBestTourLength());
            });

        } catch (IOException e){
            e.printStackTrace();
        }

    }
}
