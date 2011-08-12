// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.balanceandtorque.teetertotter.view;

import java.awt.geom.Point2D;

import edu.colorado.phet.balanceandtorque.teetertotter.model.BalancingActModel;
import edu.colorado.phet.balanceandtorque.teetertotter.model.UserMovableModelElement;
import edu.colorado.phet.balanceandtorque.teetertotter.model.masses.BrickStack;
import edu.colorado.phet.common.phetcommon.model.property.BooleanProperty;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.common.piccolophet.PhetPCanvas;

/**
 * This class represents a stack of bricks in a tool box.  When the user clicks
 * on this node, the corresponding model element is added to the model at the
 * user's mouse location.
 *
 * @author John Blanco
 */
public class BrickStackCreatorNode extends ModelElementCreatorNode {

    // Model-view transform for scaling the node used in the tool box.  This
    // may scale the node differently than what is used in the model.
    protected static final ModelViewTransform SCALING_MVT =
            ModelViewTransform.createOffsetScaleMapping( new Point2D.Double( 0, 0 ), 150 );

    private final int numBricks;

    public BrickStackCreatorNode( int numBricks, final BalancingActModel model, final ModelViewTransform mvt, final PhetPCanvas canvas ) {
        super( model, mvt, canvas );
        this.numBricks = numBricks;
        setSelectionNode( new BrickStackNode( new BrickStack( numBricks, new Point2D.Double( 0, 0 ) ), SCALING_MVT, canvas, new BooleanProperty( false ) ) );
        setPositioningOffset( 0, getSelectionNode().getFullBoundsReference().height / 2 );
        // TODO: i18n (units too)
        setCaption( BrickStack.BRICK_MASS * numBricks + " kg" );
    }

    @Override protected UserMovableModelElement addElementToModel( Point2D position ) {
        BrickStack brickStack = new BrickStack( numBricks, position );
        brickStack.userControlled.set( true );
        brickStack.setAnimationDestination( position.getX(), position.getY() );
        model.addMass( brickStack );
        return brickStack;
    }
}
