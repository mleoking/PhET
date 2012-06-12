// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.linegraphing.pointslope.model;

import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.DoubleRange;
import edu.colorado.phet.common.phetcommon.util.IntegerRange;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.linegraphing.common.LGColors;
import edu.colorado.phet.linegraphing.common.model.LineFormsModel;
import edu.colorado.phet.linegraphing.common.model.StraightLine;
import edu.colorado.phet.linegraphing.common.model.WellDefinedLineProperty;

/**
 * Model for the "Point Slope" module.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class PointSlopeModel extends LineFormsModel {

    public final Property<DoubleRange> riseRange, runRange, x1Range, y1Range; // ranges of things that the user can manipulate

    // Constructor with default values.
    public PointSlopeModel() {
        this( 10, 3, 4, 1, 2 );
    }

    // Constructs a square grid, with uniform quadrant sizes.
    private PointSlopeModel( int quadrantSize, int rise, int run, int x1, int y1 ) {
        this( new IntegerRange( -quadrantSize, quadrantSize ), new IntegerRange( -quadrantSize, quadrantSize ), rise, run, x1, y1 );
    }

    /*
     * Constructor.
     * @param xRange range of the x axis
     * @param yRange range of the y axis
     * @param rise initial rise value
     * @param run initial run value
     * @param x1 initial x1 value
     * @param y1 initial y1 value
     */
    private PointSlopeModel( final IntegerRange xRange, final IntegerRange yRange, int rise, int run, int x1, int y1 ) {
        super( xRange, yRange,
               new WellDefinedLineProperty( new StraightLine( rise, run, x1, y1, LGColors.INTERACTIVE_LINE, LGColors.INTERACTIVE_LINE ) ) );

        assert( xRange.contains( x1 ) );
        assert( yRange.contains( y1 ) );

        this.riseRange = new Property<DoubleRange>( new DoubleRange( yRange.getMin(), yRange.getMax(), rise ) );
        this.runRange = new Property<DoubleRange>( new DoubleRange( xRange.getMin(), xRange.getMax(), run ) );
        this.x1Range = new Property<DoubleRange>( new DoubleRange( xRange.getMin(), xRange.getMax(), x1 ) );
        this.y1Range = new Property<DoubleRange>( new DoubleRange( yRange.getMin(), yRange.getMax(), y1 ) );

        // Dynamically adjust ranges so that variables are constrained to the bounds of the graph.
        interactiveLine.addObserver( new VoidFunction1<StraightLine>() {
            public void apply( StraightLine line ) {

                // rise
                final double minRise = yRange.getMin() - line.y1;
                final double maxRise = yRange.getMax() - line.y1;
                PointSlopeModel.this.riseRange.set( new DoubleRange( minRise, maxRise ) );

                // run
                final double minRun = xRange.getMin() - line.x1;
                final double maxRun = xRange.getMax() - line.x1;
                PointSlopeModel.this.runRange.set( new DoubleRange( minRun, maxRun ) );

                // x1
                final double minX1 = Math.max( xRange.getMin(), xRange.getMin() - line.run );
                final double maxX1 = Math.min( xRange.getMax(), xRange.getMax() - line.run );
                PointSlopeModel.this.x1Range.set( new DoubleRange( minX1, maxX1 ) );

                // y1
                final double minY1 = Math.max( yRange.getMin(), yRange.getMin() - line.rise );
                final double maxY1 = Math.min( yRange.getMax(), yRange.getMax() - line.rise );
                PointSlopeModel.this.y1Range.set( new DoubleRange( minY1, maxY1 ) );
            }
        } );
    }

    @Override public void reset() {
        super.reset();
        riseRange.reset();
        runRange.reset();
        x1Range.reset();
        y1Range.reset();
    }
}