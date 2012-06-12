// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.linegraphing.pointslope.model;

import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.DoubleRange;
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

    private static final DoubleRange RISE_RANGE = new DoubleRange( -10, 10, 3 );
    private static final DoubleRange RUN_RANGE = new DoubleRange( -10, 10, 4 );
    private static final DoubleRange X1_RANGE = new DoubleRange( -10, 10, 1 );
    private static final DoubleRange Y1_RANGE = new DoubleRange( -10, 10, 2 );

    public final Property<DoubleRange> riseRange, runRange, x1Range, y1Range;

    public PointSlopeModel() {
        this( RISE_RANGE, RUN_RANGE, X1_RANGE, Y1_RANGE );
    }

    private PointSlopeModel( DoubleRange riseRange, DoubleRange runRange, DoubleRange x1Range, DoubleRange y1Range ) {
        super( new WellDefinedLineProperty( new StraightLine( riseRange.getDefault(), runRange.getDefault(), x1Range.getDefault(), y1Range.getDefault(),
                                                           LGColors.INTERACTIVE_LINE, LGColors.INTERACTIVE_LINE ) ) );
        this.riseRange = new Property<DoubleRange>( riseRange );
        this.runRange = new Property<DoubleRange>( runRange );
        this.x1Range = new Property<DoubleRange>( x1Range );
        this.y1Range = new Property<DoubleRange>( y1Range );

        // Dynamically adjust ranges so that variables are constrained to the bounds of the graph.
        interactiveLine.addObserver( new VoidFunction1<StraightLine>() {
            public void apply( StraightLine line ) {

                // rise
                final double minRise = graph.yRange.getMin() - line.y1;
                final double maxRise = graph.yRange.getMax() - line.y1;
                PointSlopeModel.this.riseRange.set( new DoubleRange( minRise, maxRise ) );

                // run
                final double minRun = graph.xRange.getMin() - line.x1;
                final double maxRun = graph.xRange.getMax() - line.x1;
                PointSlopeModel.this.runRange.set( new DoubleRange( minRun, maxRun ) );

                // x1
                final double minX1 = Math.max( graph.xRange.getMin(), graph.xRange.getMin() - line.run );
                final double maxX1 = Math.min( graph.xRange.getMax(), graph.xRange.getMax() - line.run );
                PointSlopeModel.this.x1Range.set( new DoubleRange( minX1, maxX1 ) );

                // y1
                final double minY1 = Math.max( graph.yRange.getMin(), graph.yRange.getMin() - line.rise );
                final double maxY1 = Math.min( graph.yRange.getMax(), graph.yRange.getMax() - line.rise );
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