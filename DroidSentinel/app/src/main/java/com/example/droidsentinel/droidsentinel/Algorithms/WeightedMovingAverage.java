package com.example.droidsentinel.droidsentinel.Algorithms;

/**
 * Created by andreshg on 1/12/17.
 */

import org.apache.commons.lang.ArrayUtils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class WeightedMovingAverage {

    public WeightedMovingAverage() {
    }

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
        List<Double> SMA_data = getWMA((List) ((ArrayList) data).clone());
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
    public List<Double> getWMAatT(List<Double> data, int T){

        for(int i=0;i<T;i++){
            List<Double> list2 = ((List) ((ArrayList) data).clone());
            List<Double> list3= getWMA(list2);
            data.add((Double) list3.get(data.size()-1));
        }

        return data;
    }


    public List<Double> getWMA(List<Double> obs){
        List<Double> sol =new ArrayList<Double>();

        for(int countObs =0; countObs<obs.size();countObs++){
            int totalW=0;
            int currentW=1;
            Double acc=0.0;
            Iterator it = obs.iterator();
            int c2=0;
            while(it.hasNext() && c2<countObs){
                Double auxObs= (Double) it.next();
                acc+=auxObs*currentW;
                totalW+=currentW;
                currentW++;
                c2++;
            }//while
            sol.add((double)acc/(double)totalW);
        }

        return sol;
    }
}
