// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.linegraphing.common.model;

import java.awt.Color;

/**
 * Factories for creating lines.
 * These factories are used to handle instantiation of lines in classes that are parameterized using generics.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public interface LineFactory<T extends PointSlopeLine> {

    // Creates a simplified version of the specified line. Simplification involves reducing the slope.
    public T simplify( T line );

    // Creates a line with a different point
    public T withPoint( T line, double x1, double y1 );

    // Creates a line with a different slope
    public T withSlope( T line, double rise, double run );

    // Creates a line with a different color
    public T withColor( T line, Color color );

    // Factory for creating lines in point-slope form.
    public static class PointSlopeLineFactory implements LineFactory<PointSlopeLine> {

        public PointSlopeLine simplify( PointSlopeLine line ) {
            return line.simplified();
        }

        public PointSlopeLine withPoint( PointSlopeLine line, double x1, double y1 ) {
            return new PointSlopeLine( x1, y1, line.rise, line.run, line.color );
        }

        public PointSlopeLine withSlope( PointSlopeLine line, double rise, double run ) {
            return new PointSlopeLine( line.x1, line.y1, rise, run, line.color );
        }

        public PointSlopeLine withColor( PointSlopeLine line, Color color ) {
            return new PointSlopeLine( line.x1, line.y1, line.rise, line.run, color );
        }
    }

    // Factory for creating lines in slope-intercept form.
    public static class SlopeInterceptLineFactory implements LineFactory<SlopeInterceptLine> {

        public SlopeInterceptLine simplify( SlopeInterceptLine line ) {
            PointSlopeLine psLine = line.simplified();
            assert ( psLine.x1 == 0 ); //TODO would be nice if we could avoid this
            return new SlopeInterceptLine( psLine.rise, psLine.run, psLine.y1, psLine.color );
        }

        public SlopeInterceptLine withPoint( SlopeInterceptLine line, double x1, double y1 ) {
            assert ( x1 == 0 ); //TODO would be nice if we could avoid this
            return new SlopeInterceptLine( line.rise, line.run, y1, line.color );
        }

        public SlopeInterceptLine withSlope( SlopeInterceptLine line, double rise, double run ) {
            return new SlopeInterceptLine( rise, run, line.y1, line.color );
        }

        public SlopeInterceptLine withColor( SlopeInterceptLine line, Color color ) {
            return new SlopeInterceptLine( line.rise, line.run, line.y1, color );
        }
    }
}
