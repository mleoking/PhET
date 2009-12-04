package edu.colorado.phet.genenetwork.view;

import java.awt.geom.Point2D;

import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform2D;
import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.genenetwork.model.IGeneNetworkModelControl;
import edu.colorado.phet.genenetwork.model.LacIGene;
import edu.colorado.phet.genenetwork.model.SimpleModelElement;

/**
 * Class the represents LacI in the tool box, and that allows users to
 * click on it to add it to the model.
 * 
 * @author John Blanco
 */
public class LacIGeneToolBoxNode extends ToolBoxItemNode {

	public LacIGeneToolBoxNode(IGeneNetworkModelControl model, ModelViewTransform2D mvt, PhetPCanvas canvas) {
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
	
	@Override
	protected void updatePositionWhenReleased() {
		getModelElement().setPosition(getModel().getDnaStrand().getLacIGeneLocation());
	}

	@Override
	protected void handleModelElementAdded(SimpleModelElement modelElement) {
		if (modelElement instanceof LacIGene){
			// Since only one of this type of model element can exist in the
			// model at once, when one comes into existence this node should
			// change state to indicate that another one can not be added.
			getSelectionNode().setGhostMode(true);
		}
	}
}
