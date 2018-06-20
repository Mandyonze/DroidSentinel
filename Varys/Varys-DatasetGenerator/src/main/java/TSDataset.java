
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Date;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Iterator;
import org.pcap4j.packet.IllegalRawDataException;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Alkahest
 */
public class TSDataset {
    
    String LocalIP;
    double alpha;
    int granularity;//milliseconds
    boolean debug;
    String SampleFolder;
    String reportFolder;
    java.sql.Date refDate;
    
    //TimeStamps   
    ArrayList<java.sql.Date> listTimeStamps; //ArrayList timestamps obs
    ArrayList<Integer> listTotalInputPaquets;
    ArrayList<Integer> listTotalOutpuPaquets;
    
    ArrayList<Integer> listTotalInputBytes;
    ArrayList<Integer> listTotalOutpuBytes;
    ArrayList<Double> listEntropyDisributionInputPaquetsPerFlow;
    ArrayList<Double> listEntropyDisributionOutputPaquetsPerFlow;
    
    ArrayList<Double> listEntropyDisributionPaquetsPerFlow;
    ArrayList<Double> listEntropyDisributionInputBytesPerFlow;
    ArrayList<Double> listEntropyDisributionOutputBytesPerFlow;
    ArrayList<Double> listEntropyDisributionBytesPerFlow;
    ArrayList<Double> listdifInput_OutputPaquets;
    ArrayList<Double> listdifInput_OutputBytes;

