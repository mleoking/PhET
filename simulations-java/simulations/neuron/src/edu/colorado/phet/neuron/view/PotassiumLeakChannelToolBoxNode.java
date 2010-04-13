package edu.colorado.phet.neuron.view;

import java.awt.geom.Point2D;
import java.awt.geom.Point2D.Double;

import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform2D;
import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.common.piccolophet.nodes.HTMLNode;
import edu.colorado.phet.neuron.model.MembraneDiffusionModel;
import edu.colorado.phet.neuron.model.PotassiumLeakageChannel;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PText;

public class PotassiumLeakChannelToolBoxNode extends ToolBoxItem {

	public PotassiumLeakChannelToolBoxNode(MembraneDiffusionModel model, ModelViewTransform2D mvt, PhetPCanvas canvas) {
		super(model, mvt, canvas);
	}

	@Override
	protected void handleAddRequest(Point2D position) {
		// TODO Fill this in - it should add a channel to the model.
		System.out.println(getClass().getName() + "handleAddRequest called.");
	}

	@Override
	protected void initializeSelectionNode() {
		PNode representation = new MembraneChannelNode(new PotassiumLeakageChannel(), SCALING_MVT);
		representation.rotate(-Math.PI / 2);
		representation.setOffset(
				-representation.getFullBoundsReference().width / 2 - representation.getFullBoundsReference().getMinX(),
				-representation.getFullBoundsReference().height / 2 - representation.getFullBoundsReference().getMinY());
		setSelectionNode(representation);
		// TODO i18n
		setCaption("<html><center>Potassium Leak<br>Channel</center></html>");
	}

}
