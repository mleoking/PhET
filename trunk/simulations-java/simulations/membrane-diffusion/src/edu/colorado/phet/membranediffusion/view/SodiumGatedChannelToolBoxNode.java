/* Copyright 2010, University of Colorado */

package edu.colorado.phet.membranediffusion.view;

import java.awt.geom.Point2D;

import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform2D;
import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.membranediffusion.model.MembraneChannel;
import edu.colorado.phet.membranediffusion.model.MembraneDiffusionModel;
import edu.colorado.phet.membranediffusion.model.PotassiumGatedChannel;
import edu.colorado.phet.membranediffusion.model.SodiumGatedChannel;
import edu.umd.cs.piccolo.PNode;

/**
 * Node that goes in the membrane channel tool box and allows users to add
 * gated sodium channels to the membrane.
 * 
 * @author John Blanco
 */
public class SodiumGatedChannelToolBoxNode extends ToolBoxItem {

	public SodiumGatedChannelToolBoxNode(MembraneDiffusionModel model, ModelViewTransform2D mvt, PhetPCanvas canvas) {
		super(model, mvt, canvas);
	}

	@Override
	protected MembraneChannel createModelElement( Point2D position ) {
	    return new SodiumGatedChannel(getModel(), getModel().getHodgkinHuxleyModel());
	}

	@Override
	protected void initializeSelectionNode() {
		MembraneChannel channel = new SodiumGatedChannel();
		PNode representation = new MembraneChannelNode(channel, SCALING_MVT);
		setSelectionNode(representation);
	}
}
