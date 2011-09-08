// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.balanceandtorque.teetertotter.view;

import java.awt.Color;

import edu.colorado.phet.balanceandtorque.teetertotter.model.PositionedVector;
import edu.colorado.phet.common.phetcommon.model.property.BooleanProperty;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PText;

/**
 * Class that depicts a "mystery vector", which is a vector that is presented to
 * the user in the appropriate location but that has a fixed size and it labeled
 * with a question mark (or some other symbol).
 *
 * @author John Blanco
 */
public class MysteryVectorNode extends PNode {

    private static final int FONT_SIZE = 24;
    private static final double Y_DIST_FROM_POSITION = 10;

    /**
     * Constructor.
     *
     * @param positionedVectorProperty
     * @param visibilityProperty
     * @param mvt
     */
    public MysteryVectorNode( final Property<PositionedVector> positionedVectorProperty,
                              BooleanProperty visibilityProperty, final Color fillColor, final ModelViewTransform mvt ) {
        // Create the label and add it as a child.
        // TODO: i18n
        final PText mysterySymbolNode = new PText( "?" ) {{
            setFont( new PhetFont( FONT_SIZE, true ) );
        }};
        addChild( mysterySymbolNode );
        // Listen to the vector and update the node when changes occur.
        positionedVectorProperty.addObserver( new VoidFunction1<PositionedVector>() {
            public void apply( PositionedVector positionedVector ) {
                mysterySymbolNode.setOffset( mvt.modelToViewX( positionedVectorProperty.get().origin.getX() ) - mysterySymbolNode.getFullBoundsReference().width / 2,
                                             mvt.modelToViewY( positionedVectorProperty.get().origin.toPoint2D().getY() ) + Y_DIST_FROM_POSITION );
            }
        } );
        // Set up visibility control.
        visibilityProperty.addObserver( new VoidFunction1<Boolean>() {
            public void apply( Boolean visible ) {
                setVisible( visible );
            }
        } );
    }
}
