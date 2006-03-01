/* Copyright 2004, Sam Reid */
package edu.colorado.phet.qm.view.piccolo;

import edu.colorado.phet.common.view.clock.StopwatchPanel;
import edu.colorado.phet.common.view.util.ImageLoader;
import edu.colorado.phet.piccolo.event.CursorHandler;
import edu.colorado.phet.piccolo.nodes.BoundGraphic;
import edu.colorado.phet.piccolo.nodes.ConnectorGraphic;
import edu.colorado.phet.qm.HorizontalConnector;
import edu.colorado.phet.qm.SchrodingerModule;
import edu.colorado.phet.qm.model.Detector;
import edu.colorado.phet.qm.model.DiscreteModel;
import edu.colorado.phet.qm.model.ParticleUnits;
import edu.colorado.phet.qm.phetcommon.RulerGraphic;
import edu.colorado.phet.qm.phetcommon.SchrodingerRulerGraphic;
import edu.colorado.phet.qm.view.ClockGraphic;
import edu.colorado.phet.qm.view.SchrodingerPanel;
import edu.colorado.phet.qm.view.gun.AbstractGunGraphic;
import edu.colorado.phet.qm.view.gun.GunControlPanel;
import edu.colorado.phet.qm.view.piccolo.detectorscreen.DetectorSheetPNode;
import edu.colorado.phet.qm.view.piccolo.detectorscreen.IntensityManager;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PDragEventHandler;
import edu.umd.cs.piccolo.nodes.PText;
import edu.umd.cs.piccolox.pswing.PSwing;

import javax.swing.*;
import javax.swing.border.BevelBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * User: Sam Reid
 * Date: Sep 17, 2005
 * Time: 7:52:28 PM
 * Copyright (c) Sep 17, 2005 by Sam Reid
 */

public class SchrodingerScreenNode extends PNode {
    private SchrodingerModule module;
    private SchrodingerPanel schrodingerPanel;

    private WavefunctionGraphic wavefunctionGraphic;
    private ArrayList rectanglePotentialGraphics = new ArrayList();
    private ArrayList detectorGraphics = new ArrayList();

    private AbstractGunGraphic abstractGunGraphic;
    private IntensityManager intensityManager;
    private SchrodingerRulerGraphic rulerGraphic;

    private Dimension lastLayoutSize = null;
    private static final int WAVE_AREA_LAYOUT_INSET_X = 20;
    private static final int WAVE_AREA_LAYOUT_INSET_Y = 20;
    public static int numIterationsBetwenScreenUpdate = 2;
    private DetectorSheetPNode detectorSheetPNode;
    private StopwatchPanel stopwatchPanel;
    private ParticleUnits particleUnits = new ParticleUnits.ElectronUnits();
    private Color TEXT_BACKGROUND = new Color( 255, 245, 190 );
    private PNode gunTypeChooserGraphic;
    private PSwing stopwatchPanelPSwing;
    private PNode gunControlPanelPSwing;
//    private String slowdownText = "Slowing down the simulation to observe faster phenomenon...";
//    private String speedupText = "Speeding up the simulation to observe slower phenomenon...";
    private String slowdownText = "Slowing Time Down";
    private String speedupText = "Speeding Time Up";

