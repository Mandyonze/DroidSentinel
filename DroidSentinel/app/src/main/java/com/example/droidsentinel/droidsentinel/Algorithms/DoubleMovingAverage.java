package com.example.droidsentinel.droidsentinel.Algorithms;

/**
 * Created by andreshg on 1/12/17.
 */

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import org.apache.commons.lang.ArrayUtils;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

/**
 * Este artículo sigue la descripción publicada por P.G. Mulloy,
 * en el paper "Smoothing Data with Faster Moving Averages",
 * publicado en la revista International Journal of Forecasting, Vol. 22, Issue 4, pp. 637-666, December 2006
 */
public class DoubleMovingAverage {
    Queue<Double> window = new LinkedList<Double>();
    private final int period; //Sliding window
    private double sum;
    List<Double> MA;




    public List<Double> convertDoubletoArrayList(double[] dList){
        ArrayList<Double> sol = new ArrayList<Double>();
        for(int i=0;i<dList.length;i++){
            sol.add(dList[i]);
        }

        return sol;
    }

    public List<Double> getAllPredictions(List<Double> data, int T){
        //returns all the predictions for a time serie
        //this is expensive, but helps in testing
        double[] dObs=ArrayUtils.toPrimitive(data.toArray(new Double[data.size()]));
        double[] sol= new double[data.size()+T];
        for(int i=0;i<data.size();i++){
            List<Double> subList = new ArrayList<Double>(data.subList(0, i));
            if(i<T){
                sol[i]=dObs[i];
                sol[i+T]=getPrediction(subList);
            }
            else{
                sol[i+T]=getPrediction(subList);
            }
        }//for

        List<Double> auxSOl=convertDoubletoArrayList(sol);
        return auxSOl;
    }

    public double getPrediction(List<Double> data){
        //Predict a simple value
        //pred[i]= SMA[i-1]+(O[i-2]-SMA[i-2])
        double pred=0.0;

        double[] dObs=ArrayUtils.toPrimitive(data.toArray(new Double[data.size()]));
        List<Double> SMA_data = get2MA((List) ((ArrayList) data).clone());
        double[] dSMA=ArrayUtils.toPrimitive(SMA_data.toArray(new Double[SMA_data.size()]));

        int i = data.size()-1;
        if(i>2){
            pred=dSMA[i]+(dObs[i-1]-dSMA[i-1]);
            //System.out.println("pred: "+pred+"dSMA[i-1]:" + dSMA[i-1]+" + (dObs[i-2]"+dObs[i-2]+"-dSMA[i-2]): "+dSMA[i-2]+")");
        }
        else{
            pred= 0;
        }
        return pred;
    }

    public List<Double> get2MAatT(List<Double> data, int T){
        for(int i=0;i<T;i++){
            List<Double> list2 = ((List) ((ArrayList) data).clone());
            List<Double> list3= get2MA(list2);
            data.add((Double) list3.get(data.size()-1));
        }
        return data;
    }

    public List<Double> get2MA(List<Double> data){
        this.MA=getMA(data);
        List<Double> DMA = getMA((List) ((ArrayList) MA).clone());
        return DMA;
    }

    public List<Double> getMA(List<Double> data){
        List<Double> ma_data = new ArrayList<Double>(data.size());
        for (double x : data) {
            newNum(x);
            ma_data.add(getAvg());
        }
        return ma_data;
    }

    public DoubleMovingAverage(int period) {
        assert period > 0 : "Period must be a positive integer!";
        this.period = period;
    }

    public void newNum(double num) {
        sum += num;
        window.add(num);
        if (window.size() > period) {
            sum -= window.remove();
        }
    }

    public double getAvg() {
        if (window.isEmpty()) return 0; // technically the average is undefined
        return sum / window.size();
    }


}
