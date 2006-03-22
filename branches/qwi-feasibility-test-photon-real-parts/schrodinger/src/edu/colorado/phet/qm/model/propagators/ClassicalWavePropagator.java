/* Copyright 2004, Sam Reid */
package edu.colorado.phet.qm.model.propagators;

import edu.colorado.phet.qm.model.Potential;
import edu.colorado.phet.qm.model.Propagator;
import edu.colorado.phet.qm.model.Wavefunction;
import edu.colorado.phet.qm.model.math.Complex;

import java.awt.*;

/**
 * See: http://www.mtnmath.com/whatth/node47.html
 */

public class ClassicalWavePropagator extends Propagator {
    private Wavefunction last2;
    private Wavefunction last;

    public ClassicalWavePropagator( Potential potential ) {
        super( potential );
    }

    public void initialize( Wavefunction last, Wavefunction last2 ) {
        this.last2 = last2;
        this.last = last;
    }

    public void addInitialization( Wavefunction last, Wavefunction last2 ) {
        if( this.last2 == null ) {
            this.last2 = last2.createEmptyWavefunction();
        }
        if( this.last == null ) {
            this.last = last.createEmptyWavefunction();
        }
        this.last2.add( last2 );
        this.last.add( last );
    }

    public void propagate( Wavefunction w ) {
        Complex neigh = new Complex();
        if( last == null ) {
            last = w.copy();
            last2 = w.copy();
            return;
        }

//        double c=0.5;
//        double c=1;
//        double c=0.25;
//        double c=0.6;
//        double c=0.6;
        double c = 0.5;
        double cSquared = c * c;
        for( int i = 1; i < w.getWidth() - 1; i++ ) {
            for( int j = 1; j < w.getHeight() - 1; j++ ) {

                if( getPotential().getPotential( i, j, 0 ) != 0 ) {
                    w.setValue( i, j, 0, 0 );
                }
                else {
                    neigh.setValue( 0, 0 );
                    neigh.add( last( i + 1, j ) );
                    neigh.add( last( i - 1, j ) );
                    neigh.add( last( i, j + 1 ) );
                    neigh.add( last( i, j - 1 ) );

                    Complex lastVal = last( i, j );
                    neigh.add( lastVal.getReal() * -4, lastVal.getImaginary() * -4 );

                    neigh.scale( cSquared );
                    w.setValue( i, j,
                                last.valueAt( i, j ).getReal() * 2 - last2.valueAt( i, j ).getReal() + neigh.getReal(),
                                last.valueAt( i, j ).getImaginary() * 2 - last2.valueAt( i, j ).getImaginary() + neigh.getImaginary() );

                }
            }
        }

        dampHorizontal( w, 0, +1 );
        dampHorizontal( w, w.getHeight() - 1, -1 );
        dampVertical( w, 0, +1 );
        dampVertical( w, w.getWidth() - 1, -1 );

        last.copyTo( last2 );
        w.copyTo( last );
        w.setMagnitudeDirty();
        last.setMagnitudeDirty();
        last2.setMagnitudeDirty();
    }

    private void dampHorizontal( Wavefunction wavefunction, int j, int dj ) {
        for( int i = 0; i < wavefunction.getWidth(); i++ ) {
            wavefunction.setValue( i, j, last2.valueAt( i, j + dj ) );
        }
    }

    private void dampVertical( Wavefunction wavefunction, int i, int di ) {
        for( int j = 0; j < wavefunction.getHeight(); j++ ) {
            wavefunction.setValue( i, j, last2.valueAt( i + di, j ) );
        }
    }

    private Complex ZERO = new Complex();

    private Complex last( int i, int j ) {
        if( getPotential().getPotential( i, j, 0 ) == 0 ) {
            return last.valueAt( i, j );
        }
        else {
            return ZERO;
        }
    }

    public void setDeltaTime( double deltaTime ) {
    }

