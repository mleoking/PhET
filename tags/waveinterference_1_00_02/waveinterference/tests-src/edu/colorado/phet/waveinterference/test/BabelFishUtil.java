/* Copyright 2004, Sam Reid */
package edu.colorado.phet.waveinterference.test;

import java.io.*;
import java.util.StringTokenizer;

/**
 * User: Sam Reid
 * Date: May 22, 2006
 * Time: 7:42:03 PM
 * Copyright (c) May 22, 2006 by Sam Reid
 */

public class BabelFishUtil {
    private static String sep;

    public static void parsePostBabel( String orig, String babelOut, String dst ) throws IOException {
        File source = new File( orig );
        File babelFile = new File( babelOut );
//        File dstFile = new File( dst );
//        Properties properties = new Properties();
//        properties.load( new FileInputStream( source ) );
        BufferedReader babelReader = new BufferedReader( new FileReader( babelFile ) );
        String babelFileLine = babelReader.readLine();
        babelReader.close();

        BufferedReader bufferedReader = new BufferedReader( new FileReader( source ) );
        BufferedWriter writer = new BufferedWriter( new FileWriter( dst ) );
        String line = bufferedReader.readLine();
        StringTokenizer stringTokenizer = new StringTokenizer( babelFileLine, "?" );
        while( line != null ) {
            String key = line.substring( 0, line.indexOf( '=' ) ).trim();
//            line=line.replaceAll( "¿","");
//            line = line.replace( '¿', ' ' ).trim();
            System.out.println( "key = " + key );
            String value = stringTokenizer.nextToken().trim();
            System.out.println( "value = " + value );
//            String SEP_TOKEN = ",,, ";
//            sep = System.getProperty( "line.separator" );
//            sep = "";//System.getProperty( "line.separator" );
            writer.write( key + " = " + value + System.getProperty( "line.separator" ) );
            line = bufferedReader.readLine();
        }
        bufferedReader.close();
        writer.flush();
    }

    public static void writePreBabel( String pathname ) throws IOException {
        File source = new File( pathname );
        File dst = new File( pathname + "_pre_babelfish" );
//        Properties properties = new Properties();
//        properties.load( new FileInputStream( source ) );
        BufferedReader bufferedReader = new BufferedReader( new FileReader( source ) );
        BufferedWriter writer = new BufferedWriter( new FileWriter( dst ) );
        String line = bufferedReader.readLine();
        while( line != null ) {
            line = line.substring( line.indexOf( '=' ) + 1 ).trim();
//            String SEP_TOKEN = "!";
//            sep = System.getProperty( "line.separator" );
//            sep = "";//System.getProperty( "line.separator" );
            writer.write( line + "?" + System.getProperty( "line.separator" ) );
            line = bufferedReader.readLine();
        }
        bufferedReader.close();
        writer.flush();
    }

    public static void main( String[] args ) throws IOException {
        String orig = "C:\\PhET\\projects-ii\\waveinterference\\data\\localization\\wi.properties";
        String pathname = "C:\\PhET\\projects-ii\\waveinterference\\data\\localization\\wi.properies_post_babelfish_es";
        String dst = "C:\\PhET\\projects-ii\\waveinterference\\data\\localization\\wi_es.properties";
//        writePreBabel( orig );
        parsePostBabel( orig, pathname, dst );
    }
}
