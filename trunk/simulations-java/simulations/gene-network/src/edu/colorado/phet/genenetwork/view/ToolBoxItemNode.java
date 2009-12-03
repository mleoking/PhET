/* Copyright 2009, University of Colorado */

package edu.colorado.phet.genenetwork.view;

import java.awt.geom.Point2D;

import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform2D;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.common.piccolophet.event.CursorHandler;
import edu.colorado.phet.common.piccolophet.nodes.HTMLNode;
import edu.colorado.phet.genenetwork.model.IObtainGeneModelElements;
import edu.colorado.phet.genenetwork.model.SimpleModelElement;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolox.nodes.PComposite;

/**
 * Base class for the model elements that appear in the tool box.  This class
 * handles the common functionality, and subclasses implement the needed
 * unique behavior.
 * 
 * @author John Blanco
 */
public abstract class ToolBoxItemNode extends PComposite {
	
	//----------------------------------------------------------------------------
	// Class Data
	//----------------------------------------------------------------------------

	// Fixed transform for setting the size of the items in the tool box,
	// which may not be exactly what it is in the model.
	protected static final ModelViewTransform2D SCALING_MVT = 
		new ModelViewTransform2D(new Point2D.Double(0, 0), new Point2D.Double(0, 0), 5, true);
	
	private static final double CAPTION_OFFSET_FROM_SELECTION_NODE = 4;
	
    //----------------------------------------------------------------------------
    // Instance Data
    //----------------------------------------------------------------------------

	private final IObtainGeneModelElements model;
	private SimpleModelElementNode selectionNode = null;
	private HTMLNode caption = null;
	private SimpleModelElement modelElement = null;

    //----------------------------------------------------------------------------
    // Constructor(s)
    //----------------------------------------------------------------------------
    
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
            	// Release our reference to the model element so that we will
            	// create a new one if clicked again.
            	modelElement = null;
            }
        });
	}

	//----------------------------------------------------------------------------
    // Methods
    //----------------------------------------------------------------------------

	/**
	 * Method overridden by subclasses to set up the node that users will
	 * click on in order to add one to the model.
	 */
	protected abstract void initializeSelectionNode();
	
	/**
	 * Method overridden by subclasses for adding the appropriate model
	 * element to the model.
	 */
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
		caption.setFont(new PhetFont(16, true));
		addChild(caption);
	}
	
	private void updateLayout(){
		// This only does something if both the element node and the caption
		// are set.
		if (caption != null && selectionNode != null){
			caption.setOffset(-caption.getFullBoundsReference().getWidth() / 2,
					selectionNode.getFullBoundsReference().getMaxY() + CAPTION_OFFSET_FROM_SELECTION_NODE);
		}
	}
	
	protected IObtainGeneModelElements getModel(){
		return model;
	}
}
