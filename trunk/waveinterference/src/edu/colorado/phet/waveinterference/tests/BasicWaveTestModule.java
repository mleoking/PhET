/* Copyright 2004, Sam Reid */
package edu.colorado.phet.waveinterference.tests;

import edu.colorado.phet.common.application.Module;
import edu.colorado.phet.common.model.ModelElement;
import edu.colorado.phet.common.model.clock.SwingClock;
import edu.colorado.phet.piccolo.PhetPCanvas;
import edu.colorado.phet.waveinterference.model.ClassicalWavePropagator;
import edu.colorado.phet.waveinterference.model.Lattice2D;
import edu.colorado.phet.waveinterference.model.Potential;

/**
 * User: Sam Reid
 * Date: Mar 22, 2006
 * Time: 7:04:39 PM
 * Copyright (c) Mar 22, 2006 by Sam Reid
 */

public class BasicWaveTestModule extends Module {
    private Lattice2D lattice2D;
    private ClassicalWavePropagator classicalWavePropagator;
    private double period = 2;
    private PhetPCanvas panel = new PhetPCanvas();

    public BasicWaveTestModule( String name ) {
        super( name, new SwingClock( 30, 1 ) );
        setSimulationPanel( panel );
        lattice2D = new Lattice2D( 80, 80 );
        classicalWavePropagator = new ClassicalWavePropagator( new Potential() );
        addModelElement( new ModelElement() {
            public void stepInTime( double dt ) {
                step();
            }
        } );

    }

    public void setLatticeSize( int width, int height ) {
        lattice2D.setSize( width, height );
        classicalWavePropagator.setSize( width, height );
    }

    protected void step() {
        double t = System.currentTimeMillis() / 1000.0;
        double frequency = 1 / period;
        int oscillatorRadius = 5;
        int oscillatorX = oscillatorRadius + 3;
        int oscillatorY = lattice2D.getHeight() / 2;
        for( int i = oscillatorX - oscillatorRadius; i <= oscillatorX + oscillatorRadius; i++ ) {
            for( int j = oscillatorY - oscillatorRadius; j <= oscillatorY + oscillatorRadius; j++ ) {
                if( Math.sqrt( ( i - oscillatorX ) * ( i - oscillatorX ) + ( j - oscillatorY ) * ( j - oscillatorY ) ) < oscillatorRadius )
                {
                    double value = Math.cos( 2 * Math.PI * frequency * t );
                    lattice2D.setValue( i, j, (float)value );
                    classicalWavePropagator.setBoundaryCondition( i, j, (float)value );
                }
            }
        }
        classicalWavePropagator.propagate( lattice2D );

    }

    public Lattice2D getLattice() {
        return lattice2D;
    }

    public PhetPCanvas getPhetPCanvas() {
        return panel;
    }

    public double getPeriod() {
        return period;
    }

    public void setPeriod( double value ) {
        this.period = value;
    }
}
