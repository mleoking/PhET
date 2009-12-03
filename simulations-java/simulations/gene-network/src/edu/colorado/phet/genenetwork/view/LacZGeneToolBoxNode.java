package edu.colorado.phet.genenetwork.view;

import java.awt.geom.Point2D;

import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform2D;
import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.common.piccolophet.event.CursorHandler;
import edu.colorado.phet.genenetwork.model.IObtainGeneModelElements;
import edu.colorado.phet.genenetwork.model.LacZGene;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolox.nodes.PComposite;

/**
 * Class the represents LacZ in the tool box, and that allows users to
 * click on it to add it to the model.
 * 
 * @author John Blanco
 */
public class LacZGeneToolBoxNode extends PComposite {

	private static final ModelViewTransform2D SCALING_MVT = 
		new ModelViewTransform2D(new Point2D.Double(0, 0), new Point2D.Double(0, 0), 5, true);
	private PPath availableForInsertionNode;
	private LacZGene lacZGene = null; // Element in the model.
	
	public LacZGeneToolBoxNode(final IObtainGeneModelElements model, final ModelViewTransform2D mvt, final PhetPCanvas canvas) {
		availableForInsertionNode = new SimpleModelElementNode(new LacZGene(model), SCALING_MVT, true);
		addChild(availableForInsertionNode);
		
        addInputEventListener(new CursorHandler());
        addInputEventListener(new PBasicInputEventHandler(){
        	@Override
            public void mouseDragged(PInputEvent event) {
        		// Figure out the correspondence between this drag event and model space.
    			Point2D mouseCanvasPos = event.getCanvasPosition();
    			Point2D mouseWorldPos = new Point2D.Double(mouseCanvasPos.getX(), mouseCanvasPos.getY()); 
    			canvas.getPhetRootNode().screenToWorld(mouseWorldPos);
    			Point2D mouseModelPos = mvt.viewToModel(mouseWorldPos);
        		
        		if (lacZGene == null){
        			// Add a new LacZ to the model.
        			lacZGene = model.createAndAddLacZGene(mouseModelPos);
        		}
       			// Move the model element.
       			lacZGene.setPosition(mouseModelPos);
            }

            @Override
            public void mouseReleased(PInputEvent event) {
            }
        });

	}
}
