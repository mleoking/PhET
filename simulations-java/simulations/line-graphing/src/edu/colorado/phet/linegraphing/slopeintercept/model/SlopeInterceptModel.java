// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.linegraphing.slopeintercept.model;

import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.DoubleRange;
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

    private static final DoubleRange RISE_RANGE = new DoubleRange( -10, 10, 2 );
    private static final DoubleRange RUN_RANGE = new DoubleRange( -10, 10, 3 );
    private static final DoubleRange INTERCEPT_RANGE = new DoubleRange( -10, 10, 1 );

    public final Property<DoubleRange> riseRange, runRange, interceptRange;

    public SlopeInterceptModel() {
        this( RISE_RANGE, RUN_RANGE, INTERCEPT_RANGE );
    }

    private SlopeInterceptModel( DoubleRange riseRange, DoubleRange runRange, DoubleRange interceptRange ) {
        super( new WellDefinedLineProperty( new StraightLine( riseRange.getDefault(), runRange.getDefault(), interceptRange.getDefault(),
                                                           LGColors.INTERACTIVE_LINE, LGColors.INTERACTIVE_LINE ) ) );

        this.riseRange = new Property<DoubleRange>( riseRange );
        this.runRange = new Property<DoubleRange>( runRange );
        this.interceptRange = new Property<DoubleRange>( interceptRange );

        // Dynamically adjust ranges so that variables are constrained to the bounds of the graph.
        interactiveLine.addObserver( new VoidFunction1<StraightLine>() {
            public void apply( StraightLine line ) {

                // rise
                final double minRise = graph.yRange.getMin() - line.yIntercept;
                final double maxRise = graph.yRange.getMax() - line.yIntercept;
                SlopeInterceptModel.this.riseRange.set( new DoubleRange( minRise, maxRise ) );

                // y intercept
                final double minIntercept = ( line.rise >= 0 ) ? graph.yRange.getMin() : graph.yRange.getMin() - line.rise;
                final double maxIntercept = ( line.rise <= 0 ) ? graph.yRange.getMax() : graph.yRange.getMax() - line.rise;
                SlopeInterceptModel.this.interceptRange.set( new DoubleRange( minIntercept, maxIntercept ) );
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
