package edu.colorado.phet.genenetwork.view;

import java.awt.geom.Point2D;

import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform2D;
import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.genenetwork.GeneNetworkStrings;
import edu.colorado.phet.genenetwork.model.IObtainGeneModelElements;
import edu.colorado.phet.genenetwork.model.RnaPolymerase;

/**
 * Class that represents an RNA polymerase enzyme in the tool box, and that
 * allows users to click on it to add it to the model.
 * 
 * @author John Blanco
 */
public class RnaPolymeraseToolBoxNode2 extends ToolBoxItemNode {

	public RnaPolymeraseToolBoxNode2(IObtainGeneModelElements model, ModelViewTransform2D mvt, PhetPCanvas canvas) {
		super(model, mvt, canvas);
	}

	@Override
	protected void addModelElement(Point2D position) {
		setModelElement(getModel().createAndAddRnaPolymerase(position));
	}

	@Override
	protected void initializeSelectionNode() {
		setSelectionNode(new SimpleModelElementNode(new RnaPolymerase(getModel()), SCALING_MVT, true));
		setCaption(GeneNetworkStrings.POLYMERASE_TOOL_BOX_CAPTION);
	}
}
