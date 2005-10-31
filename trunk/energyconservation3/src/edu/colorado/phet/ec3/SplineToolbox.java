/* Copyright 2004, Sam Reid */
package edu.colorado.phet.ec3;

import edu.colorado.phet.ec3.model.spline.AbstractSpline;
import edu.colorado.phet.ec3.model.spline.CubicSpline;
import edu.colorado.phet.ec3.model.spline.SplineSurface;
import edu.colorado.phet.ec3.view.SplineGraphic;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.nodes.PText;
import edu.umd.cs.piccolo.util.PBounds;
import edu.umd.cs.piccolo.util.PDimension;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Dimension2D;
import java.awt.geom.Rectangle2D;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/**
 * User: Sam Reid
 * Date: Sep 30, 2005
 * Time: 12:03:00 AM
 * Copyright (c) Sep 30, 2005 by Sam Reid
 */

public class SplineToolbox extends PNode {
    private EC3Canvas ec3Canvas;
    private PText textGraphic;
    private EC3RootNode ec3RootNode;
    private SplineGraphic currentIcon;

    public SplineToolbox( final EC3Canvas ec3Canvas, EC3RootNode ec3RootNode ) {
        this.ec3RootNode = ec3RootNode;
        this.ec3Canvas = ec3Canvas;
        SplineGraphic splineGraphic = createSpline();
        currentIcon = splineGraphic;

        PPath boundGraphic = new PPath( new Rectangle( 200, 60 ) );
        boundGraphic.setStroke( new BasicStroke( 2 ) );
        boundGraphic.setStrokePaint( Color.blue );
        boundGraphic.setPaint( Color.yellow );
        addChild( boundGraphic );
        textGraphic = new PText( "Drag to add Track" );
        textGraphic.setFont( new Font( "Lucida Sans", Font.BOLD, 14 ) );
        textGraphic.setOffset( boundGraphic.getFullBounds().getX() + 5, boundGraphic.getFullBounds().getY() + 2 );
        addChild( textGraphic );
        addToRoot( splineGraphic );

        PropertyChangeListener listener = new PropertyChangeListener() {
            public void propertyChange( PropertyChangeEvent evt ) {
                centerTheNode();
                System.out.println( "System.currentTimeMillis() = " + System.currentTimeMillis() );
            }
        };
        ec3RootNode.getWorldNode().addPropertyChangeListener( PNode.PROPERTY_TRANSFORM, listener );
        ec3RootNode.getWorldNode().addPropertyChangeListener( PNode.PROPERTY_FULL_BOUNDS, listener );
        ec3RootNode.getWorldNode().addPropertyChangeListener( PNode.PROPERTY_BOUNDS, listener );
        ec3RootNode.getWorldNode().addPropertyChangeListener( PNode.PROPERTY_CLIENT_PROPERTIES, listener );
        centerTheNode();
    }

    public void centerTheNode() {
        currentIcon.setTransform( new AffineTransform() );
        currentIcon.setSplineSurface( createSplineSurface() );

        PBounds globalIconBounds = currentIcon.getGlobalFullBounds();
        Rectangle2D globalMyBounds = getGlobalFullBounds();
        Dimension2D globalDX = new PDimension( globalMyBounds.getX() - globalIconBounds.x + 20, globalMyBounds.getY() - globalIconBounds.y + 30 );
        currentIcon.globalToLocal( globalDX );

        currentIcon.getSplineSurface().translate( globalDX.getWidth() / 2, globalDX.getHeight() / 2 );
        currentIcon.forceUpdate();
        currentIcon.updateAll();

    }

    private void addToRoot( SplineGraphic splineGraphic ) {
        ec3RootNode.layerAt( 1 ).getWorldNode().addChild( splineGraphic );
    }

    private void removeFromRoot( SplineGraphic splineGraphic ) {
        ec3RootNode.layerAt( 1 ).getWorldNode().removeChild( splineGraphic );
    }

//    private Rectangle2D getExpandBounds( PNode src, int insetX, int insetY ) {
//        Point2D r1c = src.getGlobalFullBounds().getCenter2D();
//        Point2D r2c = new Point2D.Double( src.getGlobalFullBounds().getMaxX(), src.getGlobalFullBounds().getMaxY() );
//        globalToLocal( r1c );
//        globalToLocal( r2c );
//        Rectangle2D rect = new Rectangle2D.Double();
//        rect.setFrameFromCenter( r1c, r2c );
//        rect = RectangleUtils.expand( rect, insetX, insetY );
//        return rect;
//    }

    private SplineGraphic createSpline() {
        SplineSurface surface = createSplineSurface();

        final SplineGraphic splineGraphic = new SplineGraphic( ec3Canvas, surface );
        splineGraphic.disableDragControlPoints();
        splineGraphic.addInputEventListener( new PBasicInputEventHandler() {
            public void mouseDragged( PInputEvent event ) {
                splineGraphic.removeInputEventListener( this );
                removeFromRoot( splineGraphic );

                ec3Canvas.getEnergyConservationModel().addSplineSurface( splineGraphic.getSplineSurface() );
                ec3Canvas.addSplineGraphic( splineGraphic );

                SplineGraphic newSplineGraphic = createSpline();
                addToRoot( newSplineGraphic );
                currentIcon = newSplineGraphic;
                centerTheNode();
            }
        } );
        return splineGraphic;
    }

    private SplineSurface createSplineSurface() {
        AbstractSpline spline = new CubicSpline( EC3Canvas.NUM_CUBIC_SPLINE_SEGMENTS );
        spline.addControlPoint( 0, 0 );
        spline.addControlPoint( 75, 0 );
        spline.addControlPoint( 150, 0 );

        SplineSurface surface = new SplineSurface( spline );
        return surface;
    }
}
