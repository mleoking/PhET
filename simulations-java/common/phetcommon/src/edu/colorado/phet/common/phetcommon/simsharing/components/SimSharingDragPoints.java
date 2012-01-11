// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.common.phetcommon.simsharing.components;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Collections;

import edu.colorado.phet.common.phetcommon.simsharing.Parameter;
import edu.colorado.phet.common.phetcommon.simsharing.messages.ParameterKeys;
import edu.colorado.phet.common.phetcommon.simsharing.messages.ParameterSet;
import edu.colorado.phet.common.phetcommon.util.function.Function1;

import static edu.colorado.phet.common.phetcommon.simsharing.messages.ParameterKeys.numberDragEvents;

/**
 * Helper class for sim-sharing drag handlers.
 * Accumulates drag points, and (on demand) creates a set of sim-sharing parameters that describes the accumulated points.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @author Sam Reid
 */
public class SimSharingDragPoints {

    private ArrayList<Point2D> points = new ArrayList<Point2D>();

    public SimSharingDragPoints() {
        this.points = new ArrayList<Point2D>();
    }

    public void add( Point2D p ) {
        points.add( p );
    }

    public void clear() {
        points.clear();
    }

    public ParameterSet getParameters() {
        ArrayList<Double> xValues = extract( points, new Function1<Point2D, Double>() {
            public Double apply( Point2D point2D ) {
                return point2D.getX();
            }
        } );
        ArrayList<Double> yValues = extract( points, new Function1<Point2D, Double>() {
            public Double apply( Point2D point2D ) {
                return point2D.getY();
            }
        } );
        double minX = min( xValues );
        double maxX = max( xValues );
        double averageX = average( xValues );
        double minY = min( yValues );
        double maxY = max( yValues );
        double averageY = average( yValues );
        return Parameter.param( numberDragEvents, points.size() ).
                param( ParameterKeys.minX, minX ).
                param( ParameterKeys.maxX, maxX ).
                param( ParameterKeys.averageX, averageX ).
                param( ParameterKeys.minY, minY ).
                param( ParameterKeys.maxY, maxY ).
                param( ParameterKeys.averageY, averageY );
    }

    private ArrayList<Double> extract( ArrayList<Point2D> all, Function1<Point2D, Double> extractor ) {
        ArrayList<Double> list = new ArrayList<Double>();
        for ( Point2D point2D : all ) {
            list.add( extractor.apply( point2D ) );
        }
        return list;
    }

    private static double min( ArrayList<Double> v ) {
        return Collections.min( v );
    }

    private static double max( ArrayList<Double> v ) {
        return Collections.max( v );
    }

    private static double average( ArrayList<Double> v ) {
        double sum = 0;
        for ( Double entry : v ) {
            sum += entry;
        }
        return sum / v.size();
    }
}
