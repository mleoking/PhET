/* Copyright 2007-2008, University of Colorado */

package edu.colorado.phet.nuclearphysics.view;

import java.awt.Color;
import java.awt.Cursor;

import edu.colorado.phet.common.piccolophet.event.CursorHandler;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PDragEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.util.PDimension;

/**
 * This class extends the LabeledNucleusNode to add the ability to grab and
 * move the node.
 * 
 * @author John Blanco
 */
public class GrabbableLabeledNucleusNode extends LabeledNucleusNode {

    //------------------------------------------------------------------------
    // Constructor
    //------------------------------------------------------------------------

	public GrabbableLabeledNucleusNode(String imageName, String isotopeNumber, String chemicalSymbol, 
			Color labelColor) {
		
		// TODO: JPB TBD - Figure out if this is needed or if it is called automatically.
		super(imageName, isotopeNumber, chemicalSymbol, labelColor);
		
		// Set the nucleus node to be "pickable", meaning that it can be grabbed with the mouse.
		setPickable(true);
		setChildrenPickable(true);
		
        // Put a cursor handler into place.
        addInputEventListener( new CursorHandler(Cursor.HAND_CURSOR) );
        
        // Add a handle for mouse drag events.
        addInputEventListener( new PDragEventHandler(){
            
            public void drag(PInputEvent event){
                handleMouseDragEvent( event );
            }
            
            public void endDrag( PInputEvent event ){
                super.endDrag(event);     
                handleMouseEndDragEvent( event );
            }
        });
	}
	
    //----------------------------------------------------------------------------
    // Private Methods
    //----------------------------------------------------------------------------

    private void handleMouseDragEvent(PInputEvent event){
        
    	/*
        PNode draggedNode = event.getPickedNode();
        PDimension d = event.getDeltaRelativeTo(draggedNode);
        draggedNode.localToParent(d);

        // Move the particle based on the amount of mouse movement.
        setOffset( getFullBoundsReference().getX() + d.width, getFullBoundsReference().getY() + d.height );
        System.out.println("x = " + (getFullBoundsReference().getX() + d.width) + ", y = " + (getFullBoundsReference().getY() + d.height));
        */
        PNode draggedNode = event.getPickedNode();
        PDimension d = event.getDeltaRelativeTo(draggedNode);
        draggedNode.localToParent(d);
        offset(d.getWidth(), d.getHeight());
    }
    
    private void handleMouseEndDragEvent(PInputEvent event){
    	// TODO: JPB TBD - This is where the particle will be added to the model,
    	// but for now it is stubbed.
    }
}
