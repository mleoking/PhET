package edu.colorado.phet.genenetwork.view;

import java.awt.geom.Point2D;

import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform2D;
import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.genenetwork.GeneNetworkStrings;
import edu.colorado.phet.genenetwork.model.IGeneNetworkModelControl;
import edu.colorado.phet.genenetwork.model.LacIPromoter;
import edu.colorado.phet.genenetwork.model.ModelElementListenerAdapter;
import edu.colorado.phet.genenetwork.model.SimpleModelElement;

/**
 * Class that represents a polymerase binding region, also known as the lac I
 * promoter, in the tool box, and that allows users to click on it to add it
 * to the model.
 * 
 * @author John Blanco
 */
public class LacIPromoterToolBoxNode extends ToolBoxItemNode {

	public LacIPromoterToolBoxNode(IGeneNetworkModelControl model, ModelViewTransform2D mvt, PhetPCanvas canvas) {
		super(model, mvt, canvas);
	}

	@Override
	protected void handleAddRequest(Point2D position) {
		if (okToAddElement()){
			// No corresponding model element currently exists, so go ahead
			// and add one.
			setModelElement(getModel().createAndAddLacIPromoter(position));
		}
	}
	
	@Override
	protected boolean okToAddElement() {
		return (getModel().getLacIPromoter() == null);
	}

	@Override
	protected void initializeSelectionNode() {
		setSelectionNode(new SimpleModelElementNode(new LacIPromoter(getModel()), SCALING_MVT, true));
		setCaption(GeneNetworkStrings.LAC_I_PROMOTER_TOOL_BOX_CAPTION);
	}
	
	@Override
	protected void handleModelElementAdded(SimpleModelElement modelElement) {
		if (modelElement instanceof LacIPromoter){
			// Since only one of this type of model element can exist in the
			// model at once, when one comes into existence this node should
			// change state to indicate that another one can not be added.
			getSelectionNode().setGhostMode(true);

			// Listen for when this element is removed from the model and exit
			// "ghost mode" when it does.
			modelElement.addListener(new ModelElementListenerAdapter(){
				public void removedFromModel() {
					getSelectionNode().setGhostMode(false);
				};
			});
		}
	}
}
