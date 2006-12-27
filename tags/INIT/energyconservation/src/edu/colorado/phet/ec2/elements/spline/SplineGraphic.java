/*Copyright, Sam Reid, 2003.*/
package edu.colorado.phet.ec2.elements.spline;

import edu.colorado.phet.common.view.CompositeGraphic;
import edu.colorado.phet.coreadditions.graphics.transform.ModelViewTransform2d;
import edu.colorado.phet.coreadditions.graphics.transform.TransformListener;

import java.awt.*;
import java.util.ArrayList;

/**
 * User: Sam Reid
 * Date: Jul 25, 2003
 * Time: 7:45:29 PM
 * Copyright (c) Jul 25, 2003 by Sam Reid
 */
public class SplineGraphic extends CompositeGraphic implements SplineObserver, TransformListener {

    private ModuleSplineInterface moduleInterface;
    protected Spline spline;
    protected CurveGraphic curve;
    protected ArrayList vertices = new ArrayList();
    protected ModelViewTransform2d transform;

    public SplineGraphic( ModelViewTransform2d transform, ModuleSplineInterface moduleInterface, Spline spline ) {
        this.transform = transform;
        this.moduleInterface = moduleInterface;
        this.spline = spline;
        transform.addTransformListener( this );
        spline.addSplineObserver( this );
        curve = new CurveGraphic( transform, this, spline );
        pointStructureChanged( spline );
    }

    public VertexGraphic vertexGraphicAt( int i ) {
        return (VertexGraphic)vertices.get( i );
    }

    public void remove() {
        moduleInterface.removeSpline( this );
    }

    public synchronized void paint( Graphics2D g ) {
        super.paint( g );
    }

    public void transformChanged( ModelViewTransform2d transform ) {
        this.transform = transform;
        curve.setTransform( transform );
        pointStructureChanged( null );
        for( int i = 0; i < vertices.size(); i++ ) {
            VertexGraphic vertexGraphic = (VertexGraphic)vertices.get( i );
            vertexGraphic.viewChanged( transform );
        }
    }

    public void splineTranslated( Spline source, double dx, double dy ) {
        curve.update( source, null );
        Point[] pts = curve.getPoints();
        for( int i = 0; i < vertices.size(); i++ ) {
            VertexGraphic vertexGraphic = (VertexGraphic)vertices.get( i );
            vertexGraphic.setPosition( pts[i].x, pts[i].y );
        }
    }

    public void pointStructureChanged( Spline source ) {
        removeAllGraphics();
        addGraphic( curve, 0 );

        curve.update( source, null );
        Point[] pts = curve.getPoints();
        vertices = new ArrayList();
        for( int i = 0; i < pts.length; i++ ) {
            Point pt = pts[i];
            VertexGraphic vg = new VertexGraphic( this, spline, i, pt.x, pt.y );
            addGraphic( vg, 1 );
            vertices.add( vg );
            vg.viewChanged( transform );
        }
    }

    public void addNewSpline( Spline spline ) {
        moduleInterface.addSpline( spline );
    }

    public Spline getSpline() {
        return spline;
    }

    public CurveGraphic getCurveGraphic() {
        return curve;
    }

    public int numVertexGraphics() {
        return vertices.size();
    }

    public void removeVertexGraphicAt( int i ) {
        removeGraphic( vertexGraphicAt( i ) );
        vertices.remove( i );
    }

}
