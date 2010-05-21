package edu.colorado.phet.reids.admin.v2;

import java.text.DecimalFormat;

/**
 * Created by IntelliJ IDEA.
 * User: Sam
 * Date: May 20, 2010
 * Time: 9:54:07 PM
 * To change this template use File | Settings | File Templates.
 */
public class Util {
    private static DecimalFormat format = new DecimalFormat(".000");

    public static String millisecondsToElapsedTimeString(long v) {
        long sec = v / 1000;
        long msecRemain = v % 1000;

        long minutes = sec / 60;
        long secRemain = sec % 60;

        long hours = minutes / 60;
        long minRemain = minutes % 60;

        String text = hours + ":" + padZero(minRemain) + ":" + padZero(secRemain)  + format.format(msecRemain / 1000.0);
        return text;
    }

    private static String padZero(long minRemain) {
        String str = minRemain+"";
        if (str.length()==1) return "0"+str;
        else return str;
    }

    public static void main(String[] args) {
        System.out.println("mil = " + millisecondsToElapsedTimeString(5));
        System.out.println("mil = " + millisecondsToElapsedTimeString(50));
        System.out.println("mil = " + millisecondsToElapsedTimeString(500));
        System.out.println("mil = " + millisecondsToElapsedTimeString(1000));
        System.out.println("mil = " + millisecondsToElapsedTimeString(2000));
        System.out.println("mil = " + millisecondsToElapsedTimeString(10000));
        System.out.println("mil = " + millisecondsToElapsedTimeString(1000*12));
        System.out.println("mil = " + millisecondsToElapsedTimeString(1000*60*2));
        System.out.println("mil = " + millisecondsToElapsedTimeString(1000*60*4));
        System.out.println("mil = " + millisecondsToElapsedTimeString(1000*60*60));
        System.out.println("mil = " + millisecondsToElapsedTimeString(1000*60*60*2+2000));
        for (long x=0;x<1000*60*60;x++){
            System.out.println("x="+x+", "+millisecondsToElapsedTimeString(x));
        }
    }
}
