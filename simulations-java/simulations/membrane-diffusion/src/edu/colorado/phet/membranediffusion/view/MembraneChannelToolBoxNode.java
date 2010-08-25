/* Copyright 2010, University of Colorado */

package edu.colorado.phet.membranediffusion.view;

import java.awt.geom.Point2D;

import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform2D;
import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.membranediffusion.model.GenericMembraneChannel;
import edu.colorado.phet.membranediffusion.model.MembraneChannel;
import edu.colorado.phet.membranediffusion.model.MembraneChannelTypes;
import edu.colorado.phet.membranediffusion.model.MembraneDiffusionModel;
import edu.umd.cs.piccolo.PNode;

/**
 * Node that goes in the membrane channel tool box and allows users to add
 * gated potassium channels to the membrane.
 * 
 * @author John Blanco
 */
public abstract class MembraneChannelToolBoxNode extends ToolBoxItem {
    
    protected MembraneChannel membraneChannel = null;

	public MembraneChannelToolBoxNode(MembraneDiffusionModel model, ModelViewTransform2D mvt, PhetPCanvas canvas) {
		super(model, mvt, canvas);
	}

    /* (non-Javadoc)
     * @see edu.colorado.phet.membranediffusion.view.ToolBoxItem#releaseModelElement()
     */
    @Override
    protected void releaseModelElement() {
        if (membraneChannel != null){
            membraneChannel.setUserControlled( false );
            membraneChannel = null;
        }
    }

    /* (non-Javadoc)
     * @see edu.colorado.phet.membranediffusion.view.ToolBoxItem#setModelElementPosition(java.awt.geom.Point2D)
     */
    @Override
    protected void setModelElementPosition( Point2D position ) {
        membraneChannel.setCenterLocation( position );
    }
}
