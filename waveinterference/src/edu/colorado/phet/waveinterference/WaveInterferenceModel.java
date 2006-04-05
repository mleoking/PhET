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

public class WaveInterferenceModel implements ModelElement {
    private WaveModel waveModel;
    private SlitPotential slitPotential;
    private Oscillator primaryOscillator;
    private Oscillator secondaryOscillator;

//    private static final double startTime = System.currentTimeMillis() / 1000.0;
    private double time = 0.0;

    public WaveInterferenceModel() {
        waveModel = new WaveModel( 60, 60 );
        slitPotential = new SlitPotential( waveModel );
        primaryOscillator = new Oscillator( waveModel );
        secondaryOscillator = new Oscillator( waveModel );
        secondaryOscillator.setEnabled( false );
        secondaryOscillator.setLocation( 10, 10 );
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

    public Oscillator getSecondaryOscillator() {
        return secondaryOscillator;
    }

    public void stepInTime( double dt ) {
        time += dt;
        waveModel.propagate();
        primaryOscillator.setTime( getTime() );
        secondaryOscillator.setTime( getTime() );
    }

    private double getTime() {
        return time;
//        return System.currentTimeMillis() / 1000.0 - startTime;
    }

}
