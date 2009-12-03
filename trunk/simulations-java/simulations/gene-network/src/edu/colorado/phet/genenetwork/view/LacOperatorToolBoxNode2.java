package edu.colorado.phet.genenetwork.view;

import java.awt.geom.Point2D;

import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform2D;
import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.genenetwork.GeneNetworkStrings;
import edu.colorado.phet.genenetwork.model.IObtainGeneModelElements;
import edu.colorado.phet.genenetwork.model.LacOperator;

public class LacOperatorToolBoxNode2 extends ToolBoxItemNode {

	public LacOperatorToolBoxNode2(IObtainGeneModelElements model, ModelViewTransform2D mvt, PhetPCanvas canvas) {
		super(model, mvt, canvas);
	}

	@Override
	protected void addModelElement(Point2D position) {
		setModelElement(getModel().createAndAddLacOperator(position));
	}

	@Override
	protected void initializeSelectionNode() {
		setSelectionNode(new SimpleModelElementNode(new LacOperator(getModel()), SCALING_MVT, true));
		setCaption(GeneNetworkStrings.LACI_BINDING_REGION_TOOL_BOX_CAPTION);
	}
}
