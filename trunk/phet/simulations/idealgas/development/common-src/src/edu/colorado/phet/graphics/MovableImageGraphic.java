/*
 * Class: MovableImageGraphic
 * Package: edu.colorado.phet.graphicaldomain
 *
 * Created by: Ron LeMaster
 * Date: Oct 28, 2002
 */
package edu.colorado.phet.graphics;

import javax.swing.event.MouseInputListener;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.ImageObserver;

/**
 * This class implements an ImageGraphic that can be moved with the mouse.
 * <p>
 * This class implements the ImageObserver interface so that it can get the bounding
 * rectangle of the image.
 */
public class MovableImageGraphic extends ImageGraphic implements MouseInputListener, ImageObserver {

    private float  minX, minY, maxX, maxY;
    private boolean selected;
    private Rectangle2D.Float hotSpot = new Rectangle2D.Float();


    /**
     *
     */
    public MovableImageGraphic( Image image, float  x, float  y,
                                float  minX, float  minY,
                                float  maxX, float  maxY ) {
        super( image, x, y );
        this.minX = minX;
        this.minY = minY;
        this.maxX = maxX;
        this.maxY = maxY;
        hotSpot.setRect( minX, minY, maxX, maxY );
    }

    public void setMinX( float  minX ) {
        this.minX = minX;
    }

    public void setMinY( float  minY ) {
        this.minY = minY;
    }

    public void setMaxX( float  maxX ) {
        this.maxX = maxX;
    }

    public void setMaxY( float  maxY ) {
        this.maxY = maxY;
    }

    public float  getMinX() {
        return minX;
    }

    public float  getMinY() {
        return minY;
    }

    public float  getMaxX() {
        return maxX;
    }

    public float  getMaxY() {
        return maxY;
    }

    public void setPosition( Point2D.Float location ) {
        this.setPosition( (float)location.getX(), (float)location.getY() );
    }

    public void setPosition( float  x, float  y ) {
        float  xConstrained = Math.max( this.getMinX(),
                                        Math.min( x, this.getMaxX() ) );
        float  yConstrained = Math.max( this.getMinY(),
                                        Math.min( y, this.getMaxY() ) );
        super.setPosition( new Point2D.Float( xConstrained, yConstrained ) );
    }

    //
    // ImageObserver methods
    //
    int x, y, width, height;
    Rectangle2D.Float boundingRect = new Rectangle2D.Float();
    public boolean imageUpdate( Image img, int infoflags,
                                int x, int y, int width, int height ) {
        return false;
    }

    /**
     *
     * @return
     */
    protected boolean isSelected() {
        return selected;
    }

    //
    // Mouse-related methods
    //
    Point2D.Float dragStartPt = new Point2D.Float();
    Point2D.Float imageStartPt = new Point2D.Float();

    public void mouseClicked( MouseEvent e ) {
    }

    public void mousePressed( MouseEvent e ) {
        if( isInHotSpot( e.getPoint() )) {
            selected = true;
            getApparatusPanel().setCursor( Cursor.getPredefinedCursor( Cursor.W_RESIZE_CURSOR ) );
            dragStartPt.setLocation( e.getPoint() );

            // This may not work. We may need to make a copy of the point
            imageStartPt.setLocation( getLocationPoint2D().getX(), getLocationPoint2D().getY() );
        }
    }

    public void mouseReleased( MouseEvent e ) {
        if( selected ) {
            selected = false;
            getApparatusPanel().setCursor( Cursor.getDefaultCursor() );
        }
    }

    public void mouseEntered( MouseEvent e ) {
    }

    public void mouseExited( MouseEvent e ) {
        getApparatusPanel().setCursor( Cursor.getDefaultCursor() );
    }

    public void mouseDragged( MouseEvent e ) {
        if( selected ) {
            float  deltaX = (float)( e.getX() - this.dragStartPt.getX() );
            float  deltaY = (float)( e.getY() - this.dragStartPt.getY() );
            float  newX = (float)( this.imageStartPt.getX() + deltaX );
            float  newY = (float)( this.imageStartPt.getY() + deltaY );
            this.setPosition( newX, newY );

            // We do this so the panel will update even if the clock is stopped
            this.getApparatusPanel().invalidate();
            this.getApparatusPanel().repaint();
        }
    }

    public void mouseMoved( MouseEvent e ) {
    }

    public boolean isInHotSpot( Point p ) {
        boundingRect.setRect( getLocationPoint2D().getX(),
                              getLocationPoint2D().getY(),
                              getImage().getWidth( this ),
                              getImage().getHeight( this ));
        return boundingRect.contains( p );
    }

    public Rectangle2D getHotSpot() {
        return this.hotSpot;
    }

    protected Point2D.Float getDragStartPt() {
        return dragStartPt;
    }

    protected Point2D.Float getImageStartPt() {
        return imageStartPt;
    }

    /**
     * Gets the location of the graphic relative to its minimum X and Y coordinates
     */
    public Point2D.Float getLocationInRange() {
        Point2D.Float result = new Point2D.Float(
                (float)getLocationPoint2D().getX() - this.getMinX(),
                (float)getLocationPoint2D().getY() - this.getMinY() );
        return result;
    }
}
