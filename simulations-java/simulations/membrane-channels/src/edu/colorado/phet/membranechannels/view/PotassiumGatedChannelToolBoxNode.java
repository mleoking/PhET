// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.membranechannels.view;

import java.awt.geom.Point2D;

import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform2D;
import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.membranechannels.model.*;
import edu.umd.cs.piccolo.PNode;

/**
 * Node that goes in the membrane channel tool box and allows users to add
 * gated potassium channels to the membrane.
 *
 * @author John Blanco
 */
public class PotassiumGatedChannelToolBoxNode extends MembraneChannelToolBoxNode {

    public PotassiumGatedChannelToolBoxNode( MembraneChannelsModel model, ModelViewTransform2D mvt, PhetPCanvas canvas ) {
        super( model, mvt, canvas );
    }

    @Override
    protected void initializeSelectionNode() {
        // Need to have a dynamic openness strategy because the representation
        // of the node needs to be correct, but in this case the openness will
        // not actually change.
        MembraneChannelOpennessStrategy opennessStrategy = new TimedSettableOpennessStrategy( 0 );
        PNode representation = new MembraneChannelNode( MembraneChannel.createChannel(
                MembraneChannelTypes.POTASSIUM_GATED_CHANNEL, getModel(),
                opennessStrategy ),
                                                        SCALING_MVT );
        setSelectionNode( representation );
    }

    /* (non-Javadoc)
    * @see edu.colorado.phet.membranechannels.view.WeightBoxItem#addElementToModel(java.awt.geom.Point2D)
    */
    @Override
    protected void addElementToModel( Point2D positionInModelSpace ) {
        membraneChannel = getModel().createUserControlledMembraneChannel( MembraneChannelTypes.POTASSIUM_GATED_CHANNEL,
                                                                          positionInModelSpace );
    }
}
