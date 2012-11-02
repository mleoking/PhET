// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.linegraphing.common.model;

import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.DoubleRange;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.linegraphing.common.LGColors;
import edu.colorado.phet.linegraphing.common.LGConstants;

//TODO subclass PointSlopeInterceptModel and provide a specialization of LineParameterRange that doesn't change x1 range

/**
 * Model for the "Point-Slope" tab.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class SlopeInterceptModel extends LineFormsModel {

    public final Property<DoubleRange> x1Range, y1Range, riseRange, runRange; // ranges of things that the user can manipulate

    public SlopeInterceptModel() {
        this( Line.createSlopeIntercept( 2, 3, 1, LGColors.INTERACTIVE_LINE ) );
    }

    private SlopeInterceptModel( Line interactiveLine ) {
        super( interactiveLine );

        // ranges
        x1Range = new Property<DoubleRange>( new DoubleRange( 0, 0 ) ); /* x1 is fixed at zero */
        y1Range = new Property<DoubleRange>( new DoubleRange( LGConstants.Y_AXIS_RANGE ) );
        riseRange = new Property<DoubleRange>( new DoubleRange( LGConstants.Y_AXIS_RANGE ) );
        runRange = new Property<DoubleRange>( new DoubleRange( LGConstants.X_AXIS_RANGE ) );

        // Dynamically adjust ranges so that variables are constrained to the bounds of the graph.
        this.interactiveLine.addObserver( new VoidFunction1<Line>() {
            public void apply( Line line ) {
                // x1 range should not be changed for slope-intercept form, x1 is fixed at zero.
                y1Range.set( LineParameterRange.y1( line, graph ) );
                riseRange.set( LineParameterRange.rise( line, graph ) );
                runRange.set( LineParameterRange.run( line, graph ) );
            }
        } );
    }

    @Override public void reset() {
        super.reset();
        // no need to reset ranges, they will be reset when interactiveLine is reset
    }
}