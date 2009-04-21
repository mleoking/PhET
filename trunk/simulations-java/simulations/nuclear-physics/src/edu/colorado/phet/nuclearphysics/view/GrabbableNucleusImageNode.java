/* Copyright 2007-2008, University of Colorado */

package edu.colorado.phet.nuclearphysics.view;

import java.awt.geom.Point2D;
import java.util.ArrayList;

import edu.colorado.phet.common.piccolophet.event.CursorHandler;
import edu.colorado.phet.nuclearphysics.common.model.AtomicNucleus;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PDragEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.util.PDimension;

/**
 * This class adds the ability to grab and move the nucleus using the mouse
 * to the class that represents a nucleus as a fixed image.
 * 
 * @author John Blanco
 */
public class GrabbableNucleusImageNode extends AtomicNucleusImageNode {

    //------------------------------------------------------------------------
    // Instance Data
    //------------------------------------------------------------------------

    private ArrayList _listeners = new ArrayList();
	
    //------------------------------------------------------------------------
    // Constructor
    //------------------------------------------------------------------------

	public GrabbableNucleusImageNode(AtomicNucleus atomicNucleus) {
		super(atomicNucleus);
		
		setPickable(true);
		setChildrenPickable(true);

		// Put a cursor handler into place.
		addInputEventListener(new CursorHandler(CursorHandler.HAND));
		
        // Add a handle for mouse drag events.
        addInputEventListener( new PDragEventHandler(){

            public void startDrag(PInputEvent event){
            	super.startDrag(event);
                handleMouseStartDragEvent( event );
            }

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

    private void handleMouseStartDragEvent(PInputEvent event){
    	// Notify listeners that the user grabbed this node.
    	notifyNodeGrabbed();
    }
    
    private void handleMouseDragEvent(PInputEvent event){
    	// Move the node as indicated by the drag event.
        PNode draggedNode = event.getPickedNode();
        PDimension d = event.getDeltaRelativeTo(draggedNode);
        draggedNode.localToParent(d);
        AtomicNucleus nucleus = getNucleusRef();
        double newPosX = nucleus.getPositionReference().getX() + d.getWidth();
        double newPosY = nucleus.getPositionReference().getY() + d.getHeight();
        nucleus.setPosition(newPosX, newPosY);
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
    
    private void notifyNodeGrabbed(){
        for (int i = 0; i < _listeners.size(); i++){
            ((Listener)_listeners.get( i )).nodeGrabbed(this);
        }        
    }
    
    //------------------------------------------------------------------------
    // Inner Interfaces and Adapters
    //------------------------------------------------------------------------
    
    public static interface Listener {
        /**
         * This informs the listener that this node was grabbed by the user.
         * 
         * @param node - a reference to this node.
         */
        public void nodeGrabbed(GrabbableNucleusImageNode node);
        
        /**
         * This informs the listener that this node was released after having
         * been dragged.
         * 
         * @param node - a reference to this node.
         */
        public void nodeReleased(GrabbableNucleusImageNode node);
    }
}
