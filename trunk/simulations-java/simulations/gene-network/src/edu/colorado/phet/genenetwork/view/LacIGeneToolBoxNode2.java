package edu.colorado.phet.genenetwork.view;

import java.awt.geom.Point2D;

import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform2D;
import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.genenetwork.model.IObtainGeneModelElements;
import edu.colorado.phet.genenetwork.model.LacIGene;

public class LacIGeneToolBoxNode2 extends ToolBoxItemNode {

	public LacIGeneToolBoxNode2(IObtainGeneModelElements model, ModelViewTransform2D mvt, PhetPCanvas canvas) {
		super(model, mvt, canvas);
	}

	@Override
	protected void addModelElement(Point2D position) {
		setModelElement(getModel().createAndAddLacIGene(position));
	}

	@Override
	protected void initializeSelectionNode() {
		setSelectionNode(new SimpleModelElementNode(new LacIGene(getModel()), SCALING_MVT, true));
	}
}
