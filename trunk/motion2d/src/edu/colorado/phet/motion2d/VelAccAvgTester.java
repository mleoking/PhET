package edu.colorado.phet.motion2d;

import java.text.DecimalFormat;

public class VelAccAvgTester {
    public static void main( String[] args ) {
        DecimalFormat df = new DecimalFormat( "#0.00" );
        int nP = 13;
        int nA = 2;
        int nGroup = 3;
        VelAccAvg vaa = new VelAccAvg( nP, nA );//, nGroup);
        double acc = 0; //1.75;
        int x0 = 10;
        int v0 = 2;


        for( int t = 0; t < 25; t++ ) {
            int x = x0 + v0 * t + (int)( ( 0.5 ) * acc * ( t * t ) );
            double vel = (double)x0 + acc * (double)t;
            vaa.addPoint( x, 0 );
            vaa.updateAvgXYs();
            double vXComputed = vaa.getXVel();
            double aXComputed = vaa.getXAcc();
            double xNow = vaa.getAvgXNow();
            double xMid = vaa.getAvgXMid();
            double xBefore = vaa.getAvgXBefore();
            System.out.println( "t=" + t + " x=" + x + " v=" + df.format( vel ) + " velComp=" + df.format( vXComputed ) + " accComp=" + df.format( aXComputed ) );
            System.out.println( "t=" + t + " x=" + x + " xNow=" + df.format( xNow ) + " xMid=" + df.format( xMid ) + " xBefore=" + df.format( xBefore ) );
        }
    }
}