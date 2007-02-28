/* Copyright 2004, Sam Reid */
package edu.colorado.phet.theramp.view;

import edu.colorado.phet.piccolo.PDebugKeyHandler;
import edu.colorado.phet.piccolo.PhetPCanvas;
import edu.colorado.phet.piccolo.TargetedWiggleMe;
import edu.colorado.phet.piccolo.pswing.PSwing;
import edu.colorado.phet.theramp.RampModule;
import edu.colorado.phet.theramp.RampPlotSet;
import edu.colorado.phet.theramp.TheRampStrings;
import edu.colorado.phet.theramp.common.LucidaSansFont;
import edu.colorado.phet.theramp.model.RampObject;
import edu.colorado.phet.theramp.model.RampPhysicalModel;
import edu.colorado.phet.theramp.view.bars.BarGraphSuite;
import edu.colorado.phet.theramp.view.plot.TimeSeriesPNode;
import edu.colorado.phet.timeseries.TimeSeriesModel;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.util.PBounds;
import edu.umd.cs.piccolo.util.PPaintContext;

import java.awt.*;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Rectangle2D;

/**
 * User: Sam Reid
 * Date: Feb 11, 2005
 * Time: 10:01:59 AM
 * Copyright (c) Feb 11, 2005 by Sam Reid
 */

public class RampPanel extends PhetPCanvas {
    private RampModule module;
    private RampLookAndFeel rampLookAndFeel;
    private BarGraphSuite barGraphSuite;
    private TimeGraphic timeGraphic;
    private SpeedReadoutGraphic velocityGraphic;
    private RampWorld rampWorld;
    private RampPlotSet rampPlotSet;

    private static final Dimension ORIG_RENDER_SIZE = new Dimension( 1042, 818 );
    public final PNode appliedForceControl;
    public final PNode goPauseClear;
    private boolean recursing = false;
    public static boolean redRampEnabled = false;

    public static Dimension getDefaultRenderSize() {
        return new Dimension( ORIG_RENDER_SIZE );
    }

    public Dimension getDefaultRenderingSize() {
        return ORIG_RENDER_SIZE;
    }

