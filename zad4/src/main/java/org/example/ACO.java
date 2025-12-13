package org.example;

import java.util.ArrayList;
import java.util.List;

public class ACO {
    private List<Point> points;
    private double[][] distMatrix;
    private double[][] pheromones;
    private int numAnts;

    private double alpha, beta, evaporation, pRandom;
    private int interations;

    private int[] bestTourOrder;
    private double bestTourLength = Double.MAX_VALUE;
    private List<Double> historyBest = new ArrayList<>();

    public ACO(List<Point> points, int numAnts, int interations, double alpha, double beta, double evaporation, double pRandom) {
        this.points = points;
        this.numAnts = numAnts;
        this.interations = interations;
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

        for(int iter = 0; iter < interations; iter++){
            for(Ant ant : ants){
                ant.visitCities(distMatrix, pheromones, alpha, beta, pRandom);

                if(ant.getTourLength() < bestTourLength){
                    bestTourLength = ant.getTourLength();
                    bestTourOrder = ant.getTour().clone();
                }
            }
            historyBest.add(bestTourLength);

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
}
