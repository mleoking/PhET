/* Copyright 2007-2008, University of Colorado */

package edu.colorado.phet.nuclearphysics.view;

import java.awt.Color;
import java.awt.Cursor;
import java.util.ArrayList;

import edu.colorado.phet.common.piccolophet.event.CursorHandler;
import edu.colorado.phet.nuclearphysics.view.BucketOfNucleiNode.Listener;
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
    // Instance Data
    //------------------------------------------------------------------------

    private ArrayList _listeners = new ArrayList();
	
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
            	super.drag(event);
                handleMouseDragEvent( event );
            }
            
            public void endDrag( PInputEvent event ){
                super.endDrag(event);
                System.out.println("end of drag");
                handleMouseEndDragEvent( event );
            }
        });
	}
	
    //----------------------------------------------------------------------------
    // Public Methods
    //----------------------------------------------------------------------------

	/**
     * This method allows the caller to register for notifications from this
     * object.
     * 
     * @param listener
     */
    public void addListener(Listener listener)
    {
        if ( !_listeners.contains( listener ) ){
            _listeners.add( listener );
        }
    }
    
    public boolean removeListener(Listener listener){
        return _listeners.remove( listener );
    }
    
    //----------------------------------------------------------------------------
    // Private Methods
    //----------------------------------------------------------------------------

    private void handleMouseDragEvent(PInputEvent event){
    }
    
    private void handleMouseEndDragEvent(PInputEvent event){
    	
    	// Notify listeners that the user released this node.
    	notifyNodeReleased();
    }
    
    private void notifyNodeReleased(){
        for (int i = 0; i < _listeners.size(); i++){
            ((Listener)_listeners.get( i )).nodeReleased(this);
        }        
    }
    
    //------------------------------------------------------------------------
    // Inner interfaces
    //------------------------------------------------------------------------
    
    public static interface Listener {
        /**
         * This informs the listener that this node was released after having
         * been dragged.
         * 
         * @param node - a reference to this node.
         */
        public void nodeReleased(GrabbableLabeledNucleusNode node);
    }
}
