package com.example.droidsentinel.droidsentinel.GeneticAlgorithm.Chromosome;

/**
 * Created by andreshg on 1/12/17.
 */

import com.example.droidsentinel.droidsentinel.Algorithms.ARIMA;
import com.example.droidsentinel.droidsentinel.ConPrediction;
import com.example.droidsentinel.droidsentinel.GeneticAlgorithm.GeneticCalibrator;

import java.util.ArrayList;
import java.util.List;

public class ChromosomeARIMA extends Chromosome {

    private int p;
    private int d;
    private int q;
//    private int period;

    public ChromosomeARIMA(int dist, List<Double> data, int p, int d, int q){
        super(dist, data);
        this.p = p;
        this.d = d;
        this.q = q;
        generateChromosome();
    }

    @Override
    public void generateChromosome() {

        List<Double> tsCloneABS = ((List) ((ArrayList) this.data).clone());
        List<Double> tsFt = new ArrayList();
        List<Double> tsClone = ((List) ((ArrayList) tsCloneABS).clone());
//        List<ConPrediction> AlgTests5 = new ArrayList();


        //List<Double> tsFtAux = new ArrayList();
        List<Double> slistARIMA = (List<Double>) tsClone.subList(0, this.data.size() - this.dist);
        ARIMA auxARIMA = new ARIMA(slistARIMA,this.p,this.d,this.q);
        tsFt = auxARIMA.getAllPredictions(this.dist);

        this.ChormosomeConPrediction = new ConPrediction();
        this.ChormosomeConPrediction.setIdAlgorithm("ARIMA");
        this.ChormosomeConPrediction.algParams.put("p", (double) p);
        this.ChormosomeConPrediction.algParams.put("d", (double) d);
        this.ChormosomeConPrediction.algParams.put("q", (double) q);

        this.ChormosomeConPrediction.setTs(this.data);
        this.ChormosomeConPrediction.setFt(((List) ((ArrayList) tsFt).clone()));
        //auxCon5.calculateErrors();
        //auxCon5.calculateMAWeightedError(dist);  //change formula

        this.ChormosomeConPrediction.calculateSMAPE();
        this.ChormosomeConPrediction.smape = this.ChormosomeConPrediction.getAvgSmapeLastN(this.dist);
        this.smape = this.ChormosomeConPrediction.smape;
        //System.out.println("8888888888888888888888888888888888");
        //System.out.println(auxCon5.getSmape());
        //System.out.println("8888888888888888888888888888888888");
        if (this.smape < GeneticCalibrator.SMAPE_MIN) {
            this.bueno = true;
        }
    }

    @Override
    public void crossover(Chromosome crossover, int pos) {
        Double value = crossover.getValueAttibute(pos);
        setValueAttibute(pos, value);
    }

    @Override
    public Double getValueAttibute(int pos) {
        int value = 0;
        switch (pos){
            case 0:
                value = this.p;
                break;
            case 1:
                value = this.d;
                break;
            case 2:
                value = this.q;
                break;
            default:
                value = this.p;
                break;

        }

        return (double) value;
    }

    @Override
    public void setValueAttibute(int pos, Double value) {

        int aux = value.intValue();
        switch (pos){
            case 0:
                this.p = aux;
                break;
            case 1:
                this.d = aux;
                break;
            case 2:
                this.q = aux;
                break;
            default:
                this.p = aux;
                break;
        }

    }

    @Override
    public String toString() {
        return "[" + this.p + "," + this.d + "," + this.q + "] SMAPE: " + this.smape;
    }
}
