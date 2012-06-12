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

    private static final IntegerRange X_RANGE = new IntegerRange( -10, 10 );
    private static final IntegerRange Y_RANGE = X_RANGE;

    public final Property<DoubleRange> riseRange, runRange, interceptRange;

    public SlopeInterceptModel() {
        this( X_RANGE, Y_RANGE, 2, 3, 1 );
    }

    private SlopeInterceptModel( final IntegerRange xRange, final IntegerRange yRange, int rise, int run, int intercept ) {
        super( xRange, yRange,
               new WellDefinedLineProperty( new StraightLine( rise, run, intercept, LGColors.INTERACTIVE_LINE, LGColors.INTERACTIVE_LINE ) ) );

        assert( yRange.contains( intercept ) );

        this.riseRange = new Property<DoubleRange>( new DoubleRange( yRange.getMin(), yRange.getMax(), rise ) );
        this.runRange = new Property<DoubleRange>( new DoubleRange( xRange.getMin(), xRange.getMax(), run ) );
        this.interceptRange = new Property<DoubleRange>( new DoubleRange( yRange.getMin(), yRange.getMax(), intercept ) );

        // Dynamically adjust ranges so that variables are constrained to the bounds of the graph.
        interactiveLine.addObserver( new VoidFunction1<StraightLine>() {
            public void apply( StraightLine line ) {

                // rise
                final double minRise = yRange.getMin() - line.yIntercept;
                final double maxRise = yRange.getMax() - line.yIntercept;
                riseRange.set( new DoubleRange( minRise, maxRise ) );

                // y intercept
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
