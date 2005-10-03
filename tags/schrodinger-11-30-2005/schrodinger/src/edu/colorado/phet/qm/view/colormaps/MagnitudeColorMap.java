/* Copyright 2004, Sam Reid */
package edu.colorado.phet.qm.view.colormaps;

import edu.colorado.phet.qm.view.gun.Photon;
import edu.colorado.phet.qm.view.swing.SchrodingerPanel;

import java.awt.*;

/**
 * User: Sam Reid
 * Date: Jul 18, 2005
 * Time: 11:11:22 PM
 * Copyright (c) Jul 18, 2005 by Sam Reid
 */

public class MagnitudeColorMap implements ColorMap {
    private SchrodingerPanel schrodingerPanel;
    private ColorMap magnitudeInGrayscale;
    private PhotonColorMap photonColorMap;

    private ColorMap currentMap;
    private WaveValueAccessor waveValueAccessor;

    public MagnitudeColorMap( SchrodingerPanel schrodingerPanel, ColorMap grayscaleMap, WaveValueAccessor waveValueAccessor ) {
        this.schrodingerPanel = schrodingerPanel;
        this.magnitudeInGrayscale = grayscaleMap;
        this.currentMap = grayscaleMap;
        this.waveValueAccessor = waveValueAccessor;
    }

    public void setGrayscale() {
        currentMap = magnitudeInGrayscale;
    }

    public void setPhotonColorMap() {
        currentMap = photonColorMap;
    }

    public Paint getColor( int i, int k ) {
        return currentMap.getColor( i, k );
    }

    public void setPhoton( Photon photon ) {
        if( photon != null ) {
            this.photonColorMap = new PhotonColorMap( schrodingerPanel, photon, waveValueAccessor );
            currentMap = photonColorMap;
        }
        else {
            photonColorMap = null;
            currentMap = magnitudeInGrayscale;
        }
    }
}
