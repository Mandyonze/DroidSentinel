package com.example.droidsentinel.droidsentinel.GeneticAlgorithm.Chromosome;

/**
 * Created by andreshg on 1/12/17.
 */

import com.example.droidsentinel.droidsentinel.ConPrediction;

import java.util.List;

public abstract class Chromosome implements Cloneable{
    protected int dist;
    protected List<Double> data;
    protected Double smape;
    protected ConPrediction ChormosomeConPrediction;
    protected Boolean bueno = false;

    Chromosome(){

    }
    Chromosome(int dist,List<Double> data){
        this.dist = dist;
        this.data = data;
    }


    public abstract void generateChromosome();
    //    public abstract Chromosome generateChormosomeRandom();
    public abstract void crossover(Chromosome crossover, int pos);
    public abstract Double getValueAttibute(int pos);
    public abstract void setValueAttibute(int pos, Double value);
    //    public abstract Chromosome mutate(Chromosome mutate);
    public abstract String toString();

    //GET
    public int getDist(){
        return this.dist;
    }
    public List<Double> getData(){
        return this.data;
    }
    public Double getSmape(){
        return this.smape;
    }
    //    public ConPrediction getConPrediction(){
//        return this.ChormosomeConPrediction;
//    }
    //SET
    public void setDist(int dist){
        this.dist = dist;
    }
    public void setData(List<Double> data){
        this.data = data;
    }
    public void setSmape(double smape){
        this.smape = smape;
    }
//    public void setConPrediction(ConPrediction ChormosomeConPrediction){
//        this.ChormosomeConPrediction = ChormosomeConPrediction;
//    }

    public Boolean getBueno() {
        return this.bueno;
    }

    public Object clone()
    {
        Object clone = null;
        try
        {
            clone = super.clone();
        }
        catch(CloneNotSupportedException e)
        {
            // No deberia suceder
        }
        return clone;
    }
}
