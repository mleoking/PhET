/* Copyright 2004, Sam Reid */
package edu.colorado.phet.qm.view;

import edu.colorado.phet.qm.model.DiscreteModel;
import edu.colorado.phet.qm.model.Wavefunction;
import edu.colorado.phet.qm.model.operators.PxValue;
import edu.colorado.phet.qm.model.operators.XValue;
import edu.colorado.phet.qm.model.operators.YValue;
import edu.colorado.phet.qm.view.colormaps.*;
import edu.colorado.phet.qm.view.gun.Photon;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PImage;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.util.PPaintContext;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

/**
 * User: Sam Reid
 * Date: Jun 30, 2005
 * Time: 12:48:33 PM
 * Copyright (c) Jun 30, 2005 by Sam Reid
 */

public class WavefunctionGraphic extends PNode {

    public static int numIterationsBetwenScreenUpdate = 2;//TODO make this obvious at top level!
    private boolean displayXExpectation;
    private boolean displayYExpectation;
    private boolean displayCollapsePoint;
    private ColorGrid colorGrid;
    private int colorGridWidth = 400;
    private SchrodingerPanel schrodingerPanel;
    private DefaultPainter painter;
    private PImage imageGraphic;

    private boolean displayPyExpectation = false;
    private PPath borderGraphic;
    private MagnitudeColorMap magnitudeColorMap;
    private MagnitudeColorMap realColorMap;
    private MagnitudeColorMap imagColorMap;
    private double wavefunctionScale = 1.0;

    public WavefunctionGraphic( final SchrodingerPanel schrodingerPanel ) {
        this.schrodingerPanel = schrodingerPanel;

        colorGrid = createColorGrid();

        magnitudeColorMap = new MagnitudeColorMap( schrodingerPanel, new MagnitudeInGrayscale( schrodingerPanel ), new WaveValueAccessor.Magnitude() );
        realColorMap = new MagnitudeColorMap( schrodingerPanel, new RealGrayColorMap( schrodingerPanel ), new WaveValueAccessor.Real() );
        imagColorMap = new MagnitudeColorMap( schrodingerPanel, new ImaginaryGrayColorMap( schrodingerPanel ), new WaveValueAccessor.Imag() );


        painter = new DefaultPainter( schrodingerPanel, magnitudeColorMap );
        colorGrid.colorize( painter );

        imageGraphic = new PImage();
        addChild( imageGraphic );
        imageGraphic.setImage( colorGrid.getBufferedImage() );

        borderGraphic = new PPath( new Rectangle( colorGrid.getWidth(), colorGrid.getHeight() ) );
        borderGraphic.setStroke( new BasicStroke( 2 ) );
        borderGraphic.setStrokePaint( Color.white );
        addChild( borderGraphic );

        getDiscreteModel().addListener( new DiscreteModel.Adapter() {
            public void finishedTimeStep( DiscreteModel model ) {
                if( model.getTimeStep() % numIterationsBetwenScreenUpdate == 0 ) {
                    repaintAll();
                }
            }
        } );

        setRenderingHint( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF );
        setRenderingHint( RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR );
    }

    public int getWaveformWidth() {
        return (int)imageGraphic.getWidth();
    }

    public void fullPaint( PPaintContext paintContext ) {
//        paintContext.getGraphics().setRenderingHint( Render);
        super.fullPaint( paintContext );
    }

    public void setWavefunctionColorMap( ColorMap painter ) {
        this.painter.setWavefunctionColorMap( painter );
        repaintAll();
//        imageGraphic.setBoundsDirty();
//        imageGraphic.autorepaint();
        schrodingerPanel.paintImmediately( 0, 0, schrodingerPanel.getWidth(), schrodingerPanel.getHeight() );
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
//        imageGraphic.setBoundsDirty();
//        imageGraphic.autorepaint();
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

    public int getWaveformX() {
        return (int)imageGraphic.getX();
    }

    public MagnitudeColorMap getMagnitudeColorMap() {
        return magnitudeColorMap;
    }

    public MagnitudeColorMap getRealColorMap() {
        return realColorMap;
    }

    public MagnitudeColorMap getImagColorMap() {
        return imagColorMap;
    }

    public void setPhoton( Photon photon ) {
        magnitudeColorMap.setPhoton( photon );
        realColorMap.setPhoton( photon );
        imagColorMap.setPhoton( photon );
    }

    public void setWaveSize( int width, int height ) {
        colorGrid.setModelSize( width, height );
        imageGraphic.setImage( colorGrid.getBufferedImage() );
        double aspectRatio = colorGrid.getBufferedImage().getWidth() / ( (double)colorGridWidth );
        imageGraphic.setTransform( new AffineTransform() );
        wavefunctionScale = 1.0 / aspectRatio;
        imageGraphic.scale( wavefunctionScale );
    }

    public double getWaveImageScaleX() {
        return wavefunctionScale;
    }
}
