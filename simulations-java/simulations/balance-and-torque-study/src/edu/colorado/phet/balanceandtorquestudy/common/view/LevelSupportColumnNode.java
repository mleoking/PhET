// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.balanceandtorquestudy.common.view;

import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Image;
import java.awt.Insets;

import javax.swing.Icon;
import javax.swing.ImageIcon;

import edu.colorado.phet.balanceandtorquestudy.common.model.ColumnState;
import edu.colorado.phet.balanceandtorquestudy.common.model.LevelSupportColumn;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.resources.PhetCommonResources;
import edu.colorado.phet.common.phetcommon.simsharing.components.SimSharingJButton;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.common.phetcommon.view.util.ColorUtils;
import edu.colorado.phet.common.piccolophet.event.ButtonEventHandler;
import edu.colorado.phet.common.piccolophet.event.CursorHandler;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolox.pswing.PSwing;

import static edu.colorado.phet.balanceandtorquestudy.BalanceAndTorqueSimSharing.UserComponents.redXRemoveSupportsButton;

/**
 * Node that represents a support column with a flat top in the view.
 *
 * @author John Blanco
 */
public class LevelSupportColumnNode extends ModelObjectNode {
    private static final Color BASE_COLOR = new Color( 153, 102, 204 );

    public LevelSupportColumnNode( final ModelViewTransform mvt, final LevelSupportColumn supportColumn, final Property<ColumnState> columnState, boolean showCloseButton ) {
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
                setVisible( columnState == ColumnState.DOUBLE_COLUMNS );
            }
        } );

        if ( showCloseButton ) {
            // Add the button that can be used to deactivate the column, which
            // means that it will essentially go away.
            PNode closeButton = new PSwing( new SimSharingCloseButton() ) {{
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

    // Class that defines that close button that is shown on the supports and
    // that looks like a standard close button, i.e. a red x.
    private static class SimSharingCloseButton extends SimSharingJButton {
        private SimSharingCloseButton() {
            super( redXRemoveSupportsButton );
            Image image = PhetCommonResources.getInstance().getImage( PhetCommonResources.IMAGE_CLOSE_BUTTON );
            Icon icon = new ImageIcon( image );
            setIcon( icon );
            setOpaque( false );
            setMargin( new Insets( 0, 0, 0, 0 ) );
        }
    }
}