    public RampPanel( RampModule module ) {
//        setRenderingSize( getDefaultRenderingSize() );//DEC_05
        super( getDefaultRenderSize() );
        addMouseListener( new MouseAdapter() {
            public void mousePressed( MouseEvent e ) {
                requestFocus();
                System.out.println( "getSize( ) = " + getSize() );
            }
        } );
        addKeyListener( new PDebugKeyHandler() );
        this.module = module;
        rampLookAndFeel = new RampLookAndFeel();
        rampWorld = new RampWorld( module, this );

        double rampWorldScale = 0.96;
        rampWorld.scale( rampWorldScale );
        rampWorld.translate( 0, -100 );
        addWorldChild( rampWorld );

        barGraphSuite = new BarGraphSuite( this, module.getRampPhysicalModel() );
        barGraphSuite.setOffset( getDefaultRenderingSize().width - barGraphSuite.getFullBounds().getWidth() - 20, barGraphSuite.getY() + 20 );
        addScreenChild( barGraphSuite );

        getRampModule().getRampPhysicalModel().addListener( new RampPhysicalModel.Adapter() {

            public void stepFinished() {
//                System.out.println( "<********RampPanel.stepFinished" );
//                System.out.println( "getRampModule().getRampPhysicalModel().getThermalEnergy() = " + getRampModule().getRampPhysicalModel().getThermalEnergy() );
//                System.out.println( "getOverheatEnergy() = " + getOverheatEnergy() );
                if( getRampModule().getRampPhysicalModel().getThermalEnergy() >= getOverheatEnergy() ) {
                    //colorize heat.
                    rampWorld.setHeatColor( true );
                }
                else {
                    rampWorld.setHeatColor( false );
                }
            }
        } );
        getRampModule().getTimeSeriesModel().addPlaybackTimeChangeListener( new TimeSeriesModel.PlaybackTimeListener() {
            public void timeChanged() {
                if( getRampModule().getRampPhysicalModel().getThermalEnergy() >= getOverheatEnergy() ) {
                    //colorize heat.
                    rampWorld.setHeatColor( true );
                }
                else {
                    rampWorld.setHeatColor( false );
                }
            }
        } );

//        PhetRootPNode.Layer layer = getPhetRootNode().addLayer();
        addScreenChild( new OverheatButton( this, module.getRampPhysicalModel(), module ) );

        timeGraphic = new TimeGraphic( module.getTimeSeriesModel() );
        timeGraphic.setOffset( 60, 60 );
        addWorldChild( timeGraphic );
        module.getModel().addModelElement( timeGraphic );

        velocityGraphic = new SpeedReadoutGraphic( module.getRampPhysicalModel() );
        velocityGraphic.setOffset( timeGraphic.getFullBounds().getX(), timeGraphic.getFullBounds().getMaxY() + 5 );
        addWorldChild( velocityGraphic );
        module.getModel().addModelElement( velocityGraphic );

        requestFocus();
        addMouseListener( new MouseAdapter() {
            public void mousePressed( MouseEvent e ) {
                requestFocus();
            }
        } );

        addMouseListener( new UserAddingEnergyHandler( module ) );

//        addInputEventListener( getZoomEventHandler() );

//        addInputEventListener( getPanEventHandler() );
//        module.getModel().addModelElement( new ModelElement() {
//            public void stepInTime( double dt ) {
//                PCamera cam = getCamera();
//                System.out.println( "cam.getViewTransform() = " + cam.getViewTransform() );
//                System.out.println( "cam.getViewScale() = " + cam.getViewScale() );
//                Point2D viewOffset = new Point2D.Double( cam.getViewTransform().getTranslateX(), cam.getViewTransform().getTranslateY() );
//                System.out.println( "viewOffset = " + viewOffset );
//            }
//        } );

        //find parameters you like:
//cam.getViewScale() = 0.969001148105626
//viewOffset = Point2D.Double[23.0, -21.0]
        //then set them here.
//        getCamera().setViewScale( 0.969001148105626 );
//        getCamera().setViewScale( 1.0 );
//        getCamera().setViewOffset( 23.0, -21.0 );

//        getLayer().setRenderingHint( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON );//DEC_05

        rampPlotSet = new RampPlotSet( module, this );
        addScreenChild( rampPlotSet );
        appliedForceControl = new AppliedForceSimpleControl( module, this );
        addScreenChild( appliedForceControl );

        goPauseClear = new PSwing( this, new GoPauseClearPanel( module.getTimeSeriesModel() ) );
        addScreenChild( goPauseClear );

        layoutAll();
        addComponentListener( new ComponentListener() {
            public void componentHidden( ComponentEvent e ) {
            }

            public void componentMoved( ComponentEvent e ) {
            }

            public void componentResized( ComponentEvent e ) {
                layoutAll();
            }

            public void componentShown( ComponentEvent e ) {
                layoutAll();
            }

        } );
        setInteractingRenderQuality( PPaintContext.HIGH_QUALITY_RENDERING );
        setDefaultRenderQuality( PPaintContext.HIGH_QUALITY_RENDERING );
    }

    private void layoutAll() {
        layoutChildren();
        rampPlotSet.layoutChildren();
        layoutChildren();
    }

    public double getOverheatEnergy() {
        return barGraphSuite.getMaxDisplayableEnergy() * 0.82;
    }

