// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.linegraphing.slopeintercept.model;

import java.awt.geom.Point2D;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.model.Resettable;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.DoubleRange;
import edu.colorado.phet.common.phetcommon.util.IntegerRange;
import edu.colorado.phet.common.phetcommon.util.ObservableList;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.linegraphing.common.LGColors;
import edu.colorado.phet.linegraphing.common.model.Graph;
import edu.colorado.phet.linegraphing.common.model.StraightLine;

/**
 * Model for the "Intro" module.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class SlopeInterceptModel implements Resettable {

    private static final int GRID_VIEW_UNITS = 530; // max dimension of the grid in the view
    private static final int GRID_MODEL_UNITS = 10; // dimensions of the grid in the model

    private static final IntegerRange X_RANGE = new IntegerRange( -GRID_MODEL_UNITS, GRID_MODEL_UNITS );
    private static final IntegerRange Y_RANGE = X_RANGE;
    private static final double MVT_SCALE = GRID_VIEW_UNITS / Math.max( X_RANGE.getLength(), Y_RANGE.getLength() ); // view units / model units

    public final ModelViewTransform mvt;
    public final Property<DoubleRange> riseRange, runRange, interceptRange;
    public final Property<StraightLine> interactiveLine;
    public final ObservableList<StraightLine> savedLines;
    public final ObservableList<StraightLine> standardLines;
    public final Graph graph;
    public final PointTool pointTool1, pointTool2;

    public SlopeInterceptModel() {

        mvt = ModelViewTransform.createOffsetScaleMapping( new Point2D.Double( 1.2 * GRID_VIEW_UNITS / 2, 1.25 * GRID_VIEW_UNITS / 2 ), MVT_SCALE, -MVT_SCALE ); // y is inverted

        riseRange = new Property<DoubleRange>( new DoubleRange( -10, 10, 2 ) );
        runRange = new Property<DoubleRange>( new DoubleRange( -10, 10, 2 ) );
        interceptRange = new Property<DoubleRange>( new DoubleRange( -10, 10, 2 ) );

        interactiveLine = new Property<StraightLine>( new StraightLine( riseRange.get().getDefault(), runRange.get().getDefault(), interceptRange.get().getDefault(),
                                                                        LGColors.INTERACTIVE_LINE, LGColors.INTERACTIVE_LINE ) ) {
            @Override public void set( StraightLine line ) {
                double newRise = line.rise;
                double newRun = line.run;
                // Skip over values that would result in slope=0/0
                if ( line.rise == 0 && line.run == 0 ) {
                    if ( get().run != 0 ) {
                        // run changed, skip over run = 0
                        newRun = ( get().run > 0 ) ? -1 : 1;
                    }
                    else if ( get().rise != 0 ) {
                        // rise changed, skip over rise = 0
                        newRise = ( get().rise > 0 ) ? -1 : 1;
                    }
                    else {
                        // rise and run haven't changed, arbitrarily move rise toward origin
                        newRise = ( get().yIntercept > 0 ) ? -1 : 1;
                    }
                }
                super.set( new StraightLine( newRise, newRun, line.yIntercept, line.color, line.highlightColor ) );
            }
        };
        savedLines = new ObservableList<StraightLine>();
        standardLines = new ObservableList<StraightLine>();

        graph = new Graph( X_RANGE, Y_RANGE );

        pointTool1 = new PointTool( new ImmutableVector2D( X_RANGE.getMax() + 2.75, Y_RANGE.getMin() - 2 ), interactiveLine, savedLines, standardLines );
        pointTool2 = new PointTool( pointTool1.location.get(), interactiveLine, savedLines, standardLines );

        // Dynamically set the rise and intercept ranges so that rise + intercept is constrained to the bounds of the graph
        interactiveLine.addObserver( new VoidFunction1<StraightLine>() {
            public void apply( StraightLine line ) {
                // Constrain rise to prevent slope=0/0
                final double minRise = ( line.run == 0 && line.yIntercept == graph.yRange.getMin() ) ? 1 : graph.yRange.getMin() - line.yIntercept;
                final double maxRise = ( line.run == 0 && line.yIntercept == graph.yRange.getMax() ) ? -1 : graph.yRange.getMax() - line.yIntercept;
                riseRange.set( new DoubleRange( minRise, maxRise ) );
                interceptRange.set( new DoubleRange( line.rise >= 0 ? graph.yRange.getMin() : graph.yRange.getMin() - line.rise,
                                                     line.rise <= 0 ? graph.yRange.getMax() : graph.yRange.getMax() - line.rise ) );
            }
        } );
    }

    public void reset() {
        riseRange.reset();
        runRange.reset();
        interceptRange.reset();
        interactiveLine.reset();
        savedLines.clear();
        standardLines.clear();
        pointTool1.reset();
        pointTool2.reset();
    }
}