    public double getSimulationTime() {
        return 0;
    }

    public void reset() {
        last2 = null;
        last = null;
    }

    public void setBoundaryCondition( int i, int k, Complex value ) {
        if( last != null ) {
            last.setValue( i, k, value );
        }
        if( last2 != null ) {
            last2.setValue( i, k, value );
        }
    }

    public Propagator copy() {
        return new ClassicalWavePropagator( getPotential() );
    }

    public void normalize() {
        if( last2 != null ) {
            last2.normalize();
        }
        if( last != null ) {
            last.normalize();
        }
    }

    public void setWavefunctionNorm( double norm ) {
        if( last2 != null ) {
            last2.setMagnitude( norm );
        }
        if( last != null ) {
            last.setMagnitude( norm );
        }
    }

    public void scale( double scale ) {
        if( last2 != null ) {
            last2.scale( scale );
        }
        if( last != null ) {
            last.scale( scale );
        }
    }

    public Wavefunction getLast() {
        return last;
    }

    public Wavefunction getLast2() {
        return last2;
    }

    public void copyTo( int i, int j, Propagator dst ) {
        super.copyTo( i, j, dst );

        if( dst instanceof ClassicalWavePropagator ) {
            ClassicalWavePropagator classicalDst = (ClassicalWavePropagator)dst;
            if( last2 != null && classicalDst.last2 != null ) {
                Complex val2 = last2.valueAt( i, j );
                classicalDst.last2.setValue( i, j, val2 );
            }
            if( last != null && classicalDst.last != null ) {
                Complex val = last.valueAt( i, j );
                classicalDst.last.setValue( i, j, val );
            }
        }
        else {
            throw new RuntimeException( "Tried to copy Classical to wrong propagator type: " + dst.getClass().getName() );
        }
    }

    public void clearWave( Rectangle rect ) {
        super.clearWave( rect );
        if( last2 != null ) {
            last2.clearRect( rect );
        }
        if( last != null ) {
            last.clearRect( rect );
        }
    }

    public void splitWave( Rectangle region, Propagator a, Propagator b ) {
        super.splitWave( region, a, b );

        if( a instanceof ClassicalWavePropagator && b instanceof ClassicalWavePropagator ) {
            ClassicalWavePropagator ca = (ClassicalWavePropagator)a;
            ClassicalWavePropagator cb = (ClassicalWavePropagator)b;
            if( last2 != null && ca.last2 != null && cb.last2 != null ) {
                last2.splitWave( region, ca.last2, cb.last2 );
            }
            if( last != null && ca.last != null && cb.last != null ) {
                last.splitWave( region, ca.last, cb.last );
            }
        }
        else {
            throw new RuntimeException( "Tried to split Classical to wrong propagator type: a=" + a.getClass().getName() + ", b=" + b.getClass().getName() );
        }
    }

    public void combineWaves( Rectangle region, Propagator a, Propagator b ) {
        super.combineWaves( region, a, b );
        if( a instanceof ClassicalWavePropagator && b instanceof ClassicalWavePropagator ) {
            ClassicalWavePropagator ca = (ClassicalWavePropagator)a;
            ClassicalWavePropagator cb = (ClassicalWavePropagator)b;
            if( last2 != null && ca.last2 != null && cb.last2 != null ) {
                last2.combineWaves( region, ca.last2, cb.last2 );
            }
            if( last != null && ca.last != null && cb.last != null ) {
                last.combineWaves( region, ca.last, cb.last );
            }
        }
        else {
            throw new RuntimeException( "Tried to combine Classical to wrong propagator type: a=" + a.getClass().getName() + ", b=" + b.getClass().getName() );
        }
    }

    public void setValue( int i, int j, double real, double imag ) {
        if( last != null ) {
            last.setValue( i, j, real, imag );
        }
        if( last2 != null ) {
            last2.setValue( i, j, real, imag );
        }
    }
}
