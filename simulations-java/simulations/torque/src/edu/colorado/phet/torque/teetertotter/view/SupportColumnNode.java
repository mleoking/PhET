// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.torque.teetertotter.view;

import java.awt.*;

import edu.colorado.phet.common.phetcommon.model.property.BooleanProperty;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.torque.teetertotter.model.SupportColumn;

/**
 * Node the represents the support columns in the view.
 *
 * @author John Blanco
 */
public class SupportColumnNode extends ModelObjectNode {
    private static final Color BASE_COLOR = new Color( 200, 200, 200 );

    public SupportColumnNode( final ModelViewTransform mvt, final SupportColumn supportColumn, final BooleanProperty supportColumnsActive ) {
        super( mvt, supportColumn, Color.LIGHT_GRAY );
        /*
//                new GradientPaint(
//                        -(float) supportColumn.getShape().getBounds2D().getWidth() / 2,
//                        0f,
//                        ColorUtils.brighterColor( BASE_COLOR, 0.5 ),
//                        (float) supportColumn.getShape().getBounds2D().getWidth() / 2,
//                        0f,
//                        ColorUtils.darkerColor( BASE_COLOR, 0.5 ) ) );
//        new GradientPaint(
//                (float) mvt.modelToViewY( supportColumn.getShape().getBounds2D().getMinX() ),
//                0f,
//                Color.YELLOW,
//                (float)  mvt.modelToViewY( supportColumn.getShape().getBounds2D().getMaxX() ),
//                0f,
//                Color.BLACK ) );
        new GradientPaint(
                (float) mvt.modelToViewY( -2 ),
                0f,
                Color.YELLOW,
                (float) mvt.modelToViewY( -1 ),
                0f,
                Color.BLACK ) );
        System.out.println( "Bounds: " + supportColumn.getShape().getBounds2D() );
        */
    }
}
