
import java.sql.Date;
import java.util.HashMap;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Alkahest
 */
public class PacketMetrics {
    
    String IpSrcAdress;
    String IpDstAdress;
    String PtSrc;
    String PtDst;
    Integer lenght;
    java.sql.Date when;

    HashMap<String, Boolean> Headers;
    
    public PacketMetrics() {
        
        Headers = new HashMap<String, Boolean>();
        Headers.put("IP", false);
        Headers.put("TCP", false);
        Headers.put("UDP", false);
        Headers.put("ICMP", false);
    }

    
    public PacketMetrics(java.sql.Date now,String IpSrcAdress, String IpDstAdress, String PtSrc, String PtDst, Integer lenght) {
        this.IpSrcAdress = IpSrcAdress;
        this.IpDstAdress = IpDstAdress;
        this.PtSrc = PtSrc;
        this.PtDst = PtDst;
        this.lenght = lenght; //bytes
        this.when=now;
        
        Headers = new HashMap<String, Boolean>();
        Headers.put("IP", false);
        Headers.put("TCP", false);
        Headers.put("UDP", false);
        Headers.put("ICMP", false);
    }

    public String getIpSrcAdress() {
        return IpSrcAdress;
    }

    
    public String getIpDstAdress() {
        return IpDstAdress;
    }

    public String getPtSrc() {
        return PtSrc;
    }

    public String getPtDst() {
        return PtDst;
    }

    public Integer getLenght() {
        return lenght;
    }

    public void setIpSrcAdress(String IpSrcAdress) {
        this.IpSrcAdress = IpSrcAdress;
    }

    public void setIpDstAdress(String IpDstAdress) {
        this.IpDstAdress = IpDstAdress;
    }

    public void setPtSrc(String PtSrc) {
        this.PtSrc = PtSrc;
    }

    public void setPtDst(String PtDst) {
        this.PtDst = PtDst;
    }

    public void setLenght(Integer lenght) {
        this.lenght = lenght;
    }

    public Date getWhen() {
        return when;
    }

    public void setWhen(Date when) {
        this.when = when;
    }

    public HashMap<String, Boolean> getHeaders() {
        return Headers;
    }
    
    
    
    
}
