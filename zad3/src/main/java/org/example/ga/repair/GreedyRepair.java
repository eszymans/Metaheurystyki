package org.example.ga.repair;

import org.example.model.Backpack;
import org.example.model.Individual;
import org.example.model.Item;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class GreedyRepair {
    private final Backpack backpack;

    public GreedyRepair(Backpack backpack) {
        this.backpack = backpack;
    }

    public void repair(Individual ind) {
        List<Item> items = backpack.getItems();
        int maxWeight = backpack.getCapacity();
        boolean[] g = ind.getGenes();

        int totalValue = 0;
        int totalWeight = 0;
        List<Integer> included = new ArrayList<>();
        for(int i = 0; i < g.length; i++) {
            if(g[i]) {
                totalWeight += items.get(i).getWeight();
                totalValue += items.get(i).getValue();
                included.add(i);
            }
        }
        if(totalWeight <= maxWeight) {
            ind.setWeight(totalWeight);
            ind.setFitness(totalValue);
            return;
        }

        included.sort(Comparator.comparingDouble(i -> (double) items.get(i).getValue() / items.get(i).getWeight()));

        for (int idx : included) {
            if (totalWeight <= maxWeight) break;
            g[idx] = false;
            totalWeight -= items.get(idx).getWeight();
            totalValue -= items.get(idx).getValue();
        }

        ind.setWeight(totalWeight);
        ind.setFitness(totalValue);
    }
}
