package com.example.droidsentinel.droidsentinel;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.v4.app.NotificationCompat;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;

import static android.content.Context.NOTIFICATION_SERVICE;

/**
 * Created by blorenzo on 21/11/2017.
 */

public class LogTask extends AsyncTask<Void, String, Boolean> {


    private TextView consola;
    private String log;
    private Context contexto;
    private Thread t;

    public final static String NAME_LOG = "tcpdump.log";
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

    public String start(){

        crearTcpdump();

        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        leerArchivo();


        return "The application it's already begun!\n";
    }

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

}
