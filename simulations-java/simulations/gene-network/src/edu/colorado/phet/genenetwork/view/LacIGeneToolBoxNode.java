package edu.colorado.phet.genenetwork.view;

import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform2D;
import edu.colorado.phet.genenetwork.model.IObtainGeneModelElements;
import edu.colorado.phet.genenetwork.model.LacIGene;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PPath;

/**
 * Class the represents LacI in the tool box, and that allows users to
 * click on it to add it to the model.
 * 
 * @author John Blanco
 */
public class LacIGeneToolBoxNode extends PNode {

	private PPath availableForInsertionNode;
	
	public LacIGeneToolBoxNode(IObtainGeneModelElements model, ModelViewTransform2D mvt) {
		availableForInsertionNode = new SimpleModelElementNode(new LacIGene(model), mvt, true);
		addChild(availableForInsertionNode);
	}
}
