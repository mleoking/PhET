// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.torque.teetertotter.view;

import java.awt.geom.Point2D;

import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.torque.teetertotter.model.TeeterTotterTorqueModel;
import edu.colorado.phet.torque.teetertotter.model.UserMovableModelElement;
import edu.colorado.phet.torque.teetertotter.model.weights.BrickStack;

/**
 * This class represents a brick or stack of bricks in the weight box.  When
 * the user clicks on this node, a stack of bricks of the appropriate size is
 * added to the model at the user's mouse location.
 *
 * @author John Blanco
 */
public class BrickStackInWeightBoxNode extends WeightBoxItem {

    public BrickStackInWeightBoxNode( final TeeterTotterTorqueModel model, final ModelViewTransform mvt, final PhetPCanvas canvas ) {
        super( model, mvt, canvas );
    }

    @Override protected void initializeSelectionNode() {
        setSelectionNode( new BrickNode( SCALING_MVT, new BrickStack( 1, new Point2D.Double( 0, 0 ) ) ) );
        setCaption( "10 kg" );
    }

    @Override protected UserMovableModelElement addElementToModel( Point2D position ) {
        BrickStack brickStack = new BrickStack( 1, position );
        model.addWeight( brickStack );
        return brickStack;
    }
}
