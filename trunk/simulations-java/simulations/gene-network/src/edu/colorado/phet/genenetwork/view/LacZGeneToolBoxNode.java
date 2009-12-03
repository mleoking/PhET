package edu.colorado.phet.genenetwork.view;

import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;
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
        		if (lacZGene == null){
        			System.out.println("canvas position: " + event.getPosition());
        			// Add a new LacZ to the model.
        			AffineTransform screenToIntermediateTransform = null;
        			try {
						screenToIntermediateTransform = event.getCamera().getTransform().createInverse();
						screenToIntermediateTransform = canvas.getTransform().createInverse();
					} catch (NoninvertibleTransformException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
        			Point2D mouseScreenPos = event.getPosition();
        			System.out.println("Mouse pos in screen coords: " + mouseScreenPos);
        			Point2D mouseIntermediatePos = screenToIntermediateTransform.transform(mouseScreenPos, null);
        			System.out.println("Mouse pos in intermediate coords: " + mouseIntermediatePos);
        			Point2D mouseModelPos = mvt.viewToModel(mouseIntermediatePos);
        			System.out.println("Mouse pos in model coords: " + mouseModelPos);
        			lacZGene = model.createAndAddLacZGene(mouseModelPos);
        		}
        		else{
        			// Move the gene.
            		Point2D mouseMovement = mvt.viewToModelDifferential(
            				new Point2D.Double(event.getDeltaRelativeTo(getParent()).width,
            				event.getDeltaRelativeTo(getParent()).height));
        			lacZGene.move(mouseMovement.getX(), mouseMovement.getY());
        			System.out.println("Mouse Pos: " + event.getCanvasPosition());
        		}
            }

            @Override
            public void mouseReleased(PInputEvent event) {
            }
        });

	}
}
