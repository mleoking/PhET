// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.linegraphing.common.model;

import java.awt.geom.Point2D;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.model.Resettable;
import edu.colorado.phet.common.phetcommon.util.IntegerRange;
import edu.colorado.phet.common.phetcommon.util.ObservableList;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;

/**
 * Base class model for the 2 tabs that deal with line forms (slope-intercept and point-slope).
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class LineFormsModel implements Resettable {

    private static final int GRID_MODEL_UNITS = 10; // dimensions of the grid in the model
    private static final int GRID_VIEW_UNITS = 530; // max dimension of the grid in the view

    private static final IntegerRange X_RANGE = new IntegerRange( -GRID_MODEL_UNITS, GRID_MODEL_UNITS );
    private static final IntegerRange Y_RANGE = X_RANGE;
    private static final double MVT_SCALE = GRID_VIEW_UNITS / Math.max( X_RANGE.getLength(), Y_RANGE.getLength() ); // view units / model units

    public final ModelViewTransform mvt;
    public final WellDefinedLineProperty interactiveLine;
    public final ObservableList<StraightLine> savedLines;
    public final ObservableList<StraightLine> standardLines;
    public final Graph graph;
    public final PointTool pointTool1, pointTool2;

    public LineFormsModel( WellDefinedLineProperty interactiveLine ) {
        this.mvt = ModelViewTransform.createOffsetScaleMapping( new Point2D.Double( 1.2 * GRID_VIEW_UNITS / 2, 1.25 * GRID_VIEW_UNITS / 2 ), MVT_SCALE, -MVT_SCALE ); // y is inverted
        this.interactiveLine = interactiveLine;
        this.savedLines = new ObservableList<StraightLine>();
        this.standardLines = new ObservableList<StraightLine>();
        this.graph = new Graph( X_RANGE, Y_RANGE );
        this.pointTool1 = new PointTool( new ImmutableVector2D( X_RANGE.getMin() + ( 0.75 * X_RANGE.getLength() ), Y_RANGE.getMin() - 3 ), interactiveLine, savedLines, standardLines );
        this.pointTool2 = new PointTool( new ImmutableVector2D( X_RANGE.getMin() + ( 0.25 * X_RANGE.getLength() ), pointTool1.location.get().getY() ), interactiveLine, savedLines, standardLines );
    }

    public void reset() {
        interactiveLine.reset();
        savedLines.clear();
        standardLines.clear();
        pointTool1.reset();
        pointTool2.reset();
    }
}
