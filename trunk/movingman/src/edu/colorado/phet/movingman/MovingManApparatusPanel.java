/* Copyright 2004, Sam Reid */
package edu.colorado.phet.movingman;

import edu.colorado.phet.common.math.ImmutableVector2D;
import edu.colorado.phet.common.model.CompositeModelElement;
import edu.colorado.phet.common.view.ApparatusPanel;
import edu.colorado.phet.common.view.util.SimStrings;
import edu.colorado.phet.movingman.common.BufferedGraphicForComponent;
import edu.colorado.phet.movingman.common.LinearTransform1d;
import edu.colorado.phet.movingman.common.WiggleMe;

import java.awt.*;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.io.IOException;
import java.util.Arrays;

/**
 * User: Sam Reid
 * Date: Mar 23, 2005
 * Time: 1:39:13 AM
 * Copyright (c) Mar 23, 2005 by Sam Reid
 */
public class MovingManApparatusPanel extends ApparatusPanel {
    private boolean inited;
    private MovingManModule module;
    private MovingManLayout layout;
    private ManGraphic manGraphic;
    private TimeGraphic timerGraphic;
    private BufferedGraphicForComponent backgroundGraphic;
    private WalkWayGraphic walkwayGraphic;
    private Color purple = new Color( 200, 175, 250 );
    private Color backgroundColor;

    private WiggleMe wiggleMe;
    private ManGraphic.Listener wiggleMeListener;

    public MovingManApparatusPanel( MovingManModule module ) throws IOException {
        this.module = module;
        backgroundColor = new Color( 250, 190, 240 );
        backgroundGraphic = new BufferedGraphicForComponent( 0, 0, 800, 400, backgroundColor, this );


        manGraphic = new ManGraphic( module, this, module.getMan(), 0, module.getManPositionTransform() );

        this.addGraphic( manGraphic, 1 );
        timerGraphic = new TimeGraphic( module, this, module.getTimeModel().getRecordTimer(), module.getTimeModel().getPlaybackTimer(), 80, 40 );
        this.addGraphic( timerGraphic, 1 );

        walkwayGraphic = new WalkWayGraphic( module, this, 11 );
        backgroundGraphic.addGraphic( walkwayGraphic, 0 );

        this.addGraphic( backgroundGraphic, 0 );


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
//        layout = new MovingManLayout( module );
    }

    public void repaint( long tm, int x, int y, int width, int height ) {
        super.repaint( tm, x, y, width, height );
    }

    public void repaint() {
        super.repaint();
    }

    public void repaint( long tm ) {
        super.repaint( tm );
    }

    public void repaint( int x, int y, int width, int height ) {
//                rectList.add( new Rectangle( x, y, width, height ) );
        super.repaint( x, y, width, height );
    }

    protected void paintComponent( Graphics graphics ) {
        if( inited ) {
            super.paintComponent( graphics );
        }
//                Graphics2D g2 = (Graphics2D)graphics;
//                for (int i=0;i<rectList.size();i++){
//                    Rectangle rect=(Rectangle)rectList.get(i);
//                    g2.setColor( Color.green );
//                    g2.setStroke( new BasicStroke( 15 ) );
//                    g2.draw( rect );
//                }
//                rectList.clear();
    }

    public void paintComponents( Graphics g ) {
        if( inited ) {
            super.paintComponents( g );
        }
    }

    protected void paintChildren( Graphics g ) {
        setOpaque( true );
        setDoubleBuffered( true );
        if( inited ) {
            super.paintChildren( g );
        }
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
//               rectList.add (new Rectangle( x, y, w, h ));
    }

    public void repaint( Rectangle r ) {
        super.repaint( 0, r.x, r.y, r.width, r.height );
    }

    public void paintAll( Graphics g ) {
        if( inited ) {
            super.paintAll( g );
        }
    }

    public Component add( Component comp ) {
        KeyListener[] kl = comp.getKeyListeners();
        if( !Arrays.asList( kl ).contains( module.getKeySuite() ) ) {
            comp.addKeyListener( module.getKeySuite() );
        }
        return super.add( comp );
    }

    public void setInited( boolean b ) {
        this.inited = b;
    }

    public void relayout() {
        layout.relayout();
    }

    public void initLayout() {
        layout = new MovingManLayout( module );
    }

    public BufferedGraphicForComponent getBuffer() {
        return backgroundGraphic;
    }

    public void setManTransform( LinearTransform1d transform ) {
        manGraphic.setTransform( transform );
    }

    public void paintBufferedImage() {

        backgroundGraphic.paintBufferedImage();
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

        backgroundGraphic.paintBufferedImage();
        repaint();
    }

    public void repaintBackground( Rectangle rect ) {

        if( backgroundGraphic != null ) {
            backgroundGraphic.paintBufferedImage( rect );
            repaint( rect );
        }
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

    public void setTheSize( int width, int height ) {
        backgroundGraphic.setSize( width, height );
        backgroundGraphic.paintBufferedImage();
    }
}
