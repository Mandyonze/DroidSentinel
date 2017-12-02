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
    private static final int THRESHOLD = 20;
    private static List<Double> FORECASTED;
    private static int WINDOW_LEN;
    private static final int MAXTS = 52;
    private static final int READYFORECAST = 50;
    private static LogAgent agent;

    private TextView consola;
    private String log;
    private Context contexto;
    private Thread t;

    //archivo en el que guardamos los paquetes analizados
    public final static String NAME_LOG = "tcpdump.log";
    //Ruta donde guardamos el archivo
    public final static String RUTA = "/sdcard/tcpdump.log";

    public LogTask(Context contexto, TextView consola, String log) {
        super();
        this.consola = consola;
        this.log = log;
        this.contexto = contexto;


    }


    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        consola.setText("");
        log = "";

    }

    @Override
    protected Boolean doInBackground(Void... voids) {

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
                publishProgress("Estoy dando vueltas, espera" + cont + "\n");
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
        publishProgress("Se ha creado el archivo"+ NAME_LOG + "" + this.RUTA + "\n");

        try {
            Runtime.getRuntime().exec(new String[]{"su", "-c", "chmod 777 /system/bin/tcpdump"});
        } catch (IOException e) {
            e.printStackTrace();
        }
        t = new Thread(new Runnable() {
            @Override
            public void run() {
                RunCommand(new String[]{"su", "-c", "/system/bin/tcpdump -n -tt -i any > /sdcard/tcpdump.log"});
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
                }
            }
//            List<Double> new_list = ts;

            FORECASTED.add(new_list.get(new_list.size() - 1));
            FORECASTED.remove(0);

            publishProgress("" + new_list.get(new_list.size()-1) + "\n");
        }
        return new_value;
    }

    public static List<Double> calculateMHW(){
        List<Double> tsCloneABS = ((List) ((ArrayList) ts).clone());
        List<Double> tsFt = new ArrayList();
        List<Double> tsClone = ((List) ((ArrayList) tsCloneABS).clone());
        //Add as many coeficients as needed
        List<Double> alphaAHTES = new ArrayList();
        for(int i=0;i<9;i=i+2) alphaAHTES.add(0.1*(i+1));

        List<Double> betaAHTES = new ArrayList();
        for(int i=0;i<9;i=i+2) betaAHTES.add(0.1*(i+1));

        List<Double> gammaAHTES = new ArrayList();
        for(int i=0;i<9;i=i+2) gammaAHTES.add(0.1*(i+1));

        List<ConPrediction> AlgTests1 = new ArrayList();
        Iterator it = alphaAHTES.iterator();

        List<Double> slist = (List<Double>) tsClone.subList(0,ts.size() - WINDOW_LEN);
        tsFt= TripleExpSmoothingAdd.forecast(slist, 0.7, 0.7, 0.7, WINDOW_LEN, WINDOW_LEN, false);
        ConPrediction auxCon = new ConPrediction();
        auxCon.setIdAlgorithm("Additive Holt-Winters (triple exponential smoothing)");
        auxCon.algParams.put("period",(double)WINDOW_LEN);
        auxCon.setTs(ts);
        auxCon.setFt(((List) ((ArrayList) tsFt).clone()));
        //auxCon.calculateErrors();
        //auxCon.calculateMAWeightedError(dist);  //change formula
        //auxCon.calculateSMAPESmoothing(dist,period);
        //auxCon.calculateSMAPE();
        //auxCon.smape = auxCon.getAvgSmapeLastN(WINDOW_LEN);
        //AlgTests1.add(auxCon);


        /*while(it.hasNext()){
            double al = (Double) it.next();
            Iterator itBett = betaAHTES.iterator();
            while(itBett.hasNext()){
                double be = (Double) itBett.next();
                Iterator itGamt = gammaAHTES.iterator();
                while(itGamt.hasNext()){
                    double ga = (Double) itGamt.next();
                    List<Double> slist = (List<Double>) tsClone.subList(0,ts.size() - WINDOW_LEN);
                    tsFt= TripleExpSmoothingAdd.forecast(slist, al, be, ga, WINDOW_LEN, WINDOW_LEN, false);
                    ConPrediction auxCon = new ConPrediction();
                    auxCon.setIdAlgorithm("Additive Holt-Winters (triple exponential smoothing)");
                    auxCon.algParams.put("alpha",al);
                    auxCon.algParams.put("beta",be);
                    auxCon.algParams.put("gamma",ga);
                    auxCon.algParams.put("period",(double)WINDOW_LEN);
                    auxCon.setTs(ts);
                    auxCon.setFt(((List) ((ArrayList) tsFt).clone()));
                    //auxCon.calculateErrors();
                    //auxCon.calculateMAWeightedError(dist);  //change formula
                    //auxCon.calculateSMAPESmoothing(dist,period);
                    auxCon.calculateSMAPE();
                    auxCon.smape = auxCon.getAvgSmapeLastN(WINDOW_LEN);
                    AlgTests1.add(auxCon);
                }
            }
        }*/
        //this.TSAllPredictions.put("Additive Holt-Winters (triple exponential smoothing)", AlgTests1);
        ConPrediction bestConPred1 = new ConPrediction();
        bestConPred1 = getBestPredAlg(AlgTests1);

        return tsFt;
    }

    public static List<Double> HoltWintersMul(){

        List<Double> tsCloneABS = ((List) ((ArrayList) ts).clone());
        List<Double> tsFt = new ArrayList();
        List<Double> tsClone = ((List) ((ArrayList) tsCloneABS).clone());

        //Add as many coeficients as needed
        List<Double> alphaHTES = new ArrayList();
        for(int i=0;i<9;i=i+2) alphaHTES.add(0.1*(i+1));

        List<Double> betaHTES = new ArrayList();
        for(int i=0;i<9;i=i+2) betaHTES.add(0.1*(i+1));

        List<Double> gammaHTES = new ArrayList();
        for(int i=0;i<9;i=i+2) gammaHTES.add(0.1*(i+1));

        List<ConPrediction> AlgTests2 = new ArrayList();
        Iterator itAlpht = alphaHTES.iterator();

        while(itAlpht.hasNext()){
            double aCoef = (Double) itAlpht.next();
            Iterator itBett = betaHTES.iterator();
            while(itBett.hasNext()){
                double bCoef = (Double) itBett.next();
                Iterator itGamt = gammaHTES.iterator();
                while(itGamt.hasNext()){
                    double gCoef = (Double) itGamt.next();
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
                    //auxCon2.calculateErrors();
                    //auxCon2.calculateMAWeightedError(dist);  //change formula
                    //auxCon2.calculateSMAPESmoothing(dist,period);
                    auxCon2.calculateSMAPE();
                    auxCon2.smape = auxCon2.getAvgSmapeLastN(WINDOW_LEN);
                    AlgTests2.add(auxCon2);
                }
            }
        }
        //this.TSAllPredictions.put("Multiplicative Holt-Winters (triple exponential smoothing)", AlgTests2);
        ConPrediction bestConPred2 = new ConPrediction();
        bestConPred2 = getBestPredAlg(AlgTests2);
        System.out.println("El SMAPE es: " + bestConPred2.smape);

        tsCloneABS = ((List) ((ArrayList) ts).clone());
        return TripleExpSmoothingMul.forecast(tsCloneABS, bestConPred2.algParams.get("alpha"),
                bestConPred2.algParams.get("beta"), bestConPred2.algParams.get("gamma"), WINDOW_LEN, WINDOW_LEN, false);
    }

    public static List<Double> DoubleMovingAVG(){

        List<Double> tsCloneABS = ((List) ((ArrayList) ts).clone());
        List<Double> tsFt = new ArrayList();
        List<Double> tsClone = ((List) ((ArrayList) tsCloneABS).clone());

        int [] lagSizeDMA = new int[9];
        int dmaLength = tsClone.size();
        double lagDMA;
        for(int i=0;i<9;i++){
            lagDMA = dmaLength*0.1*(i+1);
            lagSizeDMA[i]= (int) lagDMA;
        }
        List<ConPrediction> AlgTests9 = new ArrayList();

        for(int i=0;i<5;i++){
            DoubleMovingAverage auxDSMA= new DoubleMovingAverage(lagSizeDMA[i]);
            List<Double> slist = (List<Double>) tsClone.subList(0,ts.size() - WINDOW_LEN);
            tsFt = auxDSMA.getAllPredictions(slist,WINDOW_LEN);

            ConPrediction auxCon9 = new ConPrediction();
            auxCon9.setIdAlgorithm("Double Moving Average");
            auxCon9.algParams.put("lag",(double) lagSizeDMA[i]);
            auxCon9.setTs(ts);
            auxCon9.setFt(((List) ((ArrayList) tsFt).clone()));
            //auxCon9.calculateErrors();
            //auxCon9.calculateMAWeightedError(dist);
            auxCon9.calculateSMAPE();
            auxCon9.smape = auxCon9.getAvgSmapeLastN(WINDOW_LEN);
            AlgTests9.add(auxCon9);
        }
        //this.TSAllPredictions.put("Double Moving Average", AlgTests9);
        ConPrediction bestConPred9 = new ConPrediction();
        bestConPred9 = getBestPredAlg(AlgTests9);

        return bestConPred9.ts;
    }

    public static List<Double> WeigthedMovingAVG(){

        List<Double> tsCloneABS = ((List) ((ArrayList) ts).clone());
        List<Double> tsFt = new ArrayList();
        List<Double> tsClone = ((List) ((ArrayList) tsCloneABS).clone());

        WeightedMovingAverage auxWMA= new WeightedMovingAverage();

        List<Double> slistWMA = (List<Double>) tsClone.subList(0,ts.size() - WINDOW_LEN);
        tsFt = auxWMA.getAllPredictions(slistWMA,WINDOW_LEN);

        ConPrediction auxCon7 = new ConPrediction();
        auxCon7.setIdAlgorithm("Weighted Moving Average");
        auxCon7.setTs(ts);
        auxCon7.setFt(((List) ((ArrayList) tsFt).clone()));
        //auxCon7.calculateErrors();
        //auxCon7.calculateMAWeightedError(dist);
        auxCon7.calculateSMAPE();
        auxCon7.smape = auxCon7.getAvgSmapeLastN(WINDOW_LEN);
        List<ConPrediction> AlgTests7 = new ArrayList();
        AlgTests7.add(auxCon7);

        return tsFt;
    }

    public static List<Double> calculateArima(){

        int p = 1;
        int d = 0;
        int q = 0;
        //List<Double> tsFtAux = new ArrayList();

        List<Double> tsFt = new ArrayList();
        List<Double> tsClone = ((List) ((ArrayList) ts).clone());
        List<ConPrediction> AlgTests5 = new ArrayList();

        for(int i=0;i<2; i++){

            //List<Double> tsFtAux = new ArrayList();
            List<Double> slistARIMA = (List<Double>) tsClone.subList(0,ts.size() - WINDOW_LEN);
            p = i +1;
            d = i;
            q = i +2;
            ARIMA auxARIMA = new ARIMA(slistARIMA,p,d,q);

            tsFt = auxARIMA.getAllPredictions(WINDOW_LEN);

            ConPrediction auxCon5 = new ConPrediction();
            auxCon5.setIdAlgorithm("ARIMA");
            auxCon5.algParams.put("p", (double) p);
            auxCon5.algParams.put("d", (double) d);
            auxCon5.algParams.put("q", (double) q);

            auxCon5.setTs(ts);
            auxCon5.setFt(((List) ((ArrayList) tsFt).clone()));
            //auxCon5.calculateErrors();
            //auxCon5.calculateMAWeightedError(dist);  //change formula

            auxCon5.calculateSMAPE();
            auxCon5.smape = auxCon5.getAvgSmapeLastN(WINDOW_LEN);
            //System.out.println("8888888888888888888888888888888888");
            //System.out.println(auxCon5.getSmape());
            //System.out.println("8888888888888888888888888888888888");
            AlgTests5.add(auxCon5);
        }
        ConPrediction bestConPred5 = new ConPrediction();
        bestConPred5 = getBestPredAlg(AlgTests5);
//        System.out.println("El SMAPE es: " + bestConPred5.smape);
        return bestConPred5.getFt();
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
