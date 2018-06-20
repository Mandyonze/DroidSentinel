
import java.io.FileNotFoundException;
import java.sql.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
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
public class TSobservations {
    
    String LocalIP;
    java.sql.Date init;
    java.sql.Date end;
    boolean debug;
    
    //About NumPAquets
    int totalInputPaquets;
    int totalOutpuPaquets;
    HashMap<String,Integer> InputPaquetsPerFlow; // <SrcIP> <numPaquets>
    HashMap<String,Integer> OutputPaquetsPerFlow; // <DestIP> <numPaquets>
    ArrayList<Double> DisributionInputPaquetsPerFlow;
    ArrayList<Double> DisributionOutputPaquetsPerFlow;
    ArrayList<Double> DisributionPaquetsPerFlow;
    
    //ABout NumBytes
    int totalInputBytes;
    int totalOutpuBytes;
    HashMap<String,Integer> InputBytesPerFlow; // <SrcIP> <numPaquets>
    HashMap<String,Integer> OutputBytesPerFlow; // <DestIP> <numPaquets>
    ArrayList<Double> DisributionInputBytesPerFlow;
    ArrayList<Double> DisributionOutputBytesPerFlow;
    ArrayList<Double> DisributionBytesPerFlow;
    
    //Aggregated Metrics
    double alpha;
    double EntropyDisributionInputPaquetsPerFlow;
    double EntropyDisributionOutputPaquetsPerFlow;
    double EntropyDisributionPaquetsPerFlow;
    double EntropyDisributionInputBytesPerFlow;
    double EntropyDisributionOutputBytesPerFlow;
    double EntropyDisributionBytesPerFlow;
    double difInput_OutputPaquets;
    double difInput_OutputBytes;
    
    
    public TSobservations(){
        //nothing
    }
    
