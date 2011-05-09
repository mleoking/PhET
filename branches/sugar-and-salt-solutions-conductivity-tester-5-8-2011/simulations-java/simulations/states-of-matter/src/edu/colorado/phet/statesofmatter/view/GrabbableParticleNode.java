// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.statesofmatter.view;

import java.awt.Cursor;

import edu.colorado.phet.common.piccolophet.event.CursorHandler;
import edu.colorado.phet.statesofmatter.model.DualAtomModel;
import edu.colorado.phet.statesofmatter.model.particle.StatesOfMatterAtom;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PDragEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.util.PDimension;

/**
 * This extension of the ParticleNode class allows users to grab the node and
 * move it, thus changing the position within the underlying model.
 *
 * @author John Blanco
 */
public class GrabbableParticleNode extends ParticleForceNode {

    //----------------------------------------------------------------------------
    // Class Data
    //----------------------------------------------------------------------------
    
    //----------------------------------------------------------------------------
    // Instance Data
    //----------------------------------------------------------------------------

    DualAtomModel m_model;
    double m_minX; // The minimum value allowed in the x direction.
    double m_maxX; // The minimum value allowed in the x direction.
    
    //----------------------------------------------------------------------------
    // Constructor(s)
    //----------------------------------------------------------------------------
    
    /**
     * Constructor.
     * @param particle - The particle within the model.
     * @param mvt - Model-view transform.
     * @param enableOverlap TODO
     * @param minX - Minimum value in the X direction to which the particle can be moved.
     * @param maxX - Maximum value in the X direction to which the particle can be moved.
     */
    public GrabbableParticleNode( DualAtomModel model, StatesOfMatterAtom particle, ModelViewTransform mvt,
    		boolean useGradient, boolean enableOverlap, double minX, double maxX ) {
        
        super( particle, mvt, useGradient, enableOverlap );

        m_model = model;
        m_minX = minX;
        m_maxX = maxX;
        
        // This node will need to be pickable so the user can grab it.
        setPickable( true );
        setChildrenPickable( true );
        
        // Put a cursor handler into place.
        addInputEventListener( new CursorHandler(Cursor.HAND_CURSOR) );
        
        addInputEventListener( new PDragEventHandler(){
            
            public void startDrag( PInputEvent event) {
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
    
    /**
     * Constructor
     */
    public GrabbableParticleNode( DualAtomModel model, StatesOfMatterAtom particle, ModelViewTransform mvt, 
            boolean useGradient ) {

        this(model, particle, mvt, useGradient, true, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY);
    }
    
    //----------------------------------------------------------------------------
    // Accessor Methods
    //----------------------------------------------------------------------------
    
    public double getMinX() {
        return m_minX;
    }

    
    public void setMinX( double minX ) {
        m_minX = minX;
    }

    
    public double getMaxX() {
        return m_maxX;
    }

    
    public void setMaxX( double maxX ) {
        m_maxX = maxX;
    }

    //----------------------------------------------------------------------------
    // Private Methods
    //----------------------------------------------------------------------------

    private void handleMouseStartDragEvent(PInputEvent event){
        // Stop the model from moving the particle at the same time the user
        // is moving it.
        m_model.setMotionPaused( true );
    }

    private void handleMouseDragEvent(PInputEvent event){

    	// Only allow the user to move unbonded atoms.
    	if (m_model.getBondingState() != DualAtomModel.BONDING_STATE_UNBONDED){
    		// Need to release the bond before we can move the atom.
    		m_model.releaseBond();
    	}
    		
		// Determine the amount of motion indicated by the drag event.
        PNode draggedNode = event.getPickedNode();
        PDimension d = event.getDeltaRelativeTo(draggedNode);
        draggedNode.localToParent(d);

        // Make sure we don't exceed the positional limits.
        double newPosX = m_particle.getX() + d.width;
        if (newPosX > m_maxX) {
            newPosX = m_maxX;
        }
        else if (newPosX < m_minX) {
            newPosX = m_minX;
        }
        
        // Move the particle based on the amount of mouse movement.
        m_particle.setPosition( newPosX, m_particle.getY() );
    		
    }
    
    private void handleMouseEndDragEvent(PInputEvent event){
        // Let the model move the particles again.  Note that this happens
        // even if the motion was paused by some other means.
        m_model.setMotionPaused( false );
    }
}
