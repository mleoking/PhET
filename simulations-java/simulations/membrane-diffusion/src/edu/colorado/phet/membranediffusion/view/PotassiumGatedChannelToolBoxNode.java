/* Copyright 2010, University of Colorado */

package edu.colorado.phet.membranediffusion.view;

import java.awt.geom.Point2D;

import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform2D;
import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.membranediffusion.model.MembraneChannel;
import edu.colorado.phet.membranediffusion.model.MembraneDiffusionModel;
import edu.colorado.phet.membranediffusion.model.PotassiumGatedChannel;
import edu.umd.cs.piccolo.PNode;

/**
 * Node that goes in the membrane channel tool box and allows users to add
 * gated potassium channels to the membrane.
 * 
 * @author John Blanco
 */
public class PotassiumGatedChannelToolBoxNode extends ToolBoxItem {

	public PotassiumGatedChannelToolBoxNode(MembraneDiffusionModel model, ModelViewTransform2D mvt, PhetPCanvas canvas) {
		super(model, mvt, canvas);
	}

	@Override
	protected void handleAddRequest(Point2D position) {
		setMembraneChannel(new PotassiumGatedChannel(getModel(), getModel().getHodgkinHuxleyModel()));
		getMembraneChannel().setCenterLocation(getMvt().viewToModel(position));
		getModel().addUserControlledMembraneChannel(getMembraneChannel());
	}

	@Override
	protected void initializeSelectionNode() {
		MembraneChannel channel = new PotassiumGatedChannel();
		PNode representation = new MembraneChannelNode(channel, SCALING_MVT);
		setSelectionNode(representation);
	}
}
