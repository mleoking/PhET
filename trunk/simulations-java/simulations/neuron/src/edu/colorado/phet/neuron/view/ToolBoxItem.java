/* Copyright 2009, University of Colorado */

package edu.colorado.phet.neuron.view;

import java.awt.Font;
import java.awt.geom.Point2D;

import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform2D;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.common.piccolophet.event.CursorHandler;
import edu.colorado.phet.common.piccolophet.nodes.HTMLNode;
import edu.colorado.phet.genenetwork.model.SimpleModelElement;
import edu.colorado.phet.neuron.model.MembraneChannel;
import edu.colorado.phet.neuron.model.MembraneDiffusionModel;
import edu.umd.cs.piccolo.PNode;
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
public abstract class ToolBoxItem extends PComposite {
	
	//----------------------------------------------------------------------------
	// Class Data
	//----------------------------------------------------------------------------

	// Fixed transform for setting the size of the items in the tool box,
	// which may not be exactly what it is in the model.
	protected static final ModelViewTransform2D SCALING_MVT = 
		new ModelViewTransform2D(new Point2D.Double(0, 0), new Point2D.Double(0, 0), 10, true);
	
	private static final double CAPTION_OFFSET_FROM_SELECTION_NODE = 4;
	
	// Font to use for labels.
	private static final Font LABEL_FONT = new PhetFont(14, true);
	
    //----------------------------------------------------------------------------
    // Instance Data
    //----------------------------------------------------------------------------

	private final MembraneDiffusionModel model;
	private final ModelViewTransform2D mvt;
	private PNode selectionNode = null;
	private HTMLNode caption = null;
	private MembraneChannel membraneChannel = null;

    //----------------------------------------------------------------------------
    // Constructor(s)
    //----------------------------------------------------------------------------
    
	public ToolBoxItem(final MembraneDiffusionModel model, final ModelViewTransform2D mvt, final PhetPCanvas canvas) {
		
		this.model = model;
		this.mvt = mvt;
		initializeSelectionNode();
		updateLayout();
		
		// Set up handling of mouse events.
        addInputEventListener(new CursorHandler());
        addInputEventListener(new PBasicInputEventHandler(){
        	@Override
            public void mousePressed(PInputEvent event) {
        		// Figure out the correspondence between this press event and model space.
    			Point2D mouseCanvasPos = event.getCanvasPosition();
    			Point2D mouseWorldPos = new Point2D.Double(mouseCanvasPos.getX(), mouseCanvasPos.getY()); 
    			canvas.getPhetRootNode().screenToWorld(mouseWorldPos);
    			Point2D mouseModelPos = mvt.viewToModel(mouseWorldPos);
        		
        		if (membraneChannel == null){
        			// Add the new model element to the model.
        			handleAddRequest(mouseCanvasPos);
        			membraneChannel.setCenterLocation(mouseModelPos);
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
        		
        		if (membraneChannel == null){
        			// Add the new model element to the model.
        			System.out.println(getClass().getName() + " - Warning: Drag event received but no model element yet, adding it.");
        			handleAddRequest(mouseCanvasPos);
        		}
        		else{
    				// If a model element was added, it is now being dragged.
        		}
        		
       			// Move the model element (if it exists).
        		if (membraneChannel != null){
        			membraneChannel.setCenterLocation(mouseModelPos);
        		}
            }

            @Override
            public void mouseReleased(PInputEvent event) {
            	model.releaseUserControlledMembraneChannel();
            	if (membraneChannel != null){
            		// Release our reference to the model element so that we will
            		// create a new one if clicked again.
            		membraneChannel = null;
            	}
            }
        });
	}

	//----------------------------------------------------------------------------
    // Methods
    //----------------------------------------------------------------------------

	protected MembraneDiffusionModel getModel() {
		return model;
	}

	protected ModelViewTransform2D getMvt() {
		return mvt;
	}
	
	protected void setMembraneChannel(MembraneChannel membraneChannel){
		this.membraneChannel = membraneChannel;
	}
	
	protected MembraneChannel getMembraneChannel(){
		return membraneChannel;
	}

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
	 * Called when a new simple model element is added to the model.  This
	 * method should be overridden for subclass-specific behavior.
	 * 
	 * @param modelElement
	 */
	protected void handleModelElementAdded(SimpleModelElement modelElement){
		// Does nothing in base class.
	}
	
	protected void setSelectionNode(PNode selectionNode){
		assert this.selectionNode == null; // Currently doesn't support setting this multiple times.
		this.selectionNode = selectionNode;
		addChild(selectionNode);
	}
	
	protected PNode getSelectionNode(){
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
}
