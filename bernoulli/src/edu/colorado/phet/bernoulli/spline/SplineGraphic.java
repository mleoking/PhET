package edu.colorado.phet.bernoulli.spline;

import edu.colorado.phet.common.view.graphics.Graphic;
import edu.colorado.phet.coreadditions.graphics.transform.ModelViewTransform2d;
import edu.colorado.phet.coreadditions.graphics.transform.TransformListener;
import edu.colorado.phet.coreadditions.simpleobserver.SimpleObserver;

import java.awt.*;
import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;

/**
 * User: Sam Reid
 * Date: Aug 22, 2003
 * Time: 2:45:28 PM
 * Copyright (c) Aug 22, 2003 by Sam Reid
 */
public class SplineGraphic implements SimpleObserver, TransformListener, Graphic {
    Spline spline;
    ModelViewTransform2d transform;
    Shape shape;
    Stroke stroke;
    Color color;

    public SplineGraphic( Spline spline, ModelViewTransform2d transform ) {
        this( spline, transform, new BasicStroke( 2.0f ), Color.black );
    }

    public SplineGraphic( Spline spline, ModelViewTransform2d transform, Stroke stroke, Color color ) {
        this.spline = spline;
        this.transform = transform;
        transform.addTransformListener( this );
        spline.addObserver( this );
        this.stroke = stroke;
        this.color = color;
    }

    public void update() {
        if( spline.numInterpolatedPoints() < 2 ) {
            shape = null;
            return;
        }
        GeneralPath path = new GeneralPath();
        Point2D.Double pt = spline.interpolatedPointAt( 0 );
        Point viewPt = transform.modelToView( pt );
        path.moveTo( viewPt.x, viewPt.y );
        for( int i = 1; i < spline.numInterpolatedPoints(); i++ ) {
            pt = spline.interpolatedPointAt( i );
            viewPt = transform.modelToView( pt );
            path.lineTo( viewPt.x, viewPt.y );
        }
        shape = stroke.createStrokedShape( path );
    }

    public void transformChanged( ModelViewTransform2d mvt ) {
        update();
    }

    public void paint( Graphics2D g ) {
        if( shape != null ) {
            g.setColor( color );
            g.fill( shape );
        }
    }

    public Spline getSpline() {
        return spline;
    }

}
