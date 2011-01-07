// Copyright 2002-2011, University of Colorado

/*  */
package edu.colorado.phet.quantumwaveinterference.view.colormaps;

import edu.colorado.phet.quantumwaveinterference.model.Wavefunction;
import edu.colorado.phet.quantumwaveinterference.view.QWIPanel;
import edu.colorado.phet.quantumwaveinterference.view.colorgrid.ColorMap;

import java.awt.*;

/**
 * User: Sam Reid
 * Date: Jul 18, 2005
 * Time: 10:55:00 PM
 */

public class PhotonColorMap implements ColorMap {
    private QWIPanel QWIPanel;
    private WaveValueAccessor waveValueAccessor;
    private double intensityScale = 20;
    private ColorData rootColor;

    public PhotonColorMap( QWIPanel QWIPanel, double wavelengthNM, WaveValueAccessor waveValueAccessor ) {
        if( waveValueAccessor == null ) {
            throw new RuntimeException( "Null waveValueAccessor" );
        }
        this.QWIPanel = QWIPanel;
        this.waveValueAccessor = waveValueAccessor;
        this.rootColor = new ColorData( wavelengthNM );
    }

    public String toString() {
        return super.toString() + ", waveValueAccessor=" + waveValueAccessor + ", rootColor=" + rootColor;
    }

    public Paint getColor( int i, int k ) {
        Wavefunction wavefunction = QWIPanel.getDiscreteModel().getWavefunction();
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

}
