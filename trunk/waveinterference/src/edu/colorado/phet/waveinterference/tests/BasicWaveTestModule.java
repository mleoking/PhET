/* Copyright 2004, Sam Reid */
package edu.colorado.phet.waveinterference.tests;

import edu.colorado.phet.common.application.Module;
import edu.colorado.phet.common.model.ModelElement;
import edu.colorado.phet.common.model.clock.SwingClock;
import edu.colorado.phet.piccolo.PhetPCanvas;
import edu.colorado.phet.waveinterference.model.Lattice2D;
import edu.colorado.phet.waveinterference.model.Oscillator;
import edu.colorado.phet.waveinterference.model.WaveModel;

/**
 * User: Sam Reid
 * Date: Mar 22, 2006
 * Time: 7:04:39 PM
 * Copyright (c) Mar 22, 2006 by Sam Reid
 */

public class BasicWaveTestModule extends Module {
    private WaveModel waveModel;
    private Oscillator oscillator;
    private PhetPCanvas panel = new PhetPCanvas();

    public BasicWaveTestModule( String name ) {
        super( name, new SwingClock( 30, 1 ) );
        setSimulationPanel( panel );
        waveModel = new WaveModel( 80, 80 );
        oscillator = new Oscillator( waveModel );
        addModelElement( new ModelElement() {
            public void stepInTime( double dt ) {
                step();
            }
        } );

    }

    public Oscillator getOscillator() {
        return oscillator;
    }

    public WaveModel getWaveModel() {
        return waveModel;
    }

    public int getOscillatorRadius() {
        return oscillator.getRadius();
    }

    public void setOscillatorRadius( int oscillatorRadius ) {
        oscillator.setRadius( oscillatorRadius );
    }

    public void setLatticeSize( int width, int height ) {
        waveModel.setSize( width, height );
    }

    protected void step() {
        double t = getTime();
        oscillator.setTime( t );
        waveModel.propagate();
    }

    protected double getTime() {
        double t = System.currentTimeMillis() / 1000.0;
        return t;
    }

    public Lattice2D getLattice() {
        return waveModel.getLattice();
    }

    public PhetPCanvas getPhetPCanvas() {
        return panel;
    }

    public double getPeriod() {
        return oscillator.getPeriod();
    }

    public void setPeriod( double value ) {
        oscillator.setPeriod( value );
    }
}
