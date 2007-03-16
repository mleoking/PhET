package edu.colorado.phet.cck.elements.kirkhoff.equations;


/**
 * Represents numBranches values of current, then numBranches values of voltage, then the right-hand-side.
 * The matrix will be:
 * Ax=B
 * where A is a big matrix with the left side representing current coefficients and the right side representing voltage coefficients.
 * B is the right-hand-side vector (usually zero.)
 * and x is the row vector of currents first, then voltages.
 */
public class Equation {
    double[] data;

    public Equation( int numParameters ) {
        this.data = new double[numParameters + 1];//and a RHS
    }

    public double getRHS() {
        return data[data.length - 1];
    }

    public String toString() {
        StringBuffer sb = new StringBuffer();
        for( int i = 0; i < data.length; i++ ) {
            double v = data[i];
            sb.append( data[i] );
            if( i < data.length - 1 ) {
                sb.append( ", " );
            }
        }
        return sb.toString();
    }

    public void setRHS( double value ) {
        setEntry( data.length - 1, value );
    }

    public void setEntry( int index, double value ) {
        data[index] = value;
    }

    public void addRHS( double v ) {
        data[data.length - 1] += v;
    }

    public int getColumnCount() {
        return data.length;
    }

    public static double[][] toMatrix( Equation[] r ) {
        int rows = r.length;
        if( rows == 0 ) {
            return new double[0][0];
        }
        int cols = r[0].data.length;
        double[][] data = new double[rows][cols];
        for( int i = 0; i < data.length; i++ ) {
            for( int k = 0; k < data[0].length; k++ ) {
                data[i][k] = r[i].data[k];
            }
        }
        return data;
    }

    public static String printToString( Equation[] system ) {
        StringBuffer sb = new StringBuffer();
//        O.d("Got " + system.length + " equations.");
        sb.append( "Got " + system.length + " equations." );
        for( int i = 0; i < system.length; i++ ) {
            Equation kirkhoffEquation = system[i];

            sb.append( "equation[" + i + "] = " + kirkhoffEquation + "\n" );
        }
        return sb.toString().substring( 0, sb.length() - 1 );
    }

    public static double[][] toJamaLHSMatrix( Equation[] r ) {
        int rows = r.length;
        if( rows == 0 ) {
            return new double[0][0];
        }
        int cols = r[0].data.length - 1;
        double[][] data = new double[rows][cols];
        for( int i = 0; i < data.length; i++ ) {
            for( int k = 0; k < cols; k++ ) {
                data[i][k] = r[i].data[k];
            }
        }
        return data;
    }

    public static double[][] toJamaRHSMatrix( Equation[] ke ) {
        int rows = ke.length;
        int cols = ke[0].getColumnCount();
        double[][] data = new double[rows][1];
        for( int i = 0; i < rows; i++ ) {
            data[i][0] = ke[i].data[cols - 1];
        }
        return data;
    }

}
