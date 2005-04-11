/**
 * Class: DefaultInteractiveGraphic
 * Package: edu.colorado.phet.coreadditions.graphics
 * Author: Another Guy
 * Date: Aug 7, 2003
 */
package edu.colorado.phet.coreadditions.graphics;

import edu.colorado.phet.common.model.BaseModel;
import edu.colorado.phet.common.view.graphics.DragHandler;
import edu.colorado.phet.common.view.graphics.InteractiveGraphic;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

/**
 * This class of InteractiveGraphic handles move clicking and dragging
 */
public class DefaultInteractiveGraphic /*implements InteractiveGraphic */{
    private DragHandler dragHandler;
    int painty;
    int paintx;
    private JPanel container;
    private BaseModel model;
    private Image image;
    private Point2D location;
    double yoffset;
    Rectangle2D bounds = new Rectangle2D.Double();

    public DefaultInteractiveGraphic( JPanel container, BaseModel model, Image image, Point2D location ) {
        this.container = container;
        this.model = model;
        this.image = image;
        this.location = location;
        this.yoffset = yoffset;
        bounds.setFrame( location, new Dimension( image.getWidth( null ), image.getHeight( null ) ) );
    }

    public synchronized void paint( Graphics2D graphics2D ) {
        graphics2D.drawImage( image, (int)location.getX(), (int)location.getY(), null );
    }

    public boolean canHandleMousePress( MouseEvent event ) {
        return bounds.contains( event.getX(), event.getY() );
    }

    public void mousePressed( MouseEvent event ) {
        Point current = new Point( (int)bounds.getX(), (int)bounds.getY() );
//        dragHandler = new DragHandler( event.getPoint(), current );
    }

    public void mouseDragged( MouseEvent event ) {
//        final Point rel = dragHandler.getNewLocation( event.getPoint() );
//        location.setLocation( rel.getX(),
//                              rel.getY() );
//        bounds.setRect( rel.getX(), rel.getY(), bounds.getWidth(), bounds.getHeight() );
    }

    public void mouseReleased( MouseEvent event ) {
    }

    public void mouseEntered( MouseEvent event ) {
        container.setCursor( Cursor.getPredefinedCursor( Cursor.HAND_CURSOR ) );
    }

    public void mouseExited( MouseEvent event ) {
        container.setCursor( Cursor.getDefaultCursor() );
    }
}
