package com.example.droidsentinel.droidsentinel.Algorithms;

import java.util.ArrayList;
import java.util.List;

import data.*;
import timeseries.TimeSeries;
import timeseries.Ts;
import timeseries.models.Forecast;
import timeseries.models.arima.*;
import timeseries.models.arima.Arima;

/**
 * La clae ARIMA instancia una predicción completa para una TS a partir de unos parámetros P,D,Q
 * @author mandyonze
 */
public class ARIMA {
    private int p; //Number of autoregressive terms.
    private int d; //Number of nonseasonal differences needed for stationarity.
    private int q; //Number of lagged forecast errors in the prediction equation.
    private TimeSeries timeSeries;
    private ArimaOrder modelOrder;
    private Arima model;
    private List<Double> data;

    public ARIMA(List<Double> data, int p, int d, int q) {
        this.p = p;
        this.q= q;
        this.q = q;
        this.data=data;
        this.timeSeries = Ts.newAnnualSeries(1975, DoubleFunctions.arrayFrom(data));
        this.modelOrder = ArimaOrder.order(this.p, this.d, this.q);
        this.model = Arima.model(this.timeSeries, this.modelOrder);
    }

    public List<Double> convertDoubletoArrayList(double[] dList){
        ArrayList<Double> sol = new ArrayList<Double>();
        for(int i=0;i<dList.length;i++){
            sol.add(dList[i]);
        }

        return sol;
    }

    public List<Double> getAllPredictions(int T){

        for (int i = 0; i < T; i++) {
            this.data.add(this.getPrediction());

        }
        double[] sol= new double[this.data.size()];


        for(int i=0;i<this.data.size();i++){
            List<Double> subList = new ArrayList<Double>(this.data.subList(0, i));
            sol[i]=this.data.get(i);
            //System.out.println( i + ": " +  sol[i]);

        }//for
        List<Double> auxSOl=convertDoubletoArrayList(sol);
        return auxSOl;
    }

    public Double getPrediction() {
        this.timeSeries = Ts.newAnnualSeries(1975, DoubleFunctions.arrayFrom(data));
        this.modelOrder = ArimaOrder.order(this.p, this.d, this.q);
        this.model = Arima.model(this.timeSeries, this.modelOrder);
        Forecast forecast = this.model.forecast(1);
        TimeSeries prediccion = forecast.forecast();
        return prediccion.asList().get(0);


    }

    public static double[] arrayFrom(List<Double> data) {

        final int size = data.size();

        final double[] doubles = new double[size];

        for (int i = 0; i < size; i++) {
            doubles[i] = data.get(i);
        }
        return doubles;
    }
}

