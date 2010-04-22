/* Copyright 2010, University of Colorado */

package edu.colorado.phet.neuron.view;

import java.awt.geom.Point2D;

import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform2D;
import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.neuron.model.MembraneChannel;
import edu.colorado.phet.neuron.model.MembraneDiffusionModel;
import edu.colorado.phet.neuron.model.SodiumDualGatedChannel;
import edu.umd.cs.piccolo.PNode;

/**
 * Node that goes in the membrane channel tool box and allows users to add
 * gated sodium channels to the membrane.
 * 
 * @author John Blanco
 */
public class SodiumDualGatedChannelToolBoxNode extends ToolBoxItem {

	public SodiumDualGatedChannelToolBoxNode(MembraneDiffusionModel model, ModelViewTransform2D mvt, PhetPCanvas canvas) {
		super(model, mvt, canvas);
	}

	@Override
	protected void handleAddRequest(Point2D position) {
		setMembraneChannel(new SodiumDualGatedChannel(getModel(), getModel().getHodgkinHuxleyModel()));
		getMembraneChannel().setRotationalAngle(Math.PI / 2);
		getMembraneChannel().setCenterLocation(getMvt().viewToModel(position));
		getModel().addUserControlledMembraneChannel(getMembraneChannel());
	}

	@Override
	protected void initializeSelectionNode() {
		MembraneChannel channel = new SodiumDualGatedChannel();
		channel.setRotationalAngle(Math.PI / 2);
		PNode representation = new MembraneChannelNode(channel, SCALING_MVT);
		setSelectionNode(representation);
		// TODO i18n
		setCaption("<html><center>Sodium Gated<br>Channel</center></html>");
	}
}
