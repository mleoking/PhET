package edu.colorado.phet.genenetwork.view;

import java.awt.geom.Point2D;

import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform2D;
import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.genenetwork.GeneNetworkStrings;
import edu.colorado.phet.genenetwork.model.IGeneNetworkModelControl;
import edu.colorado.phet.genenetwork.model.LacIGene;
import edu.colorado.phet.genenetwork.model.LacOperator;
import edu.colorado.phet.genenetwork.model.SimpleModelElement;

/**
 * Class that represents a LacI binding region, also known as the lac operator,
 * in the tool box, and that allows users to click on it to add it to the
 * model.
 * 
 * @author John Blanco
 */
public class LacOperatorToolBoxNode extends ToolBoxItemNode {

	public LacOperatorToolBoxNode(IGeneNetworkModelControl model, ModelViewTransform2D mvt, PhetPCanvas canvas) {
		super(model, mvt, canvas);
	}

	@Override
	protected void handleAddRequest(Point2D position) {
		setModelElement(getModel().createAndAddLacOperator(position));
	}

	@Override
	protected void initializeSelectionNode() {
		setSelectionNode(new SimpleModelElementNode(new LacOperator(getModel()), SCALING_MVT, true));
		setCaption(GeneNetworkStrings.LACI_BINDING_REGION_TOOL_BOX_CAPTION);
	}
	
	@Override
	protected void updatePositionWhenReleased() {
		getModelElement().setPosition(getModel().getDnaStrand().getLacOperatorLocation());
	}
	
	@Override
	protected void handleModelElementAdded(SimpleModelElement modelElement) {
		if (modelElement instanceof LacOperator){
			// Since only one of this type of model element can exist in the
			// model at once, when one comes into existence this node should
			// change state to indicate that another one can not be added.
			getSelectionNode().setGhostMode(true);
		}
	}
}
