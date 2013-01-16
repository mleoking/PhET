// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.linegraphing.common.model;

import java.awt.geom.Point2D;

import edu.colorado.phet.common.phetcommon.math.vector.Vector2D;
import edu.colorado.phet.common.phetcommon.model.Resettable;
import edu.colorado.phet.common.phetcommon.model.property.ChangeObserver;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.ObservableList;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.linegraphing.common.LGConstants;
import edu.colorado.phet.linegraphing.common.model.PointTool.Orientation;

/**
 * Base class model for the "Slope", "Slope-Intercept" and "Point-Slope" models.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public abstract class LineFormsModel implements Resettable {

    private static final int GRID_VIEW_UNITS = 530; // max dimension (width or height) of the grid in view coordinates
    private static final Point2D ORIGIN_OFFSET = new Point2D.Double( 305, 320 ); // offset of the graph's origin in view coordinates

    public final Graph graph; // the graph that plots the lines
    public final ModelViewTransform mvt; // transform between model and view coordinate frames
    public final Property<Line> interactiveLine; // the line that can be manipulated by the user
    public final ObservableList<Line> savedLines; // lines that have been saved by the user
    public final ObservableList<Line> standardLines; // standard lines (eg, y=x) that are available for viewing
    public final PointTool pointTool1, pointTool2; // tools for measuring points on the graph

    /**
     * Constructor.
     *
     * @param interactiveLine line that the user can manipulate
     */
    protected LineFormsModel( Line interactiveLine ) {

        // graph
        this.graph = new Graph( LGConstants.X_AXIS_RANGE, LGConstants.Y_AXIS_RANGE );

        // model-view transform, created in the model because it's dependent on graph axes ranges
        final double mvtScale = GRID_VIEW_UNITS / Math.max( graph.xRange.getLength(), graph.yRange.getLength() ); // view units / model units
        this.mvt = ModelViewTransform.createOffsetScaleMapping( ORIGIN_OFFSET, mvtScale, -mvtScale ); // y is inverted

        // lines
        this.interactiveLine = new Property<Line>( interactiveLine );
        this.savedLines = new ObservableList<Line>();
        this.standardLines = new ObservableList<Line>();

        // Update the lines seen by the graph.
        VoidFunction1<Line> updateGraphLines = new VoidFunction1<Line>() {
            public void apply( Line line ) {
                graph.lines.clear();
                // add lines in the order that they would be rendered
                graph.lines.add( LineFormsModel.this.interactiveLine.get() );
                for ( Line l : savedLines ) {
                    graph.lines.add( l );
                }
                for ( Line l : standardLines ) {
                    graph.lines.add( l );
                }
            }
        };
        this.interactiveLine.addObserver( updateGraphLines );
        savedLines.addElementAddedObserver( updateGraphLines );
        savedLines.addElementRemovedObserver( updateGraphLines );
        standardLines.addElementAddedObserver( updateGraphLines );
        standardLines.addElementRemovedObserver( updateGraphLines );

        // point tools
        this.pointTool1 = new PointTool( new Vector2D( graph.xRange.getMin() + ( 0.35 * graph.xRange.getLength() ), graph.yRange.getMin() - 0.25 ), Orientation.UP, graph.lines );
        this.pointTool2 = new PointTool( new Vector2D( graph.xRange.getMin() + ( 0.65 * graph.xRange.getLength() ), graph.yRange.getMin() - 2.75 ), Orientation.DOWN, graph.lines );
    }

    public void reset() {
        interactiveLine.reset();
        savedLines.clear();
        standardLines.clear();
        pointTool1.reset();
        pointTool2.reset();
    }
}
