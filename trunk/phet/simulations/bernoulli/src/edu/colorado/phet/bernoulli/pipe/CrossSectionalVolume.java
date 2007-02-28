package edu.colorado.phet.bernoulli.pipe;

import edu.colorado.phet.bernoulli.common.DoubleGeneralPath;
import edu.colorado.phet.bernoulli.spline.Spline;
import edu.colorado.phet.bernoulli.spline.segments.Segment;
import edu.colorado.phet.bernoulli.spline.segments.SegmentPath;
import edu.colorado.phet.coreadditions.math.PhetVector;
import edu.colorado.phet.coreadditions.simpleobserver.SimpleObservable;
import edu.colorado.phet.coreadditions.simpleobserver.SimpleObserver;

import java.awt.geom.Area;
import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;

/**
 * User: Sam Reid
 * Date: Aug 23, 2003
 * Time: 9:17:12 PM
 * Copyright (c) Aug 23, 2003 by Sam Reid
 */
public class CrossSectionalVolume extends SimpleObservable {
    Pipe parent;
    double scalarDist;
    SegmentPath middlePath;
    private Area area;
    private Point2D.Double topIntersection;
    private Point2D.Double coord2d;
    private Point2D.Double bottomIntersection;
    private double width;
    private SegmentPath bottomPath;//just here for showDebuggingGraphics.  Its a temp var.
    private Segment topIntersectionSegment;
    private double volumeWidth;
    private SegmentPath topPath;
//    private static double speedScaleFactor = .001;
    private static double speedScaleFactor = .00035;

    public double getVolumeWidth() {
        return volumeWidth;
    }

    public SegmentPath getTopPath() {
        return topPath;
    }

    public CrossSectionalVolume( final Pipe parent, double x ) {
        this.parent = parent;
        this.scalarDist = x;
        parent.addObserver( new SimpleObserver() {
            public void update() {
                parentChanged();
            }
        } );
        parentChanged();
    }

    public double getWidth() {
        if( topIntersection == null || bottomIntersection == null ) {
            return Double.POSITIVE_INFINITY;
        }
        return topIntersection.distance( bottomIntersection );
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

    public Area getVolume() {
        return area;
    }

    public void parentChanged() {
        //state of the parent pipe changed.  We probably need to recompute SegmentPaths.
        Spline midline = parent.flowLineAt( parent.numFlowLines() / 2 );
        this.middlePath = toSegmentPath( midline );
        recomputeShape();
    }

    private void recomputeShape() {
        coord2d = middlePath.getPosition( scalarDist );
        if( coord2d == null ) {
            scalarDist = 0;
            coord2d = middlePath.getPosition( scalarDist );
            if( coord2d == null ) {
                throw new RuntimeException( "No position for scalardist." );
            }
        }
        topPath = toSegmentPath( this.parent.getTopSpline() );
        bottomPath = toSegmentPath( this.parent.getBottomSpline() );

        Segment seg = middlePath.getSegment( scalarDist );

        PhetVector directionVector = seg.getDirectionVector();
        PhetVector normal = directionVector.getNormalVector().getScaledInstance( 2 );

        topIntersectionSegment = new Segment( coord2d.x, coord2d.y, normal.getX() + coord2d.x, normal.getY() + coord2d.y );
        topIntersection = topPath.getFirstIntersection( topIntersectionSegment );

        PhetVector otherDir = normal.getScaledInstance( -1 );
        Segment n2 = new Segment( coord2d.x, coord2d.y, otherDir.getX() + coord2d.x, otherDir.getY() + coord2d.y );
        bottomIntersection = bottomPath.getFirstIntersection( n2 );

        width = getWidth();
        double widthScale = .3;

        PhetVector widthVector = new PhetVector( directionVector.getScaledInstance( 1 / width * widthScale ) );
        volumeWidth = widthVector.getMagnitude();
        PhetVector a = new PhetVector( coord2d.x, coord2d.y );
        PhetVector b = a.getAddedInstance( normal );
        PhetVector c = b.getAddedInstance( widthVector );
        PhetVector d = c.getAddedInstance( normal.getScaledInstance( -2 ) );
        PhetVector e = d.getAddedInstance( widthVector.getScaledInstance( -1 ) );
        DoubleGeneralPath path = new DoubleGeneralPath( a );
        path.lineTo( b );
        path.lineTo( c );
        path.lineTo( d );
        path.lineTo( e );
        GeneralPath gpath = path.getGeneralPath();
        gpath.closePath();
        area = new Area( gpath );
        area.intersect( new Area( parent.getShape() ) );
        updateObservers();
    }

    public void stepInTime( double dt ) {
        double speed;// = .001;
        if( !Double.isInfinite( width ) ) {
            speed = widthToSpeed( width );//1 / width * speedScaleFactor;
        }
        else {
            speed = .001;
        }
        scalarDist = scalarDist + speed * dt;
        recomputeShape();
    }

    public static double widthToSpeed( double width ) {
        return 1 / width * speedScaleFactor;
    }

    public Point2D.Double getTopIntersection() {
        return topIntersection;
    }

    public Point2D.Double getBottomIntersection() {
        return bottomIntersection;
    }

    public Segment getTopIntersectionSegment() {
        return topIntersectionSegment;
    }

    public SegmentPath getBottomPath() {
        return bottomPath;
    }

    public Point2D.Double getCenter() {
        return coord2d;
    }

}
