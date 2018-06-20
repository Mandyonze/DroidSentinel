/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Alkahest
 */
public class IPv4Header {
    public static final int VERSION_START_BIT = 0;

  public static final int VERSION_START_BYTE = 0;

  public static final int VERSION_END_BIT = 3;

  public static final int VERSION_END_BYTE = 0;

  public static final int IHL_START_BIT = 4;

  public static final int IHL_START_BYTE = 0;

  public static final int IHL_END_BIT = 7;

  public static final int IHL_END_BYTE = 0;

  public static final int DIFFSERV_START_BIT = 8;

  public static final int DIFFSERV_START_BYTE = 1;

  public static final int DIFFSERV_END_BIT = 15;

  public static final int DIFFSERV_END_BYTE = 1;

  public static final int TOTALLEN_START_BIT = 16;

  public static final int TOTALLEN_START_BYTE = 2;

  public static final int TOTALLEN_END_BIT = 31;

  public static final int TOTALLEN_END_BYTE = 3;

  public static final int IDENTIFICATION_START_BIT = 32;

  public static final int IDENTIFICATION_START_BYTE = 4;

  public static final int IDENTIFICATION_END_BIT = 47;

  public static final int IDENTIFICATION_END_BYTE = 5;

  public static final int FLAGS_START_BIT = 48;

  public static final int FLAGS_START_BYTE = 6;

  public static final int FLAGS_END_BIT = 50;

  public static final int FLAGS_END_BYTE = 6;

  public static final int FRAGOFFSET_START_BIT = 51;

  public static final int FRAGOFFSET_START_BYTE = 6;

  public static final int FRAGOFFSET_END_BIT = 63;

  public static final int FRAGOFFSET_END_BYTE = 7;

  public static final int TTL_START_BIT = 64;

  public static final int TTL_START_BYTE = 8;

  public static final int TTL_END_BIT = 71;

  public static final int TTL_END_BYTE = 8;

  public static final int PROTOCOL_START_BIT = 72;

  public static final int PROTOCOL_START_BYTE = 9;

  public static final int PROTOCOL_END_BIT = 79;

  public static final int PROTOCOL_END_BYTE = 9;

  public static final int HDRCHECKSUM_START_BIT = 80;

  public static final int HDRCHECKSUM_START_BYTE = 10;

  public static final int HDRCHECKSUM_END_BIT = 95;

  public static final int HDRCHECKSUM_END_BYTE = 11;

  public static final int SRCADDR_START_BIT = 96;

  public static final int SRCADDR_START_BYTE = 12;

  public static final int SRCADDR_END_BIT = 127;

  public static final int SRCADDR_END_BYTE = 15;

  public static final int DSTADDR_START_BIT = 128;

  public static final int DSTADDR_START_BYTE = 16;

  public static final int DSTADDR_END_BIT = 159;

  public static final int DSTADDR_END_BYTE = 19;

  public static final int TOTAL_HEADER_LENGTH = 20;
}