    public SchrodingerScreenNode( SchrodingerModule module, final SchrodingerPanel schrodingerPanel ) {
        this.module = module;
        this.schrodingerPanel = schrodingerPanel;
        wavefunctionGraphic = new WavefunctionGraphic( getDiscreteModel(), module.getDiscreteModel().getWavefunction() );
        getDiscreteModel().addListener( new DiscreteModel.Adapter() {
            public void finishedTimeStep( DiscreteModel model ) {
                if( model.getTimeStep() % numIterationsBetwenScreenUpdate == 0 ) {
                    wavefunctionGraphic.update();
                }
            }
        } );

        String[]digits = new String[11];
        for( int i = 0; i < digits.length; i++ ) {
            digits[i] = new String( i + "" );
        }
        RulerGraphic rg = new RulerGraphic( digits, "units", 500, 60 );
        rulerGraphic = new SchrodingerRulerGraphic( getDiscreteModel(), schrodingerPanel, rg );

        getDiscreteModel().addListener( new DiscreteModel.Adapter() {
            public void sizeChanged() {
                updateRulerUnits();
            }
        } );
        wavefunctionGraphic.addPropertyChangeListener( PNode.PROPERTY_FULL_BOUNDS, new PropertyChangeListener() {
            public void propertyChange( PropertyChangeEvent evt ) {
                updateRulerUnits();
            }
        } );

        rulerGraphic.setOffset( 50, 200 );
        setRulerVisible( false );

        detectorSheetPNode = new DetectorSheetPNode( schrodingerPanel, wavefunctionGraphic, 60 );
        detectorSheetPNode.setOffset( wavefunctionGraphic.getX(), 0 );
        intensityManager = new IntensityManager( getSchrodingerModule(), schrodingerPanel, detectorSheetPNode );
        addChild( detectorSheetPNode );
        addChild( wavefunctionGraphic );
        addChild( rulerGraphic );
        schrodingerPanel.addComponentListener( new ComponentAdapter() {
            public void componentResized( ComponentEvent e ) {
                invalidateFullBounds();
                repaint();
            }

            public void componentShown( ComponentEvent e ) {
                invalidateFullBounds();
                repaint();
            }

        } );

        layoutChildren();
        stopwatchPanel = new StopwatchPanel( schrodingerPanel.getSchrodingerModule().getClock(), "ps", 1.0, new DecimalFormat( "0.00" ) );
        stopwatchPanel.setBorder( BorderFactory.createBevelBorder( BevelBorder.RAISED ) );
        stopwatchPanelPSwing = new PSwing( schrodingerPanel, stopwatchPanel );
        stopwatchPanelPSwing.addInputEventListener( new PDragEventHandler() );
        stopwatchPanelPSwing.addInputEventListener( new CursorHandler( Cursor.HAND_CURSOR ) );
        addChild( stopwatchPanelPSwing );
        stopwatchPanelPSwing.setOffset( 300, 300 );
        setStopwatchVisible( false );
    }

    public WavefunctionGraphic getWavefunctionGraphic() {
        return wavefunctionGraphic;
    }

    private DiscreteModel getDiscreteModel() {
        return getSchrodingerModule().getDiscreteModel();
    }

    private SchrodingerModule getSchrodingerModule() {
        return schrodingerPanel.getSchrodingerModule();
    }

    public void setGunGraphic( AbstractGunGraphic abstractGunGraphic ) {
        if( abstractGunGraphic != null ) {
            if( getChildrenReference().contains( abstractGunGraphic ) ) {
                removeChild( abstractGunGraphic );
            }
        }
        this.abstractGunGraphic = abstractGunGraphic;
        addChild( abstractGunGraphic );

        invalidateLayout();
        repaint();
    }

    private int getGunGraphicOffsetY() {
        return 50;
    }

    public void setRulerVisible( boolean rulerVisible ) {
        rulerGraphic.setVisible( rulerVisible );
    }

    public void reset() {
        detectorSheetPNode.reset();
        intensityManager.reset();
    }

    public void addDetectorGraphic( DetectorGraphic detectorGraphic ) {
        detectorGraphics.add( detectorGraphic );
        addChild( detectorGraphic );
    }

    public void addRectangularPotentialGraphic( RectangularPotentialGraphic rectangularPotentialGraphic ) {
        rectanglePotentialGraphics.add( rectangularPotentialGraphic );
        addChild( rectangularPotentialGraphic );
    }

    public void clearPotential() {
        while( rectanglePotentialGraphics.size() > 0 ) {
            removePotentialGraphic( (RectangularPotentialGraphic)rectanglePotentialGraphics.get( 0 ) );
        }
    }

    public IntensityManager getIntensityDisplay() {
        return intensityManager;
    }

    public SchrodingerRulerGraphic getRulerGraphic() {
        return rulerGraphic;
    }

    public AbstractGunGraphic getGunGraphic() {
        return abstractGunGraphic;
    }

    public void removeDetectorGraphic( DetectorGraphic detectorGraphic ) {
        removeChild( detectorGraphic );
        getDiscreteModel().removeDetector( detectorGraphic.getDetector() );
        detectorGraphics.remove( detectorGraphic );
    }

    public DetectorGraphic getDetectorGraphic( Detector detector ) {
        for( int i = 0; i < detectorGraphics.size(); i++ ) {
            DetectorGraphic detectorGraphic = (DetectorGraphic)detectorGraphics.get( i );
            if( detectorGraphic.getDetector() == detector ) {
                return detectorGraphic;
            }
        }
        return null;
    }

    public void setWaveSize( int width, int height ) {
        wavefunctionGraphic.setGridDimensions( width, height );
        relayout();
    }

    public void relayout() {
        layoutChildren( true );
    }

    protected void layoutChildren() {
        layoutChildren( false );
    }