    public TSDataset(String SampleFolder, String reportFolder, String LocalIP,double alpha,int granularity, boolean debug) {
        
        this.SampleFolder=SampleFolder;
        this.reportFolder=reportFolder;
        this.debug=debug;
        this.LocalIP=LocalIP;
        this.alpha=alpha;
        this.granularity=granularity;//milliseconds
        
    
        listTimeStamps=new ArrayList<java.sql.Date>();
        listTotalInputPaquets=new ArrayList<Integer>();
        listTotalOutpuPaquets=new ArrayList<Integer>();
        listTotalInputBytes=new ArrayList<Integer>();
        listTotalOutpuBytes=new ArrayList<Integer>();
        listEntropyDisributionInputPaquetsPerFlow=new ArrayList<Double>();
        listEntropyDisributionOutputPaquetsPerFlow=new ArrayList<Double>();
        listEntropyDisributionPaquetsPerFlow=new ArrayList<Double>();
        listEntropyDisributionInputBytesPerFlow=new ArrayList<Double>();
        listEntropyDisributionOutputBytesPerFlow=new ArrayList<Double>();
        listEntropyDisributionBytesPerFlow=new ArrayList<Double>();
        listdifInput_OutputPaquets=new ArrayList<Double>();
        listdifInput_OutputBytes=new ArrayList<Double>();
        this.refDate=null;
        
    }//constructor

    
    public void processSampleFolder() throws FileNotFoundException, IOException, ParseException, IllegalRawDataException{
     
        //List files in folder:
        ArrayList<String>  listReports= new ArrayList<>();
        try (DirectoryStream<Path> directoryStream = Files.newDirectoryStream(Paths.get(SampleFolder))) {
            for (Path path : directoryStream) {
                listReports.add(path.toString());
                //System.out.println(path.toString());
            }
        } catch (IOException ex){}
        listReports.sort(String::compareToIgnoreCase);
        
        // Open the file
        Iterator it = listReports.iterator();
        while(it.hasNext()){
                //String otherfile=fileDir+File.separator.toString()+it.next();
                String otherFile=(String)it.next();
                FileInputStream fstream = new FileInputStream(otherFile);
                BufferedReader br = new BufferedReader(new InputStreamReader(fstream));
                TSobservations obs=new TSobservations(refDate, refDate, LocalIP, alpha, debug);

                String strLine;
                //Read File Line By Line
                while ((strLine = br.readLine()) != null)   {
                  // Print the content on the console
                  String[] separated = strLine.split(" ");
                  String firsPartDate=separated[0];
                  String secondPartDate=separated[1];
                  String formatedDate=firsPartDate+" "+secondPartDate;
                  DateFormat simpleDateFormat=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                  java.util.Date timestampUtil = (java.util.Date) simpleDateFormat.parse(formatedDate);
                  java.sql.Date auxTimestamp= new java.sql.Date(timestampUtil.getTime());
                  String rest=separated[2];

                  if(this.refDate==null){
                      //its the first instance
                      refDate=auxTimestamp;
                      obs=new TSobservations(refDate,refDate,LocalIP, alpha, debug);
                      obs.processPacket(rest, debug);

                  }else{
                      if(auxTimestamp.getTime()<(refDate.getTime()+this.granularity)){
                          //in current obs
                          obs.setEnd(auxTimestamp);
                          obs.processPacket(rest, debug);
                      }else{
                          //in next obs
                          //Change of obs

                            obs.calulateAggregatedMetrics();
                            listTimeStamps.add(refDate);
                            listTotalInputPaquets.add(obs.getTotalInputPaquets());
                            listTotalOutpuPaquets.add(obs.getTotalOutpuPaquets());
                            listTotalInputBytes.add(obs.getTotalInputBytes());
                            listTotalOutpuBytes.add(obs.getTotalOutpuBytes());
                            listEntropyDisributionInputPaquetsPerFlow.add(obs.getEntropyDisributionInputPaquetsPerFlow());
                            listEntropyDisributionOutputPaquetsPerFlow.add(obs.getEntropyDisributionOutputPaquetsPerFlow());
                            listEntropyDisributionPaquetsPerFlow.add(obs.getEntropyDisributionPaquetsPerFlow());
                            listEntropyDisributionInputBytesPerFlow.add(obs.getEntropyDisributionInputBytesPerFlow());
                            listEntropyDisributionOutputBytesPerFlow.add(obs.getEntropyDisributionOutputBytesPerFlow());
                            listEntropyDisributionBytesPerFlow.add(obs.getEntropyDisributionBytesPerFlow());
                            listdifInput_OutputPaquets.add(obs.getDifInput_OutputPaquets());
                            listdifInput_OutputBytes.add(obs.getDifInput_OutputBytes());

                            refDate=auxTimestamp;
                            obs=new TSobservations(refDate,refDate,LocalIP, alpha, debug);
                            obs.processPacket(rest, debug);


                      }

                  }


                }

                //Close the input stream
                br.close();
        }//while iterator files
 
    }
    
    
    public void createSample(String basePathName, String MetricName, ArrayList listToDump, String typeData) throws FileNotFoundException, UnsupportedEncodingException, IOException, ParseException{
        
        Writer writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(basePathName+MetricName), "utf-8"));
        Iterator it = listToDump.iterator();
        while(it.hasNext()){
            if(typeData.equals("String"))
               writer.append((String)it.next()+"\n");
            else if(typeData.equals("Integer")){
                String aux = String.valueOf(it.next()+"\n");
                writer.append(aux);
            }
            else if(typeData.equals("Double")){
               String aux = String.valueOf(it.next()+"\n");
                writer.append(aux);
            }else if(typeData.equals("Date")){
                DateFormat simpleDateFormat=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String text = simpleDateFormat.format(it.next())+"\n";
                writer.append(text);
                
            }else{
                //Nothing to do here
                it.next();//for avoiding infine lopp
            }   
        }//iteration while                
        writer.close();
    
    }
    
    public void CreateReport() throws UnsupportedEncodingException, IOException, FileNotFoundException, ParseException{
        
        //obtaind Report ID
        Path p = Paths.get(SampleFolder);
        String filename = p.getFileName().toString();
        //create dataset folder
        String resultsDir=reportFolder+filename;
        File dir = new File(resultsDir);
        if(!dir.exists()) dir.mkdir();
        
        //drop results
        String resbase= resultsDir+File.separator.toString();
        
        //dropEverySample
        createSample(resbase, filename+"-TotalInputBytes-"+granularity, this.listTotalOutpuBytes, "Integer");
        createSample(resbase, filename+"-TotalOutputBytes-"+granularity, this.listTotalInputBytes, "Integer");
        createSample(resbase, filename+"-TotalInputPaquets-"+granularity, this.listTotalInputPaquets, "Integer");
        createSample(resbase, filename+"-TotalOutputPaquets-"+granularity, this.listTotalOutpuPaquets, "Integer");
        createSample(resbase, filename+"-TimeStamps-"+granularity, this.listTimeStamps, "Date");
        createSample(resbase, filename+"-EntropyDisributionInputBytesPerFlow-"+granularity, this.listEntropyDisributionInputBytesPerFlow, "Double");
        createSample(resbase, filename+"-EntropyDisributionOutputBytesPerFlow-"+granularity, this.listEntropyDisributionOutputBytesPerFlow, "Double");
        createSample(resbase, filename+"-EntropyDisributionInputPaquetsPerFlow-"+granularity, this.listEntropyDisributionInputPaquetsPerFlow, "Double");
        createSample(resbase, filename+"-EntropyDisributionOutputPaquetsPerFlow-"+granularity, this.listEntropyDisributionOutputPaquetsPerFlow, "Double");
        createSample(resbase, filename+"-DisributionPaquetsPerFlow-"+granularity, this.listEntropyDisributionPaquetsPerFlow, "Double");
        createSample(resbase, filename+"-EntropyDisributionBytesPerFlow-"+granularity, this.listEntropyDisributionBytesPerFlow, "Double");
        createSample(resbase, filename+"-difInput_OutputBytes-"+granularity, this.listdifInput_OutputBytes, "Double");
        createSample(resbase, filename+"-difInput_OutputPaquets-"+granularity, this.listdifInput_OutputPaquets, "Double");
        
    }
    
    public String getLocalIP() {
        return LocalIP;
    }

    public void setLocalIP(String LocalIP) {
        this.LocalIP = LocalIP;
    }

    public void setAlpha(double alpha) {
        this.alpha = alpha;
    }

    public void setGranularity(int granularity) {
        this.granularity = granularity;
    }
    
    
    public double getAlpha() {
        return alpha;
    }

    public int getGranularity() {
        return granularity;
    }

    
    public ArrayList<Date> getListTimeStamps() {
        return listTimeStamps;
    }

    public ArrayList<Integer> getListTotalInputPaquets() {
        return listTotalInputPaquets;
    }

    public ArrayList<Integer> getListTotalOutpuPaquets() {
        return listTotalOutpuPaquets;
    }

    public ArrayList<Integer> getListTotalInputBytes() {
        return listTotalInputBytes;
    }

    public ArrayList<Integer> getListTotalOutpuBytes() {
        return listTotalOutpuBytes;
    }

    public ArrayList<Double> getListEntropyDisributionInputPaquetsPerFlow() {
        return listEntropyDisributionInputPaquetsPerFlow;
    }

    public ArrayList<Double> getListEntropyDisributionOutputPaquetsPerFlow() {
        return listEntropyDisributionOutputPaquetsPerFlow;
    }

    public ArrayList<Double> getListEntropyDisributionPaquetsPerFlow() {
        return listEntropyDisributionPaquetsPerFlow;
    }

    public ArrayList<Double> getListEntropyDisributionInputBytesPerFlow() {
        return listEntropyDisributionInputBytesPerFlow;
    }

    public ArrayList<Double> getListEntropyDisributionOutputBytesPerFlow() {
        return listEntropyDisributionOutputBytesPerFlow;
    }

    public ArrayList<Double> getListEntropyDisributionBytesPerFlow() {
        return listEntropyDisributionBytesPerFlow;
    }

    public ArrayList<Double> getListdifInput_OutputPaquets() {
        return listdifInput_OutputPaquets;
    }

    public ArrayList<Double> getListdifInput_OutputBytes() {
        return listdifInput_OutputBytes;
    }

    public void setListTimeStamps(ArrayList<Date> listTimeStamps) {
        this.listTimeStamps = listTimeStamps;
    }

    public void setListTotalInputPaquets(ArrayList<Integer> listTotalInputPaquets) {
        this.listTotalInputPaquets = listTotalInputPaquets;
    }

    public void setListTotalOutpuPaquets(ArrayList<Integer> listTotalOutpuPaquets) {
        this.listTotalOutpuPaquets = listTotalOutpuPaquets;
    }

    public void setListTotalInputBytes(ArrayList<Integer> listTotalInputBytes) {
        this.listTotalInputBytes = listTotalInputBytes;
    }

    public void setListTotalOutpuBytes(ArrayList<Integer> listTotalOutpuBytes) {
        this.listTotalOutpuBytes = listTotalOutpuBytes;
    }

    public void setListEntropyDisributionInputPaquetsPerFlow(ArrayList<Double> listEntropyDisributionInputPaquetsPerFlow) {
        this.listEntropyDisributionInputPaquetsPerFlow = listEntropyDisributionInputPaquetsPerFlow;
    }

    public void setListEntropyDisributionOutputPaquetsPerFlow(ArrayList<Double> listEntropyDisributionOutputPaquetsPerFlow) {
        this.listEntropyDisributionOutputPaquetsPerFlow = listEntropyDisributionOutputPaquetsPerFlow;
    }

    public void setListEntropyDisributionPaquetsPerFlow(ArrayList<Double> listEntropyDisributionPaquetsPerFlow) {
        this.listEntropyDisributionPaquetsPerFlow = listEntropyDisributionPaquetsPerFlow;
    }

    public void setListEntropyDisributionInputBytesPerFlow(ArrayList<Double> listEntropyDisributionInputBytesPerFlow) {
        this.listEntropyDisributionInputBytesPerFlow = listEntropyDisributionInputBytesPerFlow;
    }

    public void setListEntropyDisributionOutputBytesPerFlow(ArrayList<Double> listEntropyDisributionOutputBytesPerFlow) {
        this.listEntropyDisributionOutputBytesPerFlow = listEntropyDisributionOutputBytesPerFlow;
    }

    public void setListEntropyDisributionBytesPerFlow(ArrayList<Double> listEntropyDisributionBytesPerFlow) {
        this.listEntropyDisributionBytesPerFlow = listEntropyDisributionBytesPerFlow;
    }

    public void setListdifInput_OutputPaquets(ArrayList<Double> listdifInput_OutputPaquets) {
        this.listdifInput_OutputPaquets = listdifInput_OutputPaquets;
    }

    public void setListdifInput_OutputBytes(ArrayList<Double> listdifInput_OutputBytes) {
        this.listdifInput_OutputBytes = listdifInput_OutputBytes;
    }

        
    
}
