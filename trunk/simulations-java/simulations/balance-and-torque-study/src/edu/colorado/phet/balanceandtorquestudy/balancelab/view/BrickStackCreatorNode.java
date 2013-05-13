// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.balanceandtorquestudy.balancelab.view;

import java.awt.geom.Point2D;

import edu.colorado.phet.balanceandtorquestudy.common.model.BalanceModel;
import edu.colorado.phet.balanceandtorquestudy.common.model.UserMovableModelElement;
import edu.colorado.phet.balanceandtorquestudy.common.model.masses.BrickStack;
import edu.colorado.phet.balanceandtorquestudy.common.view.BrickStackNode;
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
public class BrickStackCreatorNode extends MassCreatorNode {

    // Model-view transform for scaling the node used in the tool box.  This
    // may scale the node differently than what is used in the model.
    protected static final ModelViewTransform SCALING_MVT =
            ModelViewTransform.createOffsetScaleMapping( new Point2D.Double( 0, 0 ), 150 );

    private final int numBricks;

    public BrickStackCreatorNode( int numBricks, final BalanceModel model, final ModelViewTransform mvt, final PhetPCanvas canvas ) {
        super( model, mvt, canvas, numBricks * BrickStack.BRICK_MASS, true );
        this.numBricks = numBricks;
        setSelectionNode( new BrickStackNode( new BrickStack( numBricks, new Point2D.Double( 0, 0 ) ), SCALING_MVT, canvas, new BooleanProperty( false ) ) );
        setPositioningOffset( 0, -mvt.modelToViewDeltaY( BrickStack.BRICK_HEIGHT * numBricks / 2 ) );
    }

    @Override protected UserMovableModelElement addElementToModel( Point2D position ) {
        BrickStack brickStack = new BrickStack( numBricks, position );
        brickStack.userControlled.set( true );
        brickStack.setAnimationDestination( position.getX(), position.getY() );
        model.addMass( brickStack );
        return brickStack;
    }
}
