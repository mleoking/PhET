// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.beerslawlab.view;

import java.awt.Color;
import java.awt.Font;

import edu.colorado.phet.beerslawlab.BLLResources.Images;
import edu.colorado.phet.beerslawlab.model.Solute;
import edu.colorado.phet.beerslawlab.model.SoluteForm;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.PhetPNode;
import edu.colorado.phet.common.piccolophet.event.CursorHandler;
import edu.colorado.phet.common.piccolophet.nodes.HTMLNode;
import edu.colorado.phet.common.piccolophet.nodes.kit.ZeroOffsetNode;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PImage;

/**
 * Dropper that contains a solute in solution form.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class DropperNode extends PhetPNode {

    private static final double IMAGE_SCALE = 0.65;
    private static final double LABEL_X_OFFSET = -50 * IMAGE_SCALE; // x offset of the label's center from the dropper image's center
    private static final double BUTTON_Y_OFFSET = 10 * IMAGE_SCALE; // y offset of button from the dropper image's center

    public DropperNode( final Property<Solute> solute, final Property<SoluteForm> soluteFormProperty, Property<Boolean> dropperOn ) {

        final PImage imageNode = new PImage( Images.DROPPER ) {{
            scale( IMAGE_SCALE );
        }};
        final HTMLNode labelNode = new HTMLNode( "", Color.BLACK, new PhetFont( Font.BOLD, 22 ) );

        // Combine image and label into a parent, to simplify rotation and label alignment.
        PNode parentNode = new PNode();
        parentNode.addChild( imageNode );
        parentNode.addChild( labelNode );
        imageNode.setOffset( -imageNode.getFullBoundsReference().getWidth() / 2, -( imageNode.getFullBoundsReference().getHeight() / 2 ) );
        parentNode.rotate( 0.5 * -Math.PI ); // Image file is assumed to be oriented with dropper hole pointing left.

        // Apply a wrapper node to move the origin to (0,0). Do this after transforming parentNode and its children.
        addChild( new ZeroOffsetNode( parentNode ) );

        // On/off button
        MomentaryButtonNode buttonNode = new MomentaryButtonNode( dropperOn ) {{
            scale( 0.45 );
        }};
        addChild( buttonNode );
        buttonNode.setOffset( ( parentNode.getFullBoundsReference().getWidth() - buttonNode.getFullBoundsReference().getWidth() ) / 2, BUTTON_Y_OFFSET );

        // Change the label when the solute changes.
        solute.addObserver( new SimpleObserver() {
            public void update() {
                labelNode.setHTML( solute.get().formula );
                labelNode.setOffset( -( labelNode.getFullBoundsReference().getWidth() / 2 ) + LABEL_X_OFFSET,
                                     -( labelNode.getFullBoundsReference().getHeight() / 2 ) );
            }
        } );

        // Make this node visible only when the solute is in solid form.
        soluteFormProperty.addObserver( new SimpleObserver() {
            public void update() {
                setVisible( soluteFormProperty.get().equals( SoluteForm.SOLUTION ) );
            }
        } );

        addInputEventListener( new CursorHandler() );
    }
}
