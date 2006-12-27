/* Copyright 2004, Sam Reid */
package edu.colorado.phet.ec3.view;

import edu.colorado.phet.ec3.model.spline.AbstractSpline;
import edu.colorado.phet.ec3.model.spline.AbstractSpline;
import edu.colorado.phet.piccolo.CursorHandler;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.util.PDimension;

import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;

/**
 * User: Sam Reid
 * Date: Sep 26, 2005
 * Time: 1:17:41 PM
 * Copyright (c) Sep 26, 2005 by Sam Reid
 */

public class SplineGraphic extends PNode {
    private AbstractSpline spline;
    private PPath pathLayer;
    private PNode controlPointLayer;

    public SplineGraphic( AbstractSpline spline ) {
        this.spline = spline;
        pathLayer = new PPath();
        controlPointLayer = new PNode();

        addChild( pathLayer );
        addChild( controlPointLayer );

        updateAll();
    }

    private void updateAll() {
        pathLayer.removeAllChildren();
        controlPointLayer.removeAllChildren();
        GeneralPath path = spline.getInterpolationPath();
        pathLayer.setPathTo( path );
        for( int i = 0; i < spline.numControlPoints(); i++ ) {
            Point2D point = spline.controlPointAt( i );
            addControlPoint( point, i );
        }
    }

    private void addControlPoint( Point2D point, final int index ) {
        int width = 20;
        PPath controlCircle = new PPath( new Ellipse2D.Double( point.getX() - width / 2, point.getY() - width / 2, width, width ) );
        controlCircle.setStroke( new BasicStroke( 2 ) );
        controlCircle.setStrokePaint( Color.black );
        controlCircle.setPaint( new Color( 0, 0, 1f, 0.5f ) );
        pathLayer.addChild( controlCircle );
        controlCircle.addInputEventListener( new PBasicInputEventHandler() {
            public void mouseDragged( PInputEvent event ) {
                PDimension rel = event.getDeltaRelativeTo( SplineGraphic.this );
                spline.translateControlPoint( index, rel.getWidth(), rel.getHeight() );
                updateAll();
            }
        } );
        controlCircle.addInputEventListener( new CursorHandler( Cursor.HAND_CURSOR) );
    }
}
