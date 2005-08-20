/* Copyright 2004, Sam Reid */
package edu.colorado.phet.theramp.view;

import edu.colorado.phet.piccolo.ConnectorGraphic;
import edu.colorado.phet.piccolo.PhetPCanvas;
import edu.colorado.phet.piccolo.WiggleMe;
import edu.colorado.phet.piccolo.pswing.PSwing;
import edu.colorado.phet.theramp.RampModule;
import edu.colorado.phet.theramp.RampObject;
import edu.colorado.phet.theramp.RampPlotSet;
import edu.colorado.phet.theramp.view.bars.BarGraphSuite;
import edu.umd.cs.piccolo.PNode;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

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

//    private static final Dimension ORIG_RENDER_SIZE = new Dimension( 1061, 871 );
//    private static final Dimension ORIG_RENDER_SIZE = new Dimension( 1061, 871 );
//    private static final Dimension ORIG_RENDER_SIZE = new Dimension( 1032, 686 );
    private static final Dimension ORIG_RENDER_SIZE = new Dimension( 786, 562 );

    public static Dimension getDefaultRenderSize() {
        return new Dimension( ORIG_RENDER_SIZE );
    }

    public Dimension getDefaultRenderingSize() {
        return ORIG_RENDER_SIZE;
    }

    public RampPanel( RampModule module ) {
        super();
        setRenderingSize( getDefaultRenderingSize() );
        addMouseListener( new MouseListener() {
            public void mouseClicked( MouseEvent e ) {
            }

            public void mouseEntered( MouseEvent e ) {
            }

            public void mouseExited( MouseEvent e ) {
            }

            public void mousePressed( MouseEvent e ) {
                System.out.println( "getSize( ) = " + getSize() );
            }

            public void mouseReleased( MouseEvent e ) {
            }
        } );
        this.module = module;
        rampLookAndFeel = new RampLookAndFeel();
//        setBackground( new Color( 240, 200, 255 ) );

        rampWorld = new RampWorld( module, this );
//        double rampWorldScale = 1.0;
        double rampWorldScale = 0.7;
        rampWorld.scale( rampWorldScale );
        rampWorld.translate( 0, -50 );
        addChild( rampWorld );

        barGraphSuite = new BarGraphSuite( this, module.getRampPhysicalModel() );
//        barGraphSuite.scale( 0.80 );
        barGraphSuite.setOffset( getDefaultRenderingSize().width - barGraphSuite.getFullBounds().getWidth() - 20, barGraphSuite.getY() + 20 );
        addChild( new OverheatButton( this, module.getRampPhysicalModel(), barGraphSuite.getMaxDisplayableEnergy(), module ) );

        addChild( barGraphSuite );

        timeGraphic = new TimeGraphic( module.getTimeSeriesModel() );
        timeGraphic.setOffset( 60, 60 );
        addChild( timeGraphic );
        module.getModel().addModelElement( timeGraphic );

        velocityGraphic = new SpeedReadoutGraphic( module.getRampPhysicalModel() );
        velocityGraphic.setOffset( timeGraphic.getX(), timeGraphic.getFullBounds().getMaxY() + 5 );
        addChild( velocityGraphic );
        module.getModel().addModelElement( velocityGraphic );

        requestFocus();
        addMouseListener( new MouseAdapter() {
            public void mousePressed( MouseEvent e ) {
                requestFocus();
            }
        } );

        addMouseListener( new UserAddingEnergyHandler( module ) );

//        setPanEventHandler();

        addInputEventListener( getZoomEventHandler() );

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
        getCamera().setViewScale( 1.0 );
        getCamera().setViewOffset( 23.0, -21.0 );


        getLayer().setRenderingHint( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON );

//        getLayer().addInputEventListener( new PBasicInputEventHandler() {
//            public void mouseMoved( PInputEvent event ) {
//                super.mouseMoved( event );
////                System.out.println( "event = " + event );
//                Point2D position = event.getPosition();
////                System.out.println( "position = " + position );
//            }
//        } );

        rampPlotSet = new RampPlotSet( module, this );
        addChild( rampPlotSet );

        PNode appliedForceControl = new AppliedForceControl( module, this );
        appliedForceControl.setOffset( rampPlotSet.getFullBounds().getX(), rampPlotSet.getFullBounds().getY() - appliedForceControl.getFullBounds().getHeight() );
        addChild( appliedForceControl );

        PNode goPauseClear = new PSwing( this, new GoPauseClearPanel( module.getTimeSeriesModel() ) );
        goPauseClear.setOffset( appliedForceControl.getFullBounds().getMaxX(), appliedForceControl.getFullBounds().getY() );
        addChild( goPauseClear );

        addWiggleMe();
    }

    private void addWiggleMe() {
        final WiggleMe wiggleMe = new WiggleMe( "<html>Apply a Force<br>to the Filing Cabinet</html>", (int)( ORIG_RENDER_SIZE.getWidth() / 2 - 50 ), 350 );
        final ConnectorGraphic connectorGraphic = new ConnectorGraphic( wiggleMe, getBlockGraphic().getObjectGraphic() );
        getLayer().getRoot().addActivity( connectorGraphic.getConnectActivity() );
        connectorGraphic.setStroke( new BasicStroke( 2, BasicStroke.CAP_SQUARE, BasicStroke.JOIN_ROUND, 2 ) );//, new float[]{10, 5}, 0 ) );
        connectorGraphic.setPaint( new GradientPaint( 0, 0, Color.red, 1000, 0, Color.blue, false ) );
        connectorGraphic.setPickable( false );
        connectorGraphic.setChildrenPickable( false );

        wiggleMe.setPickable( false );
        wiggleMe.setChildrenPickable( false );
        getLayer().addChild( connectorGraphic );
        getLayer().addChild( wiggleMe );
        wiggleMe.ensureActivityCorrect();
        addMouseListener( new MouseAdapter() {
            public void mousePressed( MouseEvent e ) {
                wiggleMe.setVisible( false );
                getLayer().removeChild( wiggleMe );
                getLayer().getRoot().getActivityScheduler().removeActivity( connectorGraphic.getConnectActivity() );
                getLayer().removeChild( connectorGraphic );
                removeMouseListener( this );
            }
        } );
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
        barGraphSuite.setEnergyBarsVisible( selected );
    }

    public void setWorkBarsVisible( boolean selected ) {
        barGraphSuite.setWorkBarsVisible( selected );
    }

    public void setAllBarsMinimized( boolean visible ) {
        setEnergyBarsVisible( visible );
        setWorkBarsVisible( visible );
    }

    public void reset() {
        rampPlotSet.reset();
    }

    public void repaintBackground() {
        rampPlotSet.repaintBackground();
    }

    public RampPlotSet getRampPlotSet() {
        return rampPlotSet;
    }
}