    public TSobservations(java.sql.Date init, java.sql.Date end, String localIP, double alpha, boolean debug){
        
        this.LocalIP=localIP;
        this.init=init;
        this.end=end;
        this.debug=debug;
        
        //About NumPAquets
        totalInputPaquets=0;
        totalOutpuPaquets=0;
        InputPaquetsPerFlow= new HashMap<String,Integer>(); // <SrcIP> <numPaquets>
        OutputPaquetsPerFlow= new HashMap<String,Integer>(); // <DestIP> <numPaquets>
        DisributionInputPaquetsPerFlow= new ArrayList<Double>();
        DisributionOutputPaquetsPerFlow= new ArrayList<Double>();
        DisributionPaquetsPerFlow= new ArrayList<Double>();
        DisributionBytesPerFlow= new ArrayList<Double>();

        //ABout NumBytes
        totalInputBytes=0;
        totalOutpuBytes=0;
        InputBytesPerFlow= new HashMap<String,Integer>(); // <SrcIP> <numPaquets>
        OutputBytesPerFlow= new HashMap<String,Integer>(); // <DestIP> <numPaquets>
        DisributionInputBytesPerFlow= new ArrayList<Double>();
        DisributionOutputBytesPerFlow= new ArrayList<Double>();
        
        this.alpha=alpha;
        EntropyDisributionInputPaquetsPerFlow=0;
        EntropyDisributionOutputPaquetsPerFlow=0;
        EntropyDisributionPaquetsPerFlow=0;
        EntropyDisributionInputBytesPerFlow=0;
        EntropyDisributionOutputBytesPerFlow=0;
        EntropyDisributionBytesPerFlow=0;
        difInput_OutputPaquets=0;
        difInput_OutputBytes=0;        
    }
    
    
    public void DisplayAggregatedMetrics(){
        System.out.println("+++++++++++++++++++++++++++++++++++++++++++++++++");
        System.out.println("                   Observation");
        DateFormat simpleDateFormat=new SimpleDateFormat("yyyy-MM-dd_HH.mm.ss");
        String iniFormated = simpleDateFormat.format(init);
        String endFormated = simpleDateFormat.format(end);
        System.out.println("Ini: "+iniFormated);
        System.out.println("End: "+endFormated);
        System.out.println("+++++++++++++++++++++++++++++++++++++++++++++++++");
        System.out.println("IP Local: "+this.LocalIP);
        System.out.println("Total Packets: "+this.totalInputPaquets+this.totalOutpuPaquets);
        System.out.println("Total Bytes: "+totalInputBytes+totalOutpuBytes);
        System.out.println("Input Packets: "+this.totalInputPaquets);
        System.out.println("Input Bytes: "+this.totalInputBytes);
        System.out.println("Output Packets: "+this.totalOutpuPaquets);
        System.out.println("Output Bytes: "+this.totalOutpuPaquets);
        System.out.println("EntropyDisributionInputPacketsPerFlow: "+EntropyDisributionInputPaquetsPerFlow);
        System.out.println("EntropyDisributionOutputPacketsPerFlow "+EntropyDisributionOutputPaquetsPerFlow);
        System.out.println("EntropyDisributionPacketsPerFlow "+EntropyDisributionPaquetsPerFlow);
        System.out.println("EntropyDisributionInputBytesPerFlow "+EntropyDisributionInputBytesPerFlow);
        System.out.println("EntropyDisributionOutputBytesPerFlow "+EntropyDisributionOutputBytesPerFlow);
        System.out.println("EntropyDisributionBytesPerFlow "+EntropyDisributionBytesPerFlow);
        System.out.println("difInput_OutputPackets: "+difInput_OutputPaquets);
        System.out.println("difInput_OutputBytes: "+difInput_OutputBytes);
    
    }
    public void calulateAggregatedMetrics(){
        
        try{
        calculateDistributions();
        if(DisributionInputPaquetsPerFlow.size()==0) {
            int stop=666;
                    }
        
        EntropyDisributionInputPaquetsPerFlow= (double)RenyiEntropy(alpha,  DisributionInputPaquetsPerFlow);
        EntropyDisributionOutputPaquetsPerFlow=(double) RenyiEntropy(alpha,  DisributionOutputPaquetsPerFlow);
        EntropyDisributionPaquetsPerFlow=(double)RenyiEntropy(alpha,  DisributionPaquetsPerFlow);
        EntropyDisributionInputBytesPerFlow=(double)RenyiEntropy(alpha,  DisributionInputBytesPerFlow);
        EntropyDisributionOutputBytesPerFlow=(double)RenyiEntropy(alpha,  DisributionOutputBytesPerFlow);
        EntropyDisributionBytesPerFlow=(double)RenyiEntropy(alpha,  DisributionBytesPerFlow);
        difInput_OutputPaquets=(double)Math.sqrt(Math.pow(totalInputPaquets-totalOutpuPaquets, 2));
        difInput_OutputBytes=(double)Math.sqrt(Math.pow(totalInputBytes-totalOutpuBytes, 2));
        DisplayAggregatedMetrics();
        }catch(Exception E){
            System.out.println("Something weird happends");
        }
        
    }
    public void calculateDistributions(){
        
        /*if(InputPaquetsPerFlow!=null && totalInputPaquets!=null){
        
        }*/
        
        calculateDistributionPerFlow(DisributionInputPaquetsPerFlow,InputPaquetsPerFlow,totalInputPaquets);
        calculateDistributionPerFlow(DisributionOutputPaquetsPerFlow,OutputPaquetsPerFlow,totalOutpuPaquets);
        calculateComplexDistribution(DisributionPaquetsPerFlow,InputPaquetsPerFlow,OutputPaquetsPerFlow,totalInputPaquets+totalOutpuPaquets);                
        calculateDistributionPerFlow(DisributionInputBytesPerFlow,InputBytesPerFlow,totalInputBytes);
        calculateDistributionPerFlow(DisributionOutputBytesPerFlow,OutputBytesPerFlow,totalOutpuBytes);
        calculateComplexDistribution(DisributionBytesPerFlow,InputBytesPerFlow,OutputBytesPerFlow,totalInputBytes+totalOutpuBytes);
        
    }
    
    
    public void calculateComplexDistribution(ArrayList<Double> myDistributions, HashMap<String,Integer> myMap1, HashMap<String,Integer> myMap2, Integer total){
    
      if(myMap1!=null && myMap2!=null){
        Iterator it = myMap1.entrySet().iterator();
        while (it.hasNext()) {
            HashMap.Entry pair = (HashMap.Entry)it.next();
                String myKey=(String) pair.getKey();
                int myValue=(Integer) pair.getValue();
                double myDist=0;
                    if(total!=0){
                        myDist= (double) ((double) myValue/ (double) total);
                    }
                myDistributions.add(myDist);
        }//while
        
        it = myMap2.entrySet().iterator();
        while (it.hasNext()) {
            HashMap.Entry pair = (HashMap.Entry)it.next();
                String myKey=(String) pair.getKey();
                int myValue=(Integer) pair.getValue();
                double myDist=0;
                if(total!=0){
                    myDist= (double) ((double) myValue/ (double) total);
                }    
                myDistributions.add(myDist);
        }//while
         } 
    
    }
    public void calculateDistributionPerFlow(ArrayList<Double> myDistributions, HashMap<String,Integer> myMap, Integer total){
        
       if(myMap!=null){
        Iterator it = myMap.entrySet().iterator();
        while (it.hasNext()) {
            HashMap.Entry pair = (HashMap.Entry)it.next();
                String myKey=(String) pair.getKey();
                int myValue=(Integer) pair.getValue();
                
                double myDist=0;
                if(total!=0){
                    myDist= (double) ((double) myValue/ (double) total);
                }
                myDistributions.add(myDist);
        }//while
    }   
    }
    
