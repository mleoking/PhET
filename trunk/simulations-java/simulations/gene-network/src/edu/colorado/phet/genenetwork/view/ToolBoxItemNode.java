/* Copyright 2009, University of Colorado */

package edu.colorado.phet.genenetwork.view;

import java.awt.Cursor;
import java.awt.Font;
import java.awt.geom.Point2D;

import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform2D;
import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.common.piccolophet.event.CursorHandler;
import edu.colorado.phet.common.piccolophet.nodes.HTMLNode;
import edu.colorado.phet.genenetwork.model.GeneNetworkModelAdapter;
import edu.colorado.phet.genenetwork.model.IGeneNetworkModelControl;
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
		new ModelViewTransform2D(new Point2D.Double(0, 0), new Point2D.Double(0, 0), 6, true);
	
	private static final double CAPTION_OFFSET_FROM_SELECTION_NODE = 4;
	
	// Font to use for labels.
	private static final Font LABEL_FONT = GeneNetworkFontFactory.getFont(16, Font.BOLD);
	
    //----------------------------------------------------------------------------
    // Instance Data
    //----------------------------------------------------------------------------

	private final IGeneNetworkModelControl model;
	private SimpleModelElementNode selectionNode = null;
	private HTMLNode caption = null;
	private SimpleModelElement modelElement = null;
	private CursorHandler cursorHandler = new CursorHandler(Cursor.HAND_CURSOR);

    //----------------------------------------------------------------------------
    // Constructor(s)
    //----------------------------------------------------------------------------
    
	public ToolBoxItemNode(final IGeneNetworkModelControl model, final ModelViewTransform2D mvt, final PhetPCanvas canvas) {
		
		this.model = model;
		initializeSelectionNode();
		updateLayout();
		
		// Set up handling of mouse events.
        addInputEventListener(cursorHandler);
        addInputEventListener(new PBasicInputEventHandler(){
        	@Override
            public void mousePressed(PInputEvent event) {
        		// Figure out the correspondence between this press event and model space.
    			Point2D mouseCanvasPos = event.getCanvasPosition();
    			Point2D mouseWorldPos = new Point2D.Double(mouseCanvasPos.getX(), mouseCanvasPos.getY()); 
    			canvas.getPhetRootNode().screenToWorld(mouseWorldPos);
    			Point2D mouseModelPos = mvt.viewToModel(mouseWorldPos);
        		
        		if (modelElement == null){
        			if (okToAddElement()){
        				// Add the new model element to the model.
        				handleAddRequest(mouseCanvasPos);
        				modelElement.setDragging(true);
        				modelElement.setPosition(mouseModelPos);
        			}
        			else{
        				// Ignore this request, since it is not okay to add
        				// another element (probably because the model already
        				// has one and another isn't allowed).
        			}
        		}
        		else{
        			// This isn't expected to happen.  If it does, we need to
        			// figure out why.
        			System.out.println(getClass().getName() + " - Warning: Mouse press event received but element already exists.");
        			assert false;
        		}
            }

        	@Override
            public void mouseDragged(PInputEvent event) {
        		// Figure out the correspondence between this drag event and model space.
    			Point2D mouseCanvasPos = event.getCanvasPosition();
    			Point2D mouseWorldPos = new Point2D.Double(mouseCanvasPos.getX(), mouseCanvasPos.getY()); 
    			canvas.getPhetRootNode().screenToWorld(mouseWorldPos);
    			Point2D mouseModelPos = mvt.viewToModel(mouseWorldPos);
        		
       			// Move the model element (if it exists).
        		if (modelElement != null){
        			modelElement.setPosition(mouseModelPos);
        		}
            }

            @Override
            public void mouseReleased(PInputEvent event) {
    			Point2D mouseCanvasPos = event.getCanvasPosition();
    			Point2D mouseWorldPos = new Point2D.Double(mouseCanvasPos.getX(), mouseCanvasPos.getY()); 
    			canvas.getPhetRootNode().screenToWorld(mouseWorldPos);
            	if (modelElement != null){
            		modelElement.setDragging(false);
            		// Release our reference to the model element.
            		modelElement = null;
            	}
            }
        });
        
        // Register for model events that might affect us.
        model.addListener(new GeneNetworkModelAdapter(){
        	@Override
        	public void modelElementAdded(SimpleModelElement modelElement) { 
        		handleModelElementAdded(modelElement);
        	};
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
	 * Handle a request made the the user to add an instance of the
	 * corresponding model element to the model.  This method is overridden
	 * by subclasses for implementing the appropriate behavior.
	 */
	protected abstract void handleAddRequest(Point2D position);
	
	/**
	 * Obtain a boolean value that indicates whether it is okay to add an
	 * instance of the corresponding model element to the model.  The most
	 * common reason for this to return false is when only one such model
	 * element is allowed and it has already been added.
	 */
	protected abstract boolean okToAddElement();
	
	/**
	 * Called when a new simple model element is added to the model.  This
	 * method should be overridden for subclass-specific behavior.
	 * 
	 * @param modelElement
	 */
	protected void handleModelElementAdded(SimpleModelElement modelElement){
		// Does nothing in base class.
	}
	
	protected void setSelectionNode(SimpleModelElementNode selectionNode){
		assert this.selectionNode == null; // Currently doesn't support setting this multiple times.
		this.selectionNode = selectionNode;
		addChild(selectionNode);
	}
	
	protected void setModelElement(SimpleModelElement modelElement){
		assert this.modelElement == null; // Shouldn't end up setting this on top of another. 
		this.modelElement = modelElement;
	}
	
	protected SimpleModelElementNode getSelectionNode(){
		return selectionNode;
	}
	
	protected void setCaption(String captionString){
		caption = new HTMLNode(captionString);
		caption.setFont(LABEL_FONT);
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
	
	protected void removeCursorHandler(){
		removeInputEventListener(cursorHandler);
	}
	
	protected void restoreCursorHandler(){
		addInputEventListener(cursorHandler);
	}
	
	protected IGeneNetworkModelControl getModel(){
		return model;
	}
}
