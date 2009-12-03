package edu.colorado.phet.genenetwork.view;

import java.awt.geom.Point2D;

import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform2D;
import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.genenetwork.GeneNetworkStrings;
import edu.colorado.phet.genenetwork.model.IObtainGeneModelElements;
import edu.colorado.phet.genenetwork.model.LacPromoter;

/**
 * Class that represents a polymerase binding region, also known as the lac
 * promoter, in the tool box, and that allows users to click on it to add it
 * to the model.
 * 
 * @author John Blanco
 */
public class LacPromoterToolBoxNode2 extends ToolBoxItemNode {

	public LacPromoterToolBoxNode2(IObtainGeneModelElements model, ModelViewTransform2D mvt, PhetPCanvas canvas) {
		super(model, mvt, canvas);
	}

	@Override
	protected void addModelElement(Point2D position) {
		setModelElement(getModel().createAndAddLacPromoter(position));
	}

	@Override
	protected void initializeSelectionNode() {
		setSelectionNode(new SimpleModelElementNode(new LacPromoter(getModel()), SCALING_MVT, true));
		setCaption(GeneNetworkStrings.POLYMERASE_BINDING_REGION_TOOL_BOX_CAPTION);
	}
}
