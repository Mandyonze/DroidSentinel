
public class icmp {
	short TYPE_START = 0;
	short CHKSUM_END = 4;
	short AFTER_CHKSUM = 4*2;
	
	public String ReadICMPHeader(StringBuilder hexData, int data_size, PacketMetrics myPkt, boolean debug) {
	StringBuilder ICMPHeader = new StringBuilder();
	StringBuilder parsing_header = new StringBuilder();
	
	// Copying ICMP header from hexData
	for(int itr=pktanalyzer.total_header_size;itr<pktanalyzer.total_header_size+8;itr++) {
		ICMPHeader.append(hexData.charAt(itr));
	}
	/* parsing the first four bytes of the ICMP header as this header is fixed.
	 * Parsing till Checksum
	 */
	for(int which_field=0;which_field<ICMPHeader.length();which_field++) {
		if(which_field >=0 && which_field <=1) {
			parsing_header.append(ICMPHeader.charAt(which_field));
		}
		if(which_field == TYPE_START+1) {
			int val = Integer.parseInt(parsing_header.toString(), 16);
			if(debug) System.out.println("ICMP:  ----- ICMP Header -----");
			if(debug) System.out.println("ICMP:");
			switch(val) {
			case 0:	if(debug) System.out.println("ICMP:  Type = "+val+" Echo Reply");
				break;
			case 3: if(debug) System.out.println("ICMP:  Type = "+val+" Destination Unreachable");
				break;
			case 4: if(debug) System.out.println("ICMP:  Type = "+val+" Source Quench");
				break;
			case 5: if(debug) System.out.println("ICMP:  Type = "+val+" Redirect Message");
				break;
			case 8: if(debug) System.out.println("ICMP:  Type = "+val+" Echo Request");
				break;
			case 9: if(debug) System.out.println("ICMP:  Type = "+val+" Router Advertisement");
				break;
			case 10: if(debug) System.out.println("ICMP:  Type = "+val+" Router Solicitationt");
				break;
			case 11: if(debug) System.out.println("ICMP:  Type = "+val+" Time Exceeded");
				break;
			case 12: if(debug) System.out.println("ICMP:  Type = "+val+" Bad IP header");
				break;
			case 13: if(debug) System.out.println("ICMP:  Type = "+val+" Timestamp");
				break;
			case 14: if(debug) System.out.println("ICMP:  Type = "+val+" Timestamp Reply");
				break;
			}
			pktanalyzer.reset_parsing_buffer(parsing_header);
		}
		if(which_field >=TYPE_START+2 && which_field < CHKSUM_END) {
			parsing_header.append(ICMPHeader.charAt(which_field));
		}
		if(which_field == CHKSUM_END-1) {
			int val = Integer.parseInt(parsing_header.toString(), 16);
			if(debug) System.out.println("ICMP:  Code = "+val);
			pktanalyzer.reset_parsing_buffer(parsing_header);
		}
		if(which_field >= CHKSUM_END && which_field < AFTER_CHKSUM) {
			parsing_header.append(ICMPHeader.charAt(which_field));
		}
		if(which_field == AFTER_CHKSUM - 1) {
			if(debug) System.out.println("ICMP:  Checksum = "+"0x"+parsing_header.toString());
			if(debug) System.out.println("ICMP:");
		}
	}
	return null;
	}
}