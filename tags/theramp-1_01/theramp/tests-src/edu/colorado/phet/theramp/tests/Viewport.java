/* Copyright 2004, Sam Reid */
package edu.colorado.phet.theramp.tests;

import edu.colorado.phet.common.view.phetgraphics.GraphicLayerSet;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Point2D;

/**
 * User: Sam Reid
 * Date: Feb 27, 2005
 * Time: 4:14:11 PM
 * Copyright (c) Feb 27, 2005 by Sam Reid
 */

public class Viewport extends GraphicLayerSet {

    public Viewport( Component component ) {
        super( component );
    }

    private MouseEvent xformEventPt( MouseEvent event ) {
        Point2D.Double p = new Point2D.Double( event.getPoint().getX(), event.getPoint().getY() );
        AffineTransform graphicTx = getTransform();
        try {
            graphicTx.inverseTransform( p, p );
        }
        catch( NoninvertibleTransformException e ) {
            e.printStackTrace();
        }
        int dx = (int)( p.getX() - event.getPoint().getX() );
        int dy = (int)( p.getY() - event.getPoint().getY() );
        event.translatePoint( dx, dy );
        return event;
    }

    public void paint( Graphics2D g2 ) {
        saveGraphicsState( g2 );
        g2.transform( getTransform() );
        super.paint( g2 );
        restoreGraphicsState();
    }

    public void fireMouseExitedBecauseInvisible( MouseEvent e ) {
        super.fireMouseExitedBecauseInvisible( xformEventPt( e ) );
    }

    public void fireMouseDragged( MouseEvent e ) {
        super.fireMouseDragged( xformEventPt( e ) );
    }

    public void fireMouseMoved( MouseEvent e ) {
        super.fireMouseMoved( xformEventPt( e ) );
    }

    public void fireMouseClicked( MouseEvent e ) {
        super.fireMouseClicked( xformEventPt( e ) );
    }

    public void fireMousePressed( MouseEvent e ) {
        super.fireMousePressed( xformEventPt( e ) );
    }

    public void fireMouseReleased( MouseEvent e ) {
        super.fireMouseReleased( xformEventPt( e ) );
    }

    public void fireMouseEntered( MouseEvent e ) {
        super.fireMouseEntered( xformEventPt( e ) );
    }

    public void fireMouseExited( MouseEvent e ) {
        super.fireMouseExited( xformEventPt( e ) );
    }

}
