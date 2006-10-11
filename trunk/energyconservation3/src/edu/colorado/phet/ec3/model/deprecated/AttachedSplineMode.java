/* Copyright 2004, Sam Reid */
package edu.colorado.phet.ec3.model.deprecated;

import edu.colorado.phet.ec3.model.Body;
import edu.colorado.phet.ec3.model.EnergyConservationModel;
import edu.colorado.phet.ec3.model.UpdateMode;
import edu.colorado.phet.ec3.model.spline.AbstractSpline;
import edu.colorado.phet.ec3.model.spline.Segment;
import edu.colorado.phet.ec3.model.spline.SegmentPath;
import edu.umd.cs.piccolo.util.PBounds;

import java.awt.*;
import java.awt.geom.Area;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;

/**
 * User: Sam Reid
 * Date: Sep 26, 2005
 * Time: 7:33:54 PM
 * Copyright (c) Sep 26, 2005 by Sam Reid
 */

public class AttachedSplineMode implements UpdateMode {
    private AbstractSpline spline;
    private Body body;
    private double velocity = 3;
    private double position = 0;

    public AttachedSplineMode( AbstractSpline spline, Body body ) {
        this.spline = spline;
        this.body = body;
        position = guessPositionAlongSpline( spline );
    }

    private double guessPositionAlongSpline( AbstractSpline spline ) {
        SegmentPath segmentPath = spline.getSegmentPath();
        Shape bodyShape = body.getShape();
        //find all segments that overlap.
        ArrayList overlap = new ArrayList();
        for( int i = 0; i < segmentPath.numSegments(); i++ ) {
            Segment segment = segmentPath.segmentAt( i );
            Area a = new Area( bodyShape );
            a.intersect( new Area( segment.getShape() ) );
            if( !a.isEmpty() ) {
                overlap.add( segment );
            }
        }

        //return the centroid.
        Rectangle2D rect = null;
        for( int i = 0; i < overlap.size(); i++ ) {
            Segment segment = (Segment)overlap.get( i );
            if( rect == null ) {
                rect = segment.toLine2D().getBounds2D();
            }
            else {
                rect = rect.createUnion( segment.toLine2D().getBounds2D() );
            }
        }
        Point2D center = new PBounds( rect ).getCenter2D();
        return getClosestScalar( center );
    }

    private double getClosestScalar( Point2D center ) {
        SegmentPath segmentPath = spline.getSegmentPath();
        double closestPosition = Double.POSITIVE_INFINITY;
        Segment bestSegment = null;
        for( int i = 0; i < segmentPath.numSegments(); i++ ) {
            Segment seg = segmentPath.segmentAt( i );
            double distance = center.distance( seg.getCenter2D() );
            if( distance < closestPosition ) {
                bestSegment = seg;
                closestPosition = distance;
            }
        }
        return segmentPath.getScalarPosition( bestSegment );
    }

    public void stepInTime( EnergyConservationModel model, Body body, double dt ) {
        position += velocity;
        updateBody( model, body, dt );
    }

    private void updateBody( EnergyConservationModel model, Body body, double dt ) {
        if( position < 0 || position > spline.getSegmentPath().getLength() ) {
            body.setFreeFallMode();
        }
        else {
            Point2D baseLocation = splineToWorld();
            body.setPosition( baseLocation.getX(), baseLocation.getY() );
            Segment segment = spline.getSegmentPath().getSegmentAtPosition( position );//todo this duplicates much work.
            body.setAngle( segment.getAngle() );
        }
    }

    private Point2D splineToWorld() {
        return spline.evaluate( position );
    }

    public AbstractSpline getSpline() {
        return spline;
    }
}
