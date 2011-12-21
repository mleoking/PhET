// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.beerslawlab.view;

import java.awt.Color;
import java.awt.Font;

import edu.colorado.phet.beerslawlab.BLLResources.Images;
import edu.colorado.phet.beerslawlab.BLLSimSharing.Objects;
import edu.colorado.phet.beerslawlab.model.Shaker;
import edu.colorado.phet.beerslawlab.model.SoluteForm;
import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.util.function.Function1;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.PhetPNode;
import edu.colorado.phet.common.piccolophet.event.CursorHandler;
import edu.colorado.phet.common.piccolophet.nodes.HTMLNode;
import edu.colorado.phet.common.piccolophet.nodes.kit.ZeroOffsetNode;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PImage;

/**
 * Shaker that contains a solute in solid form.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class ShakerNode extends PhetPNode {

    private static final double IMAGE_SCALE = 0.75;
    private static final double LABEL_X_OFFSET = 40 * IMAGE_SCALE; // x offset of the label's center from the image's center

    public ShakerNode( final Shaker shaker, final Property<SoluteForm> soluteFormProperty ) {

        final PImage imageNode = new PImage( Images.SHAKER ) {{
            scale( IMAGE_SCALE );
        }};
        final HTMLNode labelNode = new HTMLNode( "", Color.BLACK, new PhetFont( Font.BOLD, 22 ) );

        // Combine image and label into a parent, to simplify rotation and label alignment.
        PNode parentNode = new PNode();
        parentNode.addChild( imageNode );
        parentNode.addChild( labelNode );
        imageNode.setOffset( -imageNode.getFullBoundsReference().getWidth() / 2, -imageNode.getFullBoundsReference().getHeight() / 2 );
        parentNode.rotate( 0.25 * -Math.PI ); // Image file is assumed to be oriented with shaker holes pointing left.

        // Apply a wrapper node to move the origin to (0,0). Do this after transforming parentNode and its children.
        addChild( new ZeroOffsetNode( parentNode ) );

        // Change the label when the solute changes.
        shaker.solute.addObserver( new SimpleObserver() {
            public void update() {
                labelNode.setHTML( shaker.solute.get().formula );
                labelNode.setOffset( -( labelNode.getFullBoundsReference().getWidth() / 2 ) + LABEL_X_OFFSET,
                                     -labelNode.getFullBoundsReference().getHeight() / 2 );
            }
        } );

        // Make this node visible only when the solute is in solid form.
        soluteFormProperty.addObserver( new SimpleObserver() {
            public void update() {
                setVisible( soluteFormProperty.get().equals( SoluteForm.SOLID ) );
            }
        } );

        // Update location
        shaker.location.addObserver( new SimpleObserver() {
            public void update() {
                setOffset( shaker.location.get().toPoint2D() );
            }
        } );

        addInputEventListener( new CursorHandler() );
        addInputEventListener( new MovableDragHandler( Objects.SHAKER, shaker, this,
                                                       new Function1<ImmutableVector2D, ImmutableVector2D>() {
                                                           public ImmutableVector2D apply( ImmutableVector2D v ) {
                                                               //TODO constrain drag
                                                               return v;
                                                           }
                                                       } ) );
    }
}
