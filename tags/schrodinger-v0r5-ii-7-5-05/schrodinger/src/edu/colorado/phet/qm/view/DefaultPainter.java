/* Copyright 2004, Sam Reid */
package edu.colorado.phet.qm.view;

import edu.colorado.phet.qm.model.Potential;
import edu.colorado.phet.qm.view.colormaps.VisualColorMap;

import java.awt.*;

/**
 * User: Sam Reid
 * Date: Jun 11, 2005
 * Time: 4:19:29 PM
 * Copyright (c) Jun 11, 2005 by Sam Reid
 */

public class DefaultPainter implements ColorMap {
    private SchrodingerPanel schrodingerPanel;
    private ColorMap wavefunctionColorMap;

    public DefaultPainter( SchrodingerPanel schrodingerPanel ) {
        this( schrodingerPanel, new VisualColorMap( schrodingerPanel ) );
    }

    public DefaultPainter( SchrodingerPanel schrodingerPanel, ColorMap wavefunctionColorMap ) {
        this.schrodingerPanel = schrodingerPanel;
        this.wavefunctionColorMap = wavefunctionColorMap;
    }

    public void setWavefunctionColorMap( ColorMap wavefunctionColorMap ) {
        this.wavefunctionColorMap = wavefunctionColorMap;
    }

    public Color getColor( int i, int k ) {
        Color color = wavefunctionColorMap.getColor( i, k );
        double potval = getPotential().getPotential( i, k, 0 );
        if( potval > 0 ) {
            double r = ( Math.abs( potval ) / 50000.0 );
//            System.out.println( "r = " + r );
            r = Math.min( r, 1.0 );
            color = new Color( (int)( r * 255 ), color.getGreen(), color.getBlue() );
        }
//        Damping damping = schrodingerPanel.getDiscreteModel().getDamping();
//        double val = damping.getDamping( schrodingerPanel.getDiscreteModel().getWavefunction(), i, k );
//        if( val > 0 ) {
//            color = new Color( 255, 255, 255 );
//        }
        return color;
    }

    private Potential getPotential() {
        return schrodingerPanel.getDiscreteModel().getPotential();
    }


}
