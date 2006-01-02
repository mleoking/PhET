/* Copyright 2004, Sam Reid */
package edu.colorado.phet.qm.view.colormaps;

import edu.colorado.phet.qm.model.Potential;
import edu.colorado.phet.qm.model.Wavefunction;
import edu.colorado.phet.qm.view.SchrodingerPanel;
import edu.colorado.phet.qm.view.colorgrid.ColorMap;
import edu.colorado.phet.qm.view.gun.Photon;

import java.awt.*;

/**
 * User: Sam Reid
 * Date: Jul 18, 2005
 * Time: 10:55:00 PM
 * Copyright (c) Jul 18, 2005 by Sam Reid
 */

public class PhotonColorMap implements ColorMap {
    private SchrodingerPanel schrodingerPanel;
    private Photon photon;
    private WaveValueAccessor waveValueAccessor;
    private double intensityScale = 20;
    private ColorData rootColor;

    public PhotonColorMap( SchrodingerPanel schrodingerPanel, final Photon photon, WaveValueAccessor waveValueAccessor ) {
        if( waveValueAccessor == null ) {
            throw new RuntimeException( "Null waveValueAccessor" );
        }
        this.schrodingerPanel = schrodingerPanel;
        this.photon = photon;
        this.waveValueAccessor = waveValueAccessor;
        this.rootColor = new ColorData( photon.getWavelengthNM() );
    }

    public Paint getColor( int i, int k ) {
        Wavefunction wavefunction = schrodingerPanel.getDiscreteModel().getWavefunction();
        double abs = waveValueAccessor.getValue( wavefunction, i, k ) * intensityScale;
        if( abs > 1 ) {
            abs = 1;
        }
        return rootColor.toColor( abs );
    }

    protected double getBrightness( double x ) {
        double b = x * intensityScale;
        if( b > 1 ) {
            b = 1;
        }
        return b;
    }

    public Potential getPotential() {
        return schrodingerPanel.getDiscreteModel().getPotential();
    }
}
