package edu.colorado.phet.neuron.view;

import java.awt.geom.Point2D;

import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform2D;
import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.neuron.model.MembraneChannel;
import edu.colorado.phet.neuron.model.MembraneDiffusionModel;
import edu.colorado.phet.neuron.model.PotassiumLeakageChannel;
import edu.umd.cs.piccolo.PNode;

public class PotassiumLeakChannelToolBoxNode extends ToolBoxItem {

	public PotassiumLeakChannelToolBoxNode(MembraneDiffusionModel model, ModelViewTransform2D mvt, PhetPCanvas canvas) {
		super(model, mvt, canvas);
	}

	@Override
	protected void handleAddRequest(Point2D position) {
		setMembraneChannel(new PotassiumLeakageChannel(getModel(), null));
		getMembraneChannel().setRotationalAngle(-Math.PI / 2);
		getMembraneChannel().setCenterLocation(getMvt().viewToModel(position));
		getModel().addUserControlledMembraneChannel(getMembraneChannel());
	}

	@Override
	protected void initializeSelectionNode() {
		MembraneChannel channel = new PotassiumLeakageChannel();
		channel.setRotationalAngle(-Math.PI / 2);
		PNode representation = new MembraneChannelNode(channel, SCALING_MVT);
		setSelectionNode(representation);
		// TODO i18n
		setCaption("<html><center>Potassium Leak<br>Channel</center></html>");
	}
}
