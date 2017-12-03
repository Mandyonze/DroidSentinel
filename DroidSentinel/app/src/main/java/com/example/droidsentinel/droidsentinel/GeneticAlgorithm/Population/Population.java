package com.example.droidsentinel.droidsentinel.GeneticAlgorithm.Population;

/**
 * Created by andreshg on 1/12/17.
 */

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import com.example.droidsentinel.droidsentinel.GeneticAlgorithm.Chromosome.Chromosome;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 *
 * @author mandyonze
 */
public abstract class Population {

    public static final int NUM_INITIAL_POPULATION = 100;
    public static final int NUM_WORK_POPULATION = 30;


    //Arrays de poblaciones
    protected ArrayList<Chromosome> initial_population;
    protected ArrayList<Chromosome> work_population;
    protected ArrayList<Chromosome> best_population;
    //protected ArrayList<Chromosome> only_the_best_Chromosome;

    //Atributos necesarios para calcular la predición de los cromosomas
    protected int dist;
    protected List<Double> data;

    //Atributos para cada Algoritmo
    protected String predictionAlgorithm;
    protected int numArguments;

    //Condición de parada
    protected Boolean stop;


    public Population(){
        this.stop = false;
        this.initial_population = new ArrayList<Chromosome>();
        this.work_population = new ArrayList<Chromosome>();
    }

    public Population(List<Double> data,int dist, String predictionAlgorithm){
        this.data = data;
        this.dist=dist;
        this.stop = false;
        this.predictionAlgorithm = predictionAlgorithm;
        this.initial_population = new ArrayList<Chromosome>();
        this.work_population = new ArrayList<Chromosome>();
        this.best_population = new ArrayList<Chromosome>();
    }


    //**************Genetic Algorithm*******************
    public abstract void generatePopulation();
    public abstract void generatePopulationRandom();

    public void fitnessInitialPopulation() {

        ArrayList<Chromosome> compare_population = new ArrayList<Chromosome>();

        for(int i = 0; i < this.initial_population.size(); i++) {
            compare_population.add(this.initial_population.get(i));
        }


        Collections.sort(compare_population, new Comparator<Chromosome>() {

            @Override
            public int compare(Chromosome t, Chromosome t1) {
                return Double.compare(t.getSmape(), t1.getSmape());
            }

        });

        for(int i = 0; i < NUM_WORK_POPULATION; i++) {
            this.work_population.add(compare_population.get(i));
        }

        for(int i = 0; i < this.work_population.size(); i++) {
            if (this.work_population.get(i).getBueno()) {
                this.stop = true;

            }
        }

        if (this.stop) {
            this.best_population.add(0, this.work_population.get(0));
        }
    }
    public void fitness(){
        Collections.sort(this.work_population, new Comparator<Chromosome>() {

            @Override
            public int compare(Chromosome t, Chromosome t1) {
                return Double.compare(t.getSmape(), t1.getSmape());
            }

        });

        for(int i = 0; i < this.work_population.size(); i++) {
            if (this.work_population.get(i).getBueno()) {
                this.stop = true;

            }
        }

        if (!this.best_population.isEmpty()) {
            int i = this.best_population.size();
            Double antiguo =  this.best_population.get(i - 1).getSmape();
            Double nuevo = this.work_population.get(0).getSmape();
            while ( antiguo > nuevo  && i != 0) {

                i--;
                if (i > 0) antiguo =  this.best_population.get(i - 1).getSmape();
            }

            this.best_population.add(i, this.work_population.get(0));
        } else {
            this.best_population.add(0, this.work_population.get(0));
        }
    }

