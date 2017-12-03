package com.example.droidsentinel.droidsentinel;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.v4.app.NotificationCompat;
import android.widget.TextView;

import com.example.droidsentinel.droidsentinel.Algorithms.ARIMA;
import com.example.droidsentinel.droidsentinel.Algorithms.DoubleMovingAverage;
import com.example.droidsentinel.droidsentinel.Algorithms.TripleExpSmoothingAdd;
import com.example.droidsentinel.droidsentinel.Algorithms.TripleExpSmoothingMul;
import com.example.droidsentinel.droidsentinel.Algorithms.WeightedMovingAverage;
import com.example.droidsentinel.droidsentinel.GeneticAlgorithm.Chromosome.Chromosome;
import com.example.droidsentinel.droidsentinel.GeneticAlgorithm.GeneticCalibrator;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import static android.content.Context.NOTIFICATION_SERVICE;

/**
 * Created by blorenzo on 21/11/2017.
 */

public class LogTask extends AsyncTask<Void, String, Boolean> {

    private static List<Double> ts;
    private int THRESHOLD = 20;
    private static List<Double> FORECASTED;
    private static int WINDOW_LEN;
    private int MAXTS = 52;
    private int READYFORECAST = 20;
    private static LogAgent agent;

    private ArrayList<Chromosome> best_population;

    private TextView consola;
    private String log;
    private Context contexto;
    private Thread t;

    //archivo en el que guardamos los paquetes analizados
    public final static String NAME_LOG = "tcpdump.log";
    //Ruta donde guardamos el archivo
    public final static String RUTA = "/sdcard/tcpdump.log";

    public LogTask(Context contexto, TextView consola, String log, int threshold, int window_len,int maxts,int readyforescast) {
        super();
        this.consola = consola;
        this.log = log;
        this.contexto = contexto;
        this.THRESHOLD = threshold;
        this.WINDOW_LEN = window_len;
        this.MAXTS = maxts;
        this.READYFORECAST = readyforescast;
    }


    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        consola.setText("");
        log = "";

    }

    @Override
    protected Boolean doInBackground(Void... voids) {

        //publishProgress("-->[" + this.THRESHOLD + "," + this.WINDOW_LEN+ "," + this.MAXTS+ "," + this.READYFORECAST + "]");

        publishProgress(start());

        if (isCancelled()) {
            return false;
        }

        showNotification();
        return true;
    }

    @Override
    protected void onProgressUpdate(String... values) {
        super.onProgressUpdate(values);
        log += values[0];

        consola.setText(log);
    }

    @Override
    protected void onPostExecute(Boolean ok) {
        super.onPostExecute(ok);
        publishProgress("c'est fini!\n");
    }

    @Override
    protected void onCancelled() {
        super.onCancelled();
        consola.setText(stop());
    }

