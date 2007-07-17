package edu.colorado.phet.motion2d;

import java.util.Arrays;

//Helper Class for Velocity-Acceleration GUI.  This class computes
//average position and double-averaged velocity and acceleration.

public class Motion2DModel {
    private int numPoints;  	//Number of points in stack, must be odd
    private int halfWindowSize;		//averaging radius, #of pts averaged = (2*nA + 1)
    private int numPtsAvg;	//Number of points averaged for vel, acc
    private double avgXNow;
    private double avgYNow;
    private double avgXMid;
    private double avgYMid;
    private double avgXBefore;
    private double avgYBefore;
    private int[] x, y;		//last nP x- and y-coordinates from mousemovements
    private double[] xAvg, yAvg;	//averaged position stacks

    public Motion2DModel( int halfWindowSize, int numPtsAvg ) {
        this.halfWindowSize = halfWindowSize;
        this.numPtsAvg = numPtsAvg;
        this.numPoints = 3 * numPtsAvg + 2 * halfWindowSize;
        this.x = new int[numPoints];
        this.y = new int[numPoints];
        this.xAvg = new double[numPoints - 2 * halfWindowSize];
        this.yAvg = new double[numPoints - 2 * halfWindowSize];

        for( int i = 0; i < numPoints; i++ ) {
            x[i] = 100;
            y[i] = 100;
        }
    }

    static class CurrentState {
        int[] x;
        int[] y;
        double[] xAvg;
        double[] yAvg;

        public CurrentState( int[] x, int[] y, double[] xAvg, double[] yAvg ) {
            this.x = copy( x );
            this.y = copy( y );
            this.xAvg = copy( xAvg );
            this.yAvg = copy( yAvg );
        }

        private int[] copy( int[] array ) {
            int[] out = new int[array.length];
            System.arraycopy( array, 0, out, 0, array.length );
            return out;
        }

        private double[] copy( double[] array ) {
            double[] out = new double[array.length];
            System.arraycopy( array, 0, out, 0, array.length );
            return out;
        }

        public boolean equals( Object obj ) {
            if( obj instanceof CurrentState ) {
                CurrentState cs = (CurrentState)obj;
                return Arrays.equals( x, cs.x ) && Arrays.equals( y, cs.y ) &&
                       Arrays.equals( xAvg, cs.xAvg ) && Arrays.equals( yAvg, cs.yAvg );
            }
            else {
                return false;
            }
        }
    }

    //add new point to position arrays, update averagePosition arrays
    public boolean addPoint( int xNow, int yNow ) {
        //update x and y-arrays
        CurrentState initialState = new CurrentState( x, y, xAvg, yAvg );
        for( int i = 0; i < ( numPoints - 1 ); i++ ) {
            x[i] = x[i + 1];
            y[i] = y[i + 1];
        }
        x[numPoints - 1] = xNow;
        y[numPoints - 1] = yNow;

        //update averagePosition arrays
        for( int i = 0; i < ( numPoints - 2 * halfWindowSize ); i++ ) {
            xAvg[i] = 0;
            yAvg[i] = 0;  //reset to zero
            for( int j = -halfWindowSize; j <= +halfWindowSize; j++ ) {
                xAvg[i] += (double)x[i + halfWindowSize + j];
                yAvg[i] += (double)y[i + halfWindowSize + j];
            }

            xAvg[i] = xAvg[i] / ( 2 * halfWindowSize + 1 );
            yAvg[i] = yAvg[i] / ( 2 * halfWindowSize + 1 );
        }
        CurrentState finalState = new CurrentState( x, y, xAvg, yAvg );
        if( !initialState.equals( finalState ) ) {
            return true;
        }
        return false;
    }//end of addPoint() method

    public boolean updateAverageValues() {
        int nStack = numPoints - 2 * halfWindowSize;		//# of points in averagePostion stacks
        double sumXBefore = 0;
        double sumYBefore = 0;
        double sumXMid = 0;
        double sumYMid = 0;
        double sumXNow = 0;
        double sumYNow = 0;

        //Compute avgXBefore, avgYBefore
        for( int i = 0; i <= ( numPtsAvg - 1 ); i++ ) {
            sumXBefore += xAvg[i];
            sumYBefore += yAvg[i];
        }
        this.avgXBefore = sumXBefore / numPtsAvg;
        this.avgYBefore = sumYBefore / numPtsAvg;

        //Compute avgXMid, avgYMid
        for( int i = ( nStack - numPtsAvg ) / 2; i <= ( nStack + numPtsAvg - 2 ) / 2; i++ ) {
            sumXMid += xAvg[i];
            sumYMid += yAvg[i];
        }
        this.avgXMid = sumXMid / numPtsAvg;
        this.avgYMid = sumYMid / numPtsAvg;

        //Compute avgXNow, avgYNow
        for( int i = ( nStack - numPtsAvg ); i <= ( nStack - 1 ); i++ ) {
            sumXNow += xAvg[i];
            sumYNow += yAvg[i];
        }
        double avgXNOW = sumXNow / numPtsAvg;
        double avgYNOW = sumYNow / numPtsAvg;
        if( avgXNOW != this.avgXNow || avgYNOW != this.avgYNow ) {
            this.avgXNow = sumXNow / numPtsAvg;
            this.avgYNow = sumYNow / numPtsAvg;
            return true;
        }
        return false;
    }//updateAvgXYs() method

    public double getXVel() {
        return avgXNow - avgXBefore;
    }

    public double getYVel() {
        return avgYNow - avgYBefore;
    }

    public double getXAcc() {
        return avgXNow - 2 * avgXMid + avgXBefore;
    }

    public double getYAcc() {
        return avgYNow - 2 * avgYMid + avgYBefore;
    }

    public double getAvgXNow() {
        return this.avgXNow;
    }

    public double getAvgXMid() {
        return this.avgXMid;
    }

    public double getAvgYMid() {
        return this.avgYMid;
    }

    public double getAvgXBefore() {
        return this.avgXBefore;
    }

    public int getHalfWindowSize() {
        return this.halfWindowSize;
    }

    public void setHalfWindowSize( int halfWindowSize ) {
        this.halfWindowSize = halfWindowSize;
        this.numPoints = 3 * this.numPtsAvg + 2 * this.halfWindowSize;
        this.x = new int[numPoints];
        this.y = new int[numPoints];
        this.xAvg = new double[numPoints - 2 * halfWindowSize];
        this.yAvg = new double[numPoints - 2 * halfWindowSize];
    }

    public int getNumPointsAverage() {
        return this.numPtsAvg;
    }

    public void setNumPointsAverage( int numPtsAvg ) {
        this.numPtsAvg = numPtsAvg;
        this.numPoints = 3 * this.numPtsAvg + 2 * this.halfWindowSize;
        this.x = new int[numPoints];
        this.y = new int[numPoints];
        this.xAvg = new double[numPoints - 2 * halfWindowSize];
        this.yAvg = new double[numPoints - 2 * halfWindowSize];
    }


}//end of public class