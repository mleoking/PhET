// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.rotation.util;

import JSci.maths.LinearMath;
import JSci.maths.vectors.AbstractDoubleVector;

import java.util.ArrayList;

import edu.colorado.phet.common.motion.model.TimeData;

/**
 * Created by: Sam
 * Oct 25, 2007 at 11:32:34 AM
 */
public class MemoryProfiler {
    ArrayList data = new ArrayList();
    int count = 0;
    long startTime = System.currentTimeMillis();

    public void update() {
        count++;


        Runtime runtime = Runtime.getRuntime();
        runtime.gc();
//        runtime.gc();
        long maxMemory = runtime.maxMemory();
        long allocatedMemory = runtime.totalMemory();
        long freeMemory = runtime.freeMemory();
//        System.out.println( "free memory: " + freeMemory / 1024 );
//        System.out.println( "allocated memory: " + allocatedMemory / 1024 );
//        System.out.println( "max memory: " + maxMemory / 1024 );
//        System.out.println( "total free memory: " + ( freeMemory + ( maxMemory - allocatedMemory ) ) / 1024 );
//        System.out.println( "" );
//        System.out.println( "" );

        long elapsedTime = System.currentTimeMillis() - startTime;
        data.add( new TimeData( freeMemory, elapsedTime ) );
        while ( data.size() > 10 ) {
            data.remove( 0 );
        }
//        System.out.println( "RotationClock.freeMemoryKB() = " + RotationClock.freeMemoryBytes()/1024 );
        if ( count % 1 == 0 && count > 1 ) {


            double[] x = new double[data.size()];
            double[] y = new double[data.size()];
            for ( int i = 0; i < y.length; i++ ) {
                x[i] = ( (TimeData) data.get( i ) ).getTime();
                y[i] = ( (TimeData) data.get( i ) ).getValue();
            }
            double[][] vals = new double[2][x.length];
            vals[0] = x;
            vals[1] = y;
            AbstractDoubleVector out = LinearMath.linearRegression( vals );
//                System.out.println( "out = " + out );
            double offset = out.getComponent( 0 );
            double slope = out.getComponent( 1 );

//            double crashTime = ( -offset / slope ) / 1000.0;
            //y=mx+b
            //x=(y-b)/m
            double crashTime = ( maxMemory - offset ) / slope / 1000;
//            System.out.println( "freeMemory=" + freeMemory );//+ ", slope=" + slope + ", offset=" + offset + ", Expected memory outage in " + crashTime + " sec" );
            System.out.println( "freeMemory=" + freeMemory + ", slope=" + slope + ", offset=" + offset + ", Expected memory outage in " + crashTime + " sec" );
        }
    }

    public static void main( String[] args ) {
        MemoryProfiler memoryProfiler = new MemoryProfiler();
        ArrayList data = new ArrayList();
        for ( int i = 0; i < 100000; i++ ) {
            double[] d = new double[5000];
            data.add( d );
            memoryProfiler.update();
        }
    }
}
