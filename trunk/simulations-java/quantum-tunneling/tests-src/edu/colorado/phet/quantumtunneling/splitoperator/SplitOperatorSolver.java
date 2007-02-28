
package edu.colorado.phet.quantumtunneling.splitoperator;

import edu.colorado.phet.quantumtunneling.model.RichardsonSolver;
import edu.colorado.phet.quantumtunneling.model.WavePacket;
import edu.colorado.phet.quantumtunneling.util.LightweightComplex;

/**
 * User: Sam Reid
 * Date: Feb 28, 2006
 * Time: 4:33:24 PM
 */

public class SplitOperatorSolver extends RichardsonSolver {
    private LightweightComplex[] expV;
    private LightweightComplex[] expT;
    private LightweightComplex[] temp;
//    private double scale = 0.0001;
    private double scale = 0.000075;
    private WaveDebugFrame frame;
//    private double scale = 0.0005;
    private boolean DEBUG_WAVE_DISPLAY = false;

    /**
     * Sole constructor.
     *
     * @param wavePacket
     */
    public SplitOperatorSolver( WavePacket wavePacket ) {
        super( wavePacket );
    }

    public void propagate() {
        propagateOnce();
        show();
    }

    private void show() {
        if( frame == null && DEBUG_WAVE_DISPLAY ) {
            frame = new WaveDebugFrame( this );
            frame.setVisible( true );
        }
        if( frame != null ) {
            frame.paint();
        }
    }

    public void update() {
        super.update();
        expV = null;
        expT = null;
        temp = null;
    }

    private void propagateOnce() {

        LightweightComplex[]w = getWaveFunctionValues();
        LightweightComplex[] expV = getExpV( w.length );
//        LightweightComplex[] expV = ones( w.length );
        LightweightComplex[] expT = getExpT( w.length );
//        LightweightComplex[] expT = ones( w.length );
        LightweightComplex[] temp = getTempWavefunction( w.length );
        multiplyPointwise( expV, w, temp );
        LightweightComplex[] phi = forwardFFT( temp );
        multiplyPointwise( expT, phi, temp );
        LightweightComplex[] psi2 = inverseFFT( temp );
        multiplyPointwise( expV, psi2, temp );
        for( int i = 0; i < w.length; i++ ) {
            w[i]._real = temp[i]._real;
            w[i]._imaginary = temp[i]._imaginary;
        }
    }

    private static LightweightComplex[] inverseFFT( LightweightComplex[] temp ) {
        double[] data = toDoubleArray( temp );
        new FFT().inverse( data );
        return parseDoubleArray( data );
    }

    private static LightweightComplex[] forwardFFT( LightweightComplex[] temp ) {
        double[] data = toDoubleArray( temp );
        new FFT().transform( data );
        return parseDoubleArray( data );
    }

    private static LightweightComplex[] parseDoubleArray( double[] data ) {
        LightweightComplex[]d = new LightweightComplex[data.length / 2];
        for( int i = 0; i < d.length; i++ ) {
            d[i] = new LightweightComplex( data[i * 2], data[i * 2 + 1] );
        }
        return d;
    }

    private static double[] toDoubleArray( LightweightComplex[] temp ) {
        double[]data = new double[temp.length * 2];
        for( int i = 0; i < temp.length; i++ ) {
            LightweightComplex lightweightComplex = temp[i];
            data[i * 2] = lightweightComplex.getReal();
            data[i * 2 + 1] = lightweightComplex.getImaginary();
        }
        return data;
    }

    private LightweightComplex[] getTempWavefunction( int width ) {
        if( temp == null || temp.length != width ) {
            temp = new LightweightComplex[width];
        }
        return temp;
    }

    private LightweightComplex[] getExpV( int width ) {
        if( expV == null || expV.length != width ) {
            expV = createExpV( width );
        }
        return expV;
    }

    private LightweightComplex[] createExpV( int width ) {
        LightweightComplex[] expv = new LightweightComplex[width];
        for( int i = 0; i < expv.length; i++ ) {
            expv[i] = getExpVValue( i );
        }
        return expv;
    }

    private LightweightComplex getExpVValue( int i ) {

        double deltaTOverHbar = 0.03;
//        double deltaTOverHbar = 1;
//        double deltaTOverHbar = 0.01;
        /*double hbar= QTConstants.HBAR;//0.658
        double dt=6.93;//emergent, from Richardson
        deltaTOverHbar=dt/hbar;*/

        double potential = super.getPotentialEnergy( super.getPositionValues()[i] );//0.5 is default
//        System.out.println( "potential = " + potential );
        return new LightweightComplex( Math.cos( -potential * deltaTOverHbar / 2.0 ), Math.sin( -potential * deltaTOverHbar / 2.0 ) );
    }

    private LightweightComplex[] getExpT( int width ) {
        if( expT == null || expT.length != width ) {
            expT = createExpT( width );
        }
        return expT;
    }

    private LightweightComplex[] createExpT( int width ) {
        LightweightComplex[] expt = new LightweightComplex[width];
        for( int i = 0; i < expt.length; i++ ) {
            expt[i] = new LightweightComplex( getExpTValueWraparound( i, expt ).getReal(), getExpTValueWraparound( i, expt ).getImaginary() );
        }
        return expt;
    }

    private LightweightComplex getExpTValueWraparound( int i, LightweightComplex[] w ) {
        double px = i;
        if( i >= w.length / 2 ) {
            px = w.length - i;
        }

        double psquared = px * px;
        double ke = psquared * scale;
        //scale is directly or inversely proportional to each of the following:
        // the physical area of the box L*W (in meters)
        // the time step dt (in seconds)
        //the mass of the particle (kg)
        return new LightweightComplex( Math.cos( -ke ), Math.sin( -ke ) );
    }

    private LightweightComplex[] multiplyPointwise( LightweightComplex[] a, LightweightComplex[] b, LightweightComplex[] result ) {
        for( int i = 0; i < result.length; i++ ) {
            result[i] = new LightweightComplex( a[i].getReal(), a[i].getImaginary() );
            result[i].multiply( b[i] );
        }
        return result;
    }

    private LightweightComplex[] ones( int width ) {
        LightweightComplex[] ones = new LightweightComplex[width];
        for( int i = 0; i < ones.length; i++ ) {
            ones[i] = new LightweightComplex( 1, 0 );
        }
        return ones;
    }

    public static void main( String[] args ) {
        LightweightComplex[]test = new LightweightComplex[]{new LightweightComplex( 0, 1 ), new LightweightComplex( 2, 3 ), new LightweightComplex( 4, 5 )};
        double[]v = toDoubleArray( test );
        LightweightComplex[]a = parseDoubleArray( v );
        System.out.println( "done" );
        LightweightComplex[]fft = forwardFFT( a );
        LightweightComplex[]orig = inverseFFT( fft );
        System.out.println( "a" );
    }
}
