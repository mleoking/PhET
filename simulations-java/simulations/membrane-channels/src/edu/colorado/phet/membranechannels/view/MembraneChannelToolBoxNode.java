// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.membranechannels.view;

import java.awt.geom.Point2D;

import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform2D;
import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.membranechannels.model.MembraneChannel;
import edu.colorado.phet.membranechannels.model.MembraneChannelsModel;

/**
 * Base class for the nodes that go in the tool box and allow the user to
 * add membrane channels to the model.
 *
 * @author John Blanco
 */
public abstract class MembraneChannelToolBoxNode extends ToolBoxItem {

    protected MembraneChannel membraneChannel = null;

    public MembraneChannelToolBoxNode( MembraneChannelsModel model, ModelViewTransform2D mvt, PhetPCanvas canvas ) {
        super( model, mvt, canvas );
    }

    /* (non-Javadoc)
     * @see edu.colorado.phet.membranechannels.view.WeightBoxItem#releaseModelElement()
     */
    @Override
    protected void releaseModelElement() {
        if ( membraneChannel != null ) {
            membraneChannel.setUserControlled( false );
            membraneChannel = null;
        }
    }

    /* (non-Javadoc)
     * @see edu.colorado.phet.membranechannels.view.WeightBoxItem#setModelElementPosition(java.awt.geom.Point2D)
     */
    @Override
    protected void setModelElementPosition( Point2D position ) {
        membraneChannel.setCenterLocation( position );
    }
}
