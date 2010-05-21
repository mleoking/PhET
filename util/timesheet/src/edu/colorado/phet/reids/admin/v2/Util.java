package edu.colorado.phet.reids.admin.v2;

import java.util.Date;

/**
 * Created by IntelliJ IDEA.
 * User: Sam
 * Date: May 20, 2010
 * Time: 9:54:07 PM
 * To change this template use File | Settings | File Templates.
 */
public class Util {
    public static String secondsToElapsedTimeString(long sec) {
        long minutes = sec / 60;
        long secRemain = sec % 60;

        long hours = minutes / 60;
        long minRemain = minutes % 60;

        String text = hours + ":" + padZero(minRemain) + ":" + padZero(secRemain);
        return text;
    }

    private static String padZero(long minRemain) {
        String str = minRemain + "";
        if (str.length() == 1) return "0" + str;
        else return str;
    }

    public static void main(String[] args) {
        System.out.println("mil = " + secondsToElapsedTimeString(5));
        System.out.println("mil = " + secondsToElapsedTimeString(50));
        System.out.println("mil = " + secondsToElapsedTimeString(500));
        System.out.println("mil = " + secondsToElapsedTimeString(1000));
        System.out.println("mil = " + secondsToElapsedTimeString(2000));
        System.out.println("mil = " + secondsToElapsedTimeString(10000));
        System.out.println("mil = " + secondsToElapsedTimeString(1000 * 12));
        System.out.println("mil = " + secondsToElapsedTimeString(1000 * 60 * 2));
        System.out.println("mil = " + secondsToElapsedTimeString(1000 * 60 * 4));
        System.out.println("mil = " + secondsToElapsedTimeString(1000 * 60 * 60));
        System.out.println("mil = " + secondsToElapsedTimeString(1000 * 60 * 60 * 2 + 2000));
        for (long x = 0; x < 1000 * 60 * 60; x++) {
            System.out.println("x=" + x + ", " + secondsToElapsedTimeString(x));
        }
    }

    public static Date newDateToNearestSecond() {
        return new Date(currentTimeSeconds() * 1000);
    }

    public static long currentTimeSeconds() {
        return System.currentTimeMillis() / 1000;
    }
}
