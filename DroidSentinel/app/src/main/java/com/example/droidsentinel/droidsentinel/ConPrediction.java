package com.example.droidsentinel.droidsentinel;

/**
 * Created by andreshg on 1/12/17.
 */

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class ConPrediction {
    List<Double> ts;
    List<Double> ft;
    String idAlgorithm;
    List<Double> FtError;
    List<Double> AbsFtError;
    List<Double> SquaredFtError;
    public Map<String, Double> algParams;
    double wmse;
    List<Double> listSMAPE;
    public double smape;               //Computed smape to evaluate model

    public ConPrediction() {
        this.ts = new ArrayList<Double>();
        this.ft = new ArrayList<Double>();
        this.idAlgorithm = idAlgorithm;
        this.FtError = new ArrayList<Double>();
        this.AbsFtError = new ArrayList<Double>();
        this.SquaredFtError = new ArrayList<Double>();
        this.algParams = new HashMap<String, Double>();
        this.wmse = 0.0;
        this.smape = 0.0;
        this.listSMAPE= new ArrayList<Double>();
    }

    public List<Double> getTs() {
        return ts;
    }


    public String getIdAlgorithm() {
        return idAlgorithm;
    }

    public List<Double> getFtError() {
        return FtError;
    }

    public List<Double> getAbsFtError() {
        return AbsFtError;
    }

    public List<Double> getSquaredFtError() {
        return SquaredFtError;
    }

    public void setTs(List<Double> ts) {
        this.ts = ts;
    }

    public List<Double> getFt() {
        return ft;
    }

    public void setFt(List<Double> ft) {
        this.ft = ft;
    }

    public void setIdAlgorithm(String idAlgorithm) {
        this.idAlgorithm = idAlgorithm;
    }

    public void setFtError(List<Double> FtError) {
        this.FtError = FtError;
    }

    public void setAbsFtError(List<Double> AbsFtError) {
        this.AbsFtError = AbsFtError;
    }

    public void setSquaredFtError(List<Double> SquaredFtError) {
        this.SquaredFtError = SquaredFtError;
    }

    public void calculateErrors(){

        double sum_sq = 0.0;
        double mse=0.0;

        for(int i=0;i<this.ts.size();i++){
            double tsVal = this.ts.get(i);
            double ftVal = this.ft.get(i);
            this.FtError.add(tsVal - ftVal);
            this.AbsFtError.add(Math.abs(tsVal - ftVal));

            //Mean Squared Error
            double err = tsVal - ftVal;
            sum_sq += (err * err);

            if(i!=0)
                mse = (double)sum_sq / (i);
            else
                mse=0.0;

            this.SquaredFtError.add(mse);
        }

    }//calculate errors

    public void calculateMAWeightedError(int T){

        double sum_sq = 0.0;

        int firstFtIndex = 2*T;
        int n = this.ts.size();

        for(int i=firstFtIndex;i<n;i++){
            double err = this.FtError.get(i);
            sum_sq += (err * err);
        }

        wmse = sum_sq / (n - firstFtIndex);
    }//calculate  calculateMAWeightedError


    public void calculateHoltWintersWeightedError(int T,int period){

        double sum_sq = 0.0;

        int firstFtIndex = period;
        int n = this.ts.size();

        for(int i=firstFtIndex;i<n;i++){
            sum_sq += this.AbsFtError.get(i);
        }

        wmse = sum_sq / (n - firstFtIndex);
    }//calculate  calculateMAWeightedError

    public void calculateSmoothingWeightedError(int T){

        double sum_sq = 0.0;

        int firstFtIndex = 2;
        int n = this.ts.size();

        for(int i=firstFtIndex;i<n;i++){
            sum_sq += this.AbsFtError.get(i);
        }

        wmse = sum_sq / (n - firstFtIndex);
    }//calculate  calculateSmoothingWeightedError


    public void calculateArimaWeightedError(int T){

        double sum_sq = 0.0;

        int firstFtIndex = 2*T + 2;
        int n = this.ts.size();

        for(int i=firstFtIndex;i<n;i++){
            sum_sq += this.AbsFtError.get(i);
        }

        wmse = sum_sq / (n - firstFtIndex);
    }//calculate  calculateArimaWeightedError


    public void predictionToFile(String Path) throws IOException{

        //write
        FileWriter writer = new FileWriter(Path);

        writer.write(this.idAlgorithm+"\n");
        //original time Serie

        writer.write("\nSMAPE\n");
        writer.write(String.valueOf(this.smape)+"\n");

        writer.write("\nSMAPE values\n");
        Iterator smpit = this.listSMAPE.iterator();
        while(smpit.hasNext()){
            double auxVal = (double) smpit.next();
            writer.write(String.valueOf(auxVal)+"\n");
        }

        writer.write("Original time Serie\n");
        Iterator itTs = this.ts.iterator();
        while(itTs.hasNext()){
            double auxVal = (double) itTs.next();
            writer.write(String.valueOf(auxVal)+"\n");
        }

        //Forecast
        writer.write("\nForecast\n");
        Iterator itFt = this.ft.iterator();
        while(itFt.hasNext()){
            double auxVal = (double) itFt.next();
            writer.write(String.valueOf(auxVal)+"\n");
        }

        //Error
        writer.write("\nFt Error\n");
        Iterator itErr = this.FtError.iterator();
        while(itErr.hasNext()){
            double auxVal = (double) itErr.next();
            writer.write(String.valueOf(auxVal)+"\n");
        }

        //Abs Error
        writer.write("\nABS Ft Error\n");
        Iterator itAbs = this.AbsFtError.iterator();
        while(itAbs.hasNext()){
            double auxVal = (double) itAbs.next();
            writer.write(String.valueOf(auxVal)+"\n");
        }

        //Mean Squared Error
        writer.write("\nMean Squared Error\n");
        Iterator itMSA = this.AbsFtError.iterator();
        while(itMSA.hasNext()){
            double auxVal = (double) itMSA.next();
            writer.write(String.valueOf(auxVal)+"\n");
        }

        //Weighted Mean Squared Error
        writer.write("\nWeighted Mean Squared Error\n");
        writer.write(String.valueOf(this.wmse)+"\n");

        writer.close();

    }//writePrediction

    //public List<Double> calculateSMAPE(){
    public void calculateSMAPE(){
        //Symetric mean absolite percentage error
        //  New: 200* sum{|x-ft|(x+ft))}
        //double acc = 0.0;
        int tsSize = this.ts.size();
        int ftSize = this.ft.size();

        for(int i=0;i<tsSize;i++){
            double tsVal = this.ts.get(i);
            double ftVal = this.ft.get(i);

            double supFrac=Math.abs(tsVal - ftVal);
            double infFrac=Math.abs(tsVal)+Math.abs(ftVal);
            double smape=200*(supFrac/infFrac);

            this.listSMAPE.add(smape);
        }


    }//calculate errors

    public double getAvgSmape(){
        Iterator it = this.listSMAPE.iterator();
        double sum=0.0;
        while(it.hasNext()){
            sum+=(Double) it.next();
        }
        double avgsmape = sum/(this.listSMAPE.size());

        System.out.println("Smape:" + avgsmape);
        return avgsmape;
    }

    public double getAvgSmapeLastN(int n){
        double sum=0.0;
        int start = this.ts.size()- n;
        for(int i=start;i<this.ts.size();i++){
            sum+=(Double) this.listSMAPE.get(i);
        }
        double avg = sum/(n);


        return avg;
    }

    public double getSmapei(int i){
        return this.listSMAPE.get(i);
    }

    public double getSmape(){
        return this.smape;
    }

    public void setSmape(double smape){
        this.smape = smape;
    }
   
}


