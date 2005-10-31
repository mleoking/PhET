/* Copyright 2004, Sam Reid */
package edu.colorado.phet.ec3;

import edu.colorado.phet.common.view.util.RectangleUtils;
import edu.colorado.phet.ec3.model.spline.AbstractSpline;
import edu.colorado.phet.ec3.model.spline.CubicSpline;
import edu.colorado.phet.ec3.model.spline.SplineSurface;
import edu.colorado.phet.ec3.view.SplineGraphic;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.nodes.PText;
import edu.umd.cs.piccolo.util.PPickPath;

import java.awt.*;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

/**
 * User: Sam Reid
 * Date: Sep 30, 2005
 * Time: 12:03:00 AM
 * Copyright (c) Sep 30, 2005 by Sam Reid
 */

public class SplineToolbox extends PNode {
    private EC3Canvas ec3Canvas;
    private PText textGraphic;
    private int x;
    private int y;
    private PBasicInputEventHandler creator;
    private PBasicInputEventHandler delegator;
    private SplineGraphic dummySplineGraphic;

    public SplineToolbox( final EC3Canvas ec3Canvas, int x, int y ) {
        this.ec3Canvas = ec3Canvas;
        this.x = x;
        this.y = y;
        dummySplineGraphic = createSpline();
        dummySplineGraphic.disableDragControlPoints();
        dummySplineGraphic.disableDragHandler();

        creator = new PBasicInputEventHandler() {
            public void mouseDragged( PInputEvent event ) {
                dummySplineGraphic.removeInputEventListener( this );


                SplineGraphic newSplineGraphic = createSpline();
                ec3Canvas.getEnergyConservationModel().addSplineSurface( newSplineGraphic.getSplineSurface() );
                ec3Canvas.addSplineGraphic( newSplineGraphic );
                dummySplineGraphic.addInputEventListener( new Delegator( newSplineGraphic ) );
                System.out.println( "Added newSplineGraphic@" + System.currentTimeMillis() );

                PPickPath oldPath = event.getPath();
                PPickPath newPath = new PPickPath( oldPath.getTopCamera(), oldPath.getPickBounds() );

                PNode node = newSplineGraphic;
                while( node != null ) {
                    newPath.pushNode( node );
                    node = node.getParent();
                }

                dummySplineGraphic.getRoot().getDefaultInputManager().setMouseFocus( newPath );
            }
        };
        dummySplineGraphic.addInputEventListener( creator );

        PPath boundGraphic = new PPath( getExpandBounds( dummySplineGraphic, 20, 20 ) );
        boundGraphic.setStroke( new BasicStroke( 2 ) );
        boundGraphic.setStrokePaint( Color.blue );
        boundGraphic.setPaint( Color.yellow );
        addChild( boundGraphic );
        textGraphic = new PText( "Drag to add Track" );
        textGraphic.setFont( new Font( "Lucida Sans", Font.BOLD, 14 ) );
        textGraphic.setOffset( boundGraphic.getFullBounds().getX() + 5, boundGraphic.getFullBounds().getY() + 2 );
        addChild( textGraphic );
        addChild( dummySplineGraphic );
    }

    private class Delegator extends PBasicInputEventHandler {
        private SplineGraphic newSplineGraphic;

        public Delegator( SplineGraphic newSplineGraphic ) {
            this.newSplineGraphic = newSplineGraphic;
        }

        public void mouseDragged( PInputEvent event ) {
            getHandler().mouseDragged( event );
        }

        private PBasicInputEventHandler getHandler() {
            return newSplineGraphic.getDragHandler();
        }

        public void mouseReleased( PInputEvent event ) {
            getHandler().mouseReleased( event );
            dummySplineGraphic.removeInputEventListener( this );
            dummySplineGraphic.addInputEventListener( creator );
        }

        public void mousePressed( PInputEvent event ) {
            getHandler().mousePressed( event );
        }
    }

    private Rectangle2D getExpandBounds( PNode src, int insetX, int insetY ) {

        // First get the center of each rectangle in the
        // local coordinate system of each rectangle.
        Point2D r1c = src.getGlobalFullBounds().getCenter2D();
        Point2D r2c = new Point2D.Double( src.getGlobalFullBounds().getMaxX(), src.getGlobalFullBounds().getMaxY() );

        this.globalToLocal( r1c );
        this.globalToLocal( r2c );

        // Finish by setting the endpoints of the line to
        // the center points of the rectangles, now that those
        // center points are in the local coordinate system of the line.
        Rectangle2D rect = new Rectangle2D.Double();
        rect.setFrameFromCenter( r1c, r2c );
        rect = RectangleUtils.expand( rect, insetX, insetY );
        return rect;
    }

    private SplineGraphic createSpline() {
        AbstractSpline spline = new CubicSpline( EC3Canvas.NUM_CUBIC_SPLINE_SEGMENTS );

        spline.addControlPoint( 0, 0 );
        spline.addControlPoint( 75, 0 );
        spline.addControlPoint( 150, 0 );
        spline.translate( x, y );
        SplineSurface surface = new SplineSurface( spline );
        final SplineGraphic splineGraphic = new SplineGraphic( ec3Canvas, surface );

        return splineGraphic;
    }
}
