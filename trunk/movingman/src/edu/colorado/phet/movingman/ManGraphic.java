/*PhET, 2004.*/
package edu.colorado.phet.movingman;

import edu.colorado.phet.common.model.Command;
import edu.colorado.phet.common.view.graphics.InteractiveGraphic;
import edu.colorado.phet.common.view.util.BufferedImageUtils;
import edu.colorado.phet.common.view.util.ImageLoader;
import edu.colorado.phet.movingman.common.CircularBuffer;
import edu.colorado.phet.movingman.common.RangeToRange;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Observable;
import java.util.Observer;

/**
 * User: Sam Reid
 * Date: Jun 30, 2003
 * Time: 12:25:37 AM
 * Copyright (c) Jun 30, 2003 by Sam Reid
 */
public class ManGraphic implements InteractiveGraphic, Observer {
    private BufferedImage standingMan;
    private BufferedImage leftMan;
    private BufferedImage rightMan;
    private int x;
    private int y;
    private RangeToRange transform;//from man to graphics device.
    private MovingManModule module;
    private Man m;
    private DragHandler dragHandler;
    private BufferedImage currentImage;
    private RangeToRange inversion;
    private CircularBuffer cb = new CircularBuffer( 10 );
    private double lastx = 0;

    public ManGraphic( MovingManModule module, Man m, int y, RangeToRange transform ) throws IOException {
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
        m.addObserver( this );
        inversion = transform.invert();
        update();
    }

    public void update() {
        update( null, null );
    }

    public void paint( Graphics2D g ) {
        g.drawImage( currentImage, x - currentImage.getWidth() / 2, y, null );
    }

    public void update( Observable o, Object arg ) {
        Rectangle origRectangle = getRectangle();

        double output = transform.evaluate( m.getX() );
        int oldX = x;
        this.x = (int)output;

        cb.addPoint( x - lastx );
        lastx = x;
        double velocity = cb.average();
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
        if( oldX != x || origImage != currentImage ) {
            repaint( origRectangle, getRectangle() );
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
        if( !module.getRecordMode() || module.isPaused() ) {
            module.getMovingManControlPanel().startRecordingManual();
        }
        final Point newPt = dragHandler.getNewLocation( event.getPoint() );
        int graphicsPt = newPt.x;
        double manPoint = inversion.evaluate( graphicsPt );
        m.setX( manPoint );
        module.setWiggleMeVisible( false );
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
    }

    public void mouseEntered( MouseEvent event ) {
        event.getComponent().setCursor( Cursor.getPredefinedCursor( Cursor.HAND_CURSOR ) );
    }

    public void mouseExited( MouseEvent event ) {
        event.getComponent().setCursor( Cursor.getPredefinedCursor( Cursor.DEFAULT_CURSOR ) );
    }

    public void setTransform( RangeToRange transform ) {
        this.transform = transform;
        this.inversion = transform.invert();
        update( null, null );
    }

    public boolean contains( int x, int y ) {
        Rectangle r = new Rectangle( this.x - currentImage.getWidth() / 2, this.y, currentImage.getWidth(), currentImage.getHeight() );
        return r.contains( x, y );
    }
}
