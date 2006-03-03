/* Copyright 2004, Sam Reid */
package edu.colorado.phet.qm.modules.mandel;

import edu.colorado.phet.qm.view.colorgrid.ColorMap;
import edu.colorado.phet.qm.view.colormaps.PhotonColorMap;
import edu.colorado.phet.qm.view.colormaps.WaveValueAccessor;

import java.awt.*;

/**
 * User: Sam Reid
 * Date: Mar 3, 2006
 * Time: 12:52:00 AM
 * Copyright (c) Mar 3, 2006 by Sam Reid
 */

public class MandelSplitColorMap implements ColorMap {
    private PhotonColorMap leftColorMap;
    private PhotonColorMap rightColorMap;
    private WaveValueAccessor waveValueAccessor = new WaveValueAccessor.Magnitude();

    public MandelSplitColorMap( MandelModule mandelModule ) {
        leftColorMap = new PhotonColorMap( mandelModule.getSchrodingerPanel(), mandelModule.getMandelSchrodingerPanel().getLeftGun().getWavelength(), waveValueAccessor );
        rightColorMap = new PhotonColorMap( mandelModule.getSchrodingerPanel(), mandelModule.getMandelSchrodingerPanel().getRightGun().getWavelength(), waveValueAccessor );
    }

    public Paint getColor( int i, int k ) {
        Color a = (Color)leftColorMap.getColor( i, k );
        Color b = (Color)rightColorMap.getColor( i, k );
        return new Color( Math.min( a.getRed() + b.getRed(), 255 ), Math.min( a.getGreen() + b.getGreen(), 255 ), Math.min( a.getBlue() + b.getBlue(), 255 ) );
    }
}