    public void updateMetrics(PacketMetrics myPkt){
    
        //is an Input packet
        if(myPkt.getIpDstAdress().equals(this.LocalIP)){
            totalInputPaquets++;
            totalInputBytes+=myPkt.getLenght();        
            updateHashMap(InputPaquetsPerFlow, myPkt.getIpSrcAdress(), 1);
            updateHashMap(InputBytesPerFlow, myPkt.getIpSrcAdress(), myPkt.getLenght());
        }
        
        //is an output packet
        if(myPkt.getIpSrcAdress().equals(this.LocalIP)){
            this.totalOutpuPaquets++;
            totalOutpuBytes+=myPkt.getLenght();
            updateHashMap(OutputPaquetsPerFlow, myPkt.getIpDstAdress(), 1);
            updateHashMap(OutputBytesPerFlow, myPkt.getIpDstAdress(), myPkt.getLenght());
        }
    }
    
    public void processPacket(String paquetSample, boolean debug) throws IllegalRawDataException, FileNotFoundException{
        StringBuilder sb = new StringBuilder();

        //opcode to bytes
        byte[] decoded = Base64.getDecoder().decode(paquetSample);
        //Hex.encodeHexString(decoded);

        //bytes to HEX
        for (byte b : decoded) {
            sb.append(String.format("%02X", b));
        }

        if(debug) System.out.println(sb.toString());
        PacketMetrics myPkt=pktanalyzer.ReadPacket(sb, sb.length(), debug);
        
        if(!myPkt.getIpDstAdress().equals("unknown") && !myPkt.getIpSrcAdress().equals("unknown")&& !myPkt.getLenght().equals("unknown")){
        //if valid packet (not default)
        updateMetrics(myPkt);
        }
        
    }//main
    
    public double RenyiEntropy(double alpha,  ArrayList<Double> distribution){
        //Renyi entropy
        //https://en.wikipedia.org/wiki/Rényi_entropy
        //normalized shanon entropy:
        //https://math.stackexchange.com/questions/395121/how-entropy-scales-with-sample-size
        //opciones de ajuste:
        // 1- base de la entropia
        // 2 -normalizada si/no
        //note max entropy value = Math.log(distribution.size())
        
        double sol=0;
        double auxSum=0.0;
        double totalValues=0.0; //used for normalized entropy
        //System.out.println("Max Entropy: "+Math.log(distribution.size()));
        
            Iterator<Double> it2 = distribution.iterator();
            while(it2.hasNext()){
                totalValues+=(double) it2.next();            
            }
                    
        if((alpha>0 || alpha==0) && !(alpha==1) ){
            //sumatorio
            Iterator<Double> it = distribution.iterator();
            while(it.hasNext()){
                auxSum += (double) Math.pow((double) it.next()/totalValues, alpha);
            }

            sol=((double)1/(double)(1-alpha))* Math.log(auxSum);
        }
        else{
            //Information entropy (Shannon)
              Iterator<Double> it = distribution.iterator();
              while(it.hasNext()){
                  double num = (double)it.next();
                  num =num/totalValues;
                  //normalized shannon entropy
                  auxSum += (double) (num*(Math.log(num)));
            }
              sol=-auxSum;
        }
        
        //return normalized entropy
        double aux1= sol;
        //double rr=Math.log10(4);
        //double aux2=Math.log10((double)distribution.size());
        //double aux=aux1/aux2;
        return sol;
    }//entropy calculate
    
