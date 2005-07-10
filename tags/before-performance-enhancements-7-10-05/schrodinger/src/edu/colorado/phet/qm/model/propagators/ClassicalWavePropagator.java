/* Copyright 2004, Sam Reid */
package edu.colorado.phet.qm.model.propagators;

import edu.colorado.phet.qm.model.Complex;
import edu.colorado.phet.qm.model.Potential;
import edu.colorado.phet.qm.model.Propagator;
import edu.colorado.phet.qm.model.Wavefunction;

/**
 * User: Sam Reid
 * Date: Jun 28, 2005
 * Time: 3:42:18 PM
 * Copyright (c) Jun 28, 2005 by Sam Reid
 * <p/>
 * See: http://www.mtnmath.com/whatth/node47.html
 */

public class ClassicalWavePropagator implements Propagator {
    private Wavefunction last2;
    private Wavefunction last;
    private double speed = 0.4;
    private Potential potential;

    public ClassicalWavePropagator( Potential potential ) {
        this.potential = potential;
    }

    public double getSpeed() {
        return speed;
    }

    public void setSpeed( double speed ) {
        this.speed = speed;
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
        if( last == null ) {
            last = w.copy();
            last2 = w.copy();
            return;
        }
        for( int i = 1; i < w.getWidth() - 1; i++ ) {
            for( int j = 1; j < w.getHeight() - 1; j++ ) {

//                Complex num = new Complex();
//                num = num.plus( last( i + 1, j ) );
//                num = num.plus( last( i - 1, j ) );
//                num = num.plus( last( i, j + 1 ) );
//                num = num.plus( last( i, j - 1 ) );
//                num = num.times( 0.5 );
//                num = num.minus( last2.valueAt( i, j ) );
//                w.setValue( i, j, num );

                if( potential.getPotential( i, j, 0 ) != 0 ) {
                    w.setValue( i, j, new Complex() );
                }
                else {
                    Complex val = new Complex();
                    val = val.plus( last.valueAt( i, j ).times( 2 ) );
                    val = val.minus( last2.valueAt( i, j ) );
                    Complex num = new Complex();
                    num = num.plus( last( i + 1, j ) );
                    num = num.plus( last( i - 1, j ) );
                    num = num.plus( last( i, j + 1 ) );
                    num = num.plus( last( i, j - 1 ) );

                    num = num.minus( last( i, j ).times( 4 ) );
                    Complex neigh = num.times( 0.25 );


                    val = val.plus( neigh );
                    w.setValue( i, j, val );
                }

//                Complex sum = last( i + 1, j ).plus( last( i - 1, j ) ).plus( last( i, j + 1 ) ).plus( last( i, j - 1 ) );
//
//                sum = sum.minus( last( i, j ).times( 4 ) );
//                sum = sum.times( speed );
//                sum = sum.minus( last2.valueAt( i, j ) );
//                sum = sum.plus( last( i, j ).times( 2 ) );
//                w.setValue( i, j, sum );
            }
        }

        dampHorizontal( w, 0, +1 );
        dampHorizontal( w, w.getHeight() - 1, -1 );
        dampVertical( w, 0, +1 );
        dampVertical( w, w.getWidth() - 1, -1 );

        last2 = last;
        last = w.copy();
//        damping.damp( last );
    }

    private void dampHorizontal( Wavefunction w, int j, int dj ) {
        for( int i = 0; i < w.getWidth(); i++ ) {
            w.setValue( i, j, last2.valueAt( i, j + dj ) );
        }
    }

    private void dampVertical( Wavefunction w, int i, int di ) {
        for( int j = 0; j < w.getHeight(); j++ ) {
            w.setValue( i, j, last2.valueAt( i + di, j ) );
        }
    }

    private Complex last( int i, int j ) {
        if( potential.getPotential( i, j, 0 ) == 0 ) {
            return last.valueAt( i, j );
        }
        else {
            return new Complex();
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
        return new ClassicalWavePropagator( potential );
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
}
