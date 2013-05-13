// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.balanceandtorque.common.view;

import java.awt.Color;
import java.awt.GradientPaint;

import edu.colorado.phet.balanceandtorque.common.model.ColumnState;
import edu.colorado.phet.balanceandtorque.common.model.ShapeModelElement;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.common.phetcommon.view.util.ColorUtils;

/**
 * Node that represents a support column with a non-level (a.k.a. tilted) top
 * in the view.
 *
 * @author John Blanco
 */
public class TiltedSupportColumnNode extends ModelObjectNode {
    private static final Color BASE_COLOR = new Color( 153, 102, 204 );

    public TiltedSupportColumnNode( final ModelViewTransform mvt, final ShapeModelElement supportColumn, final Property<ColumnState> columnState ) {
        super( mvt, supportColumn, new GradientPaint(
                (float) mvt.modelToViewX( supportColumn.getShape().getBounds2D().getMinX() ),
                0f,
                ColorUtils.brighterColor( BASE_COLOR, 0.7 ),
                (float) mvt.modelToViewX( supportColumn.getShape().getBounds2D().getMaxX() ),
                0f,
                ColorUtils.darkerColor( BASE_COLOR, 0.6 ) ) );

        // The visibility of the column is controlled by the boolean property
        // that indicates whether or not it is active.
        columnState.addObserver( new VoidFunction1<ColumnState>() {
            public void apply( ColumnState columnState ) {
                setVisible( columnState == ColumnState.SINGLE_COLUMN );
            }
        } );
    }
}