    protected void layoutChildren( boolean forceLayout ) {
        boolean sizeChanged = lastLayoutSize == null || !lastLayoutSize.equals( schrodingerPanel.getSize() );
        if( sizeChanged || forceLayout ) {
            lastLayoutSize = new Dimension( schrodingerPanel.getSize() );
            super.layoutChildren();
            if( schrodingerPanel.getWidth() > 0 && schrodingerPanel.getHeight() > 0 ) {
                wavefunctionGraphic.setCellDimensions( getCellDimensions() );
                double minX = Math.min( detectorSheetPNode.getFullBounds().getMinX(), abstractGunGraphic.getFullBounds().getMinX() );
                double maxX = Math.max( detectorSheetPNode.getFullBounds().getMaxX(), abstractGunGraphic.getFullBounds().getMaxX() );
                double mainWidth = maxX - minX;
                double availableWidth = schrodingerPanel.getWidth() - mainWidth;
                wavefunctionGraphic.setOffset( availableWidth / 2, detectorSheetPNode.getDetectorHeight() );

                detectorSheetPNode.setAlignment( wavefunctionGraphic );
                abstractGunGraphic.setOffset( wavefunctionGraphic.getFullBounds().getCenterX() - abstractGunGraphic.getGunWidth() / 2 + 10,
                                              wavefunctionGraphic.getFullBounds().getMaxY() - getGunGraphicOffsetY() );
                if( gunTypeChooserGraphic != null ) {
                    gunTypeChooserGraphic.setOffset( wavefunctionGraphic.getFullBounds().getX(), wavefunctionGraphic.getFullBounds().getMaxY() );
                }
                if( gunControlPanelPSwing != null ) {
                    double insetY = 5;
                    double screenHeight = schrodingerPanel.getHeight();
                    //double xval = wavefunctionGraphic.getFullBounds().getMaxX();
                    Point2D pt = new Point2D.Double();
                    detectorSheetPNode.getDetectorSheetControlPanelPNode().localToGlobal( pt );
                    globalToLocal( pt );

                    gunControlPanelPSwing.setOffset( pt.getX(),
                                                     screenHeight - gunControlPanelPSwing.getFullBounds().getHeight() - insetY );
                }
            }
        }
    }

    private Dimension getCellDimensions() {
        Dimension availableSize = schrodingerPanel.getSize();
        availableSize.width -= getDetectorSheetControlPanelNode().getFullBounds().getWidth();
        availableSize.width -= WAVE_AREA_LAYOUT_INSET_X;

        availableSize.height -= abstractGunGraphic.getFullBounds().getHeight();
        availableSize.height -= WAVE_AREA_LAYOUT_INSET_Y;

        Dimension availableAreaForWaveform = new Dimension( availableSize.width, availableSize.height );
        int nx = schrodingerPanel.getDiscreteModel().getGridWidth();
        int ny = schrodingerPanel.getDiscreteModel().getGridHeight();
        int cellWidth = availableAreaForWaveform.width / nx;
        int cellHeight = availableAreaForWaveform.height / ny;
        int min = Math.min( cellWidth, cellHeight );
        return new Dimension( min, min );
    }

    private PNode getDetectorSheetControlPanelNode() {
        return detectorSheetPNode.getDetectorSheetControlPanelPNode();
    }

    public void removePotentialGraphic( RectangularPotentialGraphic rectangularPotentialGraphic ) {
        removeChild( rectangularPotentialGraphic );
        rectanglePotentialGraphics.remove( rectangularPotentialGraphic );
    }

    public DetectorSheetPNode getDetectorSheetPNode() {
        return detectorSheetPNode;
    }

    public void setUnits( ParticleUnits particleUnits ) {
        if( particleUnits == null ) {
            return;
        }
        String origTimeUnits = this.particleUnits.getDt().getUnits();
        this.particleUnits = particleUnits;
//        int numLatticePointsX = getWavefunctionGraphic().getWavefunction().getWidth();
//        double maxMeasurementValue = numLatticePointsX * particleUnits.getDx().getDisplayValue();
        updateRulerUnits();
        stopwatchPanel.setTimeUnits( particleUnits.getDt().getUnits() );

        String newTimeUnits = particleUnits.getDt().getUnits();
        String[]times = new String[]{"ns", "ps"};
        ArrayList list = new ArrayList( Arrays.asList( times ) );

        int change = list.indexOf( newTimeUnits ) - list.indexOf( origTimeUnits );
        if( change == 0 ) {
            //do nothing
        }
        else if( change > 0 ) {
            //speed up time
            showTimeSpeedUp();
        }
        else if( change < 0 ) {
            //slow down time
            showTimeSlowDown();
        }
    }

