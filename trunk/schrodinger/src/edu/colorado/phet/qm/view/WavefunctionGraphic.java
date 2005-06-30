/* Copyright 2004, Sam Reid */
package edu.colorado.phet.qm.view;

import edu.colorado.phet.common.view.phetgraphics.GraphicLayerSet;
import edu.colorado.phet.common.view.phetgraphics.PhetImageGraphic;
import edu.colorado.phet.qm.model.DiscreteModel;
import edu.colorado.phet.qm.model.Wavefunction;
import edu.colorado.phet.qm.model.operators.PxValue;
import edu.colorado.phet.qm.model.operators.XValue;
import edu.colorado.phet.qm.model.operators.YValue;

import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * User: Sam Reid
 * Date: Jun 30, 2005
 * Time: 12:48:33 PM
 * Copyright (c) Jun 30, 2005 by Sam Reid
 */

public class WavefunctionGraphic extends GraphicLayerSet {

    private int numIterationsBetwenScreenUpdate = 1;

    private boolean displayXExpectation;
    private boolean displayYExpectation;
    private boolean displayCollapsePoint;

    private ColorGrid colorGrid;
    private int colorGridWidth = 600;
    private SchrodingerPanel schrodingerPanel;
    private DefaultPainter painter;
    private PhetImageGraphic imageGraphic;

    private boolean displayPyExpectation = false;

    public WavefunctionGraphic( SchrodingerPanel schrodingerPanel ) {
        this.schrodingerPanel = schrodingerPanel;

        colorGrid = createColorGrid();
        painter = new DefaultPainter( schrodingerPanel );
        colorGrid.colorize( painter );

        imageGraphic = new PhetImageGraphic( schrodingerPanel );
        addGraphic( imageGraphic );
        imageGraphic.setImage( colorGrid.getBufferedImage() );

        getDiscreteModel().addListener( new DiscreteModel.Adapter() {
            public void finishedTimeStep( DiscreteModel model ) {
                if( model.getTimeStep() % numIterationsBetwenScreenUpdate == 0 ) {
                    repaintAll();
                }
            }
        } );
    }

    public void setWavefunctionColorMap( ColorMap painter ) {
        this.painter.setWavefunctionColorMap( painter );
        //todo repaint
        repaintAll();
        imageGraphic.setBoundsDirty();
        imageGraphic.autorepaint();
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

    private Wavefunction getWavefunction() {
        return getDiscreteModel().getWavefunction();
    }

    private DiscreteModel getDiscreteModel() {
        return schrodingerPanel.getDiscreteModel();
    }

    private ColorGrid createColorGrid() {
        return new ColorGrid( colorGridWidth, colorGridWidth, getDiscreteModel().getGridWidth(), getDiscreteModel().getGridHeight() );
    }

    public void repaintAll() {
        colorGrid.colorize( painter );
        finishDrawing();
        imageGraphic.setImage( colorGrid.getBufferedImage() );
    }

    private void finishDrawing() {
        BufferedImage image = colorGrid.getBufferedImage();
        Graphics2D g2 = image.createGraphics();

        if( displayXExpectation ) {
            double xFractional = new XValue().compute( getWavefunction() );
            int x = (int)( xFractional * colorGrid.getBlockWidth() * getDiscreteModel().getGridWidth() );
            g2.setColor( Color.blue );
            g2.fillRect( (int)x, 0, 2, image.getHeight() );
        }
        if( displayYExpectation ) {
            double yFractional = new YValue().compute( getDiscreteModel().getWavefunction() );
            int y = (int)( yFractional * colorGrid.getBlockHeight() * getDiscreteModel().getGridHeight() );
            g2.setColor( Color.blue );
            g2.fillRect( 0, (int)y, image.getWidth(), 2 );
        }
        if( displayCollapsePoint ) {
            Point collapsePoint = getDiscreteModel().getCollapsePoint();
            Rectangle rect = colorGrid.getRectangle( collapsePoint.x, collapsePoint.y );
            g2.setColor( Color.green );
            g2.fillOval( rect.x, rect.y, rect.width, rect.height );
        }
        if( displayPyExpectation ) {
            double px = new PxValue().compute( getWavefunction() );
            System.out.println( "px = " + px );
        }

    }

    public int getBlockWidth() {
        return colorGrid.getBlockWidth();
    }

    public int getWavefunctionWidth() {
        return colorGrid.getWidth();
    }
}
