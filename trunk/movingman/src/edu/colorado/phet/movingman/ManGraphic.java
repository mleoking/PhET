/*PhET, 2004.*/
package edu.colorado.phet.movingman;

import edu.colorado.phet.common.math.LinearTransform1d;
import edu.colorado.phet.common.model.Command;
import edu.colorado.phet.common.view.phetgraphics.PhetGraphic;
import edu.colorado.phet.common.view.util.BufferedImageUtils;
import edu.colorado.phet.common.view.util.ImageLoader;

import javax.swing.event.MouseInputListener;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;

/**
 * User: Sam Reid
 * Date: Jun 30, 2003
 * Time: 12:25:37 AM
 * Copyright (c) Jun 30, 2003 by Sam Reid
 */
public class ManGraphic extends PhetGraphic implements MouseInputListener {
    private BufferedImage standingMan;
    private BufferedImage leftMan;
    private BufferedImage rightMan;
    private int x;
    private int y;
    private LinearTransform1d transform;//from man to graphics device.
    private LinearTransform1d inversion;
    private MovingManModule module;
    private Man m;
    private DragHandler dragHandler;
    private BufferedImage currentImage;

    private ArrayList listeners = new ArrayList();
    private int lastX;

    public ManGraphic( MovingManModule module, Man m, int y, LinearTransform1d transform ) throws IOException {
        super( module.getApparatusPanel() );
        this.module = module;
        this.m = m;
        this.y = y;
        this.transform = transform;
        standingMan = ImageLoader.loadBufferedImage( "images/stand-ii.gif" );
        leftMan = ImageLoader.loadBufferedImage( "images/left-ii.gif" );
        int height = 120;
        standingMan = BufferedImageUtils.rescaleYMaintainAspectRatio( module.getApparatusPanel(), standingMan, height );
        leftMan = BufferedImageUtils.rescaleYMaintainAspectRatio( module.getApparatusPanel(), leftMan, height );
        rightMan = BufferedImageUtils.flipX( leftMan );

        currentImage = standingMan;
        m.addListener( new Man.Listener() {
            public void positionChanged( double x ) {
                ManGraphic.this.positionChanged();
            }

            public void velocityChanged( double velocity ) {
            }

            public void accelerationChanged( double acceleration ) {
            }
        } );
        inversion = transform.getInvertedInstance();
        positionChanged();
        this.addMouseInputListener( this );
    }

    public void paint( Graphics2D g ) {
        g.drawImage( currentImage, x - currentImage.getWidth() / 2, y, null );
    }

    public void positionChanged() {
        Rectangle origRectangle = getRectangle();

        double output = transform.transform( m.getX() );
        lastX = x;
        this.x = (int)output;

        if( lastX != x ) {
            double dx = getDx();
            if( dx != 0 ) {
                setVelocity( dx );
            }
            doRepaint( origRectangle );
        }
    }

    private void doRepaint( Rectangle origRectangle ) {
        repaint( origRectangle, getRectangle() );
        for( int i = 0; i < listeners.size(); i++ ) {
            Listener listener = (Listener)listeners.get( i );
            listener.manGraphicChanged();
        }
    }

    private void repaint( Rectangle r1, Rectangle r2 ) {
        Rectangle union = r1.union( r2 );
        module.getApparatusPanel().repaint( union );
    }

    public void mouseClicked( MouseEvent e ) {
    }

    public void mousePressed( MouseEvent event ) {
        dragHandler = new DragHandler( event.getPoint(), new Point( x, y ) );
        module.getModel().execute( new Command() {
            public void doIt() {
                m.setGrabbed( true );
            }
        } );
    }

    public void removeListener( Listener listener ) {
        listeners.remove( listener );
    }

    public void setVelocity( double velocity ) {
//        System.out.println( "velocity = " + velocity );
        Rectangle rect = getRectangle();
        BufferedImage origImage = currentImage;
        if( velocity == 0 && currentImage != this.standingMan ) {
            currentImage = this.standingMan;
        }
        else if( velocity < 0 && currentImage != this.leftMan ) {
            currentImage = this.leftMan;
        }
        else if( velocity > 0 && currentImage != this.rightMan ) {
            currentImage = this.rightMan;
        }
        if( currentImage != origImage ) {
            doRepaint( rect );
        }
    }

    public boolean isDragging() {
        return dragHandler != null;
    }

    public double getDx() {
        return x - lastX;
    }

    interface Listener {
        void manGraphicChanged();

        void mouseReleased();
    }

    public void addListener( Listener listener ) {
        listeners.add( listener );
    }

    class DragHandler {

        private Point dragStartPt;
        private Point viewStart;
        private Point newLocation = new Point();

        public DragHandler( Point mouseStart, Point viewStart ) {
            this.dragStartPt = mouseStart;
            this.viewStart = viewStart;
        }

        public Point getNewLocation( Point p ) {
            int dx = p.x - dragStartPt.x;
            int dy = p.y - dragStartPt.y;
            newLocation.x = dx + viewStart.x;
            newLocation.y = dy + viewStart.y;
            return newLocation;
        }

    }

    public void mouseDragged( MouseEvent event ) {
        if( !module.isRecordMode() || module.isPaused() || module.getNumSmoothingPoints() != 12 ) {
            module.setRecordMode();
            module.setNumSmoothingPoints( 12 );
            module.setPaused( false );
        }
        final Point newPt = dragHandler.getNewLocation( event.getPoint() );
        int graphicsPt = newPt.x;
        double manPoint = inversion.transform( graphicsPt );
        m.setX( manPoint );
    }

    public void mouseMoved( MouseEvent e ) {
    }

    public Rectangle getRectangle() {
        return new Rectangle( x - currentImage.getWidth() / 2, y, currentImage.getWidth(), currentImage.getHeight() );
    }

    public void mouseReleased( MouseEvent event ) {
        module.getModel().execute( new Command() {
            public void doIt() {
                m.setGrabbed( false );
            }
        } );
        dragHandler = null;
        for( int i = 0; i < listeners.size(); i++ ) {
            Listener listener = (Listener)listeners.get( i );
            listener.mouseReleased();
        }
    }

    public void mouseEntered( MouseEvent event ) {
        event.getComponent().setCursor( Cursor.getPredefinedCursor( Cursor.HAND_CURSOR ) );
    }

    public void mouseExited( MouseEvent event ) {
        event.getComponent().setCursor( Cursor.getPredefinedCursor( Cursor.DEFAULT_CURSOR ) );
    }

    public void setTransform( LinearTransform1d transform ) {
        this.transform = transform;
        this.inversion = transform.getInvertedInstance();
        positionChanged();
    }

    public boolean contains( int x, int y ) {
        return getRectangle().contains( x, y );
    }

    protected Rectangle determineBounds() {
        return getRectangle();
    }
}
