
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Calendar;

public class pktanalyzer {

	static StringBuilder hexData = new StringBuilder();
	static String IP = "0800";
	static int total_header_size = 0;
	@SuppressWarnings("resource")
	
	/*
	 * As we know that in the IP packet first packet is all about Ethernet.
	 * So, we will parse the Ethernet Packet first and then we will proceed
	 * as follows:
	 * Ethernet header --> IP header --> UDP header/TCP header/ICMP header
	 * */
	public static PacketMetrics ReadPacket(StringBuilder hexData, int data_size, boolean debug) {

		icmp ICMP = new icmp();
		udp udp = new udp();
		ip ip = new ip();
		tcp tcp = new tcp();
		ethernet eth = new ethernet();
                
                java.sql.Date now = new java.sql.Date(Calendar.getInstance().getTimeInMillis());
                PacketMetrics myPkt = new PacketMetrics(now,"unknown","unknown","unknown","unknown",0);
		
		String next_header = eth.ReadEthernetHeader(hexData, data_size, myPkt,debug);
		/*
		 * After reading the Ethernet Packet the next encapsulated protocol
		 * is returned in the var "next_header"
		 * */
		if(next_header.contains("IP")) {
			try{
                            next_header = ip.ReadIPHeader(hexData, data_size,myPkt,debug);
                            myPkt.getHeaders().put("IP", true);
                        }catch(Exception e){
                            if(debug) System.out.println("Invalid IP Header");
                        }
		}
		if(next_header != null && next_header.contains("UDP")) {
			try{
                            next_header = udp.ReadUDPHeader(hexData, data_size,myPkt,debug);
                            myPkt.getHeaders().put("UDP", true);
                        }catch(Exception e){
                            if(debug) System.out.println("Invalid UDP Header");
                        }
		}
		if(next_header != null && next_header.contains("TCP")) {
			try{
                            next_header = tcp.ReadTCPHeader(hexData, data_size,myPkt,debug);
                            myPkt.getHeaders().put("TCP", true);
                        }catch(Exception e){
                            if(debug) System.out.println("Invalid TCP Header");
                        }
		}
		if(next_header != null && (next_header.contains("ICMP"))) {
			try{
                            next_header = ICMP.ReadICMPHeader(hexData, data_size,myPkt,debug);
                            myPkt.getHeaders().put("ICMP", true);
                        }catch(Exception e){
                            if(debug) System.out.println("Invalid IP Header");
                        }
		}
                
            return myPkt;    
	}
	
	public static void reset_parsing_buffer(StringBuilder buf_to_clear) {
		buf_to_clear.setLength(0);
	}
}
