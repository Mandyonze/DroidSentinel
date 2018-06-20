
import java.math.BigInteger;

public class tcp {
		short SRC_PORT_START = 0;
		short SRC_PORT_END = 1;
		short DEST_PORT_START = 2*2;
		short DEST_PORT_END = 3*2;
		short SEQ_START = 4*2;
		short SEQ_END = 7*2;
		short ACK_START = 8*2;
		short ACK_END = 11*2;
		short WINDOW_SIZE_START = 14*2;
		short WINDOW_SIZE_END = 15*2;
		short CHKSUM_START = 16*2;
		short CHKSUM_END = 17*2;
		short URG_PTR_START = 18*2;
		short URG_PTR_END = 19*2;
		
	public String ReadTCPHeader(StringBuilder hexData, int data_size, PacketMetrics myPkt, boolean debug) {
		StringBuilder TCPHeader = new StringBuilder();
		StringBuilder parsing_header = new StringBuilder();
		StringBuilder flag_buf = new StringBuilder();
		StringBuilder data_buf = new StringBuilder();
		
		boolean urg_status = false;
		
		//src port+dest port+seq num+ack number
		int data_len_in_tcp = 12*2; 
		
		// Start Parsing Header
		if(debug) System.out.println("TCP:  ----- TCP Header ----- ");
		if(debug)System.out.println("TCP:");
		
		for(int itr=pktanalyzer.total_header_size+data_len_in_tcp;itr<pktanalyzer.total_header_size+data_len_in_tcp+4;itr++) {
			flag_buf.append(hexData.charAt(itr));
		}
		// Calculated the DataOffest but will be printing it later to the console.
		int data_offset = Character.getNumericValue(flag_buf.charAt(0));
		int flags = Integer.parseInt(flag_buf.toString(), 16);
		
		int from = pktanalyzer.total_header_size;
		pktanalyzer.total_header_size += 20*2;
		/*
		 * Data offset is 32 bit word so 8 value means 8*4 = 32 bytes.
		 * But the header size can be only 20 bytes max excluding the 
		 * Options field. We can calculate the Options field as
		 * 32(data offset) - 20(header size) = 12 bytes.
		 */
		int to = pktanalyzer.total_header_size + (data_offset*4*2);
		pktanalyzer.reset_parsing_buffer(parsing_header);
		for(int itr = from;itr<to;itr++) {
			TCPHeader.append(hexData.charAt(itr));
		}
		for(int which_field=0;which_field<TCPHeader.length();which_field++) {
			// parsing source port
			if(which_field >=SRC_PORT_START && which_field < DEST_PORT_START) {
				parsing_header.append(TCPHeader.charAt(which_field));
			}
			if(which_field == DEST_PORT_START) {
				if(debug) System.out.println("TCP:  Source port = "+Integer.parseInt(parsing_header.toString(), 16));
                                myPkt.setPtSrc(parsing_header.toString());
                                pktanalyzer.reset_parsing_buffer(parsing_header);
			}
			// parsing Destination port.
			if(which_field >=DEST_PORT_START && which_field < SEQ_START) {
				parsing_header.append(TCPHeader.charAt(which_field));
			}
			if(which_field == SEQ_START) {
				if(debug) System.out.println("TCP:  Destination port = "+Integer.parseInt(parsing_header.toString(), 16));
				myPkt.setPtDst(parsing_header.toString());
                                pktanalyzer.reset_parsing_buffer(parsing_header);
			}
			// parsing Sequence number
			if(which_field >=SEQ_START && which_field < ACK_START) {
				parsing_header.append(TCPHeader.charAt(which_field));
			}
			if(which_field == ACK_START) {
				//BigInteger big1 = new BigInteger(parsing_header.toString(), 16);
				if(debug) System.out.println("TCP:  Sequence Number = "+BigInteger.valueOf(Long.parseLong(parsing_header.toString(), 16)));
				pktanalyzer.reset_parsing_buffer(parsing_header);
			}
			// parsing Acknowledgement number
			if(which_field >=ACK_START && which_field < ACK_END+2) {
				parsing_header.append(TCPHeader.charAt(which_field));
			}
			if(which_field == ACK_END+2) {
				//BigInteger big1 = new BigInteger(parsing_header.toString(), 16);
				if(debug) System.out.println("TCP:  Acknowledgement Number = "+BigInteger.valueOf(Long.parseLong(parsing_header.toString(), 16)));
				// We multiply by 4 as data offset is 32bit word field.
				if(debug) System.out.println("TCP:  Data offset = "+data_offset*4);
				pktanalyzer.reset_parsing_buffer(parsing_header);
			}
			if(which_field>=27 && which_field < 28) {
				// Do nothing as we have already parsed data offset and other flags
				// in the starting.
			}
			if(which_field == WINDOW_SIZE_START) {
				// parsing the Flags here, data offset already parsed.
				int bit_shifter = 32;
				// since flags field has data offset of 4 bits + 3 bits are reserved
				// so total we have to skip 7 bits from MSB to reach the first flag.
				// the value bit_shifter = 256 binary is = 0000 0001 0000 0000.
				// we will check for each bits from here.
				StringBuilder print_buffer = new StringBuilder();
				for(int iterator=0;iterator<9;iterator++) {
					// to print flags take 2 bytes i.e. LSB.
					if(iterator == 0) {
						if(debug) System.out.println("TCP:  Flags = 0x"+flag_buf.charAt(2)+flag_buf.charAt(3));
					}
					int val = (bit_shifter & flags);
					if(val == 32 && bit_shifter == 32) {
						print_buffer.append("TCP:        ..1. .... =  Urgent Pointer\n");
						urg_status = true;
					}
					if(val == 0 && bit_shifter == 32) {
						print_buffer.append("TCP:        ..0. .... =  No Urgent Pointer\n");
					}
					if(val == 16 && bit_shifter == 16) {
						print_buffer.append("TCP:        ...1 .... =  Acknowledgement\n");
					}
					if(val == 0 && bit_shifter == 16) {
						print_buffer.append("TCP:        ...0 .... =  No Acknowledgement\n");
					}
					if(val == 8 && bit_shifter == 8) {
						print_buffer.append("TCP:        .... 1... =  Push\n");
					}
					if(val == 0 && bit_shifter == 8) {
						print_buffer.append("TCP:        .... 0... =  No Push\n");
					}
					if(val == 4 && bit_shifter == 4) {
						print_buffer.append("TCP:        .... .1.. =  Reset\n");
					}
					if(val == 0 && bit_shifter == 4) {
						print_buffer.append("TCP:        .... .0.. =  No Reset\n");
					}
					if(val == 2 && bit_shifter == 2) {
						print_buffer.append("TCP:        .... ..1. =  Sync\n");
					}
					if(val == 0 && bit_shifter == 2) {
						print_buffer.append("TCP:        .... ..0. =  No Sync\n");
					}
					if(val == 1 && bit_shifter == 1) {
						print_buffer.append("TCP:        .... ...1 =  Fin");
					}
					if(val == 0 && bit_shifter == 1) {
						print_buffer.append("TCP:        .... ...0 =  No Fin");
					}
					bit_shifter = bit_shifter >> 1;
				}
				if(bit_shifter == 0) {
					if(debug) System.out.println(print_buffer.toString());
					pktanalyzer.reset_parsing_buffer(parsing_header);
					print_buffer.setLength(0);
				}
		 }
		 // Appending Window Size data to buffer
		 if(which_field >=WINDOW_SIZE_START && which_field < CHKSUM_START) {
			parsing_header.append(TCPHeader.charAt(which_field));
		 }
		 // printing the Window size value
		 if(which_field == CHKSUM_START) {
			if(debug) System.out.println("TCP:  Window Size = "+Integer.parseInt(parsing_header.toString(), 16));
			pktanalyzer.reset_parsing_buffer(parsing_header);
		 }
		 // Appending the checksum data 
		 if(which_field >=CHKSUM_START && which_field < URG_PTR_START) {
			parsing_header.append(TCPHeader.charAt(which_field));
		 }
		 // Printing the Checksum value.
		 if(which_field == URG_PTR_START) {
			 if(debug) System.out.println("TCP:  Checksum = 0x"+parsing_header.toString());
			 pktanalyzer.reset_parsing_buffer(parsing_header);
		 }
		 // Appending data for Options 
		 if(which_field >= URG_PTR_START && urg_status && which_field < URG_PTR_END+2) {
				parsing_header.append(TCPHeader.charAt(which_field));
		 }
		 // Printing the Urgent Pointer if the URG flag is not zero.
		 if(which_field == URG_PTR_END+1 && urg_status) {
			 if(debug) System.out.println("TCP:  Urgent pointer = "+Integer.parseInt(parsing_header.toString(), 16));
		 }
		 // Printing the Urgent Pointer
		 if(which_field == URG_PTR_END+1 && urg_status == false) {
			 if(debug) System.out.println("TCP:  Urgent pointer = 0");
		 }
		}
		// parse Option field, this field has some values only when the data offset > 20 bytes.
		int options_size = TCPHeader.length()/2 - 20*2;
		if(debug) System.out.println("TCP:  Options = "+options_size+" bytes");
		if(debug) System.out.println("TCP: ");
		
		pktanalyzer.reset_parsing_buffer(parsing_header);
		
		from = pktanalyzer.total_header_size;
		to = hexData.length();
		
		// for loop to only copy the data of tcp packet.
		for(int itr=from; itr<to; itr++) {
			data_buf.append(hexData.charAt(itr));
			if(itr == 108 ) {
			/* Appending value of the Options field to decide 
			 * next steps for printing.
			 */
			parsing_header.append(hexData.charAt(itr));
			parsing_header.append(hexData.charAt(itr+1));
			}
		}
		/* "val" is the value of the OPtions field of TCP Header
		 * after the TCP Header
		 */
		int val = Integer.parseInt(parsing_header.toString(), 16);
		print_data(val, data_buf, hexData.length() - pktanalyzer.total_header_size, debug);
		// End Parsing Header
		return "no header";
	}
	public static void print_data(int val, StringBuilder data_buf, int len, boolean debug) {
		int skip_bytes = 0, octet_counts = 0, pattern = 0;
		StringBuilder print_data = new StringBuilder();
		if(val == 1 || val == 0) {
			// 2 means 1 Byte
			skip_bytes = 2;
			pattern = 1;
		}
		if(val == 2) {
			// 8 means 4 bytes
			skip_bytes = 8;
			pattern = 3;
		}
		if(debug) System.out.println("TCP:  Data: ");
		if(debug) System.out.print("TCP:  ");
		for(int itr=skip_bytes; itr<len; itr++) {
			print_data.append(data_buf.charAt(itr));
			if(itr % 4 == pattern) {
				if(octet_counts == 7 && itr !=8) {
					print_data.append("\n");
					if(debug) System.out.print(print_data.toString());
					print_data.setLength(0);
					octet_counts = 0;
					if(debug) System.out.print("TCP:  ");
				}
				else {
				print_data.append(" ");
				octet_counts++;
				}
			}
		}
		if(debug) System.out.println("\n");
	}
}