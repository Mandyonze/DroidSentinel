package com.example.droidsentinel.droidsentinel;

import android.os.Environment;

import java.io.*;
import java.nio.file.Paths;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeoutException;

import static com.example.droidsentinel.droidsentinel.EntropyCalc.calculateEntropySelf;
import static com.example.droidsentinel.droidsentinel.MainActivity.RunCommand;

public class LogAgent {

    private static final double TIMECMP = 5.0;
    private static FileInputStream fstream;
    private static int contt;
    private static BufferedReader reader;
    private static String NAME_LOG;
    //    private static HashMap<String, Double> petitions;
    private static double firstTime;
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss.SSSSSSS");

//    private static final String LOG_FILE
//            = Paths.get(Paths.get("").toAbsolutePath().toString(),
//            "src", "main","resources", "logNewTimeStamp.log").toAbsolutePath().toString();

    public LogAgent(String log_name){

//        try {
//            fstream = new FileInputStream(LOG_FILE);
//            reader = new BufferedReader(new InputStreamReader(fstream));
//        } catch (Exception e) {
//            System.err.println("Error: " + e.getMessage());
//        }
        NAME_LOG = log_name;
        contt = 0;
    }

    public double getNext() throws IOException {

        ArrayList<Double> myArray = new ArrayList<Double>();
        double[] toEntropy;
        String[] data;
        int nVueltas = 0;

        double thisEntropy = -1;
        String strLine;
        double nowTimeStump = 0.0;
        int cont = 0;

        HashMap<String, Double> petitions = new HashMap<String, Double>();

        File sdcard = Environment.getExternalStorageDirectory();
        File file = new File(sdcard,NAME_LOG);
        FileReader in = null;
        String filePath = sdcard.getAbsolutePath() + "/" + NAME_LOG;
        try {
            in = new FileReader(filePath);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        BufferedReader br = new BufferedReader(in);

        while ((strLine = br.readLine()) != null) {
            try {
                data = strLine.split(" ");
                try {
                    nowTimeStump = Double.parseDouble(data[0]);
                }catch (Exception e){

                }

                nVueltas+=1;
                if (cont == 0) {
                    firstTime = nowTimeStump + TIMECMP;
                    cont++;
                }

                if (data.length < 4)
                    continue;
                String dir_now = data[2] + "::" + data[4];

                if (nowTimeStump <= firstTime) {
                    if (!petitions.containsKey(dir_now)) {
                        petitions.put(dir_now, 1.0);
                    } else {
                        double num_pack = petitions.get(dir_now);
                        petitions.put(dir_now, num_pack + 1.0);
                    }
                } else {
                    //Call method te get entropy and add to TimeSeries
                    myArray.clear();
                    double maxPetitions = 0.0;
                    for (String key : petitions.keySet()) {
                        myArray.add(petitions.get(key));
                        maxPetitions += petitions.get(key);
                    }

                    toEntropy = convertDoubles(myArray);
                    thisEntropy = calculateEntropySelf(toEntropy, maxPetitions);
//                    thisEntropy = maxPetitions;  // En este caso, numero máximo de peticiones

                    //Reset HashMap for next batch
                    petitions.clear();

                    break;
                }
                Arrays.fill(data, null);
                //}
            } catch (NullPointerException e) {
                System.out.println(e);
            }
        }
        if (petitions.size() > 0){
            //Call method te get entropy and add to TimeSeries
            myArray.clear();
            double maxPetitions = 0.0;
            for (String key : petitions.keySet()) {
                myArray.add(petitions.get(key));
                maxPetitions += petitions.get(key);
            }

            toEntropy = convertDoubles(myArray);
            thisEntropy = calculateEntropySelf(toEntropy, maxPetitions);
            //                    thisEntropy = maxPetitions;  // En este caso, numero máximo de peticiones

            //Reset HashMap for next batch
            petitions.clear();
        }


        try {
            in.close();

            String sedCMD = "busybox sed -i 1," + nVueltas + "d " + "/sdcard/"+NAME_LOG;
            Runtime.getRuntime().exec(new String[]{"su", "-c", sedCMD});
        } catch (IOException e) {
            e.printStackTrace();
        }
        return thisEntropy;
    }

    public static double[] convertDoubles(List<Double> doubles){

        double[] ret = new double[doubles.size()];
        Iterator<Double> iterator = doubles.iterator();
        int i = 0;
        while(iterator.hasNext())
        {
            ret[i] = iterator.next();
            i++;
        }
        return ret;
    }
}