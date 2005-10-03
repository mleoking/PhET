/* Copyright 2004, Sam Reid */
package edu.colorado.phet.qm.view.colormaps;

import edu.colorado.phet.qm.model.SplitModel;
import edu.colorado.phet.qm.model.Wavefunction;
import edu.colorado.phet.qm.modules.intensity.IntensityPanel;

import java.awt.*;

/**
 * User: Sam Reid
 * Date: Jul 7, 2005
 * Time: 8:43:06 PM
 * Copyright (c) Jul 7, 2005 by Sam Reid
 */

public class SplitColorMap implements ColorMap {
    private SplitModel splitModel;
    private PhotonColorMap.ColorData rootColor;
    private double intensityScale = 20;

    public SplitColorMap( SplitModel splitModel, IntensityPanel panel ) {
        this.splitModel = splitModel;
        setRootColor( panel.getRootColor() );
    }

    public Paint getColor( int i, int k ) {
        Rectangle[] areas = splitModel.getDoubleSlitPotential().getSlitAreas();
        double abs = 0;
        if( !contains( areas, i, k ) ) {
            Wavefunction wavefunction = splitModel.getWavefunction();
            abs += getValue( wavefunction, i, k );
        }
        abs += getValue( splitModel.getLeftWavefunction(), i, k );
        abs += getValue( splitModel.getRightWavefunction(), i, k );
        if( abs > 1 ) {
            abs = 1;
        }
        if( rootColor != null ) {
            return rootColor.toColor( abs );
        }
        else {
            return new Color( (float)abs, (float)abs, (float)abs );
        }
    }

    private boolean contains( Rectangle[] areas, int i, int k ) {
        for( int j = 0; j < areas.length; j++ ) {
            Rectangle area = areas[j];
            if( area.contains( i, k ) ) {
                return true;
            }
        }
        return false;
    }

    private double getValue( Wavefunction wavefunction, int i, int k ) {
        if( wavefunction.containsLocation( i, k ) ) {
            double abs = wavefunction.valueAt( i, k ).abs() * intensityScale;
            return abs;
        }
        else {
            return 0;
        }
    }

    public void setRootColor( PhotonColorMap.ColorData rootColor ) {
        this.rootColor = rootColor;
    }

}
