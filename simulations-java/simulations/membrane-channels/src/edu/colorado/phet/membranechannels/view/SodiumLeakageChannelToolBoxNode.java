// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.membranechannels.view;

import java.awt.geom.Point2D;

import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform2D;
import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.membranechannels.model.MembraneChannel;
import edu.colorado.phet.membranechannels.model.MembraneChannelOpennessStrategy;
import edu.colorado.phet.membranechannels.model.MembraneChannelTypes;
import edu.colorado.phet.membranechannels.model.MembraneChannelsModel;
import edu.umd.cs.piccolo.PNode;

/**
 * Node that goes in the membrane channel tool box and allows users to add
 * sodium leak channels to the membrane.
 *
 * @author John Blanco
 */
public class SodiumLeakageChannelToolBoxNode extends MembraneChannelToolBoxNode {

    public SodiumLeakageChannelToolBoxNode( MembraneChannelsModel model, ModelViewTransform2D mvt, PhetPCanvas canvas ) {
        super( model, mvt, canvas );
    }

    @Override
    protected void initializeSelectionNode() {
        PNode representation = new MembraneChannelNode( MembraneChannel.createChannel(
                MembraneChannelTypes.SODIUM_LEAKAGE_CHANNEL, getModel(),
                MembraneChannelOpennessStrategy.CHANNEL_ALWAYS_OPEN ),
                                                        SCALING_MVT );
        setSelectionNode( representation );
    }

    /* (non-Javadoc)
    * @see edu.colorado.phet.membranechannels.view.WeightBoxItem#addElementToModel(java.awt.geom.Point2D)
    */
    @Override
    protected void addElementToModel( Point2D positionInModelSpace ) {
        membraneChannel = getModel().createUserControlledMembraneChannel( MembraneChannelTypes.SODIUM_LEAKAGE_CHANNEL,
                                                                          positionInModelSpace );
    }
}