    private void updateRulerUnits() {
        String[]readings = new String[7];
        for( int i = 0; i < readings.length; i++ ) {
            double v = particleUnits.getDx().getDisplayScaleFactor() * i;
            DecimalFormat decimalFormat = new DecimalFormat( "0.0" );
            readings[i] = new String( "" + decimalFormat.format( v ) + "" );
        }
        rulerGraphic.getRulerGraphic().setReadings( readings );

        double waveAreaPixelWidth = wavefunctionGraphic.getWavefunctionGraphicWidth();
        double waveAreaViewWidth = wavefunctionGraphic.getWavefunction().getWidth() * particleUnits.getDx().getDisplayValue();

        double rulerViewWidth = readings.length - 1;//units
        double rulerPixelWidth = waveAreaPixelWidth / waveAreaViewWidth * rulerViewWidth;

        rulerGraphic.getRulerGraphic().setMeasurementWidth( rulerPixelWidth );
        rulerGraphic.setUnits( particleUnits.getDx().getUnits() );
    }

    private double lowDT = 0.3;
    private double highDT = 4.0;
    private double ddt = 0.1;
    int MAX = 50;

    private void showTimeSpeedUp() {
        final ClockGraphic child = new ClockGraphic();

        final Timer timer = new Timer( 30, null );
        ActionListener listener = new ActionListener() {
            double dt = lowDT;
            double t = 0;
            int numAfter = 0;

            public void actionPerformed( ActionEvent e ) {
                t += dt;
                if( !( dt < lowDT || dt > highDT ) ) {
                    dt += ddt;
                }

                child.setTime( t / 10.0 );
                if( dt < lowDT || dt > highDT ) {
                    numAfter++;
                    if( numAfter > MAX ) {
                        timer.stop();
                        removeChild( child );
                    }
                }
            }
        };
        timer.addActionListener( listener );
        addChild( child );

        PText shadowPText = new PText( speedupText );
        shadowPText.setTextPaint( Color.blue );

        BoundGraphic boundGraphic = new BoundGraphic( shadowPText, 4, 4 );

        boundGraphic.setPaint( TEXT_BACKGROUND );
        child.addChild( boundGraphic );
        child.addChild( shadowPText );
        child.setOffset( abstractGunGraphic.getFullBounds().getX(), abstractGunGraphic.getFullBounds().getY() );
        timer.start();
    }

    private void showTimeSlowDown() {
        final ClockGraphic child = new ClockGraphic();

        final Timer timer = new Timer( 30, null );
        ActionListener listener = new ActionListener() {
            double dt = highDT;
            double t = 0;
            int numAfter = 0;

            public void actionPerformed( ActionEvent e ) {
                t += dt;
                if( !( dt < lowDT || dt > highDT ) ) {
                    dt -= ddt;
                }

                child.setTime( t / 10.0 );
                if( dt < lowDT || dt > highDT ) {
                    numAfter++;

                    if( numAfter > MAX ) {
                        timer.stop();
                        removeChild( child );
                    }
                }
            }
        };
        timer.addActionListener( listener );
        addChild( child );
        PText shadowPText = new PText( slowdownText );
        shadowPText.setTextPaint( Color.blue );

        BoundGraphic boundGraphic = new BoundGraphic( shadowPText, 4, 4 );
        boundGraphic.setPaint( TEXT_BACKGROUND );
        child.addChild( boundGraphic );
        child.addChild( shadowPText );
        child.setOffset( abstractGunGraphic.getFullBounds().getX(), abstractGunGraphic.getFullBounds().getY() );
        timer.start();
    }

    public void setGunTypeChooserGraphic( PNode chooser ) {
        addChild( chooser );
        this.gunTypeChooserGraphic = chooser;
        invalidateLayout();
        repaint();
    }

    public void setStopwatchVisible( boolean selected ) {
        stopwatchPanelPSwing.setVisible( selected );
    }

    public boolean isRulerVisible() {
        return rulerGraphic.getVisible();
    }

    public void updateWaveGraphic() {
        wavefunctionGraphic.update();
    }

    public void setGunControlPanel( GunControlPanel gunControlPanel ) {
        addChild( gunControlPanel.getPSwing() );
        this.gunControlPanelPSwing = gunControlPanel.getPSwing();
        relayout();


        BufferedImage txtr = null;
        try {
//            txtr = ImageLoader.loadBufferedImage( "images/computertexture.gif" );
            txtr = ImageLoader.loadBufferedImage( "images/wire.png" );
        }
        catch( IOException e ) {
            e.printStackTrace();
        }
        ConnectorGraphic connectorGraphic = new HorizontalConnector( gunControlPanelPSwing, abstractGunGraphic );
        connectorGraphic.setTexture( txtr );

        addChild( 3, connectorGraphic );
    }
}
