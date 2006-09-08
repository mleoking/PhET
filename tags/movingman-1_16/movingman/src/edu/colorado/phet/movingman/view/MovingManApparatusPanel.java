/* Copyright 2004, Sam Reid */
package edu.colorado.phet.movingman.view;

import edu.colorado.phet.common.math.ImmutableVector2D;
import edu.colorado.phet.common.model.CompositeModelElement;
import edu.colorado.phet.common.model.clock.ClockTickEvent;
import edu.colorado.phet.common.model.clock.ClockTickListener;
import edu.colorado.phet.common.view.ApparatusPanel2;
import edu.colorado.phet.common.view.BasicGraphicsSetup;
import edu.colorado.phet.common.view.GraphicsSetup;
import edu.colorado.phet.common.view.phetgraphics.PhetGraphic;
import edu.colorado.phet.common.view.phetgraphics.PhetImageGraphic;
import edu.colorado.phet.common.view.phetgraphics.RepaintDebugGraphic;
import edu.colorado.phet.common.view.util.SimStrings;
import edu.colorado.phet.movingman.MMKeySuite;
import edu.colorado.phet.movingman.MovingManModule;
import edu.colorado.phet.movingman.common.LinearTransform1d;
import edu.colorado.phet.movingman.common.WiggleMe;
import edu.colorado.phet.movingman.model.TimeListenerAdapter;
import edu.colorado.phet.movingman.plots.MMPlotSuite;
import edu.colorado.phet.movingman.plots.PlotSet;

import javax.swing.event.MouseInputAdapter;
import java.awt.*;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Arrays;

/**
 * User: Sam Reid
 * Date: Mar 23, 2005
 * Time: 1:39:13 AM
 * Copyright (c) Mar 23, 2005 by Sam Reid
 */
public class MovingManApparatusPanel extends ApparatusPanel2 {
    private MovingManModule module;
    private LinearTransform1d manPositionTransform;
    private MMKeySuite keySuite;
    private boolean inited;
    private MovingManLayout movingManLayout;
    private ManGraphic.Listener wiggleMeListener;

    private PlotSet plotSet;
    private ManGraphic manGraphic;
    private TimeGraphic timerGraphic;
    private WalkWayGraphic walkwayGraphic;
    private Color backgroundColor;
    private WiggleMe wiggleMe;
    private PhetImageGraphic bufferedWalkwayGraphic;
    private PlotBorderGraphic xBorder;
    private PlotBorderGraphic vBorder;
    private PlotBorderGraphic aBorder;
    private ArrowSetGraphic arrowSetGraphic;

