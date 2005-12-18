/* Copyright 2004, Sam Reid */
package edu.colorado.phet.qm.view.piccolo;

import edu.colorado.phet.piccolo.PhetPCanvas;
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

public class WavefunctionGraphic extends SimpleWavefunctionGraphic {
    public static int numIterationsBetwenScreenUpdate = 2;//TODO make this obvious at top level!
    private boolean displayXExpectation;
    private boolean displayYExpectation;
    private boolean displayCollapsePoint;
    private boolean displayPyExpectation = false;

//    private MagnitudeColorMap magnitudeColorMap;
//    private MagnitudeColorMap realColorMap;
//    private MagnitudeColorMap imagColorMap;
//    private double wavefunctionScale = 1.0;

//    private PImage imageGraphic;
//    private PPath borderGraphic;

    public WavefunctionGraphic( final PhetPCanvas schrodingerPanel, Wavefunction wavefunction ) {
        super( wavefunction );
//        this.schrodingerPanel = schrodingerPanel;

//        colorGrid = createColorGrid();

//        magnitudeColorMap = new MagnitudeInGrayscale3();
//        magnitudeColorMap = new MagnitudeColorMap( schrodingerPanel, new MagnitudeInGrayscale( schrodingerPanel ), new WaveValueAccessor.Magnitude() );
//        realColorMap = new MagnitudeColorMap( schrodingerPanel, new RealGrayColorMap( schrodingerPanel ), new WaveValueAccessor.Real() );
//        imagColorMap = new MagnitudeColorMap( schrodingerPanel, new ImaginaryGrayColorMap( schrodingerPanel ), new WaveValueAccessor.Imag() );

//        setRenderingHint( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF );
//        setRenderingHint( RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR );
//        imageGraphic.setRenderingHint( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF );
//        imageGraphic.setRenderingHint( RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR );

//        PropertyChangeListener pcl = new PropertyChangeListener() {
//            public void propertyChange( PropertyChangeEvent evt ) {
//                borderGraphic.setPathTo( imageGraphic.getFullBounds() );
//            }
//        };
//        imageGraphic.addPropertyChangeListener( PNode.PROPERTY_FULL_BOUNDS, pcl );
//        imageGraphic.addPropertyChangeListener( PNode.PROPERTY_BOUNDS, pcl );
    }
//
//    public void fullPaint( PPaintContext paintContext ) {
//        Graphics2D g = paintContext.getGraphics();
//
//        Object origAnt = g.getRenderingHint( RenderingHints.KEY_ANTIALIASING );
//        Object origInt = g.getRenderingHint( RenderingHints.KEY_INTERPOLATION );
//        g.setRenderingHint( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF );
//        g.setRenderingHint( RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR );
//        super.fullPaint( paintContext );
//        if( origAnt == null ) {
//            origAnt = RenderingHints.VALUE_ANTIALIAS_DEFAULT;
//        }
//        if( origInt == null ) {
//            origInt = RenderingHints.VALUE_INTERPOLATION_BICUBIC;
//        }
//        g.setRenderingHint( RenderingHints.KEY_ANTIALIASING, origAnt );
//        g.setRenderingHint( RenderingHints.KEY_INTERPOLATION, origInt );
//    }

//    public void setWavefunctionColorMap( ColorMap painter ) {
//        this.painter.setWavefunctionColorMap( painter );
//        repaintAll();
//        schrodingerPanel.paintImmediately( 0, 0, schrodingerPanel.getWidth(), schrodingerPanel.getHeight() );
//    }

    public void setDisplayXExpectation( boolean displayXExpectation ) {
        this.displayXExpectation = displayXExpectation;
    }

    public void setDisplayYExpectation( boolean displayYExpectation ) {
        this.displayYExpectation = displayYExpectation;
    }

    public void setDisplayCollapsePoint( boolean displayCollapsePoint ) {
        this.displayCollapsePoint = displayCollapsePoint;
    }

//    public ColorGrid getColorGrid() {
//        return colorGrid;
//    }

//    private Wavefunction getWavefunction() {
//        return getDiscreteModel().getWavefunction();
//    }

//    private DiscreteModel getDiscreteModel() {
//        return schrodingerPanel.getDiscreteModel();
//    }

//    private ColorGrid createColorGrid() {
//        int cellWidth = 10;
//        int cellHeight = 10;
////        System.out.println( "WavefunctionGraphic.createColorGrid" );
//        return new ColorGrid( cellWidth, cellHeight, getDiscreteModel().getGridWidth(), getDiscreteModel().getGridHeight() );
//    }

//    public void update() {
//        super.update();
//        finishDrawing();
//    }

    protected void decorateBuffer() {
        BufferedImage image = getColorGridNode().getBufferedImage();
        Graphics2D g2 = image.createGraphics();

        if( displayXExpectation ) {
            double xFractional = new XValue().compute( getWavefunction() );
            int x = (int)( xFractional * getCellWidth() * getWavefunction().getWidth() );
            g2.setColor( Color.blue );
            g2.fillRect( (int)x, 0, 2, image.getHeight() );
        }
        if( displayYExpectation ) {
            double yFractional = new YValue().compute( getWavefunction() );
            int y = (int)( yFractional * getCellHeight() * getWavefunction().getHeight() );
            g2.setColor( Color.blue );
            g2.fillRect( 0, (int)y, image.getWidth(), 2 );
        }
//        if( displayCollapsePoint ) {
//            Point collapsePoint = getDiscreteModel().getCollapsePoint();
//            Rectangle rect = colorGrid.getRectangle( collapsePoint.x, collapsePoint.y );
//            g2.setColor( Color.green );
//            g2.fillOval( rect.x, rect.y, rect.width, rect.height );
//        }
        if( displayPyExpectation ) {
            double px = new PxValue().compute( getWavefunction() );
            System.out.println( "px = " + px );
        }

    }

    private int getCellHeight() {
        return getColorGridNode().getCellHeight();
    }

    private int getCellWidth() {
        return getColorGridNode().getCellWidth();
    }

//    public int getWavefunctionGraphicWidth() {
//        return colorGrid.getWidth();
//    }
//
//    public MagnitudeColorMap getMagnitudeColorMap() {
//        return magnitudeColorMap;
//    }
//
//    public MagnitudeColorMap getRealColorMap() {
//        return realColorMap;
//    }
//
//    public MagnitudeColorMap getImagColorMap() {
//        return imagColorMap;
//    }

//    public void setPhoton( Photon photon ) {
//        magnitudeColorMap.setPhoton( photon );
//        realColorMap.setPhoton( photon );
//        imagColorMap.setPhoton( photon );
//    }
//
//    //todo this was previously used to scale the wavefunction graphic.
//    public void setWaveSize( int width, int height ) {
//        colorGrid.setModelSize( width, height );
//        imageGraphic.setImage( colorGrid.getBufferedImage() );
//        borderGraphic.setPathTo( imageGraphic.getFullBounds() );
//    }
//
//    public double getWaveImageScaleX() {
//        return wavefunctionScale;
//    }
//
//    public void setCellDimensions( int cellWidth, int cellHeight ) {
//        colorGrid.setCellDimensions( cellWidth, cellHeight );
//        imageGraphic.setImage( colorGrid.getBufferedImage() );
//        borderGraphic.setPathTo( imageGraphic.getFullBounds() );
//    }


    public int getWavefunctionGraphicWidth() {
        return getColorGrid().getWidth();
    }

}
