package edu.colorado.phet.ec3;


/**
 * RegressCalculator performs calculations for regressions
 *
 * @author Charles S. Stanton
 * @version Mon Jul 15 07:34:20 PDT 2002
 *          see http://www.math.csusb.edu/faculty/stanton/danby/danby_package.html
 */
public class LinearRegression {

    private double[] x, y; 			//vectors of x,y
    private double sumX = 0;
    private double sumY = 0;
    private double sumXY = 0;
    private double sumXsquared = 0;
    private double sumYsquared = 0;
    private double covariance = 0;
    private double Sxx, Sxy, Syy, n;
    private double a = 0, b = 0;  //coefficients of regression
    private int dataLength;
    private double[][] residual;// residual[i][0] = x[i], residual[i][1]= residual
    private double maxAbsoluteResidual = 0.0;
    private double SSR = 0.0; //regression sum of squares
    private double SSE = 0.0; //error sum of squares
    private double sigmaHatSquared = 0.0;
    private double minX = Double.POSITIVE_INFINITY;
    private double maxX = Double.NEGATIVE_INFINITY;
    private double minY = Double.POSITIVE_INFINITY;
    private double maxY = Double.NEGATIVE_INFINITY;

    /**
     * constructor for regression calculator
     *
     * @param aX is the array of x data
     * @param aY is the array of y data
     */
    public LinearRegression( double[] aX, double[] aY ) {
        x = aX;
        y = aY;
        if( x.length != y.length ) {
            System.out.println( "x, y vectors must be of same length" );
        }
        else { dataLength = x.length;}
        doStatistics();
    }

    private void doStatistics() {
        //Find sum of squares for x,y and sum of xy
        for( int i = 0; i < dataLength; i++ ) {
            minX = Math.min( minX, x[i] );
            maxX = Math.max( maxX, x[i] );
            minY = Math.min( minY, y[i] );
            maxY = Math.max( maxY, y[i] );
            sumX += x[i];
            sumY += y[i];
            sumXsquared += x[i] * x[i];
            sumYsquared += y[i] * y[i];
            sumXY += x[i] * y[i];
        }
        //Caculate regression coefficients
        n = (double)dataLength;
        Sxx = sumXsquared - sumX * sumX / n;
        Syy = sumYsquared - sumY * sumY / n;
        Sxy = sumXY - sumX * sumY / n;
        b = Sxy / Sxx;
        a = ( sumY - b * sumX ) / n;
        SSR = Sxy * Sxy / Sxx;
        SSE = Syy - SSR;
        calculateResiduals();
    }

    private void calculateResiduals() {
        residual = new double[dataLength][];
        maxAbsoluteResidual = 0.0;
        for( int i = 0; i < dataLength; i++ ) {
            residual[i] = new double[2];
            residual[i][0] = x[i];
            residual[i][1] = y[i] - ( a + b * x[i] );
            maxAbsoluteResidual = Math.max( maxAbsoluteResidual, Math.abs( y[i] - ( a + b * x[i] ) ) );
        }
    }

    //update statistics for a single additional data point
    private void updateStatistics( double xValue, double yValue ) {
        //Find sum of squares for x,y and sum of xy
        n++;
        sumX += xValue;
        sumY += yValue;
        sumXsquared += xValue * xValue;
        sumYsquared += yValue * yValue;
        sumXY += xValue * yValue;
        //Caculate regression coefficients
        n = (double)dataLength;
        Sxx = sumXsquared - sumX * sumX / n;
        Syy = sumYsquared - sumY * sumY / n;
        Sxy = sumXY - sumX * sumY / n;
        b = Sxy / Sxx;
        a = ( sumY - b * sumX ) / n;
        SSR = Sxy * Sxy / Sxx;
        SSE = Syy - SSR;
        calculateResiduals();
    }

    /**
     * reset data to 0
     */
    public void reset() {
        x = new double[0];
        y = new double[0];
        dataLength = 0;
        n = 0.0;
        residual = new double[0][];

        sumX = 0;
        sumXsquared = 0;
        sumY = 0;
        sumYsquared = 0;
        sumXY = 0;

    }


    /*
    *	Here are the accessor methods
    *
    */
    public double getIntercept() { return a;}

    public double getSlope() { return b;}

    public double[][] getResiduals() { return residual;}

    public double[] getDataX() {return x;}

    public double[] getDataY() {return y;}

    public void addPoint( double xValue, double yValue ) {
        dataLength++;
        double[] xNew = new double[dataLength];
        double[] yNew = new double[dataLength];
        System.arraycopy( x, 0, xNew, 0, dataLength - 1 );
        System.arraycopy( y, 0, yNew, 0, dataLength - 1 );
        xNew[dataLength - 1] = xValue;
        yNew[dataLength - 1] = yValue;
        x = xNew;
        y = yNew;
        updateStatistics( xValue, yValue );
    }

    public double getMinX() { return minX;}

    public double getMaxX() { return maxX;}

    public double getMinY() {return minY;}

    public double getMaxY() { return maxY;}


    public double getMaxAbsoluteResidual() {
        return maxAbsoluteResidual;
    }

    public double getSxx() {
        return Sxx;
    }

    public double getSyy() {
        return Syy;
    }

    public double getSSR() {
        return SSR;
    }

    public double getSSE() {
        return SSE;
    }

    public double getMSE() {
        return SSE / ( n - 2 );
    }

    public double getXBar() {
        return sumX / n;
    }

    public double getYBar() {
        return sumY / n;
    }

    public int getDataLength() {
        return x.length;
    }


    public double getPearsonR() {
        return Sxy / Math.sqrt( Sxx * Syy );
    }

    public double getSumXSquared() {
        return sumXsquared;
    }
}



