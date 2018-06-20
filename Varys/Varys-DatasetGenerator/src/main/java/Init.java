
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Paths;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.Calendar;
import java.util.List;
import org.pcap4j.packet.Packet;
import org.pcap4j.util.ByteArrays;
import org.apache.commons.codec.binary.Hex;
import org.pcap4j.packet.EthernetPacket;
import org.pcap4j.packet.IllegalRawDataException;
import org.pcap4j.packet.IpV4Packet;
import org.pcap4j.packet.TcpPacket;
import org.pcap4j.packet.UdpPacket;
import org.pcap4j.util.MacAddress;


/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Alkahest
 */
public class Init {
    
    public static String rootDir = Paths.get("").toAbsolutePath().getParent().toString();
    public static String datasetDir=Paths.get(rootDir,"Dataset").toString()+File.separator.toString();
            
    public static void main(String[] args) throws IllegalRawDataException, FileNotFoundException, IOException, ParseException{
        
        boolean debug=true;
        String IPLocal= "192.168.1.43";
        int alpha=0;//renyi
        String sampleFolder="C:\\Users\\Alkahest\\Desktop\\2018-04-07_16.31.22";
        int granularity6=240000; //4 mins
        int granularity=180000; //3 mins
        int granularity2=120000;  //2 mins
        int granularity3=60000;  //1 mins
        int granularity4=30000;  //30 secs
        int granularity5=15000;  //15 secs
        
        TSDataset auxTSDataset= new TSDataset(sampleFolder,datasetDir, IPLocal, alpha,granularity, false);
        //System.out.println(datasetDir);
        auxTSDataset.processSampleFolder();
        auxTSDataset.CreateReport();
        
        TSDataset auxTSDataset2= new TSDataset(sampleFolder,datasetDir, IPLocal, alpha,granularity2, false);
        //System.out.println(datasetDir);
        auxTSDataset2.processSampleFolder();
        auxTSDataset2.CreateReport();
        
        TSDataset auxTSDataset3= new TSDataset(sampleFolder,datasetDir, IPLocal, alpha,granularity3, false);
        //System.out.println(datasetDir);
        auxTSDataset3.processSampleFolder();
        auxTSDataset3.CreateReport();
        
        TSDataset auxTSDataset4= new TSDataset(sampleFolder,datasetDir, IPLocal, alpha,granularity4, false);
        //System.out.println(datasetDir);
        auxTSDataset4.processSampleFolder();
        auxTSDataset4.CreateReport();
        
        TSDataset auxTSDataset5= new TSDataset(sampleFolder,datasetDir, IPLocal, alpha,granularity5, false);
        //System.out.println(datasetDir);
        auxTSDataset5.processSampleFolder();
        auxTSDataset5.CreateReport();
        
        TSDataset auxTSDataset6= new TSDataset(sampleFolder,datasetDir, IPLocal, alpha,granularity6, false);
        //System.out.println(datasetDir);
        auxTSDataset6.processSampleFolder();
        auxTSDataset6.CreateReport();
        
        String aux="";
    }
    

    
        
}
 