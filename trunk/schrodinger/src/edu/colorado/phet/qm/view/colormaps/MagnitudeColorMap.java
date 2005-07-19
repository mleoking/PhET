/* Copyright 2004, Sam Reid */
package edu.colorado.phet.qm.view.colormaps;

import edu.colorado.phet.qm.view.ColorMap;
import edu.colorado.phet.qm.view.SchrodingerPanel;
import edu.colorado.phet.qm.view.gun.Photon;

import java.awt.*;

/**
 * User: Sam Reid
 * Date: Jul 18, 2005
 * Time: 11:11:22 PM
 * Copyright (c) Jul 18, 2005 by Sam Reid
 */

public class MagnitudeColorMap implements ColorMap {
    private SchrodingerPanel schrodingerPanel;
    private MagnitudeInGrayscale magnitudeInGrayscale;
    private PhotonColorMap photonColorMap;

    private ColorMap currentMap;

    public MagnitudeColorMap( SchrodingerPanel schrodingerPanel, MagnitudeInGrayscale magnitudeInGrayscale, PhotonColorMap photonColorMap, ColorMap currentMap ) {
        this.schrodingerPanel = schrodingerPanel;
        this.magnitudeInGrayscale = magnitudeInGrayscale;
        this.photonColorMap = photonColorMap;
        this.currentMap = currentMap;
    }

    public void setGrayscale() {
        currentMap = magnitudeInGrayscale;
    }

    public void setPhotonColorMap() {
        currentMap = photonColorMap;
    }

    public Paint getPaint( int i, int k ) {
        return currentMap.getPaint( i, k );
    }

    public void setPhoton( Photon photon ) {
        if( photon != null ) {
            this.photonColorMap = new PhotonColorMap( schrodingerPanel, photon );
            currentMap = photonColorMap;
        }
        else {
            photonColorMap = null;
            currentMap = magnitudeInGrayscale;
        }
    }
}
