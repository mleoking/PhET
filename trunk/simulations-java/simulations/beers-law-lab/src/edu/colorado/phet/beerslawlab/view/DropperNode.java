// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.beerslawlab.view;

import java.awt.Color;
import java.awt.Font;

import edu.colorado.phet.beerslawlab.BLLResources.Images;
import edu.colorado.phet.beerslawlab.BLLSimSharing.UserComponents;
import edu.colorado.phet.beerslawlab.dev.DebugOriginNode;
import edu.colorado.phet.beerslawlab.model.Dropper;
import edu.colorado.phet.common.phetcommon.application.PhetApplication;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.PhetPNode;
import edu.colorado.phet.common.piccolophet.nodes.HTMLNode;
import edu.umd.cs.piccolo.nodes.PImage;

/**
 * Dropper that contains a solute in solution form.
 * Origin is at the center of the hole where solution comes out of the dropper (bottom center).
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class DropperNode extends PhetPNode {

    private static final double BUTTON_Y_OFFSET = 60; // y offset of button center in dropper image file
    private static final double LABEL_Y_OFFSET = 255; // y offset of the label's center in dropper image file

    public DropperNode( final Dropper dropper ) {
        scale( 0.5 ); //TODO eliminate need for scaling

        final PImage imageNode = new PImage( Images.DROPPER );
        addChild( imageNode );
        imageNode.setOffset( -imageNode.getFullBoundsReference().getWidth() / 2, -imageNode.getFullBoundsReference().getHeight() );

        final HTMLNode labelNode = new HTMLNode( "", Color.BLACK, new PhetFont( Font.BOLD, 30 ) );
        addChild( labelNode );

        // On/off button
        MomentaryButtonNode buttonNode = new MomentaryButtonNode( UserComponents.dropperButton, dropper.on, dropper.enabled ) {{
            scale( 0.65 );
        }};
        addChild( buttonNode );
        buttonNode.setOffset( imageNode.getFullBoundsReference().getCenterX() - ( buttonNode.getFullBoundsReference().getWidth() / 2 ),
                              imageNode.getFullBoundsReference().getMaxY() - ( imageNode.getFullBoundsReference().getHeight() - BUTTON_Y_OFFSET ) - ( buttonNode.getFullBoundsReference().getHeight() / 2 ) );

        // origin debugging
        if ( PhetApplication.getInstance().isDeveloperControlsEnabled() ) {
            addChild( new DebugOriginNode() );
        }

        // Change the label when the solute changes.
        dropper.solute.addObserver( new SimpleObserver() {
            public void update() {
                labelNode.setHTML( dropper.solute.get().formula );
                labelNode.setRotation( -Math.PI / 2 );
                labelNode.setOffset( -( labelNode.getFullBoundsReference().getWidth() / 2 ),
                                     imageNode.getFullBoundsReference().getMaxY() - ( imageNode.getFullBoundsReference().getHeight() - LABEL_Y_OFFSET ) + ( labelNode.getFullBoundsReference().getHeight() / 2 ) );
            }
        } );

        // Visibility
        dropper.visible.addObserver( new VoidFunction1<Boolean>() {
            public void apply( Boolean visible ) {
                setVisible( visible );
            }
        } );

        // Update location
        dropper.location.addObserver( new SimpleObserver() {
            public void update() {
                setOffset( dropper.location.get().toPoint2D() );
            }
        } );

        addInputEventListener( new MovableDragHandler( UserComponents.dropper, dropper, this ) );
    }
}