/* Método que muestra notificaciones al usuario tras ejecutar el programa y haver analizado y predicho los
    distintos poquetes salientes*/
    public void showNotification(){
        NotificationCompat.Builder mBuilder;
        NotificationManager mNotifyMgr =(NotificationManager) contexto.getApplicationContext().getSystemService(NOTIFICATION_SERVICE);
        int icono = R.mipmap.ic_launcher;
        Intent intent = new Intent(contexto, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(contexto, 0,intent, 0);
        mBuilder =new NotificationCompat.Builder(contexto.getApplicationContext())
                .setContentIntent(pendingIntent)
                .setSmallIcon(icono)
                .setContentTitle("Warning!")
                .setContentText("Eres parte de una BOTNET!")
                .setVibrate(new long[] {100, 250, 100, 500})
                .setAutoCancel(true);
        mNotifyMgr.notify(1, mBuilder.build());
    }



//--------------------------------------------------------------------------------------------------------------------------------------

    /* Método que inicia el programa, mediante la que iniciamos nuestro software para al análisis de paquetes que es el  tcpdump.
        Tcpdump es una herramienta para línea de comandos cuya utilidad principal es analizar el tráfico que circula por la red.
        Permite al usuario capturar y mostrar en tiempo real los paquetes transmitidos y recibidos por la red a la cual el ordenador 
        está conectado*/
    public String start(){
        publishProgress("Se ha creado el archivo"+ NAME_LOG + "" + this.RUTA + "\n");
        crearTcpdump();

        try {
            Thread.sleep(5100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        ts = new ArrayList<Double>();

        try {
            agent = new LogAgent(NAME_LOG);
        } catch (Exception e) {
        }

//        GeneticCalibrator initial = new GeneticCalibrator();
//        WINDOW_LEN = initial.get_window();
        WINDOW_LEN = 5;
        FORECASTED = new ArrayList<Double>(WINDOW_LEN);

        for(int h=0; h < WINDOW_LEN; h++){
            FORECASTED.add(-1.0);
        }

        double val = 0.0;

        int cont = 0;
        while(val != -1.0) {
            try {
                val = forecastNext();
//                publishProgress("" + val + "\n");
                publishProgress("Analizando datos (" + cont + ")...\n");
                cont ++;
                t.interrupt();
                crearTcpdump();
                Thread.sleep(5100);
            } catch (InterruptedException e){
                break;
            }

        }

        return "The application it's already begun!\n";
    }

    /* Método que para el programa y que borra la ruta del del archivos.
    Para ello le damos permisos de superusuario ya que sino el borrado no se produce porque no tiene suficientes privilegios */
    public String stop(){

        t.interrupt();
        String sedCMD = "rm -rf " + this.RUTA;
        try {
            Runtime.getRuntime().exec(new String[]{"su", "-c", sedCMD});
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "The application was stopped!";
    }

    /* Crea el tcpdump y muestra al usuario la carpeta donde se ha guardado el archivo.
    Para ello, ejecuta los privilegios de superusuario y le da permisos al tcpdump.
    Tras iniciar el tcpdump, lo guardamos en la tarjeta SD con el nombre definido en RUTA*/
    public Boolean crearTcpdump(){

        try {
            Runtime.getRuntime().exec(new String[]{"su", "-c", "chmod 777 /system/bin/tcpdump"});
        } catch (IOException e) {
            e.printStackTrace();
        }
        t = new Thread(new Runnable() {
            @Override
            public void run() {
                RunCommand(new String[]{"su", "-c", "/system/bin/tcpdump -n -tt -i any >> /sdcard/tcpdump.log"});
            }

        });
        t.start();

        return true;
    }

    /* Lee el archivo NAME_LOG de la tarjeta SD.
       Para que no se sature el dispositivo, vamos borrando distintos datos.*/
    public Boolean leerArchivo(){

        String text = "";
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
        String line;
        for (int x=0; x < 10; x++){
            try {
                if ((line = br.readLine()) != null) {
                    text += line + "\r\n ";
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try {
            in.close();
            int nVueltas = 10;
            String sedCMD = "busybox sed -i 1," + nVueltas + "d " + "/sdcard/"+NAME_LOG;
            Runtime.getRuntime().exec(new String[]{"su", "-c", sedCMD});
        } catch (IOException e) {
            e.printStackTrace();
        }

        publishProgress(text);
        return true;
    }

    /* Método que utilizamos para poder utilizar los distintos comandos de Linux de nuestro dispositivos mediante la terminal. */
    String RunCommand(String[] cmd) {
        StringBuffer cmdOut = new StringBuffer();
        Process process;
        try {
            process = Runtime.getRuntime().exec(cmd);
            InputStreamReader r = new InputStreamReader(process.getInputStream());
            BufferedReader bufReader = new BufferedReader(r);
            char[] buf = new char[4096];
            int nRead = 0;
            while ((nRead = bufReader.read(buf)) > 0) {
                cmdOut.append(buf, 0, nRead);
            }
            bufReader.close();
            try {
                process.waitFor();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return cmdOut.toString();
    }

    // ---------------------------------------- ALGORITHMS ---------------------------------------------
    public double forecastNext() throws InterruptedException{

        double new_value = 0.0;

        try {
            new_value = agent.getNext();
        } catch (IOException e){

        }


        if(ts.size() >= MAXTS)
            ts.remove(0);
        ts.add(new_value);

        // We can call ts Forecast
        if (ts.size() > READYFORECAST){


//            GeneticCalibrator calibrator = new GeneticCalibrator(ts);
//            Chromosome chro = calibrator.calibrate();
            System.out.println(ts);
            boolean debug = false;
            int period = WINDOW_LEN;

//           List<Double> new_list = TripleExpSmoothingAdd.forecast(ts, chro.getValueAttibute(0),
//                    chro.getValueAttibute(1), chro.getValueAttibute(2), period, WINDOW_LEN, debug);
            List<Double> new_list = HoltWintersMul();

//            List<Double> new_list = calculateArima();
            System.out.println(new_list);
//
            if(FORECASTED.get(0) != -1){
                if(Math.abs(FORECASTED.get(0) - new_value) > (double)THRESHOLD /100){
                    System.out.println("Abnormal activity");
                    publishProgress("Abnormal Activity");
                }
            }
//            List<Double> new_list = ts;

            FORECASTED.add(new_list.get(new_list.size() - 1));
            FORECASTED.remove(0);

            publishProgress("" + new_list.get(new_list.size()-1) + "\n");
        }
        return new_value;
    }

    public List<Double> HoltWintersMul(){

        List<Double> tsCloneABS = ((List) ((ArrayList) ts).clone());
        List<Double> tsFt = new ArrayList();
        List<Double> tsClone = ((List) ((ArrayList) tsCloneABS).clone());


        //if (this.best_population == null) {
        GeneticCalibrator calibrator = calibrator = new GeneticCalibrator(ts);
        //} else calibrator = new GeneticCalibrator(ts,true);


        Chromosome chro = calibrator.calibrate();
        List<ConPrediction> AlgTests2 = new ArrayList();

        //this.best_population = calibrator.getBestPopulation();

        double aCoef = chro.getValueAttibute(0);
        double bCoef = chro.getValueAttibute(1);
        double gCoef = chro.getValueAttibute(2);

        List<Double> slist = (List<Double>) tsClone.subList(0,ts.size() - WINDOW_LEN);
        tsFt= TripleExpSmoothingMul.forecast(slist, aCoef, bCoef, gCoef, WINDOW_LEN, WINDOW_LEN, false);

        ConPrediction auxCon2 = new ConPrediction();
        auxCon2.setIdAlgorithm("Multiplicative Holt-Winters (triple exponential smoothing)");
        auxCon2.algParams.put("alpha",aCoef);
        auxCon2.algParams.put("beta",bCoef);
        auxCon2.algParams.put("gamma",gCoef);
        auxCon2.algParams.put("period",(double)WINDOW_LEN);
        auxCon2.setTs(ts);
        auxCon2.setFt(((List) ((ArrayList) tsFt).clone()));
        auxCon2.calculateSMAPE();
        auxCon2.smape = auxCon2.getAvgSmapeLastN(WINDOW_LEN);
        AlgTests2.add(auxCon2);

        //tsCloneABS = ((List) ((ArrayList) ts).clone());
        return tsFt;
    }



    public static ConPrediction getBestPredAlg(List<ConPrediction> AlgTests){
        double min_smape = Double.MAX_VALUE;
        ConPrediction minConPred = new ConPrediction();

        Iterator it = AlgTests.iterator();
        while (it.hasNext()) {
            ConPrediction pred = (ConPrediction)it.next();

            if(pred.smape < min_smape){
                min_smape = pred.smape;
                minConPred = pred;
            }
        }
        return minConPred;
    }


}
