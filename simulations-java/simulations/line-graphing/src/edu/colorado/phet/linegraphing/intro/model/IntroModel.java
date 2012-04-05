// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.linegraphing.intro.model;

import java.awt.geom.Point2D;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.model.Resettable;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.IntegerRange;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;

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

    public static final IntegerRange RISE_RANGE = new IntegerRange( -10, 10 );
    public static final IntegerRange RUN_RANGE = new IntegerRange( -10, 10 );
    public static final IntegerRange INTERCEPT_RANGE = new IntegerRange( -10, 10 );

    public final ModelViewTransform mvt;
    public final Property<SlopeInterceptLine> interactiveLine;
    public final SlopeInterceptLine yEqualsXLine, yEqualsNegativeXLine;
    public final LineGraph graph;
    public final Property<ImmutableVector2D> pointToolLocation;

    public IntroModel() {

        mvt = ModelViewTransform.createOffsetScaleMapping( new Point2D.Double( 0, 0 ), MVT_SCALE, -MVT_SCALE ); // y is inverted
        interactiveLine = new Property<SlopeInterceptLine>( new SlopeInterceptLine( 5, 5, 2 ) );
        yEqualsXLine = new SlopeInterceptLine( 1, 1, 0 );
        yEqualsNegativeXLine = new SlopeInterceptLine( 1, -1, 0 );
        graph = new LineGraph( X_RANGE.getMin(), X_RANGE.getMax(), Y_RANGE.getMin(), Y_RANGE.getMax() );
        pointToolLocation = new Property<ImmutableVector2D>( new ImmutableVector2D( 0, 0 ) );
    }

    public void reset() {
        interactiveLine.reset();
        pointToolLocation.reset();
    }
}
