package com.example.droidsentinel.droidsentinel.GeneticAlgorithm.Population;

/**
 * Created by andreshg on 1/12/17.
 */

import com.example.droidsentinel.droidsentinel.GeneticAlgorithm.Chromosome.Chromosome;
import com.example.droidsentinel.droidsentinel.GeneticAlgorithm.Chromosome.ChromosomeMHW;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 *
 * @author mandyonze
 */
public class PopulationMHW extends Population {

    public PopulationMHW(List<Double> data,int dist) {
        super(data, dist, "Additive Holt-Winters (triple exponential smoothing");
        this.numArguments = 3;
    }

    @Override
    public void generatePopulation() {
        //Add as many coeficients as needed
        List<Double> alphaAHTES = new ArrayList();
        for(int i=0;i<9;i=i+2) alphaAHTES.add(0.1*(i+1));

        List<Double> betaAHTES = new ArrayList();
        for(int i=0;i<9;i=i+2) betaAHTES.add(0.1*(i+1));

        List<Double> gammaAHTES = new ArrayList();
        for(int i=0;i<9;i=i+2) gammaAHTES.add(0.1*(i+1));

        //List<ConPrediction> AlgTests1 = new ArrayList();
        Iterator it = alphaAHTES.iterator();

        while(it.hasNext()){
            double al = (Double) it.next();
            Iterator itBett = betaAHTES.iterator();
            while(itBett.hasNext()){
                double be = (Double) itBett.next();
                Iterator itGamt = gammaAHTES.iterator();
                while(itGamt.hasNext()){
                    double ga = (Double) itGamt.next();
                    Chromosome c = new ChromosomeMHW(this.dist,this.data,al,be,ga,this.dist);

                    this.initial_population.add(c);
                }
            }
        }
    }


    @Override
    public void generatePopulationRandom() {
        Double al,be,ga;

        for(int i = 0; i < NUM_INITIAL_POPULATION; i++) {
            al = Math.random()*1;
            be = Math.random()*1;
            ga = Math.random()*1;
            Chromosome c = new ChromosomeMHW(this.dist,this.data,al,be,ga,this.dist);
            this.initial_population.add(c);
        }
    }

}
