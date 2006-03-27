/* Copyright 2004, Sam Reid */
package edu.colorado.phet.waveinterference;

import edu.colorado.phet.waveinterference.model.SlitPotential;
import edu.colorado.phet.waveinterference.model.WaveModel;

/**
 * User: Sam Reid
 * Date: Mar 26, 2006
 * Time: 5:34:05 PM
 * Copyright (c) Mar 26, 2006 by Sam Reid
 */

public class WaterModel {
    private WaveModel waveModel;
    private SlitPotential slitPotential;

    public WaterModel() {
        waveModel = new WaveModel( 60, 60 );
        slitPotential = new SlitPotential( waveModel );
    }

    public WaveModel getWaveModel() {
        return waveModel;
    }

    public SlitPotential getSlitPotential() {
        return slitPotential;
    }
}
