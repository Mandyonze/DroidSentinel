package com.example.droidsentinel.droidsentinel.Algorithms;

/**
 * Created by andreshg on 2/12/17.
 */

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;


public class DoubleExpSmoothing {
    public static List<Double> forecast(List<Double> data, double alpha, double gamma, int initializationMethod, int numForecasts, boolean debug) {

        validateArguments(data, alpha, gamma, initializationMethod, numForecasts);

        double[] y = new double[data.size() + numForecasts];
        double[] s = new double[data.size()];
        double[] b = new double[data.size()];
        s[0] = y[0] = data.get(0);

        if(initializationMethod==0) {
            b[0] = data.get(1)-data.get(0);
        } else if(initializationMethod==1 && data.size()>4) {
            b[0] = (data.get(3) - data.get(0)) / 3;
        } else if(initializationMethod==2) {
            b[0] = (data.get(data.size() - 1) - data.get(0))/(data.size() - 1);
        }

        if (debug) {
            System.out.println(String.format("Total observations: %d", data.size()));
            printArray("Observations", data);
        }

        int i = 1;
        y[1] = s[0] + b[0];
        for (i = 1; i < data.size(); i++) {
            s[i] = alpha * data.get(i) + (1 - alpha) * (s[i - 1]+b[i - 1]);
            b[i] = gamma * (s[i] - s[i - 1]) + (1-gamma) * b[i-1];
            y[i+1] = s[i] + b[i];
        }

        for (int j = 0; j < numForecasts ; j++, i++) {
            y[i] = s[data.size()-1] + (j+1) * b[data.size()-1];
        }

        List myList = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            myList = Arrays.stream(y).boxed().collect(Collectors.toList());
        }

        if (debug) {
            printArray("Predictions", myList);
        }
        return myList;
    }


    private static void validateArguments(List<Double> y, double alpha,
                                          double gamma, int initializationMethod, int numForecasts) {
        if (y == null) {
            throw new IllegalArgumentException("Value of y should be not null");
        }

        if(numForecasts <= 0){
            throw new IllegalArgumentException("Value of m must be greater than 0.");
        }


        if((alpha < 0.0) || (alpha > 1.0)){
            throw new IllegalArgumentException("Value of Alpha should satisfy 0.0 <= alpha <= 1.0");
        }

        if((gamma < 0.0) || (gamma > 1.0)){
            throw new IllegalArgumentException("Value of Gamma should satisfy 0.0 <= gamma <= 1.0");
        }

        if((initializationMethod > 2)){
            throw new IllegalArgumentException("Value of initializationMethod should satisfy initializationMethod <= 2");
        }
    }

    private static void printArray(String description, List<Double> data) {
        System.out.println(description);
        for (int i = 0; i < data.size(); i++) {
            System.out.println(data.get(i));
        }
    }


}
