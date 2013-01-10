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
        this.pointTool1 = new PointTool( new Vector2D( graph.xRange.getMin() + ( 0.35 * graph.xRange.getLength() ), graph.yRange.getMin() - 0.25 ), Orientation.UP, allLines );
        this.pointTool2 = new PointTool( new Vector2D( graph.xRange.getMin() + ( 0.65 * graph.xRange.getLength() ), graph.yRange.getMin() - 2.75 ), Orientation.DOWN, allLines );
    }

    public void reset() {
        interactiveLine.reset();
        savedLines.clear();
        standardLines.clear();
        pointTool1.reset();
        pointTool2.reset();
    }
}
