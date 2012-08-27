// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.linegraphing.slopeintercept.model;

import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.DoubleRange;
import edu.colorado.phet.common.phetcommon.util.IntegerRange;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.linegraphing.common.LGColors;
import edu.colorado.phet.linegraphing.common.model.LineFormsModel;
import edu.colorado.phet.linegraphing.common.model.StraightLine;

/**
 * Model for the "Slope-Intercept" module.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class SlopeInterceptModel extends LineFormsModel {

    public final Property<DoubleRange> riseRange, runRange, yInterceptRange; // ranges of things that the user can manipulate

    // Constructor with default values.
    public SlopeInterceptModel() {
        this( 10, new StraightLine( 2, 3, 1, LGColors.INTERACTIVE_LINE ) );
    }

    /**
     * Constructs a square grid, with uniform quadrant sizes.
     * @param quadrantSize number of cells in vertical or horizontal dimension of each quadrant
     * @param interactiveLine default interactive line
     */
    private SlopeInterceptModel( int quadrantSize, StraightLine interactiveLine ) {
        this( new IntegerRange( -quadrantSize, quadrantSize ), new IntegerRange( -quadrantSize, quadrantSize ), interactiveLine );
    }

    /*
     * Constructor.
     * @param xRange range of the x axis
     * @param yRange range of the y axis
     * @param rise initial rise value
     * @param run initial run value
     * @param yIntercept initial yIntercept value
     */
    private SlopeInterceptModel( final IntegerRange xRange, final IntegerRange yRange, StraightLine interactiveLine ) {
        super( xRange, yRange, interactiveLine );

        assert( interactiveLine.x1 == 0 ); // line is in slope intercept form
        assert( yRange.contains( interactiveLine.y1 ) );

        this.riseRange = new Property<DoubleRange>( new DoubleRange( yRange.getMin(), yRange.getMax(), interactiveLine.rise ) );
        this.runRange = new Property<DoubleRange>( new DoubleRange( xRange.getMin(), xRange.getMax(), interactiveLine.run ) );
        this.yInterceptRange = new Property<DoubleRange>( new DoubleRange( yRange.getMin(), yRange.getMax(), interactiveLine.y1 ) );

        // Dynamically adjust ranges so that variables are constrained to the bounds of the graph.
        this.interactiveLine.addObserver( new VoidFunction1<StraightLine>() {
            public void apply( StraightLine line ) {

                // rise
                final double minRise = yRange.getMin() - line.y1;
                final double maxRise = yRange.getMax() - line.y1;
                riseRange.set( new DoubleRange( minRise, maxRise ) );

                // y yIntercept
                final double minIntercept = ( line.rise >= 0 ) ? yRange.getMin() : yRange.getMin() - line.rise;
                final double maxIntercept = ( line.rise <= 0 ) ? yRange.getMax() : yRange.getMax() - line.rise;
                yInterceptRange.set( new DoubleRange( minIntercept, maxIntercept ) );
            }
        } );
    }

    @Override public void reset() {
        super.reset();
        riseRange.reset();
        runRange.reset();
        yInterceptRange.reset();
    }
}
