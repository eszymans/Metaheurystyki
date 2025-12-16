package org.example;

import java.util.ArrayList;
import java.util.List;

public class ACO {
    private List<Point> points;
    private double[][] distMatrix;
    private double[][] pheromones;
    private int numAnts;

    private double alpha, beta, evaporation, pRandom;
    private int iterations;

    private int[] bestTourOrder;
    private double bestTourLength = Double.MAX_VALUE;
    private List<Double> historyBest = new ArrayList<>();

    private List<Double> historyAvg = new ArrayList<>();
    private List<Double> historyWorst = new ArrayList<>();

    private double runAvg = 0.0;
    private double runWorst = Double.MIN_VALUE;

    public ACO(List<Point> points, int numAnts, int interations, double alpha, double beta, double evaporation, double pRandom) {
        this.points = points;
        this.numAnts = numAnts;
        this.iterations = interations;
        this.alpha = alpha;
        this.beta = beta;
        this.evaporation = evaporation;
        this.pRandom = pRandom;

        initMatrices();
    }

    private void initMatrices() {
        int n = points.size();
        distMatrix = new double[n][n];
        pheromones = new double[n][n];

        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                distMatrix[i][j] = points.get(i).distanceTo(points.get(j));
                pheromones[i][j] = 1.0;
            }
        }
    }

    public void solve(){
        int n = points.size();
        Ant[] ants = new Ant[numAnts];
        for (int i = 0; i < numAnts; i++) {
            ants[i] = new Ant(n);
        }

        double sumAllTours = 0.0;
        int totalToursCount = 0;

        for(int iter = 0; iter < iterations; iter++){

            double iterBest = Double.MAX_VALUE;
            double iterWorst = Double.MIN_VALUE;
            double iterSum = 0.0;

            for(Ant ant : ants){
                ant.visitCities(distMatrix, pheromones, alpha, beta, pRandom);

                double length = ant.getTourLength();

                if(length < iterBest) iterBest = length;
                if(length > iterWorst) iterWorst = length;
                iterSum += length;

                if(length < bestTourLength){
                    bestTourLength = length;
                    bestTourOrder = ant.getTour().clone();
                }

                if(length > runWorst) runWorst = length;

                sumAllTours += length;
                totalToursCount++;
            }

            double iterAvg = iterSum / numAnts;
            historyBest.add(bestTourLength);
            historyAvg.add(iterAvg);
            historyWorst.add(iterWorst);

            for(int i = 0; i < n; i++){
                for(int j = 0; j < n; j++){
                    pheromones[i][j] *= (1.0 - evaporation);
                    if(pheromones[i][j] < 0.0001){
                        pheromones[i][j] = 0.0001;
                    }
                }
            }

            for(Ant ant : ants){
                double contribution = 100.0 / ant.getTourLength();
                int[] tour = ant.getTour();
                for(int i = 0; i < n - 1; i++){
                    pheromones[tour[i]][tour[i+1]] += contribution;
                    pheromones[tour[i+1]][tour[i]] += contribution;
                }

                pheromones[tour[n-1]][tour[0]] += contribution;
                pheromones[tour[0]][tour[n-1]] += contribution;
            }
        }

        if(totalToursCount > 0){
            runAvg = sumAllTours / totalToursCount;
        }
    }


    public List<Double> getHistoryBest() {
        return historyBest;
    }

    public double getBestTourLength() {
        return bestTourLength;
    }

    public int[] getBestTourOrder() {
        return bestTourOrder;
    }

    public List<Double> getHistoryAvg() {
        return historyAvg;
    }

    public List<Double> getHistoryWorst() {
        return historyWorst;
    }

    public double getRunAvg() {
        return runAvg;
    }

    public double getRunWorst() {
        return runWorst;
    }
}
