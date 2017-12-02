package com.example.droidsentinel.droidsentinel;

/**
 * Created by andreshg on 30/11/17.
 */

public class EntropyCalc {

    public static double LOG_BASE = 2.0;

    public static double calculateEntropySelf(double[] dataVector, double maxPackets)
    {
        double ShannonSpecificEntropy = 0.0;
        double NormalizedShannonSpecificEntropy;
        double AbsoluteEntropy;
        double NormalizedAbsoluteEntropy;

        for (double val : dataVector)
        {
            // Entropy = probability * Log2(1/probability) : OveralEntropy += entry.Value * Math.Log((1 / entry.Value), 2);
            // Shannon (specific) entropy = -1*sum(probability * ln(probability))
            val = val / maxPackets;
            ShannonSpecificEntropy += ((val * log(val, LOG_BASE) * -1));
        }

        NormalizedShannonSpecificEntropy = ShannonSpecificEntropy / log(dataVector.length, LOG_BASE);
        AbsoluteEntropy = ShannonSpecificEntropy * dataVector.length;
        NormalizedAbsoluteEntropy = AbsoluteEntropy / log(dataVector.length, LOG_BASE);

        if (Double.isNaN(NormalizedShannonSpecificEntropy)){
            NormalizedShannonSpecificEntropy = 0.0;
        }

        return NormalizedShannonSpecificEntropy;
    }

    static double log(double x, double base)
    {
        return (double) (Math.log(x) / Math.log(base));
    }
}
