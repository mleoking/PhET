/* Copyright 2004, Sam Reid */
package edu.colorado.phet.qm.view;

import edu.colorado.phet.qm.model.Potential;

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

    public Paint getPaint( int i, int k ) {
        Paint color = wavefunctionColorMap.getPaint( i, k );
        double potval = getPotential().getPotential( i, k, 0 );
        if( potval > 0 ) {
            return new Color( 100, 0, 0 );
        }
        else {
            return color;
        }
    }

    private Potential getPotential() {
        return schrodingerPanel.getDiscreteModel().getPotential();
    }


}
