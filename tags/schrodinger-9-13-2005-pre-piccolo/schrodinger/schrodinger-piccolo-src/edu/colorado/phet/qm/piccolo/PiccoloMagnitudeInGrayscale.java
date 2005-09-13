/* Copyright 2004, Sam Reid */
package edu.colorado.phet.qm.piccolo;

import edu.colorado.phet.qm.model.DiscreteModel;
import edu.colorado.phet.qm.model.Wavefunction;
import edu.colorado.phet.qm.view.ColorMap;

import java.awt.*;

/**
 * User: Sam Reid
 * Date: Jun 11, 2005
 * Time: 2:04:07 PM
 * Copyright (c) Jun 11, 2005 by Sam Reid
 */
public class PiccoloMagnitudeInGrayscale implements ColorMap {
    private double intensityScale = 20;
    private DiscreteModel discreteModel;

    public PiccoloMagnitudeInGrayscale( DiscreteModel discreteModel ) {
        this.discreteModel = discreteModel;
    }

    public Paint getPaint( int i, int k ) {
        Wavefunction wavefunction = discreteModel.getWavefunction();
        double abs = wavefunction.valueAt( i, k ).abs() * intensityScale;
        if( abs > 1 ) {
            abs = 1;
        }
        Color color = new Color( (float)abs, (float)abs, (float)abs );
        return color;
    }

    protected double getBrightness( double x ) {
        double b = x * intensityScale;
        if( b > 1 ) {
            b = 1;
        }
        return b;
    }

}
