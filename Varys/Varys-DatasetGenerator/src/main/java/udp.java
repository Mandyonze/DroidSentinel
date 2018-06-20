
public class udp {

	short SRC_PORT_START = 0;
	short DEST_PORT_START = 2*2;
	short LEN_START = 4*2;
	short CHKSUM_START = 6*2;
	short CHKSUM_END = 8*2;
	
	public String ReadUDPHeader(StringBuilder hexData, int data_size, PacketMetrics myPkt, boolean debug) {
		StringBuilder UDPHeader = new StringBuilder();
		StringBuilder parsing_header = new StringBuilder();
		int BYTE = 2;
		int UDP_LEN = 0, DATA_LEN=0;
		// parse the header+dat length from UPD header
		int length_field_offset_udp = 4*BYTE;
		
		for(int itr=pktanalyzer.total_header_size+length_field_offset_udp;itr<pktanalyzer.total_header_size+length_field_offset_udp+4;itr++) {
			parsing_header.append(hexData.charAt(itr));
		}
		UDP_LEN = Integer.parseInt(parsing_header.toString(), 16);
		DATA_LEN = UDP_LEN - 8;
		pktanalyzer.reset_parsing_buffer(parsing_header);
		// Parse the UDP header from the Ethernet Header.
		for(int iterator=pktanalyzer.total_header_size;iterator<pktanalyzer.total_header_size+UDP_LEN*2;iterator++) {
			UDPHeader.append(hexData.charAt(iterator));
		}
		for(int which_field=0;which_field<UDP_LEN*2;which_field++) {
			// parsing Source port
			if(which_field >=SRC_PORT_START && which_field<DEST_PORT_START) {
				parsing_header.append(UDPHeader.charAt(which_field));
			}
			if(which_field == (DEST_PORT_START)-1) {
				int val = Integer.parseInt(parsing_header.toString(), 16);
				if(debug) System.out.println("UDP:  ----- UDP Header ----- ");
				if(debug) System.out.println("UDP:");
				if(debug) System.out.println("UDP:  Source port = "+val);
                                myPkt.setPtSrc(String.valueOf(val));
				pktanalyzer.reset_parsing_buffer(parsing_header);
			}
			// parsing Destination port
			if(which_field >= DEST_PORT_START && which_field < LEN_START) {
				parsing_header.append(UDPHeader.charAt(which_field));
			}
			if(which_field == (LEN_START)-1) {
				int val = Integer.parseInt(parsing_header.toString(), 16);
				if(debug) System.out.println("UDP:  Destination port = "+val);
                                myPkt.setPtDst(String.valueOf(val));
				pktanalyzer.reset_parsing_buffer(parsing_header);
			}
			// parsing Length
			if(which_field >= LEN_START && which_field < CHKSUM_START) {
				parsing_header.append(UDPHeader.charAt(which_field));
			}
			if(which_field == (CHKSUM_START)-1) {
				int val = Integer.parseInt(parsing_header.toString(), 16);
				if(debug) System.out.println("UDP:  Length = "+val);
				pktanalyzer.reset_parsing_buffer(parsing_header);
			}
			// parsing Checksum
			if(which_field >= CHKSUM_START && which_field < CHKSUM_END) {
				parsing_header.append(UDPHeader.charAt(which_field));
			}
			if(which_field == (CHKSUM_END)-1) {
				if(debug) System.out.println("UDP:  Checksum = "+"0x"+parsing_header.toString());
				pktanalyzer.reset_parsing_buffer(parsing_header);
			}
			// parsing data
			if(which_field >= (CHKSUM_END) && which_field < UDP_LEN*2) {
				parsing_header.append(UDPHeader.charAt(which_field));
			}
			if(which_field == (UDP_LEN*2)-1) {
				if(debug) System.out.println("UDP:  Data: ("+DATA_LEN+ " bytes)");
				if(debug) System.out.print("UDP:  ");
				int octet_counts = 0;
				for(int itr=0;itr<parsing_header.length();itr++) {
					if(debug) System.out.print(parsing_header.charAt(itr));
					if(itr == 3 || itr % 4 == 3) {
						if(debug) System.out.print(" ");
						octet_counts++;
					}
					if(octet_counts == 7) {
						if(debug) System.out.println();
						if(debug) System.out.print("UDP:  ");
						octet_counts = 0;
					}
				}
				if(debug) System.out.println("\nUDP:  ");
				pktanalyzer.reset_parsing_buffer(parsing_header);
			}
		}
		pktanalyzer.total_header_size += UDP_LEN;
		return null;
	}
}