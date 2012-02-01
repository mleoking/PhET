// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.beerslawlab.concentration.view;

import java.awt.Color;
import java.awt.Font;
import java.awt.geom.GeneralPath;

import edu.colorado.phet.beerslawlab.common.BLLResources.Images;
import edu.colorado.phet.beerslawlab.common.BLLSimSharing.UserComponents;
import edu.colorado.phet.beerslawlab.common.view.DebugOriginNode;
import edu.colorado.phet.beerslawlab.common.view.MomentaryButtonNode;
import edu.colorado.phet.beerslawlab.common.view.MovableDragHandler;
import edu.colorado.phet.beerslawlab.concentration.model.Dropper;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.common.phetcommon.view.util.DoubleGeneralPath;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.PhetPNode;
import edu.colorado.phet.common.piccolophet.event.CursorHandler;
import edu.colorado.phet.common.piccolophet.nodes.HTMLNode;
import edu.umd.cs.piccolo.nodes.PImage;

/**
 * Dropper that contains a solute in solution form.
 * Origin is at the center of the hole where solution comes out of the dropper (bottom center).
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
class DropperNode extends PhetPNode {

    private static final boolean SHOW_ORIGIN = false;
    private static final double BUTTON_Y_OFFSET = 30; // y offset of button center in dropper image file
    private static final double LABEL_Y_OFFSET = 125; // y offset of the label's center in dropper image file

    public static final double TIP_WIDTH = 15; // specific to image file

    // glass portion of the dropper, used to fill dropper with stock solution, specific to the dropper image file
    public static final GeneralPath GLASS_PATH = new DoubleGeneralPath() {{
        final double tipWidth = TIP_WIDTH;
        final double tipHeight = 5;
        final double glassWidth = 46;
        final double glassHeight = 150;
        final double glassYOffset = tipHeight + 14;
        moveTo( -tipWidth / 2, 0 );
        lineTo( -tipWidth / 2, -tipHeight );
        lineTo( -glassWidth / 2, -glassYOffset );
        lineTo( -glassWidth / 2, -glassHeight );
        lineTo( glassWidth / 2, -glassHeight );
        lineTo( glassWidth / 2, -glassYOffset );
        lineTo( tipWidth / 2, -tipHeight );
        lineTo( tipWidth / 2, 0 );
        closePath();
    }}.getGeneralPath();

    public DropperNode( final Dropper dropper ) {

        // nodes
        final PImage foregroundImageNode = new PImage( Images.DROPPER_FOREGROUND );
        final PImage backgroundImageNode = new PImage( Images.DROPPER_BACKGROUND );
        final HTMLNode labelNode = new HTMLNode( "", Color.BLACK, new PhetFont( Font.BOLD, 15 ) );
        MomentaryButtonNode buttonNode = new MomentaryButtonNode( UserComponents.dropperButton, dropper.on, dropper.enabled ) {{
            scale( 0.3 );
            rotate( Math.toRadians( 110 ) ); // rotate to match lighting in dropper images
        }};

        // rendering order
        addChild( backgroundImageNode );
        addChild( foregroundImageNode );
        addChild( labelNode );
        addChild( buttonNode );
        if ( SHOW_ORIGIN ) {
            addChild( new DebugOriginNode() );
        }

        // layout
        {
            // move origin to bottom center (tip) of images
            foregroundImageNode.setOffset( -foregroundImageNode.getFullBoundsReference().getWidth() / 2, -foregroundImageNode.getFullBoundsReference().getHeight() );
            backgroundImageNode.setOffset( -backgroundImageNode.getFullBoundsReference().getWidth() / 2, -backgroundImageNode.getFullBoundsReference().getHeight() );
            // center the button in the dropper's bulb
            buttonNode.setOffset( foregroundImageNode.getFullBoundsReference().getCenterX(),
                                  foregroundImageNode.getFullBoundsReference().getMaxY() - ( foregroundImageNode.getFullBoundsReference().getHeight() - BUTTON_Y_OFFSET ) );
            //NOTE: label will be positioned whenever its text is set, to keep it centered in the dropper's glass
        }

        // Change the label when the solute changes.
        dropper.solute.addObserver( new SimpleObserver() {
            public void update() {
                labelNode.setHTML( dropper.solute.get().formula );
                labelNode.setRotation( -Math.PI / 2 );
                // center the label in the dropper's glass
                labelNode.setOffset( -( labelNode.getFullBoundsReference().getWidth() / 2 ),
                                     foregroundImageNode.getFullBoundsReference().getMaxY() - ( foregroundImageNode.getFullBoundsReference().getHeight() - LABEL_Y_OFFSET ) + ( labelNode.getFullBoundsReference().getHeight() / 2 ) );
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

        // Make the background visible only when the dropper is empty
        dropper.empty.addObserver( new VoidFunction1<Boolean>() {
            public void apply( Boolean empty ) {
                backgroundImageNode.setVisible( empty );
            }
        } );

        addInputEventListener( new CursorHandler() );
        addInputEventListener( new MovableDragHandler( UserComponents.dropper, dropper, this ) );
    }
}
