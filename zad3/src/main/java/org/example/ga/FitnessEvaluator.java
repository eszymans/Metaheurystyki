package org.example.ga;

import org.example.model.Backpack;
import org.example.model.Individual;
import org.example.model.Item;

import java.util.List;

public class FitnessEvaluator {
    private final Backpack backpack;

    public FitnessEvaluator(Backpack backpack) {
        this.backpack = backpack;
    }

    public void evaluate(Individual ind) {
        List<Item> items = backpack.getItems();
        int totalWeight = 0;
        int totalFitness = 0;
        boolean[] g = ind.getGenes();
        for (int i = 0; i < g.length; i++) {
            if (g[i]) {
                totalWeight += items.get(i).getWeight();
                totalFitness += items.get(i).getValue();
            }
        }
        if (totalWeight <= backpack.getCapacity())
            {
                ind.setFitness(totalFitness);
            }
            else{
                ind.setFitness(0);
        }
        ind.setWeight(totalWeight);
    }
}
