package edu.colorado.phet.genenetwork.view;

import java.awt.geom.Point2D;

import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform2D;
import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.common.piccolophet.event.CursorHandler;
import edu.colorado.phet.common.piccolophet.nodes.HTMLNode;
import edu.colorado.phet.genenetwork.model.IObtainGeneModelElements;
import edu.colorado.phet.genenetwork.model.SimpleModelElement;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolox.nodes.PComposite;

public abstract class ToolBoxItemNode extends PComposite {
	
	protected static final ModelViewTransform2D SCALING_MVT = 
		new ModelViewTransform2D(new Point2D.Double(0, 0), new Point2D.Double(0, 0), 5, true);
	private static final double CAPTION_OFFSET_FROM_SELECTION_NODE = 4;
	
	private IObtainGeneModelElements model;
	private SimpleModelElementNode selectionNode;
	private HTMLNode caption;
	private SimpleModelElement modelElement = null;

	public ToolBoxItemNode(final IObtainGeneModelElements model, final ModelViewTransform2D mvt, final PhetPCanvas canvas) {
		
		this.model = model;
		initializeSelectionNode();
		updateLayout();
		
        addInputEventListener(new CursorHandler());
        addInputEventListener(new PBasicInputEventHandler(){
        	@Override
            public void mouseDragged(PInputEvent event) {
        		// Figure out the correspondence between this drag event and model space.
    			Point2D mouseCanvasPos = event.getCanvasPosition();
    			Point2D mouseWorldPos = new Point2D.Double(mouseCanvasPos.getX(), mouseCanvasPos.getY()); 
    			canvas.getPhetRootNode().screenToWorld(mouseWorldPos);
    			Point2D mouseModelPos = mvt.viewToModel(mouseWorldPos);
        		
        		if (modelElement == null){
        			// Add the new model element to the model.
        			addModelElement(mouseCanvasPos);
        		}
       			// Move the model element.
       			modelElement.setPosition(mouseModelPos);
            }

            @Override
            public void mouseReleased(PInputEvent event) {
            }
        });
	}

	protected abstract void initializeSelectionNode();
	protected abstract void addModelElement(Point2D position);
	
	protected void setSelectionNode(SimpleModelElementNode selectionNode){
		assert this.selectionNode == null; // Currently doesn't support setting this multiple times.
		this.selectionNode = selectionNode;
		addChild(selectionNode);
	}
	
	protected void setModelElement(SimpleModelElement modelElement){
		assert this.modelElement == null; // Shouldn't end up setting this on top of another. 
		this.modelElement = modelElement;
	}
	
	protected void setCaption(String captionString){
		caption = new HTMLNode(captionString);
	}
	
	private void updateLayout(){
		// This only does something if both the element node and the caption
		// are set.
		if (caption != null && selectionNode != null){
			caption.setOffset(-caption.getFullBoundsReference().getWidth() / 2,
					selectionNode.getFullBoundsReference().getMinY() - CAPTION_OFFSET_FROM_SELECTION_NODE);
		}
	}
	
	protected IObtainGeneModelElements getModel(){
		return model;
	}
}
