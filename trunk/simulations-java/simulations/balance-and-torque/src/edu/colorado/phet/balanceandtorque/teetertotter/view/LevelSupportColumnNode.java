// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.balanceandtorque.teetertotter.view;

import java.awt.Color;
import java.awt.GradientPaint;

import edu.colorado.phet.balanceandtorque.teetertotter.model.ColumnState;
import edu.colorado.phet.balanceandtorque.teetertotter.model.SupportColumn;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.common.phetcommon.view.controls.StandardIconButton.CloseButton;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.common.phetcommon.view.util.ColorUtils;
import edu.colorado.phet.common.piccolophet.event.ButtonEventHandler;
import edu.colorado.phet.common.piccolophet.event.CursorHandler;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolox.pswing.PSwing;

/**
 * Node the represents the support columns in the view.
 *
 * @author John Blanco
 */
public class LevelSupportColumnNode extends ModelObjectNode {
    private static final Color BASE_COLOR = new Color( 153, 102, 204 );

    public LevelSupportColumnNode( final ModelViewTransform mvt, final SupportColumn supportColumn, final Property<ColumnState> columnState ) {
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

        // Add the button that can be used to deactivate the column, which
        // means that it will essentially go away.
        PNode closeButton = new PSwing( new CloseButton() ) {{
            // Tweak the button's size.  The factor is empirically determined.
            setScale( 0.8 );
            // Position at center bottom of column.
            double xPos = mvt.modelToViewX( supportColumn.getShape().getBounds2D().getCenterX() ) - getFullBoundsReference().width / 2;
            double yPos = mvt.modelToViewY( supportColumn.getShape().getBounds2D().getMinY() ) - 2 * getFullBoundsReference().height;
            setOffset( xPos, yPos );
            addInputEventListener( new CursorHandler() );
            // Add the handler that will deactivate the columns when the
            // closed button is pressed.
            addInputEventListener( new ButtonEventHandler() {
                @Override public void mouseReleased( PInputEvent event ) {
                    columnState.set( ColumnState.NONE );
                }
            } );
        }};
        addChild( closeButton );
    }
}
