package com.example.droidsentinel.droidsentinel.GeneticAlgorithm;

/**
 * Created by andreshg on 1/12/17.
 */


import com.example.droidsentinel.droidsentinel.GeneticAlgorithm.Chromosome.Chromosome;
import com.example.droidsentinel.droidsentinel.GeneticAlgorithm.Population.Population;
import com.example.droidsentinel.droidsentinel.GeneticAlgorithm.Population.PopulationMHW;

import java.util.List;

/**
 *
 * @author mandyonze
 */
public class GeneticCalibrator {

    public static final Double SMAPE_MIN = 1.0;
    public static final int NUM_INTERACCIONES = 60;
    private static List<Double> ts;
    private static final int WINDOWF = 5;

    //INIT
    //1.-Se crea una población incial en función del algoritmo dado---> Population init
    // WHILE: Mientras no se cumplan las condiciones de parada
    //2.-Se evaluan y ordenan esa población inical devolviendo una población de trabajo----->Population work
    //3.-Se cruzan los parametros a modo de padres ---> Population work cruzada
    //4.-Se muta aleatoriamente--->Population work mutado
    //FIN

    public GeneticCalibrator(List<Double> timeseries){

        ts = timeseries;
    }

    public GeneticCalibrator(){
    }

    public int get_window(){
        return WINDOWF;
    }

    public Chromosome calibrate() {

        Population population = new PopulationMHW(ts, WINDOWF);

        population.generatePopulation();
        population.fitnessInitialPopulation();


        int count = 0;

        while (!population.getStop() && count < GeneticCalibrator.NUM_INTERACCIONES) {
            count++;
            population.crossover();
            population.mutate();
            population.fitness();

        }
        return population.getBestParams();
    }
}
