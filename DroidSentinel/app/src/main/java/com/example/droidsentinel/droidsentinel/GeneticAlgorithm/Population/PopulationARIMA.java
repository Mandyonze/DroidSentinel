package com.example.droidsentinel.droidsentinel.GeneticAlgorithm.Population;

/**
 * Created by andreshg on 1/12/17.
 */

import com.example.droidsentinel.droidsentinel.GeneticAlgorithm.Chromosome.Chromosome;
import com.example.droidsentinel.droidsentinel.GeneticAlgorithm.Chromosome.ChromosomeARIMA;

import java.util.List;

public class PopulationARIMA extends Population {

    public PopulationARIMA(List<Double> data, int dist) {
        super(data, dist, "ARIMA");
        this.numArguments = 3;
    }
    @Override
    public void generatePopulation() {
        for (int p = 0; p < 3; p++) {
            for (int d = 0; d < 3; d++) {
                for (int q = 0; q < 3; q++) {
                    Chromosome c = new ChromosomeARIMA(this.dist, this.data, p,d,q);
                    this.initial_population.add(c);
                }
            }
        }
    }


    @Override
    public void generatePopulationRandom() {

    }
}
