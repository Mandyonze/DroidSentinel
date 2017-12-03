package com.example.droidsentinel.droidsentinel;

/**
 * Created by andreshg on 30/11/17.
 */
/**
 * Ventajas de entropia frente a otroas metricas:
 I. Ozcelik, R.R. Brooks. “Deceiving entropy based DoS detection”, Computers & Security, vol. 48, no. 1, pp. 234-245, 2015.

 Entropia de Shannon (información):
 C.E. Shannon. “A mathematical theory of communication”. Bell Systems Technical Journal,  vol. 27,  pp. 379-656, 1948

 Entropia de Rényi:
 M.H. Bhuyan, D. K. Bhattacharyya, J.K. Kalita. “An empirical evaluation of information metrics for low-rate and high-rate DDoS attack detection”,
 Pattern Recognition Letters, vol. 51, no. 1, pp. 1-7, 2015.

 Hemos utilizado la Entropía de Shannon pese a haber implementado la Entropía de Renyi, pero por falta de tiempos
 y pruebas nos hemos decantado por la Entropia de Shannon
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