    public MovingManApparatusPanel( MovingManModule module ) throws IOException {
        super( module.getClock() );
        removePanelResizeHandler();
        module.getClock().addClockTickListener( new ClockTickListener() {
            public void clockTicked( ClockTickEvent event ) {
                handleUserInput();
                updateGraphics( event );
                paint();
            }
        } );
        this.module = module;
        keySuite = new MMKeySuite( module );

        addKeyListener( keySuite );
        addGraphicsSetup( new BasicGraphicsSetup() );
        manPositionTransform = new LinearTransform1d( -module.getMaxManPosition(), module.getMaxManPosition(), 50, 600 );
        backgroundColor = new Color( 250, 190, 240 );
        manGraphic = new ManGraphic( module, this, module.getMan(), 0, manPositionTransform );

        this.addGraphic( manGraphic, 11 );
        timerGraphic = new TimeGraphic( module, this, module.getTimeModel().getRecordTimer(), module.getTimeModel().getPlaybackTimer(), 80, 40 );
        this.addGraphic( timerGraphic, 11 );

        walkwayGraphic = new WalkWayGraphic( module, this, 11 );
        bufferedWalkwayGraphic = createBuffer( walkwayGraphic, new BasicGraphicsSetup(),
                                               BufferedImage.TYPE_INT_RGB, walkwayGraphic.getBackgroundColor() );

        addGraphic( bufferedWalkwayGraphic, 10 );
//        addGraphic( walkwayGraphic, 0 );

        Point2D start = manGraphic.getRectangle().getLocation();
        start = new Point2D.Double( start.getX() + 50, start.getY() + 50 );
        wiggleMe = new WiggleMe( this, start,
                                 new ImmutableVector2D.Double( 0, 1 ), 15, .02, SimStrings.get( "MovingManModule.DragTheManText" ) );
        wiggleMe.setVisible( false );//TODO don't delete this line.
        module.addListener( new TimeListenerAdapter() {
            public void recordingStarted() {
                setWiggleMeVisible( false );
            }
        } );
        this.wiggleMeListener = new ManGraphic.Listener() {
            public void manGraphicChanged() {
                Point2D start = manGraphic.getRectangle().getLocation();
                start = new Point2D.Double( start.getX() - wiggleMe.getWidth() - 20, start.getY() + manGraphic.getRectangle().getHeight() / 2 );
                wiggleMe.setCenter( new Point( (int)start.getX(), (int)start.getY() ) );
            }

            public void mouseReleased() {
            }
        };
        setWiggleMeVisible( true );

        manGraphic.addListener( this.wiggleMeListener );
        this.addMouseListener( new MouseAdapter() {
            public void mousePressed( MouseEvent e ) {
                MovingManApparatusPanel.this.requestFocus();
            }
        } );
        plotSet = new PlotSet( module, this );

        xBorder = new PlotBorderGraphic( this, plotSet.getPositionPlotSuite() );
        vBorder = new PlotBorderGraphic( this, plotSet.getVelocityPlotSuite() );
        aBorder = new PlotBorderGraphic( this, plotSet.getAccelerationPlotSuite() );

        plotSet.addListener( new PlotSet.Listener() {
            public void setAccelerationControlMode() {
                highlightPlots( false, false, true );
            }

            public void setVelocityControlMode() {
                highlightPlots( false, true, false );
            }

            public void setPositionControlMode() {
                highlightPlots( true, false, false );
            }
        } );

        manGraphic.addMouseInputListener( new MouseInputAdapter() {
            // implements java.awt.event.MouseListener
            public void mousePressed( MouseEvent e ) {
                highlightPlots( false, false, false );
            }

            // implements java.awt.event.MouseMotionListener
            public void mouseDragged( MouseEvent e ) {
                highlightPlots( false, false, false );
            }
        } );

        addGraphic( plotSet.getPositionPlotSuite(), 3 );//todo fix buffering.
        addGraphic( plotSet.getVelocityPlotSuite(), 4 );
        addGraphic( plotSet.getAccelerationPlotSuite(), 5 );

        movingManLayout = new MovingManLayout( this );

        RepaintDebugGraphic.enable( this, module.getClock() );
        setDoubleBuffered( true );

        SliderWiggleMe sliderWiggleMe = new SliderWiggleMe( this, module, module.getClock() );
        addGraphic( sliderWiggleMe, Double.POSITIVE_INFINITY );

        arrowSetGraphic = new ArrowSetGraphic( module, this, getManGraphic() );
        addGraphic( arrowSetGraphic, Double.POSITIVE_INFINITY );
    }

    private void updateGraphics( ClockTickEvent event ) {
        arrowSetGraphic.updateGraphics();
    }

    private void highlightPlots( boolean x, boolean v, boolean a ) {
        getPlotSet().getPositionPlot().setSelected( x );
        getPlotSet().getVelocityPlot().setSelected( v );
        getPlotSet().getAccelerationPlot().setSelected( a );
    }

    public static PhetImageGraphic createBuffer( PhetGraphic phetGraphic, GraphicsSetup graphicsSetup,
                                                 int imageType, Paint background ) {

        PhetImageGraphic phetImageGraphic = new PhetImageGraphic( phetGraphic.getComponent() );
        Rectangle bounds = phetGraphic.getBounds();
        BufferedImage im = new BufferedImage( bounds.width, bounds.height, imageType );
        Graphics2D g2 = im.createGraphics();
        graphicsSetup.setup( g2 );
        g2.setPaint( background );

        g2.translate( -bounds.x, -bounds.y );
        g2.fillRect( bounds.x, bounds.y, bounds.width, bounds.height );

        phetGraphic.paint( g2 );

        g2.setStroke( new BasicStroke( 4 ) );
        g2.setColor( Color.black );
        g2.draw( bounds );

        phetImageGraphic.setImage( im );
        return phetImageGraphic;
    }

