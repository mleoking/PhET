package edu.colorado.phet.motion2d;

import java.text.DecimalFormat;

public class TestMotion2DModel {
    public static void main( String[] args ) {
        DecimalFormat df = new DecimalFormat( "#0.00" );
        int halfWindowSize = 13;
        int numPtsAverage = 2;
        Motion2DModel motion2DModel = new Motion2DModel( halfWindowSize, numPtsAverage );//, nGroup);
        double acc = 0; //1.75;
        int x0 = 10;
        int v0 = 2;


        for( int t = 0; t < 25; t++ ) {
            int x = x0 + v0 * t + (int)( ( 0.5 ) * acc * ( t * t ) );
            double vel = (double)x0 + acc * (double)t;
            motion2DModel.addPoint( x, 0 );
            motion2DModel.updateAverageValues();
            double vXComputed = motion2DModel.getXVel();
            double aXComputed = motion2DModel.getXAcc();
            double xNow = motion2DModel.getAvgXNow();
            double xMid = motion2DModel.getAvgXMid();
            double xBefore = motion2DModel.getAvgXBefore();
            System.out.println( "t=" + t + " x=" + x + " v=" + df.format( vel ) + " velComp=" + df.format( vXComputed ) + " accComp=" + df.format( aXComputed ) );
            System.out.println( "t=" + t + " x=" + x + " xNow=" + df.format( xNow ) + " xMid=" + df.format( xMid ) + " xBefore=" + df.format( xBefore ) );
        }
    }
}