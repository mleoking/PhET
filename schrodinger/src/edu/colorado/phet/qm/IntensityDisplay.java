/* Copyright 2004, Sam Reid */
package edu.colorado.phet.qm;

import edu.colorado.phet.common.math.Function;
import edu.colorado.phet.qm.model.DetectorSet;
import edu.colorado.phet.qm.model.DiscreteModel;
import edu.colorado.phet.qm.model.VerticalETA;
import edu.colorado.phet.qm.model.Wavefunction;
import edu.colorado.phet.qm.view.DetectorSheet;
import edu.colorado.phet.qm.view.SchrodingerPanel;

import java.awt.*;
import java.util.Random;

/**
 * User: Sam Reid
 * Date: Jun 23, 2005
 * Time: 1:43:24 PM
 * Copyright (c) Jun 23, 2005 by Sam Reid
 */

public class IntensityDisplay implements VerticalETA.Listener {
    private SchrodingerModule schrodingerModule;
    private int detectorHeight;
    private Random random;
    public DetectorSheet graphic;

    public IntensityDisplay( SchrodingerModule schrodingerModule, int detectorHeight ) {
        this.schrodingerModule = schrodingerModule;
        this.detectorHeight = detectorHeight;
        this.random = new Random();

        graphic = new DetectorSheet( getSchrodingerPanel(), getWidth(), detectorHeight );
        getSchrodingerPanel().addGraphic( graphic );
    }

    public int getWidth() {
        return getSchrodingerPanel().getColorGrid().getWidth();
    }

    public void arrived() {
        int h = 3;
        Wavefunction sub = getDiscreteModel().getWavefunction().copyRegion( 0, getDiscreteModel().getDamping().getDepth(), getDiscreteModel().getWavefunction().getWidth(), h );
        sub.normalize();

        Function.LinearFunction linearFunction = new Function.LinearFunction( 0, getDiscreteModel().getGridWidth(), 0, getWidth() );

        for( int i = 0; i < 300; i++ ) {
            Point pt = getCollapsePoint( sub );

            int displayVal = (int)linearFunction.evaluate( pt.x );
            double scale = detectorHeight / 8;
            double offset = detectorHeight / 2;
            int y = (int)( random.nextGaussian() * scale + offset );
            graphic.addDetectionEvent( displayVal, y );
        }
    }

    private SchrodingerPanel getSchrodingerPanel() {
        return schrodingerModule.getSchrodingerPanel();
    }

    private DiscreteModel getDiscreteModel() {
        return schrodingerModule.getDiscreteModel();
    }

    private Point getCollapsePoint( Wavefunction sub ) {
        DetectorSet detectorSet = new DetectorSet( sub );
        Point pt = detectorSet.getCollapsePoint();
        return pt;
    }

    public void reset() {
        graphic.reset();
    }
}