    public void paint( Graphics g ) {
        if( inited ) {
            super.paint( g );
        }
    }

    public void paintImmediately( int x, int y, int w, int h ) {
        if( inited ) {
            super.paintImmediately( x, y, w, h );
        }
    }

    public Component add( Component comp ) {
        KeyListener[] kl = comp.getKeyListeners();
        if( !Arrays.asList( kl ).contains( getKeySuite() ) ) {
            comp.addKeyListener( getKeySuite() );
        }
        return super.add( comp );
    }

    private KeyListener getKeySuite() {
        return keySuite;
    }

    public void setInited( boolean b ) {
        this.inited = b;
    }

    public void relayout() {
        movingManLayout.relayout();
    }

    public void setManTransform( LinearTransform1d transform ) {
        this.manPositionTransform = transform;
        manGraphic.setTransform( transform );
        walkwayGraphic.setTransform( transform );
        repaintBackground();
    }

    public WalkWayGraphic getWalkwayGraphic() {
        return walkwayGraphic;
    }

    public void setWiggleMeVisible( boolean b ) {
        if( b == wiggleMe.isVisible() ) {
            return;
        }
        if( !b ) {
            wiggleMe.setVisible( false );
            this.removeGraphic( wiggleMe );
            getModel().removeModelElement( wiggleMe );
            manGraphic.removeListener( wiggleMeListener );
        }
        else {
            wiggleMe.setVisible( true );
            this.addGraphic( wiggleMe, 100 );
            getModel().addModelElement( wiggleMe );
            manGraphic.addListener( wiggleMeListener );
        }
    }

    private CompositeModelElement getModel() {
        return module.getModel();
    }

    public void repaintBackground() {
        PhetImageGraphic pig = createBuffer( walkwayGraphic, new BasicGraphicsSetup(), BufferedImage.TYPE_INT_RGB, walkwayGraphic.getBackgroundColor() );

        bufferedWalkwayGraphic.setImage( pig.getImage() );
        bufferedWalkwayGraphic.setRegistrationPoint( -walkwayGraphic.getBounds().x, -walkwayGraphic.getBounds().y );
        if( movingManLayout != null ) {
            bufferedWalkwayGraphic.setLocation( 0, 0 );
        }
        repaint();
    }

    public Color getBackgroundColor() {
        return backgroundColor;
    }

    public ManGraphic getManGraphic() {
        return manGraphic;
    }

    public PlotSet getPlotSet() {
        return plotSet;
    }

    public LinearTransform1d getManPositionTransform() {
        return manPositionTransform;
    }

    public MovingManModule getModule() {
        return module;
    }

    public void reset() {
        setCursorsVisible( false );
        getPlotSet().reset();
        repaint();
    }

    public void setCursorsVisible( boolean visible ) {
        getPlotSet().setCursorsVisible( visible );
    }

    public void initialize() {
//        setCursorsVisible( true );
        repaintBackground();
    }

    public void setShowVelocityVector( boolean showVelocityVector ) {
        arrowSetGraphic.setShowVelocityVector( showVelocityVector );
    }

    public void setShowAccelerationVector( boolean showAccelerationVector ) {
        arrowSetGraphic.setShowAccelerationVector( showAccelerationVector );
    }

    public void setBoundaryConditionsClosed() {
        walkwayGraphic.setBoundaryConditionsClosed();
        repaintBackground();
        movingManLayout.relayout();
    }

    public void setBoundaryConditionsOpen() {
        walkwayGraphic.setBoundaryConditionsOpen();
        repaintBackground();
        movingManLayout.relayout();
    }

    public void requestEditInTextBox( GoPauseClearPanel goPauseClearPanel ) {
        MMPlotSuite plotSuite = plotSet.getPlotSuiteFor( goPauseClearPanel );

        MMPlotSuite[] others = plotSet.getOtherPlots( plotSuite );
        for( int i = 0; i < others.length; i++ ) {
            MMPlotSuite other = others[i];
            other.getTextBox().deselectAll();
        }
        plotSuite.getTextBox().selectAll();
        plotSuite.getTextBoxGraphic().repaint();
        plotSuite.getTextBox().getTextField().requestFocus();
        plotSuite.getTextBoxGraphic().repaint();
    }
}
