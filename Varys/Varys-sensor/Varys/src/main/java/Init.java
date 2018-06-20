/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import java.io.IOException;

import com.sun.jna.Platform;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.Calendar;
import java.util.Enumeration;
import java.util.Properties;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeoutException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.bind.DatatypeConverter;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.pcap4j.core.NotOpenException;
import org.pcap4j.core.PacketListener;
import org.pcap4j.core.PcapDumper;
import org.pcap4j.core.PcapHandle;
import org.pcap4j.core.PcapNativeException;
import org.pcap4j.core.PcapNetworkInterface;
import org.pcap4j.core.PcapStat;
import org.pcap4j.core.BpfProgram.BpfCompileMode;
import org.pcap4j.core.PcapNetworkInterface.PromiscuousMode;
import org.pcap4j.packet.AbstractPacket;
import org.pcap4j.packet.EthernetPacket;
import org.pcap4j.packet.FragmentedPacket;
import org.pcap4j.packet.IllegalRawDataException;
import org.pcap4j.packet.IpPacket;
import org.pcap4j.packet.IpV4Packet;
import org.pcap4j.packet.IpV6Packet;
import org.pcap4j.packet.Packet;
import org.pcap4j.packet.Packet.Header;
import org.pcap4j.packet.TcpPacket;
import org.pcap4j.packet.UdpPacket;
import org.pcap4j.util.NifSelector;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class Init {

    static int maxPackets = -1; //-1 means infinite loop
    static int granularity=350000;// millisecs 60000= 1 minute
    static long time = System.currentTimeMillis();
    public static PcapStat stats;
    public static String rootDir = Paths.get("").toAbsolutePath().getParent().toString();
    public static String Reports=Paths.get(rootDir,"Reports").toString()+File.separator.toString();
    public static String settings=Paths.get(rootDir,"Config").toString()+File.separator.toString()+"Settings.xml";
    
    public static String writerSummaryFIle;
    public static String samplesDir;
    public static String currentSamplesFile;
    public static String largestStringEver;
    public static boolean debug;
    public static String filter;
    
    static PcapNetworkInterface getNetworkDevice() {
        PcapNetworkInterface device = null;
        try {
            device = new NifSelector().selectNetworkInterface();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return device;
    }

    public static void main(String[] args) throws PcapNativeException, NotOpenException, IOException, ParserConfigurationException, SAXException, TimeoutException {
            
        largestStringEver="";
        //Load System details
        //Create summary file
        
        //load COnfig
        loadConnectivitySettings();
        String nowFormated=displaySystemDetails(Reports);
        
        //create dataset folder
        samplesDir=Paths.get(rootDir,"Reports").toString()+File.separator.toString()+nowFormated+File.separator.toString();
        File dir = new File(samplesDir);
        dir.mkdir();
        
        //first sample file
        currentSamplesFile=samplesDir+nowFormated;
        
        
        // The code we had before
        PcapNetworkInterface device = getNetworkDevice();
        System.out.println("You chose: " + device);

        // New code below here
        if (device == null) {
            System.out.println("No device chosen.");
            System.exit(1);
        }

        // Open the device and get a handle
        int snapshotLength = 65536; // in bytes   
        int readTimeout = 50; // in milliseconds                   
        final PcapHandle handle;
        handle = device.openLive(snapshotLength, PromiscuousMode.PROMISCUOUS, readTimeout);

        
        /**
        ++++++++++++++++++++++++++++
        * Aggregation things
        * 
        ++++++++++++++++++++++++++
         */
        Runnable aggregate = new Runnable() {
            public void run() {
                // what the data aggregator does                                
                //Drop observations in sample
                Writer writer;         
                try {
                    writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(currentSamplesFile), "utf-8"));
                    writer.write(largestStringEver);
                    writer.close();
                } catch (FileNotFoundException ex) {
                    Logger.getLogger(Init.class.getName()).log(Level.SEVERE, null, ex);
                } catch (UnsupportedEncodingException ex) {
                    Logger.getLogger(Init.class.getName()).log(Level.SEVERE, null, ex);
                } catch (IOException ex) {
                    Logger.getLogger(Init.class.getName()).log(Level.SEVERE, null, ex);
                }

               /* try {
                    Files.write(Paths.get(currentSamplesFile), largestStringEver.getBytes(), StandardOpenOption.APPEND);
                }catch (IOException e) {
                    //exception handling left as an exercise for the reader
                }*/
                
                java.sql.Date now = new java.sql.Date(Calendar.getInstance().getTimeInMillis());
                DateFormat simpleDateFormat=new SimpleDateFormat("yyyy-MM-dd_HH.mm.ss");
                String nowFormated2 = simpleDateFormat.format(now);
                currentSamplesFile=samplesDir+nowFormated2;
                System.out.println("Another Observationc at "+nowFormated2);
                //update summary                
                String theText="Added new observation at " +nowFormated2+"\n";
                try {
                    Files.write(Paths.get(Reports+writerSummaryFIle), theText.getBytes(), StandardOpenOption.APPEND);
                }catch (IOException e) {
                    //exception handling left as an exercise for the reader
                }
                
                /*try {
                    Writer writer2 = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(Reports+writerSummaryFIle), "utf-8"));
                    writer2.append("Added new observation at " +nowFormated2+"\n");
                    writer2.close();
                } catch (FileNotFoundException ex) {
                    Logger.getLogger(Init.class.getName()).log(Level.SEVERE, null, ex);
                } catch (UnsupportedEncodingException ex) {
                    Logger.getLogger(Init.class.getName()).log(Level.SEVERE, null, ex);
                } catch (IOException ex) {
                    Logger.getLogger(Init.class.getName()).log(Level.SEVERE, null, ex);
                }
                */
                largestStringEver="";            
            }
        };

        //setup aggregation listener (alarm)
        
            Timer timer = new Timer();
            timer.schedule(new TimerTask() {
                public void run() {
                    aggregate.run();
                }
            }, time % granularity, granularity);

            // This will update for the current minute, it will be updated again in at most one minute.
            aggregate.run();

        /**
        ++++++++++++++++++++++++++++
        * Packet handler
        * 
        ++++++++++++++++++++++++++
         */    
        // Set a filter to only listen for tcp packets on port 80 (HTTP)
        //https://en.wikipedia.org/wiki/Berkeley_Packet_Filter
        //sintax https://biot.com/capstats/bpf.html        
        handle.setFilter(filter, BpfCompileMode.OPTIMIZE);

        // Create a listener that defines what to do with the received packets
        PacketListener listener = new PacketListener() {
            @Override
            public void gotPacket(Packet packet) {
             
                
                             
                // Print packet information to screen
                //System.out.println(handle.getTimestamp());
                //String textObs=packet.getRawData().toString();
                /* 
                       boolean q8= packet.contains(FragmentedPacket.class);
                        boolean q7 = packet.contains(IpPacket.class);
                        boolean a6=packet.contains(AbstractPacket.class);
                      boolean a=packet.contains(EthernetPacket.class);
                      boolean a5=packet.contains(IpV6Packet.class);
                      boolean a1=packet.contains(IpV4Packet.class);
                      boolean a2=packet.contains(TcpPacket.class);
                        boolean a3=packet.contains(UdpPacket.class);
            */
                String textObs = Base64.getEncoder().encodeToString(packet.getRawData());
                
                java.sql.Date now3 = new java.sql.Date(Calendar.getInstance().getTimeInMillis());
                DateFormat simpleDateFormat=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String nowFormated3 = simpleDateFormat.format(now3);
                largestStringEver+=nowFormated3+" "+textObs+"\n";
                
                StringBuilder sb = new StringBuilder();
                for (byte b : packet.getRawData()) {
                    sb.append(String.format("%02X ", b));
                }
                
                if(debug){
                    System.out.println(textObs+": "+sb.toString());
                }
                
                      
                
                //this is the bytes[] to decimal procedure
                /*StringBuilder sb = new StringBuilder();
                for (byte b : packet.getRawData()) {
                    sb.append(String.format("%02X ", b));
                }
                System.out.println(sb.toString());
                */
            }
        };

        // Tell the handle to loop using the listener we created
        try {
            handle.loop(maxPackets, listener);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("eyyy");
        handle.close();
    }
    
    public static String displaySystemDetails(String dirRoute) throws IOException{
        /*
        Returns timestamp for name results folder
        */
        
        String sal="";
        Properties p = System.getProperties();
        Enumeration keys = p.keys();
        
        java.sql.Date now = new java.sql.Date(Calendar.getInstance().getTimeInMillis());
        DateFormat simpleDateFormat=new SimpleDateFormat("yyyy-MM-dd_HH.mm.ss");
        String nowFormated = simpleDateFormat.format(now);
        writerSummaryFIle="Summary_"+nowFormated;
        Writer writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(dirRoute+writerSummaryFIle), "utf-8")); 
    
        while (keys.hasMoreElements()) {
            String key = (String)keys.nextElement();
            String value = (String)p.get(key);
            sal+=key + ": " + value+"\n";
            System.out.println(key + ": " + value);
        }        
        sal+="\nInit: "+nowFormated+"\n";
        sal+="Max. Paquets: "+maxPackets+"\n\nObservations:";
        writer.write(sal);
     
     writer.close();
     System.out.println(sal);
       return nowFormated;
    }
    
    public static void loadConnectivitySettings() throws ParserConfigurationException, SAXException, IOException, TimeoutException{
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document document = builder.parse(new File(settings));
        document.getDocumentElement().normalize();           
        Element root = document.getDocumentElement();
    
        System.out.println("Loading Configuration");
            Node node = document.getDocumentElement();
            NodeList n1 = node.getChildNodes();
            for (int i = 0; i < n1.getLength(); i++) {
                Node aNode = n1.item(i);
                    switch(aNode.getNodeName()){
                         case "Config":
                             loadConfig(aNode);
                             //
                             break;                             
                         default: //throw new ECategoryNotValid("The Category " + descriptor + " is not valid");    
                                  //System.out.println("The Category " + myNode.getNodeName() + " is not valid");  
                     }//switch            
            }//for nlist
    }
    
    public static void loadConfig(Node myNode) throws IOException, TimeoutException{
        NodeList refNodes = myNode.getChildNodes();            

        for (int x = 0; x < refNodes.getLength(); x++) {
            Node n = refNodes.item(x);
            if(n.getNodeName().equalsIgnoreCase("maxPackets")){ maxPackets=Integer.parseInt(n.getTextContent());}
            if(n.getNodeName().equalsIgnoreCase("granularity")){ granularity=Integer.parseInt(n.getTextContent());}          
            if(n.getNodeName().equalsIgnoreCase("display")){ debug=Boolean.valueOf(n.getTextContent());}
            if(n.getNodeName().equalsIgnoreCase("filter")){ filter=n.getTextContent();}
        }//for
        
        System.out.println("maxPackets: "+maxPackets);
        System.out.println("granularity: "+granularity);
        
   } //loadConfig

}