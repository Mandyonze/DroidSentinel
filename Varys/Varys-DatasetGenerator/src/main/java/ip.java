
public class ip {
	public String ReadIPHeader(StringBuilder hexData, int data_size,PacketMetrics myPkt, boolean debug) {
		StringBuilder IPHeader = new StringBuilder();
		StringBuilder parsing_header = new StringBuilder();
		StringBuilder ip_address = new StringBuilder();
		String next_header = null;
		String no_options = null;
		int ETHERNET_HEADER_LENGTH = 14;
		int IHL = 0;
		/* As we have each nibble as a single character so the
		 * standard value of a byte is reduced to fourth.
		 */
		int BYTE = 2;

		if(debug) System.out.println("IP:   ----- IP Header ----- ");

		parsing_header.append(hexData.charAt(ETHERNET_HEADER_LENGTH*2));
		int val1 = Integer.parseInt(parsing_header.toString(), 16);
		int ihl_bit_shifter = 1;
		int ihl_val = 0;
		// parsing version
		for(int bits_in_byte=0;bits_in_byte<4;bits_in_byte++) {
			ihl_val += (val1 & ihl_bit_shifter);
			ihl_bit_shifter = ihl_bit_shifter << 1;
			if(bits_in_byte == 3) {
				IHL = ihl_val*5;
				pktanalyzer.reset_parsing_buffer(parsing_header);
			}
		}
		/* Copy Data after the Ethernet Header, which is 14 bytes,
		 * which means we have to skip 28 times in StringBuilder to
		 * reach IP Header and read till IHLx2 times.
		 */
		int total_length = (ETHERNET_HEADER_LENGTH*2)+(IHL * 2);
		for(int iterator=ETHERNET_HEADER_LENGTH*2;iterator<total_length;iterator++){
			IPHeader.append(hexData.charAt(iterator));
		}
		/* If the size of the IHL is 40 this means that Options field is empty for this
		 * data packet.
		 */
		if(IHL*2 == 40) {
			no_options = "No Options";
		}
		// Start parsing the IP Header now.
		for(int which_field=0;which_field<IHL*2;which_field++) {
			// parsing version
			if(which_field == 0) {
				parsing_header.append(IPHeader.charAt(which_field));
				int val = Integer.parseInt(parsing_header.toString(), 16);
				int ver_bit_shifter = 1;
				int ver_val = 0;
				for(int bits_in_byte=0;bits_in_byte<4;bits_in_byte++) {
					ver_val += (val & ver_bit_shifter);
					ver_bit_shifter = ver_bit_shifter << 1;
					if(bits_in_byte == 3) {
						if(debug)System.out.println("IP:   Version = "+ver_val);
						pktanalyzer.reset_parsing_buffer(parsing_header);
					}
				}
			}
			if(which_field == 1) {
				if(debug)System.out.println("IP:   Header length = "+IHL);
			}
			// Appending data for DSCP and ECN fields.
			if(which_field >= 2 && which_field <= 3) {
				parsing_header.append(IPHeader.charAt(which_field));
			}
			// parsing DSCP field
			if(which_field == 3) {
				int val = Integer.parseInt(parsing_header.toString(), 16);
				int ver_bit_shifter = 4;
				int ver_val = 0;
				for(int bits_in_byte=0;bits_in_byte<6;bits_in_byte++) {
					ver_val += (val & ver_bit_shifter);
					ver_bit_shifter = ver_bit_shifter << 1;
					if(bits_in_byte == 1) {
						/* As we chosen bit shifter as 4 to skip the 2 bits of LSB
						 * that represents ECN.
						 */
						ver_val = ver_val / 4;
						if(debug) System.out.println("IP:   DSCP (6 bits) = "+"0x"+Integer.toHexString(ver_val));
						switch(ver_val) {
						case 0: if(debug) System.out.println("IP:   000000.. = "+"Best effort");
								break;
						case 46: if(debug) System.out.println("IP:  101110.. = "+"Expedited forwarding (EF)");
								break;
						case 10: if(debug) System.out.println("IP:  001010.. = "+"AF11");
								break;
						case 12: if(debug) System.out.println("IP:  001100.. = "+"AF12");
								break;
						case 14: if(debug) System.out.println("IP:  001110.. = "+"AF13");
								break;
						case 18: if(debug) System.out.println("IP:  010010.. = "+"AF21");
								break;
						case 20: if(debug) System.out.println("IP:  010100.. = "+"AF22");
								break;
						case 22: if(debug) System.out.println("IP:  010110.. = "+"AF23");
								break;
						case 26: if(debug) System.out.println("IP:  011010.. = "+"AF31");
								break;
						case 28: if(debug) System.out.println("IP:  011100.. = "+"AF32");
								break;
						case 30: if(debug) System.out.println("IP:  011110.. = "+"AF33");
								break;
						case 34: if(debug) System.out.println("IP:  100010.. = "+"AF41");
								break;
						case 36: if(debug) System.out.println("IP:  100100.. = "+"AF42");
								break;
						case 38: if(debug) System.out.println("IP:  100110.. = "+"AF43");
								break;
						}
					}
				}
			}
			// parsing ECN field
			if(which_field == 3) {
				int val = Integer.parseInt(parsing_header.toString(), 16);
				int ver_bit_shifter = 1;
				for(int bits_in_byte=0;bits_in_byte<2;bits_in_byte++) {
					ver_bit_shifter = ver_bit_shifter << 1;
					if(bits_in_byte == 1) {
						if(debug) System.out.println("IP:   ECN (2 bits) = "+"0x"+parsing_header.toString());
						switch(val) {
						case 0: if(debug) System.out.println("IP:   ......00 = "+"Non ECT");
								break;
						case 1: if(debug) System.out.println("IP:   ......01 = "+"ECT(1)");
								break;
						case 2: if(debug) System.out.println("IP:   ......10 = "+"ECT(0)");
								break;
						case 3: if(debug) System.out.println("IP:   ......11 = "+"Congestion Encountered");
								break;
						}
						pktanalyzer.reset_parsing_buffer(parsing_header);
					}
				}
			}
			
			/* Appending the total length field. Total length contains size of header
			 * and data included.
			 */
			if(which_field > (BYTE*2)-1 && which_field < (BYTE*4)) {
				parsing_header.append(IPHeader.charAt(which_field));
			}
			// Parsing the total length field
			if(which_field == (BYTE*4) -1) {
				// Converting the string to integer value.
				int val = Integer.parseInt(parsing_header.toString(), 16);
				if(debug) System.out.println("IP:   Total length = "+val+" bytes");
				//pktanalyzer.reset_parsing_buffer(parsing_header);
				pktanalyzer.reset_parsing_buffer(parsing_header);
			}
			// Parsing the Identification field
			if(which_field >= BYTE*4 && which_field < (BYTE*6) ) {
				parsing_header.append(IPHeader.charAt(which_field));
			}
			if(which_field == (BYTE*6)-1) {
				int val = Integer.parseInt(parsing_header.toString(), 16);
				if(debug) System.out.println("IP:   Identification = "+"0x"+parsing_header.toString()+ " ("+val+")");
				pktanalyzer.reset_parsing_buffer(parsing_header);
				// iterator's value after completion of this would be 12
			}
			// If condition for checking "flags" field.
			if(which_field >= BYTE*6 && which_field < BYTE*8) {
				parsing_header.append(IPHeader.charAt(which_field));
			}
			if(which_field == BYTE*8) {
				int val = Integer.parseInt(parsing_header.toString(),16);
				int org_val_to_represent = 0;
				/* as the "val" above is originally a left nibble so we need to
				 * shift the value four time to compensate for right nibble.
				 */
				int bit_test = 8192; // equivalent binary 0001 0000 0000 0000
				for(int itr=0;itr<3;itr++) {
					org_val_to_represent += (bit_test & val);
					bit_test = bit_test<<1;
				}
				if(debug) System.out.println("IP:   Flags = "+"0x"+(org_val_to_represent / 8192));
				bit_test = 16384;
				if((bit_test & val) == 16384) {
					if(debug) System.out.println("IP:   \t.1..  = "+"Don't fragment");
				}
				else {
					if(debug) System.out.println("IP:   \t.0..  = "+"Fragment packet");
				}
				bit_test = bit_test>>1;
				if((bit_test & val) == 8192) {
					if(debug) System.out.println("IP:   \t..1.  = "+"fragment packets following");
				}
				else {
					if(debug) System.out.println("IP:    \t..0.  = "+"last fragment");
				}
			}
			// Calculate fragment offset.
			if(which_field == (BYTE*8)) {
				int val = Integer.parseInt(parsing_header.toString(), 16);
				int total_val = 0;
				/* Since flags and fragment offset are 2 bytes so setting this
				 * value for testing.
				 */
				int bit_shift_tester = 4096; // 0001 0000 0000 0000
				for(int itr=0;itr<13;itr++) {
					total_val += (bit_shift_tester & val);
					bit_shift_tester = bit_shift_tester >> 1;
				}
				if(debug) System.out.println("IP:   Fragment offset = "+total_val + " bytes");
				pktanalyzer.reset_parsing_buffer(parsing_header);
			}
			// Parsing Time to Live field
			if(which_field >= (BYTE*8) && which_field < (BYTE*9)) {
				parsing_header.append(IPHeader.charAt(which_field));
			}
			// Printing Time to Live field
			if(which_field == (BYTE*8)+1) {
				int val = Integer.parseInt(parsing_header.toString(), 16);
				if(debug) System.out.println("IP:   Time to live = "+val+" seconds/hops");
				pktanalyzer.reset_parsing_buffer(parsing_header);
			}
			if(which_field >= (BYTE*9) && which_field < (BYTE*10)) {
				parsing_header.append(IPHeader.charAt(which_field));
			}
			// check what protocol does IP header has next.
			if(which_field == (BYTE*9) + 1) {
				int val = Integer.parseInt(parsing_header.toString(), 16);
				if(val == 17)
					next_header = "UDP";
				if(val == 1)
					next_header = "ICMP";
				if(val == 6)
					next_header = "TCP";
				if(debug) System.out.println("IP:   Protocol = "+val+" ("+next_header+")");
				pktanalyzer.reset_parsing_buffer(parsing_header);
			}
			if(which_field >= (BYTE*10) && which_field < (BYTE*12)) {
				parsing_header.append(IPHeader.charAt(which_field));
			}
			// printing Header Checksum
			if(which_field == (BYTE*12) - 1) {
				if(debug) System.out.println("IP:   Header checksum = "+"0x"+parsing_header.toString());
				pktanalyzer.reset_parsing_buffer(parsing_header);
			}
			// Parsing Source address
			if(which_field >= (BYTE*12) && which_field < (BYTE*16)) {
				parsing_header.append(IPHeader.charAt(which_field));
				if(which_field % 2 != 0) {
					int val = Integer.parseInt(parsing_header.toString(),16);
					ip_address.append(val);
					ip_address.append(".");
					pktanalyzer.reset_parsing_buffer(parsing_header);
				}
			}
			if(which_field == (BYTE*16) - 1) {
				ip_address.setCharAt(ip_address.length()-1, ',');
				if(debug) System.out.println("IP:   Source address = "+ip_address);
                                myPkt.setIpSrcAdress(removeLastChar(ip_address.toString()));
				pktanalyzer.reset_parsing_buffer(parsing_header);
				ip_address.setLength(0);
			}
			// Parsing Destination Address
			if(which_field >= (BYTE*16) && which_field < (BYTE*20)) {
				parsing_header.append(IPHeader.charAt(which_field));
				if(which_field % 2 != 0) {
					int val = Integer.parseInt(parsing_header.toString(),16);
					ip_address.append(val);
					ip_address.append(".");
					pktanalyzer.reset_parsing_buffer(parsing_header);
				}
			}
			// Printing Destination Address
			if(which_field == (BYTE*20) - 1) {
				ip_address.setCharAt(ip_address.length()-1, ' ');
				if(debug)System.out.println("IP:   Destination address = "+ip_address);
                                myPkt.setIpDstAdress(removeLastChar(ip_address.toString()));
				pktanalyzer.reset_parsing_buffer(parsing_header);
				ip_address.setLength(0);
				if(IHL*2 == 40) {
					if(debug)System.out.println("IP:   "+no_options);
					if(debug)System.out.println("IP:");
				}
			}
		}
		pktanalyzer.total_header_size += (IHL*2);
		return next_header;
	}
    private static String removeLastChar(String str) {
        return str.substring(0, str.length() - 1);
    }
}