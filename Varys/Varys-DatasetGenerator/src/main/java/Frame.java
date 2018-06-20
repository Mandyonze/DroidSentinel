/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Alkahest
 */
import java.util.ArrayList;
import java.util.List;

/**
 * Created by marcelforgac on 16.3.15.
 */
public class Frame {

    public List<String> frameList ;
    public List<Integer> frameLength;
    public List<String> frameType;


    public void FrameParser (String hexString,Integer length) {
        String parseFrame = "";

        parseFrame = S32(hexString);
        parseFrame = S16(parseFrame);
        parseFrame = S2(parseFrame);

        frameList.add(parseFrame);
        frameLength.add(length);
    }

    private static String S32(String hexAddress) {
        return hexAddress.replaceAll("(.{32})","$1\n");
    }
    private static String S16(String hexAddress) {
        return hexAddress.replaceAll("(.{16})","$1  ");
    }
    private static String S2(String hexAddress){
        return hexAddress.replaceAll("(.{2})","$1 ");
    }


    public void Clear() {

        frameList = new ArrayList<>();
        frameLength = new ArrayList<>();
        frameType = new ArrayList<>();
    }

}