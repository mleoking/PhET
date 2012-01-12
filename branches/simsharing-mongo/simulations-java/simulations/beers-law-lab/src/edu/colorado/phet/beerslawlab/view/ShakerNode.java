// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.beerslawlab.view;

import java.awt.Color;
import java.awt.Font;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;

import edu.colorado.phet.beerslawlab.BLLResources.Images;
import edu.colorado.phet.beerslawlab.BLLSimSharing.UserComponents;
import edu.colorado.phet.beerslawlab.dev.DebugOriginNode;
import edu.colorado.phet.beerslawlab.model.Shaker;
import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.PhetPNode;
import edu.colorado.phet.common.piccolophet.event.CursorHandler;
import edu.colorado.phet.common.piccolophet.nodes.HTMLNode;
import edu.colorado.phet.common.piccolophet.nodes.kit.ZeroOffsetNode;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.nodes.PImage;
import edu.umd.cs.piccolo.nodes.PPath;

/**
 * Shaker that contains a solute in solid form.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class ShakerNode extends PhetPNode {

    private static final boolean SHOW_ORIGIN = false;
    private static final double IMAGE_SCALE = 0.75;
    private static final double LABEL_X_OFFSET = 40 * IMAGE_SCALE; // x offset of the label's center from the image's center

    public ShakerNode( final Shaker shaker ) {

        final PImage imageNode = new PImage( Images.SHAKER ) {{
            scale( IMAGE_SCALE );
        }};
        final HTMLNode labelNode = new HTMLNode( "", Color.BLACK, new PhetFont( Font.BOLD, 22 ) );

        // Combine image and label into a parent, to simplify rotation and label alignment.
        PNode parentNode = new PNode();
        parentNode.addChild( imageNode );
        parentNode.addChild( labelNode );
        imageNode.setOffset( -imageNode.getFullBoundsReference().getWidth() / 2, -imageNode.getFullBoundsReference().getHeight() / 2 );

        // Image file has shaker holes pointing left (orientation=Math.PI), so account for this when using model orientation.
        parentNode.rotate( shaker.getOrientation() - Math.PI );

        ZeroOffsetNode zeroOffsetNode = new ZeroOffsetNode( parentNode );
        addChild( zeroOffsetNode );
        zeroOffsetNode.setOffset( -45, -170 ); // Manually adjust these values until the origin is in the middle hole of the shaker.

        // debugging for origin and holes
        if ( SHOW_ORIGIN ) {
            addChild( new DebugOriginNode() );
            addHoleNodes( shaker.getRelativeHoleLocations() );
        }

        // Change the label when the solute changes.
        shaker.solute.addObserver( new SimpleObserver() {
            public void update() {
                labelNode.setHTML( shaker.solute.get().formula );
                labelNode.setOffset( -( labelNode.getFullBoundsReference().getWidth() / 2 ) + LABEL_X_OFFSET,
                                     -labelNode.getFullBoundsReference().getHeight() / 2 );
            }
        } );

        // Visibility
        shaker.visible.addObserver( new VoidFunction1<Boolean>() {
            public void apply( Boolean visible ) {
                setVisible( visible );
            }
        } );

        // Update location
        shaker.location.addObserver( new SimpleObserver() {
            public void update() {
                setOffset( shaker.location.get().toPoint2D() );
            }
        } );

        addInputEventListener( new CursorHandler() );
        addInputEventListener( new ShakerDragHandler( shaker, this, shaker.getDragBounds() ) );
    }

    private void addHoleNodes( ImmutableVector2D[] holeLocations ) {
        for ( ImmutableVector2D location : holeLocations ) {
            PNode holeNode = new HoleNode();
            holeNode.setOffset( location.toPoint2D() );
            addChild( holeNode );
        }
    }

    private static class HoleNode extends PPath {

        private static final double DIAMETER = 4;

        public HoleNode() {
            setPickable( false );
            setChildrenPickable( false );
            setPathTo( new Ellipse2D.Double( -( DIAMETER / 2 ), -( DIAMETER ) / 2, DIAMETER, DIAMETER ) );
            setStroke( null );
            setPaint( Color.GREEN );
        }
    }

    // Drag handler, specialized for the shaker.
    private static class ShakerDragHandler extends MovableDragHandler {

        private final Shaker shaker;

        public ShakerDragHandler( final Shaker shaker, PNode dragNode, Rectangle2D dragBounds ) {
            super( UserComponents.shaker, shaker, dragNode );
            this.shaker = shaker;
        }

        @Override protected void startDrag( PInputEvent event ) {
            super.startDrag( event );
            shaker.setDispensingRate( shaker.getMaxDispensingRate() ); //TODO set rate based on direction of shake, and vigorousness of shake
        }

        @Override protected void endDrag( PInputEvent event ) {
            super.endDrag( event );
            shaker.setDispensingRate( 0 );
        }
    }
}
