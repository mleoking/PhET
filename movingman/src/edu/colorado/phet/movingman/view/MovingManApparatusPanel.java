/* Copyright 2004, Sam Reid */
package edu.colorado.phet.movingman.view;

import edu.colorado.phet.common.math.ImmutableVector2D;
import edu.colorado.phet.common.model.CompositeModelElement;
import edu.colorado.phet.common.view.ApparatusPanel;
import edu.colorado.phet.common.view.BasicGraphicsSetup;
import edu.colorado.phet.common.view.phetgraphics.BufferedPhetGraphic2;
import edu.colorado.phet.common.view.phetgraphics.PhetImageGraphic;
import edu.colorado.phet.common.view.phetgraphics.RepaintDebugGraphic;
import edu.colorado.phet.common.view.util.SimStrings;
import edu.colorado.phet.movingman.MMKeySuite;
import edu.colorado.phet.movingman.MovingManModule;
import edu.colorado.phet.movingman.common.LinearTransform1d;
import edu.colorado.phet.movingman.common.WiggleMe;
import edu.colorado.phet.movingman.model.TimeListenerAdapter;
import edu.colorado.phet.movingman.plots.PlotSet;

import javax.swing.*;
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
public class MovingManApparatusPanel extends ApparatusPanel {
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

    public MovingManApparatusPanel( MovingManModule module ) throws IOException {
        this.module = module;
        keySuite = new MMKeySuite( module );

        addKeyListener( keySuite );
        addGraphicsSetup( new BasicGraphicsSetup() );
        setBorder( BorderFactory.createLineBorder( Color.black, 1 ) );
        manPositionTransform = new LinearTransform1d( -module.getMaxManPosition(), module.getMaxManPosition(), 50, 600 );
        backgroundColor = new Color( 250, 190, 240 );
        manGraphic = new ManGraphic( module, this, module.getMan(), 0, manPositionTransform );

        this.addGraphic( manGraphic, 1 );
        timerGraphic = new TimeGraphic( module, this, module.getTimeModel().getRecordTimer(), module.getTimeModel().getPlaybackTimer(), 80, 40 );
        this.addGraphic( timerGraphic, 1 );

        walkwayGraphic = new WalkWayGraphic( module, this, 11 );
        bufferedWalkwayGraphic = BufferedPhetGraphic2.createBuffer( walkwayGraphic, new BasicGraphicsSetup(), BufferedImage.TYPE_INT_RGB, getBackgroundColor() );

        addGraphic( bufferedWalkwayGraphic, 0 );
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
        movingManLayout = new MovingManLayout( this );

        RepaintDebugGraphic.enable( this, module.getClock() );
        setDoubleBuffered( true );

        SliderWiggleMe sliderWiggleMe = new SliderWiggleMe( this, module, module.getClock() );
        addGraphic( sliderWiggleMe, Double.POSITIVE_INFINITY );
//        sliderWiggleMe.setVisible( true );
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
        PhetImageGraphic pig = BufferedPhetGraphic2.createBuffer( walkwayGraphic, new BasicGraphicsSetup(), BufferedImage.TYPE_INT_RGB, getBackground() );
        Rectangle r = walkwayGraphic.getBounds();
        System.out.println( "r = " + r );
        System.out.println( "walkwayGraphic.getLocation() = " + walkwayGraphic.getLocation() );

        bufferedWalkwayGraphic.setImage( pig.getImage() );
        bufferedWalkwayGraphic.setRegistrationPoint( -walkwayGraphic.getBounds().x, -walkwayGraphic.getBounds().y );
        if( movingManLayout != null ) {
            bufferedWalkwayGraphic.setLocation( movingManLayout.getWalkWayInsetX(), 0 );
        }
        System.out.println( "bufferedWalkwayGraphic.getLocation() = " + bufferedWalkwayGraphic.getLocation() );
        System.out.println( "walkwayGraphic.getLocation() = " + walkwayGraphic.getLocation() );
        repaint();
    }

    public Color getBackgroundColor() {
        return backgroundColor;
    }

    public void setHelpEnabled( boolean h ) {
        setWiggleMeVisible( h );
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
        setCursorsVisible( true );
        repaintBackground();
    }
}
