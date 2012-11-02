// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.linegraphing.common.model;

import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.DoubleRange;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.linegraphing.common.LGColors;
import edu.colorado.phet.linegraphing.common.LGConstants;

/**
 * Model for the "Point-Slope" tab.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class PointSlopeModel extends LineFormsModel {

    public final Property<DoubleRange> x1Range, y1Range, riseRange, runRange; // ranges of things that the user can manipulate

    public PointSlopeModel() {
        this( Line.createPointSlope( 1, 2, 3, 4, LGColors.INTERACTIVE_LINE ) );
    }

    private PointSlopeModel( Line interactiveLine ) {
        super( interactiveLine );

        // ranges
        x1Range = new Property<DoubleRange>( new DoubleRange( LGConstants.Y_AXIS_RANGE ) );
        y1Range = new Property<DoubleRange>( new DoubleRange( LGConstants.Y_AXIS_RANGE ) );
        riseRange = new Property<DoubleRange>( new DoubleRange( LGConstants.Y_AXIS_RANGE ) );
        runRange = new Property<DoubleRange>( new DoubleRange( LGConstants.X_AXIS_RANGE ) );

        // Dynamically adjust ranges so that variables are constrained to the bounds of the graph.
        this.interactiveLine.addObserver( new VoidFunction1<Line>() {
            public void apply( Line line ) {
                x1Range.set( LineParameterRange.x1( line, graph ) );
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
