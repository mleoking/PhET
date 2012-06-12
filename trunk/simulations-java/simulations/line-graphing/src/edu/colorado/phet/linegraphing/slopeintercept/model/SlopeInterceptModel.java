// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.linegraphing.slopeintercept.model;

import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.DoubleRange;
import edu.colorado.phet.common.phetcommon.util.IntegerRange;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.linegraphing.common.LGColors;
import edu.colorado.phet.linegraphing.common.model.LineFormsModel;
import edu.colorado.phet.linegraphing.common.model.StraightLine;
import edu.colorado.phet.linegraphing.common.model.WellDefinedLineProperty;

/**
 * Model for the "Slope-Intercept" module.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class SlopeInterceptModel extends LineFormsModel {

    public final Property<DoubleRange> riseRange, runRange, interceptRange; // ranges of things that the user can manipulate

    // Constructor with default values.
    public SlopeInterceptModel() {
        this( 10, 2, 3, 1 );
    }

    // Constructs a square grid, with uniform quadrant sizes.
    private SlopeInterceptModel( int quadrantSize, int rise, int run, int yIntercept ) {
        this( new IntegerRange( -quadrantSize, quadrantSize ), new IntegerRange( -quadrantSize, quadrantSize ), rise, run, yIntercept );
    }

    /*
     * Constructor.
     * @param xRange range of the x axis
     * @param yRange range of the y axis
     * @param rise initial rise value
     * @param run initial run value
     * @param yIntercept initial yIntercept value
     */
    private SlopeInterceptModel( final IntegerRange xRange, final IntegerRange yRange, int rise, int run, int yIntercept ) {
        super( xRange, yRange,
               new WellDefinedLineProperty( new StraightLine( rise, run, yIntercept, LGColors.INTERACTIVE_LINE, LGColors.INTERACTIVE_LINE ) ) );

        assert( yRange.contains( yIntercept ) );

        this.riseRange = new Property<DoubleRange>( new DoubleRange( yRange.getMin(), yRange.getMax(), rise ) );
        this.runRange = new Property<DoubleRange>( new DoubleRange( xRange.getMin(), xRange.getMax(), run ) );
        this.interceptRange = new Property<DoubleRange>( new DoubleRange( yRange.getMin(), yRange.getMax(), yIntercept ) );

        // Dynamically adjust ranges so that variables are constrained to the bounds of the graph.
        interactiveLine.addObserver( new VoidFunction1<StraightLine>() {
            public void apply( StraightLine line ) {

                // rise
                final double minRise = yRange.getMin() - line.yIntercept;
                final double maxRise = yRange.getMax() - line.yIntercept;
                riseRange.set( new DoubleRange( minRise, maxRise ) );

                // y yIntercept
                final double minIntercept = ( line.rise >= 0 ) ? yRange.getMin() : yRange.getMin() - line.rise;
                final double maxIntercept = ( line.rise <= 0 ) ? yRange.getMax() : yRange.getMax() - line.rise;
                interceptRange.set( new DoubleRange( minIntercept, maxIntercept ) );
            }
        } );
    }

    @Override public void reset() {
        super.reset();
        riseRange.reset();
        runRange.reset();
        interceptRange.reset();
    }
}
