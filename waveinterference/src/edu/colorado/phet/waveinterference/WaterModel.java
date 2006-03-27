/* Copyright 2004, Sam Reid */
package edu.colorado.phet.waveinterference;

import edu.colorado.phet.common.model.ModelElement;
import edu.colorado.phet.waveinterference.model.Oscillator;
import edu.colorado.phet.waveinterference.model.SlitPotential;
import edu.colorado.phet.waveinterference.model.WaveModel;

/**
 * User: Sam Reid
 * Date: Mar 26, 2006
 * Time: 5:34:05 PM
 * Copyright (c) Mar 26, 2006 by Sam Reid
 */

public class WaterModel implements ModelElement {
    private WaveModel waveModel;
    private SlitPotential slitPotential;
    private Oscillator primaryOscillator;
    private static final double startTime = System.currentTimeMillis() / 1000.0;

    public WaterModel() {
        waveModel = new WaveModel( 60, 60 );
        slitPotential = new SlitPotential( waveModel );
        primaryOscillator = new Oscillator( waveModel );
        waveModel.setPotential( slitPotential );
    }

    public WaveModel getWaveModel() {
        return waveModel;
    }

    public SlitPotential getSlitPotential() {
        return slitPotential;
    }

    public Oscillator getPrimaryOscillator() {
        return primaryOscillator;
    }

    public void stepInTime( double dt ) {
        waveModel.propagate();
        primaryOscillator.setTime( getTime() );
    }

    private double getTime() {
        return System.currentTimeMillis() / 1000.0 - startTime;
    }
}
