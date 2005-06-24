/* Copyright 2004, Sam Reid */
package edu.colorado.phet.qm.view;

import edu.colorado.phet.common.view.ApparatusPanel;
import edu.colorado.phet.common.view.phetgraphics.PhetImageGraphic;
import edu.colorado.phet.qm.IntensityDisplay;
import edu.colorado.phet.qm.SchrodingerModule;
import edu.colorado.phet.qm.model.DiscreteModel;
import edu.colorado.phet.qm.model.Wavefunction;
import edu.colorado.phet.qm.model.operators.PxValue;
import edu.colorado.phet.qm.model.operators.XValue;
import edu.colorado.phet.qm.model.operators.YValue;
import edu.colorado.phet.qm.phetcommon.RulerGraphic;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

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
    private ArrayList rectanglePotentialGraphics = new ArrayList();
    private boolean displayPyExpectation = false;
    private GunGraphic gunGraphic;
    private int colorGridWidth = 600;
    private IntensityDisplay intensityDisplay;
    public RulerGraphic rulerGraphic;

    public SchrodingerPanel( SchrodingerModule module ) {
        setLayout( null );
        this.module = module;
        this.discreteModel = module.getDiscreteModel();

        colorGrid = createColorGrid();
        painter = new DefaultPainter( this );
        colorGrid.colorize( painter );

        discreteModel.addListener( this );
        wavefunctionGraphic = new PhetImageGraphic( this );
        addGraphic( wavefunctionGraphic );
        wavefunctionGraphic.setLocation( 0, 50 );
        wavefunctionGraphic.setImage( colorGrid.getBufferedImage() );

        gunGraphic = new GunGraphic( this );
        addGraphic( gunGraphic );
        gunGraphic.setLocation( wavefunctionGraphic.getX() + wavefunctionGraphic.getWidth() / 2 - gunGraphic.getGunWidth() / 2,
                                wavefunctionGraphic.getY() + wavefunctionGraphic.getHeight() );

        rulerGraphic = new RulerGraphic( this );
        addGraphic( rulerGraphic, Double.POSITIVE_INFINITY );
        rulerGraphic.setLocation( 20, 20 );
        rulerGraphic.setVisible( false );

        intensityDisplay = new IntensityDisplay( getSchrodingerModule(), this, 50 );
        setIntensityDisplayRecordsParticles();
    }

    protected void paintComponent( Graphics graphics ) {
        super.paintComponent( graphics );
        paintChildren( graphics );
    }

    public void setRulerVisible( boolean rulerVisible ) {
        rulerGraphic.setVisible( rulerVisible );
    }

    private void setIntensityDisplayRecordsParticles() {
        getDiscreteModel().getVerticalEta().addListener( intensityDisplay );
    }

    private ColorGrid createColorGrid() {
        return new ColorGrid( colorGridWidth, colorGridWidth, discreteModel.getGridWidth(), discreteModel.getGridHeight() );
    }

    public void reset() {
        intensityDisplay.reset();
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

    private Wavefunction getWavefunction() {
        return getDiscreteModel().getWavefunction();
    }

    public void sizeChanged() {
        colorGrid = createColorGrid();
    }

    public void potentialChanged() {
//        colorMap.setPotential(discreteModel.getPotential());
        System.out.println( "potential changed" );
    }

    public void beforeTimeStep( DiscreteModel discreteModel ) {
    }

    public void particleFired( DiscreteModel discreteModel ) {
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

    public void addRectangularPotentialGraphic( RectangularPotentialGraphic rectangularPotentialGraphic ) {
        rectanglePotentialGraphics.add( rectangularPotentialGraphic );
        addGraphic( rectangularPotentialGraphic );
    }

    public void clearPotential() {
        for( int i = 0; i < rectanglePotentialGraphics.size(); i++ ) {
            RectangularPotentialGraphic rectangularPotentialGraphic = (RectangularPotentialGraphic)rectanglePotentialGraphics.get( i );
            removeGraphic( rectangularPotentialGraphic );
        }
        rectanglePotentialGraphics.clear();
    }

    public PhetImageGraphic getWavefunctionGraphic() {
        return wavefunctionGraphic;
    }

    public SchrodingerModule getSchrodingerModule() {
        return module;
    }

    public IntensityDisplay getIntensityDisplay() {
        return intensityDisplay;
    }

    public RulerGraphic getRulerGraphic() {
        return rulerGraphic;
    }
}
