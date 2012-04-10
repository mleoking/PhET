// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.linegraphing.intro.model;

import java.awt.geom.Point2D;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.model.Resettable;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.IntegerRange;
import edu.colorado.phet.common.phetcommon.util.ObservableList;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.linegraphing.intro.model.SlopeInterceptLine.InteractiveLine;
import edu.colorado.phet.linegraphing.intro.model.SlopeInterceptLine.SavedLine;
import edu.colorado.phet.linegraphing.intro.model.SlopeInterceptLine.StandardLine;

/**
 * Model for the "Intro" module.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class IntroModel implements Resettable {

    private static final int GRID_VIEW_UNITS = 560; // max dimension of the grid in the view
    private static final int GRID_MODEL_UNITS = 10; // dimensions of the grid in the model

    private static final IntegerRange X_RANGE = new IntegerRange( -GRID_MODEL_UNITS, GRID_MODEL_UNITS );
    private static final IntegerRange Y_RANGE = X_RANGE;
    private static final double MVT_SCALE = GRID_VIEW_UNITS / Math.max( X_RANGE.getLength(), Y_RANGE.getLength() ); // view units / model units

    public static final IntegerRange RISE_RANGE = new IntegerRange( -10, 10, 5 );
    public static final IntegerRange RUN_RANGE = new IntegerRange( -10, 10, 5 );
    public static final IntegerRange INTERCEPT_RANGE = new IntegerRange( -10, 10, 2 );

    public final ModelViewTransform mvt;
    public final Property<InteractiveLine> interactiveLine;
    public final ObservableList<SavedLine> savedLines;
    public final ObservableList<StandardLine> standardLines;
    public final LineGraph graph;
    public final PointTool pointTool;

    public IntroModel() {
        mvt = ModelViewTransform.createOffsetScaleMapping( new Point2D.Double( 1.2 * GRID_VIEW_UNITS / 2, 1.25 * GRID_VIEW_UNITS / 2 ), MVT_SCALE, -MVT_SCALE ); // y is inverted
        interactiveLine = new Property<InteractiveLine>( new InteractiveLine( RISE_RANGE.getDefault(), RUN_RANGE.getDefault(), INTERCEPT_RANGE.getDefault() ) );
        savedLines = new ObservableList<SavedLine>();
        standardLines = new ObservableList<StandardLine>();
        graph = new LineGraph( X_RANGE, Y_RANGE );
        pointTool = new PointTool( new ImmutableVector2D( X_RANGE.getMax() + 2, Y_RANGE.getMin() ) );
    }

    public void reset() {
        interactiveLine.reset();
        savedLines.clear();
        standardLines.clear();
        pointTool.reset();
    }
}