    public void updateHashMap(HashMap<String,Integer> mapToUpdate, String NewKey, Integer acc){
        /*
        acc=1 when the map acts as counter
        acc+<integer> when accumulates values (ex. num bytes)
         */
        if(mapToUpdate.containsKey(NewKey)){
            //Si ya está, se actualiza
            int auxValue=mapToUpdate.get(NewKey);
            auxValue+=acc;
            mapToUpdate.put(NewKey, auxValue);
        }
        else{
            //n est´á, ergo nueva entrada
            mapToUpdate.put(NewKey, acc);
        }
    }

    public void setLocalIP(String LocalIP) {
        this.LocalIP = LocalIP;
    }

    public void setInit(Date init) {
        this.init = init;
    }

    public void setEnd(Date end) {
        this.end = end;
    }

    public void setDebug(boolean debug) {
        this.debug = debug;
    }

    public void setTotalInputPaquets(int totalInputPaquets) {
        this.totalInputPaquets = totalInputPaquets;
    }

    public void setTotalOutpuPaquets(int totalOutpuPaquets) {
        this.totalOutpuPaquets = totalOutpuPaquets;
    }

    public void setInputPaquetsPerFlow(HashMap<String, Integer> InputPaquetsPerFlow) {
        this.InputPaquetsPerFlow = InputPaquetsPerFlow;
    }

    public void setOutputPaquetsPerFlow(HashMap<String, Integer> OutputPaquetsPerFlow) {
        this.OutputPaquetsPerFlow = OutputPaquetsPerFlow;
    }

    public void setDisributionInputPaquetsPerFlow(ArrayList<Double> DisributionInputPaquetsPerFlow) {
        this.DisributionInputPaquetsPerFlow = DisributionInputPaquetsPerFlow;
    }

    public void setDisributionOutputPaquetsPerFlow(ArrayList<Double> DisributionOutputPaquetsPerFlow) {
        this.DisributionOutputPaquetsPerFlow = DisributionOutputPaquetsPerFlow;
    }

    public void setDisributionPaquetsPerFlow(ArrayList<Double> DisributionPaquetsPerFlow) {
        this.DisributionPaquetsPerFlow = DisributionPaquetsPerFlow;
    }

    public void setTotalInputBytes(int totalInputBytes) {
        this.totalInputBytes = totalInputBytes;
    }

    public void setTotalOutpuBytes(int totalOutpuBytes) {
        this.totalOutpuBytes = totalOutpuBytes;
    }

    public void setInputBytesPerFlow(HashMap<String, Integer> InputBytesPerFlow) {
        this.InputBytesPerFlow = InputBytesPerFlow;
    }

    public void setOutputBytesPerFlow(HashMap<String, Integer> OutputBytesPerFlow) {
        this.OutputBytesPerFlow = OutputBytesPerFlow;
    }

    public void setDisributionInputBytesPerFlow(ArrayList<Double> DisributionInputBytesPerFlow) {
        this.DisributionInputBytesPerFlow = DisributionInputBytesPerFlow;
    }

    public void setDisributionOutputBytesPerFlow(ArrayList<Double> DisributionOutputBytesPerFlow) {
        this.DisributionOutputBytesPerFlow = DisributionOutputBytesPerFlow;
    }

    public void setDisributionBytesPerFlow(ArrayList<Double> DisributionBytesPerFlow) {
        this.DisributionBytesPerFlow = DisributionBytesPerFlow;
    }

    public void setAlpha(double alpha) {
        this.alpha = alpha;
    }

    public void setEntropyDisributionInputPaquetsPerFlow(double EntropyDisributionInputPaquetsPerFlow) {
        this.EntropyDisributionInputPaquetsPerFlow = EntropyDisributionInputPaquetsPerFlow;
    }

