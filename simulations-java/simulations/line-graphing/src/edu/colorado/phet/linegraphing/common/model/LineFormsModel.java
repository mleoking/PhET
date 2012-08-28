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
import edu.colorado.phet.linegraphing.common.model.PointTool.Orientation;

/**
 * Base class model for the "Slope-Intercept" and "Point-Slope" modules.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public abstract class LineFormsModel<T extends PointSlopeLine> implements Resettable {

    protected static final int GRAPH_SIZE = 10;
    private static final int GRID_VIEW_UNITS = 530; // max dimension (width or height) of the grid in the view

    public final Property<DoubleRange> riseRange, runRange, x1Range, y1Range; // ranges of things that the user can manipulate

    public final ModelViewTransform mvt; // transform between model and view coordinate frames
    public final Property<T> interactiveLine; // the line that can be manipulated by the user
    public final ObservableList<T> savedLines; // lines that have been saved by the user
    public final ObservableList<T> standardLines; // standard lines (eg, y=x) that are available for viewing
    public final Graph graph; // the graph that plots the lines
    public final PointTool pointTool1, pointTool2; // tools for measuring points on the graph

    // For use by point-slope subclass.
    protected LineFormsModel( T interactiveLine ) {
        this( interactiveLine, new DoubleRange( -GRAPH_SIZE, GRAPH_SIZE ) );
    }

    /*
     * For use by slope-intercept subclass.
     * Slope-intercept is a specialization of point-slope form, having x1 fixed at zero.
     */
    protected LineFormsModel( T interactiveLine, DoubleRange x1Range ) {
        this( interactiveLine,
              new DoubleRange( -GRAPH_SIZE, GRAPH_SIZE ), // rise
              new DoubleRange( -GRAPH_SIZE, GRAPH_SIZE ), // run
              x1Range, // x1
              new DoubleRange( -GRAPH_SIZE, GRAPH_SIZE ), // y1
              new IntegerRange( -GRAPH_SIZE, GRAPH_SIZE ), // x axis
              new IntegerRange( -GRAPH_SIZE, GRAPH_SIZE ) // y axis
        );
    }

    /**
     * Constructor.
     *
     * @param interactiveLine line that the user can manipulate
     * @param riseRange       range of the slope's y component
     * @param runRange        range of the slope's x component
     * @param x1Range         range of the point's x component
     * @param y1Range         range of the point's y component
     * @param xRange          range of the x axis
     * @param yRange          range of the y axis
     */
    private LineFormsModel( T interactiveLine,
                            DoubleRange riseRange, DoubleRange runRange, DoubleRange x1Range, DoubleRange y1Range,
                            final IntegerRange xRange, final IntegerRange yRange ) {

        // ranges
        this.riseRange = new Property<DoubleRange>( riseRange );
        this.runRange = new Property<DoubleRange>( runRange );
        this.x1Range = new Property<DoubleRange>( x1Range );
        this.y1Range = new Property<DoubleRange>( y1Range );

        // graph
        this.graph = new Graph( xRange, yRange );

        // lines
        this.interactiveLine = new Property<T>( interactiveLine );
        this.savedLines = new ObservableList<T>();
        this.standardLines = new ObservableList<T>();

        // model-view transform
        final double mvtScale = GRID_VIEW_UNITS / Math.max( xRange.getLength(), yRange.getLength() ); // view units / model units
        this.mvt = ModelViewTransform.createOffsetScaleMapping( new Point2D.Double( 1.2 * GRID_VIEW_UNITS / 2, 1.25 * GRID_VIEW_UNITS / 2 ), mvtScale, -mvtScale ); // y is inverted

        // Observable collection of all lines, required by point tool.
        final ObservableList<PointSlopeLine> allLines = new ObservableList<PointSlopeLine>();
        {
            this.interactiveLine.addObserver( new ChangeObserver<T>() {
                public void update( T newLine, T oldLine ) {
                    allLines.remove( oldLine ); // remove first, because on observer registration oldLine == newLine
                    allLines.add( newLine ); // add interactive line to end, so we find it last
                }
            } );

            savedLines.addElementAddedObserver( new VoidFunction1<T>() {
                public void apply( T line ) {
                    allLines.add( 0, line ); // add saved lines to front, so we find them first
                }
            } );
            savedLines.addElementRemovedObserver( new VoidFunction1<T>() {
                public void apply( T line ) {
                    boolean removed = allLines.remove( line );
                    assert ( removed );
                }
            } );

            standardLines.addElementAddedObserver( new VoidFunction1<T>() {
                public void apply( T line ) {
                    allLines.add( 0, line ); // add standard lines to front, so we find them first
                }
            } );
            standardLines.addElementRemovedObserver( new VoidFunction1<T>() {
                public void apply( T line ) {
                    boolean removed = allLines.remove( line );
                    assert ( removed );
                }
            } );
        }

        // point tools
        this.pointTool1 = new PointTool( new Vector2D( xRange.getMin() + ( 0.65 * xRange.getLength() ), yRange.getMin() - 0.5 ), Orientation.UP, allLines );
        this.pointTool2 = new PointTool( new Vector2D( xRange.getMin() + ( 0.90 * xRange.getLength() ), yRange.getMin() - 3 ), Orientation.DOWN, allLines );

        // Dynamically adjust ranges so that variables are constrained to the bounds of the graph.
        this.interactiveLine.addObserver( new VoidFunction1<T>() {
            public void apply( T line ) {

                // rise
                final double minRise = yRange.getMin() - line.y1;
                final double maxRise = yRange.getMax() - line.y1;
                LineFormsModel.this.riseRange.set( new DoubleRange( minRise, maxRise ) );

                // run
                final double minRun = xRange.getMin() - line.x1;
                final double maxRun = xRange.getMax() - line.x1;
                LineFormsModel.this.runRange.set( new DoubleRange( minRun, maxRun ) );

                // x1
                final double minX1 = Math.max( xRange.getMin(), xRange.getMin() - line.run );
                final double maxX1 = Math.min( xRange.getMax(), xRange.getMax() - line.run );
                LineFormsModel.this.x1Range.set( new DoubleRange( minX1, maxX1 ) );

                // y1
                final double minY1 = Math.max( yRange.getMin(), yRange.getMin() - line.rise );
                final double maxY1 = Math.min( yRange.getMax(), yRange.getMax() - line.rise );
                LineFormsModel.this.y1Range.set( new DoubleRange( minY1, maxY1 ) );
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
