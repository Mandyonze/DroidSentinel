package com.example.droidsentinel.droidsentinel;

import android.app.Activity;
import android.os.Environment;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;


public class LogAgent{

    LogTask task;

    public LogAgent(LogTask task){
        this.task = task;
        this.task.onProgressUpdate("The application it's inicialized!");

    }
}


//------------------------------------------------------------------------------------------------------------------------------------------------
//public class LogAgent {
//
//
//    public void aux(){
//
//
//        //Se crea el archivo tcmpdump.log y se jecuta el comando
//        try {
//            Runtime.getRuntime().exec(new String[]{"su", "-c", "chmod 777 /system/bin/tcpdump"});
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        Thread t = new Thread(new Runnable() {
//            @Override
//            public void run() {
//                RunCommand(new String[]{"su", "-c", "/system/bin/tcpdump -n -tt -i any > /sdcard/tcpdump.log"});
//            }
//        });
//        t.start();
//
//        String ok = leerFicheroMemoriaInterna();
//        publishProgress(ok);
//
//
//        if (isCancelled()) {
//            RunCommand(new String[]{"su", "-c", "rm -rf /sdcard/tcpdump.log"});
//            return true;
//        }
//
//        return true;
//    }
//
//    public String leerFicheroMemoriaInterna()
//    {
//        String ok = "";
//        try
//        {
//            File ruta_sd = Environment.getExternalStorageDirectory();
//
//            File f = new File(ruta_sd, "tcpdump.log");
//
//            BufferedReader fin = new BufferedReader(new InputStreamReader(new FileInputStream(f)));
//
//
//            for (int i = 0; i < 100; i++) {
//                ok += fin.readLine()+"\n";
//            }
//
//            fin.close();
//        }
//        catch (Exception ex)
//        {
//            ok = "Error al leer fichero desde tarjeta SD";
//        }
//
//        return ok;
//    }
//
//
//    String RunCommand(String[] cmd) {
//        StringBuffer cmdOut = new StringBuffer();
//        Process process;
//        try {
//            process = Runtime.getRuntime().exec(cmd);
//            InputStreamReader r = new InputStreamReader(process.getInputStream());
//            BufferedReader bufReader = new BufferedReader(r);
//            char[] buf = new char[4096];
//            int nRead = 0;
//            while ((nRead = bufReader.read(buf)) > 0) {
//                cmdOut.append(buf, 0, nRead);
//            }
//            bufReader.close();
//            try {
//                process.waitFor();
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//        return cmdOut.toString();
//    }
//}



//------------------------------------------------------------------------------------------------------------------------------------------------

//