    public void setEntropyDisributionOutputPaquetsPerFlow(double EntropyDisributionOutputPaquetsPerFlow) {
        this.EntropyDisributionOutputPaquetsPerFlow = EntropyDisributionOutputPaquetsPerFlow;
    }

    public void setEntropyDisributionPaquetsPerFlow(double EntropyDisributionPaquetsPerFlow) {
        this.EntropyDisributionPaquetsPerFlow = EntropyDisributionPaquetsPerFlow;
    }

    public void setEntropyDisributionInputBytesPerFlow(double EntropyDisributionInputBytesPerFlow) {
        this.EntropyDisributionInputBytesPerFlow = EntropyDisributionInputBytesPerFlow;
    }

    public void setEntropyDisributionOutputBytesPerFlow(double EntropyDisributionOutputBytesPerFlow) {
        this.EntropyDisributionOutputBytesPerFlow = EntropyDisributionOutputBytesPerFlow;
    }

    public void setEntropyDisributionBytesPerFlow(double EntropyDisributionBytesPerFlow) {
        this.EntropyDisributionBytesPerFlow = EntropyDisributionBytesPerFlow;
    }

    public void setDifInput_OutputPaquets(double difInput_OutputPaquets) {
        this.difInput_OutputPaquets = difInput_OutputPaquets;
    }

    public void setDifInput_OutputBytes(double difInput_OutputBytes) {
        this.difInput_OutputBytes = difInput_OutputBytes;
    }

    public String getLocalIP() {
        return LocalIP;
    }

    public Date getInit() {
        return init;
    }

    public Date getEnd() {
        return end;
    }

    public boolean isDebug() {
        return debug;
    }

    public int getTotalInputPaquets() {
        return totalInputPaquets;
    }

    public int getTotalOutpuPaquets() {
        return totalOutpuPaquets;
    }

    public HashMap<String, Integer> getInputPaquetsPerFlow() {
        return InputPaquetsPerFlow;
    }

    public HashMap<String, Integer> getOutputPaquetsPerFlow() {
        return OutputPaquetsPerFlow;
    }

    public ArrayList<Double> getDisributionInputPaquetsPerFlow() {
        return DisributionInputPaquetsPerFlow;
    }

    public ArrayList<Double> getDisributionOutputPaquetsPerFlow() {
        return DisributionOutputPaquetsPerFlow;
    }

    public ArrayList<Double> getDisributionPaquetsPerFlow() {
        return DisributionPaquetsPerFlow;
    }

    public int getTotalInputBytes() {
        return totalInputBytes;
    }

    public int getTotalOutpuBytes() {
        return totalOutpuBytes;
    }

    public HashMap<String, Integer> getInputBytesPerFlow() {
        return InputBytesPerFlow;
    }

    public HashMap<String, Integer> getOutputBytesPerFlow() {
        return OutputBytesPerFlow;
    }

    public ArrayList<Double> getDisributionInputBytesPerFlow() {
        return DisributionInputBytesPerFlow;
    }

    public ArrayList<Double> getDisributionOutputBytesPerFlow() {
        return DisributionOutputBytesPerFlow;
    }

    public ArrayList<Double> getDisributionBytesPerFlow() {
        return DisributionBytesPerFlow;
    }

    public double getAlpha() {
        return alpha;
    }

    public double getEntropyDisributionInputPaquetsPerFlow() {
        return EntropyDisributionInputPaquetsPerFlow;
    }

    public double getEntropyDisributionOutputPaquetsPerFlow() {
        return EntropyDisributionOutputPaquetsPerFlow;
    }

    public double getEntropyDisributionPaquetsPerFlow() {
        return EntropyDisributionPaquetsPerFlow;
    }

    public double getEntropyDisributionInputBytesPerFlow() {
        return EntropyDisributionInputBytesPerFlow;
    }

    public double getEntropyDisributionOutputBytesPerFlow() {
        return EntropyDisributionOutputBytesPerFlow;
    }

    public double getEntropyDisributionBytesPerFlow() {
        return EntropyDisributionBytesPerFlow;
    }

    public double getDifInput_OutputPaquets() {
        return difInput_OutputPaquets;
    }

    public double getDifInput_OutputBytes() {
        return difInput_OutputBytes;
    }
    
    
}
