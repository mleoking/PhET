package edu.colorado.phet.bernoulli.pipe;

import edu.colorado.phet.bernoulli.common.CircleGraphic;
import edu.colorado.phet.bernoulli.common.RepaintManager;
import edu.colorado.phet.bernoulli.common.SegmentGraphic;
import edu.colorado.phet.bernoulli.spline.Spline;
import edu.colorado.phet.bernoulli.spline.segments.Segment;
import edu.colorado.phet.bernoulli.spline.segments.SegmentPath;
import edu.colorado.phet.common.view.graphics.Graphic;
import edu.colorado.phet.coreadditions.graphics.transform.ModelViewTransform2d;
import edu.colorado.phet.coreadditions.graphics.transform.TransformListener;
import edu.colorado.phet.coreadditions.math.PhetVector;
import edu.colorado.phet.coreadditions.simpleobserver.SimpleObserver;

import java.awt.*;
import java.awt.geom.Point2D;
import java.util.Observable;
import java.util.Observer;

/**
 * User: Sam Reid
 * Date: Aug 23, 2003
 * Time: 1:29:45 AM
 * Copyright (c) Aug 23, 2003 by Sam Reid
 */
public class SimpleParticleGraphic implements Graphic, TransformListener {
    private PipeGraphic pipeGraphic;
    ModelViewTransform2d transform;
    private RepaintManager rm;
    private double fractionalHeight;
    PipeParticle particle;
    CircleGraphic cg;
    private SegmentPath topSegPath;
    double topPathLength;
    private SegmentPath bottomSegPath;
    double bottomPathLength;
    private SegmentPath middleSegPath;
    private SegmentGraphic arrowGraphic;

    public SimpleParticleGraphic( PipeGraphic pipeGraphic, ModelViewTransform2d transform, final PipeParticle particle, final RepaintManager rm, double fractionalHeight ) {
        this.pipeGraphic = pipeGraphic;
        this.transform = transform;
        this.rm = rm;
        this.fractionalHeight = fractionalHeight;
        transform.addTransformListener( this );
        this.particle = particle;
        cg = new CircleGraphic( new Point2D.Double( 0, 0 ), .2, Color.black, transform );
        particle.addObserver( new Observer() {
            public void update( Observable o, Object arg ) {
                particleMoved();
            }
        } );
        splineChanged();
        pipeGraphic.getPipe().addObserver( new SimpleObserver() {
            public void update() {
                splineChanged();
            }
        } );
    }

    private void particleMoved() {
        double scalarDist = particle.getPosition();
        Point2D.Double coord2d = middleSegPath.getPosition( scalarDist );
        if( coord2d == null ) {
            particle.setPosition( 0 );
            return;
        }
        Segment seg = middleSegPath.getSegment( scalarDist );
        PhetVector normal = seg.getDirectionVector().getNormalVector();
        PhetVector halfway = normal.getAddedInstance( coord2d.x, coord2d.y );
        arrowGraphic = new SegmentGraphic( transform, coord2d.x, coord2d.y, halfway.getX(), halfway.getY(), Color.yellow, new BasicStroke( 1 ) );

        cg.setLocation( coord2d );
        rm.update();
    }

    public void paint( Graphics2D g ) {
        if( cg != null ) {
            cg.paint( g );
        }
        if( arrowGraphic != null ) {
            arrowGraphic.paint( g );
        }
    }

    public void transformChanged( ModelViewTransform2d mvt ) {
        if( cg != null ) {
            cg.transformChanged( mvt );
        }
    }

    public SegmentPath toSegmentPath( Spline sp ) {
        if( sp.numInterpolatedPoints() <= 0 ) {
            return new SegmentPath();
        }
        SegmentPath segPathMy = new SegmentPath();
        segPathMy.startAt( sp.interpolatedPointAt( 0 ).x, sp.interpolatedPointAt( 0 ).y );
        for( int i = 1; i < sp.numInterpolatedPoints(); i++ ) {
            segPathMy.lineTo( sp.interpolatedPointAt( i ).x, sp.interpolatedPointAt( i ).y );
        }
        return segPathMy;
    }

    public void splineChanged() {
        topSegPath = toSegmentPath( pipeGraphic.topGraphic.getSpline() );
        topPathLength = topSegPath.getLength();
        bottomSegPath = toSegmentPath( pipeGraphic.bottomGraphic.getSpline() );
        middleSegPath = toSegmentPath( pipeGraphic.getMiddleFlowLineSpline() );
        bottomPathLength = bottomSegPath.getLength();
        particleMoved();
        rm.update();
    }
}
