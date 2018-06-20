
public class ethernet {
	public String ReadEthernetHeader(StringBuilder hexData, int data_size, PacketMetrics myPkt, boolean debug) {
		int EHTERNET_HEADER_LENGTH = 14;
		int EHTERNET_ADD_LEN = 6;
		int itr_start = 0;
		boolean parsed_dest_add = false;
		StringBuilder header_buffer = new StringBuilder();
		String next_header = null;
		
		header_buffer.append(hexData.charAt(itr_start++));
		// parsing destination port.
		for(int which_field=itr_start;which_field<EHTERNET_HEADER_LENGTH * 2;which_field++) {
			if(which_field < EHTERNET_ADD_LEN*2) {
					header_buffer.append(hexData.charAt(which_field));
					if(which_field % 2 != 0 ) {
						header_buffer.append(":");
				}
			}
			// parsing and printing packet size & printing Dest port.  
			if(which_field == EHTERNET_ADD_LEN*2 && !parsed_dest_add) {
				header_buffer.setCharAt(which_field+5, ',');
				if(debug) System.out.println("ETHER:  ----- Ether Header -----");
				if(debug) System.out.println("ETHER:");
				if(debug) System.out.println("ETHER:  Packet Size  = "+data_size+" bytes");
                                myPkt.setLenght(data_size);
				if(debug) System.out.println("ETHER:  Destination  = "+header_buffer);
				header_buffer.setLength(0);
				parsed_dest_add = true;
			}
			// appending source port data to buffer
			if(which_field >= EHTERNET_ADD_LEN*2 && which_field < EHTERNET_ADD_LEN*4) {
				header_buffer.append(hexData.charAt(which_field));
				if(which_field % 2 != 0 ) {
					header_buffer.append(":");
				}
			}
			// parsing and printing source port data
			if(which_field == EHTERNET_ADD_LEN*4) {
				header_buffer.setCharAt(17, ',');
				if(debug) System.out.println("ETHER:  Source       = "+header_buffer);
				header_buffer.setLength(0);
				parsed_dest_add = true;
			}
			// appending data for EtherType field
			if(which_field >= EHTERNET_ADD_LEN*4 && which_field < EHTERNET_HEADER_LENGTH*2) {
				header_buffer.append(hexData.charAt(which_field));
			}
		}
		if(debug) System.out.print("ETHER:  Ethertype    = "+header_buffer);
		if(pktanalyzer.IP.equals(header_buffer.toString()) ) {
			if(debug) System.out.println(" (IP)");
			if(debug) System.out.println("ETHER:");
			next_header = "IP";
		}
		pktanalyzer.total_header_size += EHTERNET_HEADER_LENGTH*2;
	return next_header;
	}
}