    /*
    Cruce en n puntos [n-point crossover]
        Se eligen n puntos de cruce aleatoriamente.
        Se fragmentan los cromosomas en esos puntos.
        Se juntan fragmentos, alternando los padres.
    */
    public void crossover() {
        //Número de permutaciones.
        //int n = (int) Math.floor(Math.random()*(this.numArguments - 1) + 1);
        int n = 1;
        int numC1;
        int numC2;
        int pos;
        Chromosome C1,C2,auxC1,auxC2;
        //Chromosome C2;
        int[] posArray = new int[n];
        for(int l=0; l<n; l++){
            posArray[l]=-1;
        }
        int j = 0;
//       int h = 0;

        ArrayList<Chromosome> new_population = new ArrayList<Chromosome>();

        while (!this.work_population.isEmpty())  {

            numC1 = (int) Math.floor(Math.random()*this.work_population.size());
            numC2 = (int) Math.floor(Math.random()*this.work_population.size());

            while (numC1 == numC2) {
                numC1 = (int) Math.floor(Math.random()*this.work_population.size());
                numC2 = (int) Math.floor(Math.random()*this.work_population.size());
            }
//          System.out.println("--------------------ARRAYLIST INFO----------------------");
//          System.out.println("NUMC1: " + numC1);
//          System.out.println("NUMC2: " + numC2);
//          for(int x=0;x<this.work_population.size();x++) {
//                 System.out.println( x + ":" + this.work_population.get(x).toString());
//            }
//          System.out.println("------------------------------------------");

            C1 = this.work_population.get(numC1);
            C2 = this.work_population.get(numC2);
            auxC1 = (Chromosome) C1.clone();
            auxC2 = (Chromosome) C2.clone();

            this.work_population.remove(numC1);
            if (numC1 > numC2) {
                this.work_population.remove(numC2);
            } else {
                this.work_population.remove(numC2 - 1);
            }



//
//        for(int i = 0; i < this.work_population.size(); i++) {
//            compare_population.add(this.work_population.get(i));
//        }


            while (j < n) {
                pos = (int) Math.floor(Math.random()*this.numArguments);



                while (comprobarIntEnArray(posArray, pos)) {
                    pos = (int) Math.floor(Math.random()*this.numArguments);
                }


                posArray[j] = pos;


                auxC1.crossover(C2,pos);
                auxC2.crossover(C1,pos);

                j++;
            }

            //auxC1.generateChromosome();
            //auxC2.generateChromosome();

            new_population.add(auxC1);
            new_population.add(auxC2);
//          System.out.println("------------------"+ h +"------------------------");
//          System.out.println(C1.toString() + "-----" + auxC1.toString());
//          System.out.println(C2.toString() + "-----" + auxC2.toString());
//          System.out.println("------------------------------------------");
//          h++;
        }

        for(int i = 0; i < new_population.size(); i++) {
            this.work_population.add(new_population.get(i));
        }


    }
    public void mutate() {
        int n  = 0;
        Double value;

        for(int i = 0; i < this.work_population.size(); i++) {
            n = (int) Math.floor(Math.random()*this.numArguments);
            value = Math.random()*1;
            this.work_population.get(i).setValueAttibute(n, value);
            this.work_population.get(i).generateChromosome();
        }

    }

    //**************Getters and Setters*******************
    public Boolean getStop() {
        return this.stop;
    }

    //**************Others Methods*******************
    @Override
    public String toString() {

        String concatenar = "";
//
//        concatenar += "********************Initial Population********************\n";
//        for (int i = 0; i < this.best_population.size(); i++ ) {
//            concatenar += i + ": "+ this.best_population.get(i).toString() + "\n";
//       }
        //concatenar += "****************************************";
        concatenar += "********************Work Population********************\n";
        for (int i = 0; i < this.work_population.size(); i++ ) {
            concatenar += i + ": "+ this.work_population.get(i).toString() + "\n";
        }
        concatenar += "****************************************";
        return concatenar;
    }

    public String toStringFinal(){
        String concatenar = "****************************************\n";
        concatenar += "RESUME\n";
        concatenar += "The Best Parameters: "+ this.best_population.get(0).toString() +"\n";
        concatenar += "****************************************\n";
        concatenar += "********************Best Population********************\n";
        for (int i = 0; i < this.best_population.size(); i++ ) {
            concatenar += i + ": "+ this.best_population.get(i).toString() + "\n";
        }
        concatenar += "****************************************";
        concatenar += "********************Work Final Population (Última vuelta)********************\n";
        for (int i = 0; i < this.work_population.size(); i++ ) {
            concatenar += i + ": "+ this.work_population.get(i).toString() + "\n";
        }
        concatenar += "****************************************\n";
        return concatenar;

    }

    // Get best Chromosome
    public Chromosome getBestParams(){

        return this.best_population.get(0);
    }

    public Boolean comprobarIntEnArray(int[] array, int comprobar){

        Boolean ok = false;

        for(int k = 0; k < array.length; k++){
            if (array[k] == comprobar) ok = true;
        }

        return ok;

    }

//    public ArrayList<Chromosome> getBestPopulation(){
//        return this.best_population;
//    }
//
//    public void recycledPopulation(ArrayList<Chromosome> best_population){
//        for(int i = 0; i < best_population.size(); i++) {
//
//            this.initial_population.add(best_population.get(i));
//        }
//    }
}
