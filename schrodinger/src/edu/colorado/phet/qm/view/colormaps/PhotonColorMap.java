/* Copyright 2004, Sam Reid */
package edu.colorado.phet.qm.view.colormaps;

import edu.colorado.phet.common.view.util.VisibleColor;
import edu.colorado.phet.qm.model.Potential;
import edu.colorado.phet.qm.model.Wavefunction;
import edu.colorado.phet.qm.view.ColorMap;
import edu.colorado.phet.qm.view.SchrodingerPanel;
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
    private double intensityScale = 20;
    private ColorData rootColor;

    public static class ColorData {
        private float r;
        private float g;
        private float b;

        public ColorData( Photon photon ) {
            VisibleColor c = new VisibleColor( photon.getWavelengthNM() );
            this.r = c.getRed() / 255.0f;
            this.g = c.getGreen() / 255.0f;
            this.b = c.getBlue() / 255.0f;
        }

        public Color toColor( double abs ) {
            Color color = new Color( (float)abs * r, (float)abs * g, (float)abs * b );
            return color;
        }
    }

    public PhotonColorMap( SchrodingerPanel schrodingerPanel, final Photon photon ) {
        this.schrodingerPanel = schrodingerPanel;
        this.photon = photon;
        this.rootColor = new ColorData( photon );
    }

    public Paint getPaint( int i, int k ) {
        Wavefunction wavefunction = schrodingerPanel.getDiscreteModel().getWavefunction();
        double abs = wavefunction.valueAt( i, k ).abs() * intensityScale;
        if( abs > 1 ) {
            abs = 1;
        }
        Color color = rootColor.toColor( abs );
        return color;
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