    private void layoutChildren() {
        if( !recursing ) {
            recursing = true;
//            System.out.println( System.currentTimeMillis() + ": RampPanel.layoutChildren" );
            double yOrig = rampPlotSet.getFullBounds().getY() - goPauseClear.getFullBounds().getHeight() - 2;
//            System.out.println( "yOrig = " + yOrig );
            double gopY = getChartTopY() - goPauseClear.getFullBounds().getHeight() - 2;
            double sliderY = getChartTopY() - appliedForceControl.getFullBounds().getHeight() - 2;
//            System.out.println( "gopY = " + gopY );
            if( gopY <= 0 ) {
                gopY = yOrig;
//                System.out.println( "Reverting to orig bar gopY" );
            }
            else {
//                System.out.println( "Using new value" );
            }
//            gopY=yOrig;
            int insetX = 2;
            appliedForceControl.setOffset( insetX, sliderY );
            goPauseClear.setOffset( insetX, sliderY + appliedForceControl.getFullBounds().getHeight() );

            double max = Math.max( appliedForceControl.getFullBounds().getMaxX(), goPauseClear.getFullBounds().getMaxX() );
            rampPlotSet.setPlotOffsetX( (int)( max + 5 ) );

            barGraphSuite.setOffset( getWidth() - barGraphSuite.getFullBounds().getWidth() - 5, gopY - 5 );
            double maxY = ( getHeight() - barGraphSuite.getOffset().getY() ) * 0.8;
            try {
                barGraphSuite.setBarChartHeight( maxY );
            }
            catch( RuntimeException r ) {
                r.printStackTrace();//todo sometimes fails drawing arrow.
            }
            rampPlotSet.layoutChildren();
            recursing = false;
        }
    }

    private double getChartTopY() {
        return rampPlotSet.getTopY();
    }

    protected void addWiggleMe() {
        PNode target = getBlockGraphic().getObjectGraphic();
        PBounds screenBounds = target.getGlobalFullBounds();
        getCamera().globalToLocal( screenBounds );

        final TargetedWiggleMe wiggleMe = new TargetedWiggleMe( TheRampStrings.getString( "html.apply.a.force.br.to.the.filing.cabinet.html1" ),
                                                                200, 100,
                                                                getBlockGraphic().getObjectGraphic() );
        addScreenChild( wiggleMe );

        MouseAdapter wiggleMeDisappears = new MouseAdapter() {
            public void mousePressed( MouseEvent e ) {
                removeScreenChild( wiggleMe );
                removeMouseListener( this );
            }
        };
        addMouseListener( wiggleMeDisappears );
    }

    private void updateArrowSetGraphics() {
        rampWorld.updateArrowSetGraphics();
    }

    public RampModule getRampModule() {
        return module;
    }

    public RampLookAndFeel getLookAndFeel() {
        return rampLookAndFeel;
    }

    public BlockGraphic getBlockGraphic() {
        return rampWorld.getBlockGraphic();
    }

    public void setCartesianArrowsVisible( boolean selected ) {
        rampWorld.setCartesianArrowsVisible( selected );
    }

    public void setParallelArrowsVisible( boolean selected ) {
        rampWorld.setParallelArrowsVisible( selected );
    }

    public void setPerpendicularArrowsVisible( boolean selected ) {
        rampWorld.setPerpendicularArrowsVisible( selected );
    }

    public void setXArrowsVisible( boolean selected ) {
        rampWorld.setXArrowsVisible( selected );
    }

    public void setYArrowsVisible( boolean selected ) {
        rampWorld.setYArrowsVisible( selected );
    }

    public boolean isCartesianVisible() {
        return rampWorld.isCartesianVisible();
    }

    public boolean isParallelVisible() {
        return rampWorld.isParallelVisible();
    }

    public boolean isPerpendicularVisible() {
        return rampWorld.isPerpendicularVisible();
    }

    public boolean isXVisible() {
        return rampWorld.isXVisible();
    }

    public boolean isYVisible() {
        return rampWorld.isYVisible();
    }

    public void setForceVisible( String force, boolean selected ) {
        rampWorld.setForceVisible( force, selected );
    }

    public SurfaceGraphic getRampGraphic() {
        return rampWorld.getRampGraphic();
    }

