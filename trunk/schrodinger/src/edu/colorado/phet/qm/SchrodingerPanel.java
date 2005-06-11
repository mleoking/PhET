/* Copyright 2004, Sam Reid */
package edu.colorado.phet.qm;

import edu.colorado.phet.common.view.ApparatusPanel;
import edu.colorado.phet.common.view.phetgraphics.PhetImageGraphic;

/**
 * User: Sam Reid
 * Date: Jun 10, 2005
 * Time: 6:55:21 PM
 * Copyright (c) Jun 10, 2005 by Sam Reid
 */

public class SchrodingerPanel extends ApparatusPanel implements DiscreteModel.Listener {
    private DiscreteModel discreteModel;
    private SchrodingerModule module;
    private ColorGrid colorGrid;
    private ColorMap colorMap;
    private int numIterationsBetwenScreenUpdate = 1;
    public PhetImageGraphic wavefunctionGraphic;

    public SchrodingerPanel( SchrodingerModule module ) {
        this.module = module;
        this.discreteModel = module.getDiscreteModel();

        colorGrid = new ColorGrid( 600, 600, discreteModel.getXMesh(), discreteModel.getYMesh() );
        colorMap = new DefaultColorMap( this );
        colorGrid.colorize( colorMap );

        discreteModel.addListener( this );
        wavefunctionGraphic = new PhetImageGraphic( this );
        addGraphic( wavefunctionGraphic );

    }

    public void reset() {
    }

    public void updateGraphics() {
    }

    public void finishedTimeStep( DiscreteModel model ) {
        if( model.getTimeStep() % numIterationsBetwenScreenUpdate == 0 ) {
            colorGrid.colorize( colorMap );
            wavefunctionGraphic.setImage( colorGrid.getBufferedImage() );
        }
    }

    public void sizeChanged() {
        colorGrid = new ColorGrid( 600, 600, discreteModel.getXMesh(), discreteModel.getYMesh() );
    }

    public void potentialChanged() {
//        colorMap.setPotential(discreteModel.getPotential());
        System.out.println( "potential changed" );
    }

    public DiscreteModel getDiscreteModel() {
        return discreteModel;
    }

    public void setColorMap( ColorMap colorMap ) {
        this.colorMap = colorMap;
        //todo repaint
    }
}
