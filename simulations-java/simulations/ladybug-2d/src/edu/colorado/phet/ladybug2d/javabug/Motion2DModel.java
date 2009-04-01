package edu.colorado.phet.ladybug2d.javabug;

//Physics implementation for Motion2D
//computes position, velocity and acceleration.

public class Motion2DModel {
    private Motion2DValue x;
    private Motion2DValue y;

    public Motion2DModel( int halfWindowSize, int numPtsAvg, double x0, double y0 ) {
        x = new Motion2DValue( 3 * numPtsAvg + 2 * halfWindowSize, halfWindowSize, numPtsAvg, x0 );
        y = new Motion2DValue( 3 * numPtsAvg + 2 * halfWindowSize, halfWindowSize, numPtsAvg, y0 );
    }

    //add new point to position arrays, update averagePosition arrays
    public void addPointAndUpdate( double xNow, double yNow ) {
        x.addPointAndUpdate( xNow );
        y.addPointAndUpdate( yNow );
    }//end of addPoint() method

    public double getXVel() {
        return x.getVelocity();
    }

    public double getYVel() {
        return y.getVelocity();
    }

    public double getXAcc() {
        return x.getAcceleration();
    }

    public double getYAcc() {
        return y.getAcceleration();
    }

    public double getAvgXMid() {
        return x.getAvgMid();
    }

    public double getAvgYMid() {
        return y.getAvgMid();
    }

    public void reset(double x0,double y0){
        x.reset( x0 );
        y.reset( y0 );
    }

    private static class Motion2DValue {
        double avgBefore;
        double avgMid;
        double avgNow;
        double[] value;
        double[] avg;
        private int halfWindowSize;
        private int numPtsAvg;

        /**
         * @param numPoints      Number of points in stack, must be odd
         * @param halfWindowSize averaging radius, #of pts averaged = (2*nA + 1)
         * @param numPtsAvg      Number of points averaged for vel, acc
         */
        public Motion2DValue( int numPoints, int halfWindowSize, int numPtsAvg, double init ) {
            this.halfWindowSize = halfWindowSize;
            this.numPtsAvg = numPtsAvg;
            this.value = new double[numPoints];
            this.avg = new double[numPoints - 2 * halfWindowSize];
            reset( init );
        }

        private void reset( double init ) {
            for ( int i = 0; i < value.length; i++ ) {
                value[i] = init;
            }
        }

        public void addPoint( double v ) {
            int numPoints = value.length;
            //update x and y-arrays
            for ( int i = 0; i < ( numPoints - 1 ); i++ ) {
                value[i] = value[i + 1];
            }
            value[numPoints - 1] = v;

            //update averagePosition arrays
            for ( int i = 0; i < ( numPoints - 2 * halfWindowSize ); i++ ) {
                avg[i] = 0;
                for ( int j = -halfWindowSize; j <= +halfWindowSize; j++ ) {
                    avg[i] += value[i + halfWindowSize + j];
                }

                avg[i] = avg[i] / ( 2 * halfWindowSize + 1 );
            }
        }

        public void updateAverages() {
            int numPoints = value.length;
            int nStack = numPoints - 2 * halfWindowSize;        //# of points in averagePostion stacks
            double sumXBefore = 0;
            double sumXMid = 0;
            double sumXNow = 0;

            //Compute avgXBefore, avgYBefore
            for ( int i = 0; i <= ( numPtsAvg - 1 ); i++ ) {
                sumXBefore += avg[i];
            }
            this.avgBefore = sumXBefore / numPtsAvg;

            //Compute avgXMid, avgYMid
            for ( int i = ( nStack - numPtsAvg ) / 2; i <= ( nStack + numPtsAvg - 2 ) / 2; i++ ) {
                sumXMid += avg[i];
            }
            this.avgMid = sumXMid / numPtsAvg;

            //Compute avgXNow, avgYNow
            for ( int i = ( nStack - numPtsAvg ); i <= ( nStack - 1 ); i++ ) {
                sumXNow += avg[i];
            }
            this.avgNow = sumXNow / numPtsAvg;
        }

        public double getVelocity() {
            return avgNow - avgBefore;
        }

        public double getAcceleration() {
            return avgNow - 2 * avgMid + avgBefore;
        }

        public double getAvgMid() {
            return avgMid;
        }

        public void addPointAndUpdate( double val ) {
            addPoint( val );
            updateAverages();
        }
    }


}//end of public class