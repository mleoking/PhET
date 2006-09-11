package edu.colorado.phet.motion2d;

import java.util.Arrays;

//Helper Class for Velocity-Acceleration GUI.  This class computes
//average position and double-averaged velocity and acceleration.

public class Motion2DAverages {
    private int nP;  	//Number of points in stack, must be odd
    private int nA;		//averaging radius, #of pts averaged = (2*nA + 1)
    private int nGroup;	//Number of points averaged for vel, acc
    private double avgXNow;
    private double avgYNow;
    private double avgXMid;
    private double avgYMid;
    private double avgXBefore;
    private double avgYBefore;
    private int[] x, y;		//last nP x- and y-coordinates from mousemovements
    private double[] xAvg, yAvg;	//averaged position stacks

    public Motion2DAverages( int nA, int nGroup ) {

        this.nA = nA;
        this.nGroup = nGroup;
        this.nP = 3 * nGroup + 2 * nA;
        this.x = new int[nP];
        this.y = new int[nP];
        this.xAvg = new double[nP - 2 * nA];
        this.yAvg = new double[nP - 2 * nA];

        for( int i = 0; i < nP; i++ ) {
            x[i] = 100;
            y[i] = 100;
            //System.out.println(" i="+i+" x[i]="+x[i]);
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
        for( int i = 0; i < ( nP - 1 ); i++ ) {
            x[i] = x[i + 1];
            y[i] = y[i + 1];
        }
        x[nP - 1] = xNow;
        y[nP - 1] = yNow;

        //System.out.println("Got out of 1st update loop alive.");

        //update averagePosition arrays
        for( int i = 0; i < ( nP - 2 * nA ); i++ ) {
            xAvg[i] = 0;
            yAvg[i] = 0;  //reset to zero
            for( int j = -nA; j <= +nA; j++ ) {
                xAvg[i] += (double)x[i + nA + j];
                yAvg[i] += (double)y[i + nA + j];
            }

            xAvg[i] = xAvg[i] / ( 2 * nA + 1 );
            yAvg[i] = yAvg[i] / ( 2 * nA + 1 );
        }
        CurrentState finalState = new CurrentState( x, y, xAvg, yAvg );
        if( !initialState.equals( finalState ) ) {
            return true;
        }
        return false;
    }//end of addPoint() method

    public boolean updateAvgXYs() {
        int nStack = nP - 2 * nA;		//# of points in averagePostion stacks
        double sumXBefore = 0;
        double sumYBefore = 0;
        double sumXMid = 0;
        double sumYMid = 0;
        double sumXNow = 0;
        double sumYNow = 0;

        //Compute avgXBefore, avgYBefore
        for( int i = 0; i <= ( nGroup - 1 ); i++ ) {
            sumXBefore += xAvg[i];
            sumYBefore += yAvg[i];
        }
        this.avgXBefore = sumXBefore / nGroup;
        this.avgYBefore = sumYBefore / nGroup;

        //Compute avgXMid, avgYMid
        for( int i = ( nStack - nGroup ) / 2; i <= ( nStack + nGroup - 2 ) / 2; i++ ) {
            sumXMid += xAvg[i];
            sumYMid += yAvg[i];
        }
        this.avgXMid = sumXMid / nGroup;
        this.avgYMid = sumYMid / nGroup;

        //Compute avgXNow, avgYNow
        for( int i = ( nStack - nGroup ); i <= ( nStack - 1 ); i++ ) {
            sumXNow += xAvg[i];
            sumYNow += yAvg[i];
        }
        double avgXNOW = sumXNow / nGroup;
        double avgYNOW = sumYNow / nGroup;
        if( avgXNOW != this.avgXNow || avgYNOW != this.avgYNow ) {
            this.avgXNow = sumXNow / nGroup;
            this.avgYNow = sumYNow / nGroup;
            return true;
        }
        return false;
    }//updateAvgXYs() method

    public double getXVel() {
        double velX = avgXNow - avgXBefore;
        return velX;
    }

    public double getYVel() {
        double velY = avgYNow - avgYBefore;
        return velY;
    }

    public double getXAcc() {
        double accX = avgXNow - 2 * avgXMid + avgXBefore;
        return accX;
    }

    public double getYAcc() {
        double accY = avgYNow - 2 * avgYMid + avgYBefore;
        return accY;
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

    public int getNP() {
        return this.nP;
    }

    public int getNA() {
        return this.nA;
    }

    public void setNA( int nA ) {
        //edu.colorado.phet.motion2d.Motion2DApplet.setButtonFlag(edu.colorado.phet.motion2d.Motion2DApplet.SHOW_NEITHER);
        this.nA = nA;
        this.nP = 3 * this.nGroup + 2 * this.nA;
        this.x = new int[nP];
        this.y = new int[nP];
        this.xAvg = new double[nP - 2 * nA];
        this.yAvg = new double[nP - 2 * nA];
    }

    public int getNGroup() {
        return this.nGroup;
    }

    public void setNGroup( int nGroup ) {
        this.nGroup = nGroup;
        this.nP = 3 * this.nGroup + 2 * this.nA;
        this.x = new int[nP];
        this.y = new int[nP];
        this.xAvg = new double[nP - 2 * nA];
        this.yAvg = new double[nP - 2 * nA];
    }


}//end of public class