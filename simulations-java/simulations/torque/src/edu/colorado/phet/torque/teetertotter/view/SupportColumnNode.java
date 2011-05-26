// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.torque.teetertotter.view;

import java.awt.*;

import edu.colorado.phet.common.phetcommon.model.property.BooleanProperty;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.common.phetcommon.view.util.ColorUtils;
import edu.colorado.phet.torque.teetertotter.model.SupportColumn;

/**
 * Node the represents the support columns in the view.
 *
 * @author John Blanco
 */
public class SupportColumnNode extends ModelObjectNode {
    private static final Color BASE_COLOR = new Color( 153, 102, 204 );

    public SupportColumnNode( final ModelViewTransform mvt, final SupportColumn supportColumn, final BooleanProperty supportColumnsActive ) {
        super(
                mvt,
                supportColumn,
                new GradientPaint(
                        (float) mvt.modelToViewX( supportColumn.getShape().getBounds2D().getMinX() ),
                        0f,
                        ColorUtils.brighterColor( BASE_COLOR, 0.7 ),
                        (float) mvt.modelToViewX( supportColumn.getShape().getBounds2D().getMaxX() ),
                        0f,
                        ColorUtils.darkerColor( BASE_COLOR, 0.6 ) ) );
    }
}
