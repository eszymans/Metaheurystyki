package org.example;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Ant {
    private int numCities;
    private int[] tour;
    private boolean[] visited;
    private double tourLength;
    private Random rand = new Random();

    public Ant(int numCities) {
        this.numCities = numCities;
        this.tour = new int[numCities];
        this.visited = new boolean[numCities];
    }

    public void visitCities(double[][] distMatrix, double[][] pheromones, double alpha,
                            double beta, double pRandom) {
        for (int i = 0; i < numCities; i++) {
            visited[i] = false;
        }
        tourLength = 0;
        int currentCity = rand.nextInt(numCities);
        tour[0] = currentCity;
        visited[currentCity] = true;

        for(int i = 1; i < numCities; i++) {
            int nextCity = selectNextCity(currentCity, distMatrix, pheromones, alpha, beta, pRandom);
            tour[i] = nextCity;
            tourLength += distMatrix[currentCity][nextCity];
            visited[nextCity] = true;
            currentCity = nextCity;
        }

        tourLength += distMatrix[tour[numCities - 1]][tour[0]];
    }

    private int selectNextCity(int currentCity, double[][] dists, double[][] pheromones,
                               double alpha, double beta, double pRandom) {
        List<Integer> possible = new ArrayList<>();
        for (int i = 0; i < numCities; i++) {
            if (!visited[i]) {
                possible.add(i);
            }
        }

        if(rand.nextDouble() < pRandom) {
            return possible.get(rand.nextInt(possible.size()));
        }

        double[] probs = new double[possible.size()];
        double sumProbs = 0.0;
        for (int i = 0; i < possible.size(); i++) {
            int cityIdx = possible.get(i);
            double tau = pheromones[currentCity][cityIdx];
            double eta = 1.0 / dists[currentCity][cityIdx];

            double p = Math.pow(tau, alpha) * Math.pow(eta, beta);
            probs[i] = p;
            sumProbs += p;
        }

        double r = rand.nextDouble() * sumProbs;
        double currentSum = 0.0;
        for(int i = 0; i < possible.size(); i++) {
            currentSum += probs[i];
            if(currentSum >= r) {
                return possible.get(i);
            }
        }
        return possible.get(possible.size() - 1);
    }

    public int[] getTour() {
        return tour;
    }

    public double getTourLength() {
        return tourLength;
    }
}
