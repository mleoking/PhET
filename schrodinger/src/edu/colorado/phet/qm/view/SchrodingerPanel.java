/* Copyright 2004, Sam Reid */
package edu.colorado.phet.qm.view;

import edu.colorado.phet.common.view.ApparatusPanel;
import edu.colorado.phet.common.view.phetgraphics.PhetImageGraphic;
import edu.colorado.phet.qm.SchrodingerModule;
import edu.colorado.phet.qm.model.Complex;
import edu.colorado.phet.qm.model.DiscreteModel;
import edu.colorado.phet.qm.model.XValue;
import edu.colorado.phet.qm.model.YValue;

import java.awt.*;
import java.awt.image.BufferedImage;

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
    private DefaultPainter painter;
    private int numIterationsBetwenScreenUpdate = 1;
    private PhetImageGraphic wavefunctionGraphic;
    private boolean displayXExpectation;
    private boolean displayYExpectation;
    private boolean displayCollapsePoint;

    public SchrodingerPanel( SchrodingerModule module ) {
        this.module = module;
        this.discreteModel = module.getDiscreteModel();

        colorGrid = createColorGrid();
        painter = new DefaultPainter( this );
        colorGrid.colorize( painter );

        discreteModel.addListener( this );
        wavefunctionGraphic = new PhetImageGraphic( this );
        addGraphic( wavefunctionGraphic );

//        DetectorGraphic detectorGraphic = new DetectorGraphic( this, new Detector( 10, 10, 10, 10 ) );
//        addGraphic( detectorGraphic );
    }

    private ColorGrid createColorGrid() {
        return new ColorGrid( 600, 600, discreteModel.getXMesh(), discreteModel.getYMesh() );
    }

    public void reset() {
    }

    public void updateGraphics() {
    }

    public void finishedTimeStep( DiscreteModel model ) {
        if( model.getTimeStep() % numIterationsBetwenScreenUpdate == 0 ) {
            colorGrid.colorize( painter );
            finishDrawing();
            wavefunctionGraphic.setImage( colorGrid.getBufferedImage() );
        }
    }

    private void finishDrawing() {
        BufferedImage image = colorGrid.getBufferedImage();
        Graphics2D g2 = image.createGraphics();

        if( displayXExpectation ) {
            double xFractional = new XValue().compute( getWavefunction() );
            int x = (int)( xFractional * colorGrid.getBlockWidth() * getDiscreteModel().getXMesh() );
            g2.setColor( Color.blue );
            g2.fillRect( (int)x, 0, 2, image.getHeight() );
        }
        if( displayYExpectation ) {
            double yFractional = new YValue().compute( getDiscreteModel().getWavefunction() );
            int y = (int)( yFractional * colorGrid.getBlockHeight() * getDiscreteModel().getYMesh() );
            g2.setColor( Color.blue );
            g2.fillRect( 0, (int)y, image.getWidth(), 2 );
        }
        if( displayCollapsePoint ) {
            Point collapsePoint = getDiscreteModel().getCollapsePoint();
            Rectangle rect = colorGrid.getRectangle( collapsePoint.x, collapsePoint.y );
            g2.setColor( Color.green );
            g2.fillRect( rect.x, rect.y, rect.width, rect.height );
        }
    }

    private Complex[][] getWavefunction() {
        return getDiscreteModel().getWavefunction();
    }

    public void sizeChanged() {
        colorGrid = createColorGrid();
    }

    public void potentialChanged() {
//        colorMap.setPotential(discreteModel.getPotential());
        System.out.println( "potential changed" );
    }

    public DiscreteModel getDiscreteModel() {
        return discreteModel;
    }

    public void setWavefunctionColorMap( ColorMap painter ) {
        this.painter.setWavefunctionColorMap( painter );
        //todo repaint
    }


    public boolean isDisplayXExpectation() {
        return displayXExpectation;
    }

    public void setDisplayXExpectation( boolean displayXExpectation ) {
        this.displayXExpectation = displayXExpectation;
    }

    public boolean isDisplayYExpectation() {
        return displayYExpectation;
    }

    public void setDisplayYExpectation( boolean displayYExpectation ) {
        this.displayYExpectation = displayYExpectation;
    }

    public void setDisplayCollapsePoint( boolean displayCollapsePoint ) {
        this.displayCollapsePoint = displayCollapsePoint;
    }

    public ColorGrid getColorGrid() {
        return colorGrid;
    }

    public void addDetectorGraphic( DetectorGraphic detectorGraphic ) {
        addGraphic( detectorGraphic );
    }
}
