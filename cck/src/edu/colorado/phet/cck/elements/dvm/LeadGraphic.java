/*Copyright, University of Colorado, 2004.*/
package edu.colorado.phet.cck.elements.dvm;

import edu.colorado.phet.cck.common.DifferentialDragHandler;
import edu.colorado.phet.common.util.SimpleObservable;
import edu.colorado.phet.common.util.SimpleObserver;
import edu.colorado.phet.common.view.graphics.InteractiveGraphic;
import edu.colorado.phet.common.view.graphics.transforms.ModelViewTransform2D;
import edu.colorado.phet.common.view.graphics.transforms.TransformListener;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

/**
 * User: Sam Reid
 * Date: Oct 26, 2003
 * Time: 12:35:28 PM
 * Copyright (c) Oct 26, 2003 by Sam Reid
 */
public class LeadGraphic extends SimpleObservable implements InteractiveGraphic {
    Lead lead;
    BufferedImage image;
    private ModelViewTransform2D transform;
    private double angle;
    private AffineTransform trf = new AffineTransform();
    int x;
    int y;
    Shape selectionShape;
    Shape tipShape;
    DifferentialDragHandler ddh;
    private Point2D wirePoint;
    Rectangle2D.Double originalTipShape;

    public LeadGraphic( Lead lead, BufferedImage image, ModelViewTransform2D transform, double angle ) {
        this.lead = lead;
        this.image = image;
        this.transform = transform;
        double tipWidth = 3;
        double tipHeight = 26;

        originalTipShape = new Rectangle2D.Double( image.getWidth() / 2 - tipWidth / 2 + 1, 0, tipWidth, tipHeight );
        this.angle = angle;
        lead.addObserver( new SimpleObserver() {
            public void update() {
                changed();
            }
        } );
        transform.addTransformListener( new TransformListener() {
            public void transformChanged( ModelViewTransform2D ModelViewTransform2D ) {
                changed();
            }
        } );
        changed();
    }

    private void changed() {
        Point pt = transform.modelToView( lead.getX(), lead.getY() );
        this.x = pt.x;
        this.y = pt.y;
        this.trf.setToTranslation( x, y );
        trf.rotate( angle, image.getWidth() / 2, image.getHeight() / 2 );
        Rectangle2D.Double rect = new Rectangle2D.Double( 0, 0, image.getWidth(), image.getHeight() );
        Point2D wireInputPoint = new Point2D.Double( image.getWidth() / 2, image.getHeight() );
        wireInputPoint = trf.transform( wireInputPoint, wireInputPoint );
        this.wirePoint = wireInputPoint;
        selectionShape = trf.createTransformedShape( rect );
        tipShape = trf.createTransformedShape( originalTipShape );
        notifyObservers();
    }

    public boolean canHandleMousePress( MouseEvent event ) {
        return selectionShape.contains( event.getPoint() );
    }

    public void mousePressed( MouseEvent event ) {
        ddh = new DifferentialDragHandler( event.getPoint() );
    }

    public void mouseDragged( MouseEvent event ) {
        Point dx = ddh.getDifferentialLocationAndReset( event.getPoint() );
        Point2D.Double modelDX = transform.viewToModelDifferential( dx );
        lead.translate( modelDX.x, modelDX.y );
    }

    public void mouseMoved( MouseEvent e ) {
    }

    public void mouseReleased( MouseEvent event ) {

    }

    public void mouseClicked( MouseEvent e ) {
    }

    public void mouseEntered( MouseEvent event ) {
        event.getComponent().setCursor( Cursor.getPredefinedCursor( Cursor.HAND_CURSOR ) );
    }

    public void mouseExited( MouseEvent event ) {
        event.getComponent().setCursor( Cursor.getPredefinedCursor( Cursor.DEFAULT_CURSOR ) );
    }

    public void paint( Graphics2D g ) {
        g.drawRenderedImage( image, trf );
        g.setColor( Color.gray );
        g.fill( tipShape );
    }

    public Point2D getInputPoint() {
        return wirePoint;
    }

    public Shape getTipShape() {
        return tipShape;
    }

    public boolean contains( int x, int y ) {
        return selectionShape.contains( x, y );
    }

}
