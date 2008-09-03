/* Copyright 2008, University of Colorado */

package edu.colorado.phet.statesofmatter.view;

import java.awt.Cursor;

import edu.colorado.phet.common.piccolophet.event.CursorHandler;
import edu.colorado.phet.statesofmatter.model.DualParticleModel;
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

    DualParticleModel m_model;
    
    //----------------------------------------------------------------------------
    // Constructor(s)
    //----------------------------------------------------------------------------
    
    /**
     * Constructor
     * 
     * @param particle - The particle within the model.
     * @param mvt - Model-view transform.
     * @param useGradient - A boolean that controls whether to use a gradient.
     */
    public GrabbableParticleNode( DualParticleModel model, StatesOfMatterAtom particle, ModelViewTransform mvt, 
            boolean useGradient ) {
        
        super( particle, mvt, useGradient );

        m_model = model;
        
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
    
    //----------------------------------------------------------------------------
    // Private Methods
    //----------------------------------------------------------------------------

    private void handleMouseStartDragEvent(PInputEvent event){
        // Stop the model from moving the particle at the same time the user
        // is moving it.
        m_model.setParticleMotionPaused( true );
    }

    private void handleMouseDragEvent(PInputEvent event){
        
        PNode draggedNode = event.getPickedNode();
        PDimension d = event.getDeltaRelativeTo(draggedNode);
        draggedNode.localToParent(d);

        // Move the particle based on the amount of mouse movement.
        m_particle.setPosition( m_particle.getX() + d.width, m_particle.getY() );
    }
    
    private void handleMouseEndDragEvent(PInputEvent event){
        // Let the model move the particles again.  Note that this happens
        // even if the motion was paused by some other means.
        m_model.setParticleMotionPaused( false );
    }
}
