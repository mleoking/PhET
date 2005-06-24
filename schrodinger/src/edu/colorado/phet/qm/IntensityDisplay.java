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
    private SchrodingerPanel schrodingerPanel;
    private int detectorHeight;
    private Random random;
    public DetectorSheet graphic;
    public int h = 3;
    public Wavefunction sub;

    public IntensityDisplay( SchrodingerModule schrodingerModule, SchrodingerPanel schrodingerPanel, int detectorHeight ) {
        this.schrodingerModule = schrodingerModule;
        this.schrodingerPanel = schrodingerPanel;
        this.detectorHeight = detectorHeight;
        this.random = new Random();

        graphic = new DetectorSheet( schrodingerPanel, getWidth(), detectorHeight );
        getSchrodingerPanel().addGraphic( graphic );
    }

    public int getWidth() {
        return getSchrodingerPanel().getColorGrid().getWidth();
    }

    public void arrived() {
        sub = getDiscreteModel().getWavefunction().copyRegion( 0, getDiscreteModel().getDamping().getDepth(), getDiscreteModel().getWavefunction().getWidth(), h );
        sub.normalize();
        detectOne();
    }

    public void detectOne() {
        Function.LinearFunction linearFunction = new Function.LinearFunction( 0, getDiscreteModel().getGridWidth(), 0, getWidth() );
        Point pt = getCollapsePoint( sub );

        double screenGridWidth = schrodingerModule.getSchrodingerPanel().getColorGrid().getBlockWidth();
        double randOffset = 2 * ( random.nextDouble() - 0.5 ) * screenGridWidth;

        int displayVal = (int)( linearFunction.evaluate( pt.x ) + randOffset );
        double scale = detectorHeight / 8;
        double offset = detectorHeight / 2;
        int y = (int)( random.nextGaussian() * scale + offset );
        graphic.addDetectionEvent( displayVal, y );
    }

    private SchrodingerPanel getSchrodingerPanel() {
        return schrodingerPanel;
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
