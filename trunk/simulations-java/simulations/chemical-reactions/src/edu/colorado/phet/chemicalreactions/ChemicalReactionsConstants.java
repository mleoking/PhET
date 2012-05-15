// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.chemicalreactions;

import java.awt.Color;
import java.awt.Point;
import java.awt.geom.Dimension2D;
import java.awt.geom.Point2D;

import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.umd.cs.piccolo.util.PDimension;

public class ChemicalReactionsConstants {
    public static final Dimension2D STAGE_SIZE = new PDimension( 1008, 709 ); // change height to 679 once we have another tab
    public static final Color CANVAS_BACKGROUND_COLOR = new Color( 198, 226, 246 );
    public static final Color PLAY_AREA_BACKGROUND_COLOR = Color.WHITE;

    public static final ModelViewTransform MODEL_VIEW_TRANSFORM = ModelViewTransform.createSinglePointScaleInvertedYMapping(
            new Point2D.Double( 0, 0 ),
            new Point( (int) Math.round( ChemicalReactionsConstants.STAGE_SIZE.getWidth() * 0.5 ),
                       (int) Math.round( ChemicalReactionsConstants.STAGE_SIZE.getHeight() * 0.5 ) ),
            0.3 ); // "Zoom factor" - smaller zooms out, larger zooms in.

    public static final ModelViewTransform BOX2D_MODEL_TRANSFORM = ModelViewTransform.createOffsetScaleMapping( new Point2D.Double(), 100, -100 );

    public static final Color KIT_BACKGROUND = Color.WHITE; // kit area background
    public static final Color KIT_BORDER = Color.BLACK; // border around the kit area

    public static final Color KIT_ARROW_BACKGROUND_ENABLED = Color.YELLOW; // kit next/prev arrow background
    public static final Color KIT_ARROW_BORDER_ENABLED = Color.BLACK; // kit next/prev arrow border

    // size of our model area. model centered around 0 (and there are a few dependencies on this)
    public static final PDimension MODEL_SIZE = new PDimension( MODEL_VIEW_TRANSFORM.viewToModelDeltaX( STAGE_SIZE.getWidth() ), // don't simplify. working around MVT bug
                                                                Math.abs( MODEL_VIEW_TRANSFORM.viewToModelDeltaY( STAGE_SIZE.getHeight() ) ) );

    // padding between simulation edges and kit/collection areas
    public static final double VIEW_PADDING = 18;
    public static final double MODEL_PADDING = MODEL_VIEW_TRANSFORM.viewToModelDeltaX( VIEW_PADDING );
    public static final int MODEL_ITERATIONS_PER_FRAME = 10;

    public static final boolean ENABLE_BOX2D_DEBUG_DRAW = true;
}
