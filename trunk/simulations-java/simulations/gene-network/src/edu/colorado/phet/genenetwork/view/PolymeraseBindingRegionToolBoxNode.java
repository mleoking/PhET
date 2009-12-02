package edu.colorado.phet.genenetwork.view;

import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform2D;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.nodes.HTMLNode;
import edu.colorado.phet.genenetwork.GeneNetworkStrings;
import edu.colorado.phet.genenetwork.model.IObtainGeneModelElements;
import edu.colorado.phet.genenetwork.model.LacOperator;
import edu.colorado.phet.genenetwork.model.LacPromoter;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PPath;

/**
 * Class that represents a polymerase binding region, also known as the lac
 * promoter, in the tool box, and that allows users to click on it to add it
 * to the model.
 * 
 * @author John Blanco
 */
public class PolymeraseBindingRegionToolBoxNode extends PNode {

	private PPath availableForInsertionNode;
	private HTMLNode caption;
	
	public PolymeraseBindingRegionToolBoxNode(IObtainGeneModelElements model, ModelViewTransform2D mvt) {
		
		availableForInsertionNode = new SimpleModelElementNode(new LacPromoter(model), mvt, false);
		addChild(availableForInsertionNode);
		
		// Set up the caption.
		caption = new HTMLNode(GeneNetworkStrings.POLYMERASE_BINDING_REGION_TOOL_BOX_CAPTION);
		caption.setFont(new PhetFont(16, true));
		addChild(caption);
		caption.setOffset(-caption.getFullBoundsReference().getWidth() / 2,
				availableForInsertionNode.getFullBoundsReference().height);
	}
}