    public double getBlockWidthModel() {
        return rampWorld.getBlockWidthModel();
    }

    public double getModelWidth( int viewWidth ) {
        return rampWorld.getModelWidth( viewWidth );
    }

    public void setObject( RampObject rampObject ) {
        getBlockGraphic().setObject( rampObject );
    }

    public int getRampBaseY() {
        Point v = getRampGraphic().getViewLocation( getRampGraphic().getSurface().getLocation( 0 ) );
        return v.y;
    }

    public RampWorld getRampWorld() {
        return rampWorld;
    }

    public void setMeasureTapeVisible( boolean visible ) {
        rampWorld.setMeasureTapeVisible( visible );
    }

    public void updateGraphics() {
        updateArrowSetGraphics();
    }

    public void setEnergyBarsVisible( boolean selected ) {
        barGraphSuite.setEnergyBarsMaximized( selected );
    }

    public void setWorkBarsVisible( boolean selected ) {
        barGraphSuite.setWorkBarsMaximized( selected );
    }

    public int numMaximizedBarGraphs() {
        return ( barGraphSuite.getEnergyBarsMaximized() ? 1 : 0 ) + ( barGraphSuite.getWorkBarsMaximized() ? 1 : 0 );
    }

    public void setAllBarsMinimized( boolean visible ) {
        setEnergyBarsVisible( !visible );
        setWorkBarsVisible( !visible );
    }

    public void reset() {
        rampPlotSet.reset();
        resetBarStates();
        resetPlotStates();
    }

    protected void resetPlotStates() {
        rampPlotSet.setPlotsMaximized( false, true, true );
    }

    public void resetBarStates() {
        setAllBarsMinimized( false );
    }

    public void repaintBackground() {
        rampPlotSet.repaintBackground();
    }

    public RampPlotSet getRampPlotSet() {
        return rampPlotSet;
    }

    public void maximizeForcePlot() {
        rampPlotSet.maximizeForcePlot();
    }

    public double getChartLayoutMaxX() {
        int insetX = 3;
        if( barGraphSuite.areBothMinimized() && false ) {
            return getSize().width - insetX;
        }
        else {
            return barGraphSuite.getFullBounds().getX() - insetX;
        }
    }

    public double getChartLayoutMaxXORIG() {
        int insetX = 3;
        if( barGraphSuite.areBothMinimized() ) {
            return getSize().width - insetX;
        }
        else {
            return barGraphSuite.getFullBounds().getX() - insetX;
        }
    }

    public void relayoutPiccolo() {
        layoutChildren();
    }

    public Rectangle2D getClearButtonCanvasRect() {
        PBounds rect = goPauseClear.getGlobalFullBounds();
        getCamera().globalToLocal( rect );
        return rect;
    }

    public void graphLayoutChanged() {
        layoutAll();
        if( Toolkit.getDefaultToolkit().getScreenSize().width <= 1024 && RampModule.MINIMIZE_READOUT_TEXT_FOR_SMALL_SCREEN )
        {
            if( allThreeGraphsUp() ) {
                rampPlotSet.setTimeSeriesPlotFont( new LucidaSansFont( 9, true, false ) );
                rampPlotSet.setTimeSeriesPlotShadow( 0, 0 );
            }
            else {
                rampPlotSet.setTimeSeriesPlotFont( TimeSeriesPNode.createDefaultFont() );
                rampPlotSet.setTimeSeriesPlotShadow( 1, 1 );
            }
        }
    }

    private boolean allThreeGraphsUp() {
        return rampPlotSet.allThreeGraphsUp();
    }


    public BarGraphSuite getBarGraphSuite() {
        return barGraphSuite;
    }

    public void setWorldScale( double scale ) {
        if( scale > 0 ) {
            super.setWorldScale( scale );
        }
    }

    public void updateReadouts() {
        rampPlotSet.updateReadouts();
    }
}
