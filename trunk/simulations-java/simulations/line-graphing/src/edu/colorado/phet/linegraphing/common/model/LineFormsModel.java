// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.linegraphing.common.model;

import java.awt.geom.Point2D;

import edu.colorado.phet.common.phetcommon.math.vector.Vector2D;
import edu.colorado.phet.common.phetcommon.model.Resettable;
import edu.colorado.phet.common.phetcommon.model.property.ChangeObserver;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.DoubleRange;
import edu.colorado.phet.common.phetcommon.util.IntegerRange;
import edu.colorado.phet.common.phetcommon.util.ObservableList;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.linegraphing.common.LGColors;
import edu.colorado.phet.linegraphing.common.LGConstants;
import edu.colorado.phet.linegraphing.common.model.PointTool.Orientation;

/**
 * Base class model for the "Slope-Intercept" and "Point-Slope" modules.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class LineFormsModel implements Resettable {

    private static final int GRID_VIEW_UNITS = 530; // max dimension (width or height) of the grid in the view

    public final Graph graph; // the graph that plots the lines
    public final ModelViewTransform mvt; // transform between model and view coordinate frames
    public final Property<DoubleRange> x1Range, y1Range, riseRange, runRange; // ranges of things that the user can manipulate
    public final Property<Line> interactiveLine; // the line that can be manipulated by the user
    public final ObservableList<Line> savedLines; // lines that have been saved by the user
    public final ObservableList<Line> standardLines; // standard lines (eg, y=x) that are available for viewing
    public final PointTool pointTool1, pointTool2; // tools for measuring points on the graph

    /*
     * Creates a model in point-slope form: (y - y1) = m(x - x1)
     */
    public static LineFormsModel createPointSlope() {
        return new LineFormsModel( Line.createPointSlope( 1, 2, 3, 4, LGColors.INTERACTIVE_LINE ),
                                   new DoubleRange( LGConstants.X_AXIS_RANGE ) /* x1 is variable */ );
    }

    /*
     * Creates a model in slope-intercept form: y = mx + b
     * Note that slope-intercept is a specialization of point-slope form, having x1 fixed at zero.
     */
    public static LineFormsModel createSlopeIntercept() {
        return new LineFormsModel( Line.createSlopeIntercept( 2, 3, 1, LGColors.INTERACTIVE_LINE ),
                                   new DoubleRange( 0, 0 ) /* x1 is fixed at zero */ );
    }

    /**
     * Constructor is private, use factory methods to instantiate.
     *
     * @param interactiveLine line that the user can manipulate
     * @param x1Range         range of the point's x component
     */
    private LineFormsModel( Line interactiveLine, DoubleRange x1Range ) {

        // graph
        this.graph = new Graph( LGConstants.X_AXIS_RANGE, LGConstants.Y_AXIS_RANGE );

        // model-view transform
        final double mvtScale = GRID_VIEW_UNITS / Math.max( graph.xRange.getLength(), graph.yRange.getLength() ); // view units / model units
        this.mvt = ModelViewTransform.createOffsetScaleMapping( new Point2D.Double( 1.2 * GRID_VIEW_UNITS / 2, 1.25 * GRID_VIEW_UNITS / 2 ), mvtScale, -mvtScale ); // y is inverted

        // ranges
        this.x1Range = new Property<DoubleRange>( x1Range );
        this.y1Range = new Property<DoubleRange>( new DoubleRange( LGConstants.Y_AXIS_RANGE ) );
        this.riseRange = new Property<DoubleRange>( new DoubleRange( LGConstants.Y_AXIS_RANGE ) );
        this.runRange = new Property<DoubleRange>( new DoubleRange( LGConstants.X_AXIS_RANGE ) );

        // lines
        this.interactiveLine = new Property<Line>( interactiveLine );
        this.savedLines = new ObservableList<Line>();
        this.standardLines = new ObservableList<Line>();

        // Observable collection of all lines, required by point tool.
        final ObservableList<Line> allLines = new ObservableList<Line>();
        {
            this.interactiveLine.addObserver( new ChangeObserver<Line>() {
                public void update( Line newLine, Line oldLine ) {
                    allLines.remove( oldLine ); // remove first, because on observer registration oldLine == newLine
                    allLines.add( newLine ); // add interactive line to end, so we find it last
                }
            } );

            savedLines.addElementAddedObserver( new VoidFunction1<Line>() {
                public void apply( Line line ) {
                    allLines.add( 0, line ); // add saved lines to front, so we find them first
                }
            } );
            savedLines.addElementRemovedObserver( new VoidFunction1<Line>() {
                public void apply( Line line ) {
                    boolean removed = allLines.remove( line );
                    assert ( removed );
                }
            } );

            standardLines.addElementAddedObserver( new VoidFunction1<Line>() {
                public void apply( Line line ) {
                    allLines.add( 0, line ); // add standard lines to front, so we find them first
                }
            } );
            standardLines.addElementRemovedObserver( new VoidFunction1<Line>() {
                public void apply( Line line ) {
                    boolean removed = allLines.remove( line );
                    assert ( removed );
                }
            } );
        }

        // point tools
        this.pointTool1 = new PointTool( new Vector2D( graph.xRange.getMin() + ( 0.65 * graph.xRange.getLength() ), graph.yRange.getMin() - 0.25 ), Orientation.UP, allLines );
        this.pointTool2 = new PointTool( new Vector2D( graph.xRange.getMin() + ( 0.90 * graph.xRange.getLength() ), graph.yRange.getMin() - 2.75 ), Orientation.DOWN, allLines );

        // Dynamically adjust ranges so that variables are constrained to the bounds of the graph.
        this.interactiveLine.addObserver( new VoidFunction1<Line>() {
            public void apply( Line line ) {

                // range of the graph axes
                final int xMin = graph.xRange.getMin();
                final int xMax = graph.xRange.getMax();
                final int yMin = graph.yRange.getMin();
                final int yMax = graph.yRange.getMax();

                // x1 should not be changed for slope-intercept form.
                if ( !( LineFormsModel.this.x1Range.get().getMin() == 0d && LineFormsModel.this.x1Range.get().getMax() == 0d ) ) {
                    final double x1Min = Math.max( xMin, xMin - line.run );
                    final double x1Max = Math.min( xMax, xMax - line.run );
                    LineFormsModel.this.x1Range.set( new DoubleRange( x1Min, x1Max ) );
                }

                // y1
                final double y1Min = Math.max( yMin, yMin - line.rise );
                final double y1Max = Math.min( yMax, yMax - line.rise );
                LineFormsModel.this.y1Range.set( new DoubleRange( y1Min, y1Max ) );

                // rise
                final double riseMin = yMin - line.y1;
                final double riseMax = yMax - line.y1;
                LineFormsModel.this.riseRange.set( new DoubleRange( riseMin, riseMax ) );

                // run
                final double runMin = xMin - line.x1;
                final double runMax = xMax - line.x1;
                LineFormsModel.this.runRange.set( new DoubleRange( runMin, runMax ) );
            }
        } );
    }

    public void reset() {
        interactiveLine.reset();
        savedLines.clear();
        standardLines.clear();
        pointTool1.reset();
        pointTool2.reset();
        // ranges will be reset when interactiveLine is reset
    }
}
