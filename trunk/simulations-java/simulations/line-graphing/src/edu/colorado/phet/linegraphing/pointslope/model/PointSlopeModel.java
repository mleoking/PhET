// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.linegraphing.pointslope.model;

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
import edu.colorado.phet.linegraphing.common.model.PointTool;
import edu.colorado.phet.linegraphing.common.model.StraightLine;

//TODO lots of duplication with SlopeInterceptModel
/**
 * Model for the "Point Slope" module.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class PointSlopeModel implements Resettable {

    private static final int GRID_VIEW_UNITS = 530; // max dimension of the grid in the view
    private static final int GRID_MODEL_UNITS = 10; // dimensions of the grid in the model

    private static final IntegerRange X_RANGE = new IntegerRange( -GRID_MODEL_UNITS, GRID_MODEL_UNITS );
    private static final IntegerRange Y_RANGE = X_RANGE;
    private static final double MVT_SCALE = GRID_VIEW_UNITS / Math.max( X_RANGE.getLength(), Y_RANGE.getLength() ); // view units / model units

    public final ModelViewTransform mvt;
    public final Property<DoubleRange> riseRange, runRange, x1Range, y1Range;
    public final Property<StraightLine> interactiveLine;
    public final ObservableList<StraightLine> savedLines;
    public final ObservableList<StraightLine> standardLines;
    public final Graph graph;
    public final PointTool pointTool1, pointTool2;

    public PointSlopeModel() {

        mvt = ModelViewTransform.createOffsetScaleMapping( new Point2D.Double( 1.2 * GRID_VIEW_UNITS / 2, 1.25 * GRID_VIEW_UNITS / 2 ), MVT_SCALE, -MVT_SCALE ); // y is inverted

        riseRange = new Property<DoubleRange>( new DoubleRange( -10, 10, 2 ) );
        runRange = new Property<DoubleRange>( new DoubleRange( -10, 10, 2 ) );
        x1Range = new Property<DoubleRange>( new DoubleRange( -10, 10, -2 ) );
        y1Range = new Property<DoubleRange>( new DoubleRange( -10, 10, -2 ) );

        interactiveLine = new Property<StraightLine>( new StraightLine( riseRange.get().getDefault(), runRange.get().getDefault(), x1Range.get().getDefault(), y1Range.get().getDefault(),
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
                        newRise = ( get().y1 > 0 ) ? -1 : 1;
                    }
                }
                super.set( new StraightLine( newRise, newRun, line.x1,  line.y1, line.color, line.highlightColor ) );
            }
        };
        savedLines = new ObservableList<StraightLine>();
        standardLines = new ObservableList<StraightLine>();

        graph = new Graph( X_RANGE, Y_RANGE );

        pointTool1 = new PointTool( new ImmutableVector2D( X_RANGE.getMin() + ( 0.75 * X_RANGE.getLength() ), Y_RANGE.getMin() - 3 ), interactiveLine, savedLines, standardLines );
        pointTool2 = new PointTool( new ImmutableVector2D( X_RANGE.getMin() + ( 0.25 * X_RANGE.getLength() ), pointTool1.location.get().getY() ), interactiveLine, savedLines, standardLines );

        // Dynamically set ranges so that variables are constrained to the bounds of the graph.
        interactiveLine.addObserver( new VoidFunction1<StraightLine>() {
            public void apply( StraightLine line ) {

                // rise
                final double minRise = graph.yRange.getMin() - line.y1;
                final double maxRise = graph.yRange.getMax() - line.y1;
                riseRange.set( new DoubleRange( minRise, maxRise ) );

                // run
                final double minRun = graph.xRange.getMin() - line.x1;
                final double maxRun = graph.xRange.getMax() - line.x1;
                runRange.set( new DoubleRange( minRun, maxRun ) );

                // x1
                final double minX1 = Math.max( graph.xRange.getMin(), graph.xRange.getMin() - line.run );
                final double maxX1 = Math.min( graph.xRange.getMax(), graph.xRange.getMax() - line.run );
                x1Range.set( new DoubleRange( minX1, maxX1 ) );

                // y1
                final double minY1 = Math.max( graph.yRange.getMin(), graph.yRange.getMin() - line.rise );
                final double maxY1 = Math.min( graph.yRange.getMax(), graph.yRange.getMax() - line.rise );
                y1Range.set( new DoubleRange( minY1, maxY1 ) );
            }
        } );
    }

    public void reset() {
        riseRange.reset();
        runRange.reset();
        x1Range.reset();
        y1Range.reset();
        interactiveLine.reset();
        savedLines.clear();
        standardLines.clear();
        pointTool1.reset();
        pointTool2.reset();
    }
}