package com.example.droidsentinel.droidsentinel.GeneticAlgorithm.Chromosome;

import com.example.droidsentinel.droidsentinel.Algorithms.TripleExpSmoothingAdd;
import com.example.droidsentinel.droidsentinel.Algorithms.TripleExpSmoothingMul;
import com.example.droidsentinel.droidsentinel.ConPrediction;
import com.example.droidsentinel.droidsentinel.GeneticAlgorithm.GeneticCalibrator;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by andreshg on 1/12/17.
 */

public class ChromosomeMHW extends Chromosome implements Cloneable{

    private Double alpha;
    private Double beta;
    private Double gamma;
    private int period;


    public ChromosomeMHW(int dist, List<Double> data, Double alpha, Double beta, Double gamma, int period){
        super(dist,data);
        this.alpha = alpha;
        this.beta=beta;
        this.gamma=gamma;
        this.period=period;
        generateChromosome();
    }

    @Override
    public void generateChromosome() {
        //SMAPE y CONPREDICTION

        List<Double> tsCloneABS = ((List) ((ArrayList) this.data).clone());
        List<Double> tsFt = new ArrayList();
        List<Double> tsClone = ((List) ((ArrayList) tsCloneABS).clone());

        List<Double> slist = (List<Double>) tsClone.subList(0,this.data.size() - dist);
        tsFt = TripleExpSmoothingMul.forecast(slist, this.alpha, this.beta, this.gamma, this.period, this.dist, false);

        this.ChormosomeConPrediction = new ConPrediction();
        this.ChormosomeConPrediction.setIdAlgorithm("Additive Holt-Winters (triple exponential smoothing)");
        this.ChormosomeConPrediction.algParams.put("alpha",this.alpha);
        this.ChormosomeConPrediction.algParams.put("beta",this.beta);
        this.ChormosomeConPrediction.algParams.put("gamma",this.gamma);
        this.ChormosomeConPrediction.algParams.put("period",(double)this.period);
        this.ChormosomeConPrediction.setTs(this.data);
        this.ChormosomeConPrediction.setFt(((List) ((ArrayList) tsFt).clone()));
        //auxCon.calculateErrors();
        //auxCon.calculateMAWeightedError(dist);  //change formula
        //auxCon.calculateSMAPESmoothing(dist,period);
        this.ChormosomeConPrediction.calculateSMAPE();
        this.ChormosomeConPrediction.smape = this.ChormosomeConPrediction.getAvgSmapeLastN(dist);
        this.smape = this.ChormosomeConPrediction.smape;

        if (this.smape < GeneticCalibrator.SMAPE_MIN) {
            this.bueno = true;
        }
    }


    @Override
    public String toString() {
        return "[" + this.alpha + "," + this.beta + "," + this.gamma + "] SMAPE: " + this.smape;
    }

    @Override
    public void crossover(Chromosome crossover, int pos) {

        Double value = crossover.getValueAttibute(pos);
        setValueAttibute(pos, value);

    }

    @Override
    public Double getValueAttibute(int pos) {
        Double value = 0.0;
        switch (pos){
            case 0:
                value = this.alpha;
                break;
            case 1:
                value = this.beta;
                break;
            case 2:
                value = this.gamma;
                break;
            default:
                value = this.alpha;
                break;

        }

        return value;
    }

    public void setValueAttibute(int pos, Double value) {
        switch (pos){
            case 0:
                this.alpha = value;
                break;
            case 1:
                this.beta = value;
                break;
            case 2:
                this.gamma = value;
                break;
            default:
                this.alpha = value;
                break;

        }

    }

}
