/* Copyright 2008, University of Colorado */

package edu.colorado.phet.statesofmatter.view;

import java.awt.Cursor;
import java.awt.geom.Rectangle2D;

import edu.colorado.phet.common.piccolophet.event.CursorHandler;
import edu.colorado.phet.statesofmatter.StatesOfMatterConstants;
import edu.colorado.phet.statesofmatter.StatesOfMatterResources;
import edu.colorado.phet.statesofmatter.model.MultipleParticleModel;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PDragEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.nodes.PImage;
import edu.umd.cs.piccolo.util.PDimension;

/**
 * This class represents a node that looks like a large finger, which can be
 * used to push down on things.
 */
public class PointingHandNode extends PNode {

    //----------------------------------------------------------------------------
    // Class Data
    //----------------------------------------------------------------------------
    
    // Width of the finger node as a proportion of the width of the particle
    // container.
    private static final double NODE_WIDTH_PROPORTION = 0.65;
    
    // Horizontal position of the node as function of the container width.
    private static final double NODE_X_POS_PROPORTION = 0.20;
    
    // File name of the primary image.
    public static final String PRIMARY_IMAGE = "finger-2.png";
    
    //----------------------------------------------------------------------------
    // Instance Data
    //----------------------------------------------------------------------------
    
    private PImage m_fingerImageNode;
    private MultipleParticleModel m_model;
    private double m_scale;
    private double m_mouseMovementAmount;
    private double m_containerSizeAtDragStart;

    //----------------------------------------------------------------------------
    // Constructor
    //----------------------------------------------------------------------------

    public PointingHandNode(MultipleParticleModel model){

        m_model = model;
        Rectangle2D containerRect = m_model.getParticleContainerRect();
        double desiredWidth = containerRect.getWidth() * NODE_WIDTH_PROPORTION;
        
        // Listen to the model for notifications of changes to the container size.
        m_model.addListener( new MultipleParticleModel.Adapter(){
            public void containerSizeChanged(){
                handleContainerSizeChanged();
            }
        });
        
        // Load and scale the image.
        m_fingerImageNode = StatesOfMatterResources.getImageNode( PRIMARY_IMAGE );
        m_scale = desiredWidth / m_fingerImageNode.getFullBoundsReference().width;
        m_fingerImageNode.scale( m_scale );
        
        // Set up a cursor handler so that the user will get an indication
        // that the node can be moved.
        m_fingerImageNode.setPickable( true );
        m_fingerImageNode.addInputEventListener( new CursorHandler(Cursor.N_RESIZE_CURSOR) );
        
        // Set ourself up to listen for and handle mouse dragging events.
        m_fingerImageNode.addInputEventListener( new PDragEventHandler(){
            
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

        // Add the finger node as a child.
        addChild(m_fingerImageNode);

        // Set our initial offset.
        setOffset( containerRect.getX() + containerRect.getWidth() * NODE_X_POS_PROPORTION, 
                -getFullBoundsReference().height );
    }

    //----------------------------------------------------------------------------
    // Private Methods
    //----------------------------------------------------------------------------
    
    private void handleMouseDragEvent(PInputEvent event){
        
        PNode draggedNode = event.getPickedNode();
        PDimension d = event.getDeltaRelativeTo(draggedNode);
        draggedNode.localToParent(d);
        m_mouseMovementAmount += d.getHeight();

        // Resize the container based on the amount that the node has moved.
        m_model.setTargetParticleContainerHeight( m_containerSizeAtDragStart - m_mouseMovementAmount );
    }
    
    private void handleMouseStartDragEvent(PInputEvent event){
        m_mouseMovementAmount = 0;
        m_containerSizeAtDragStart = m_model.getParticleContainerHeight();
    }

    private void handleMouseEndDragEvent(PInputEvent event){
        // Set the target size to the current size, which will stop any change
        // in size that is currently underway.
        m_model.setTargetParticleContainerHeight( m_model.getParticleContainerHeight() );
    }

    private void handleContainerSizeChanged(){
        Rectangle2D containerRect = m_model.getParticleContainerRect();
        if (!m_model.getContainerExploded()){
	        setOffset( getFullBoundsReference().x, 
	                StatesOfMatterConstants.PARTICLE_CONTAINER_INITIAL_HEIGHT - containerRect.getHeight() - 
	                getFullBoundsReference().height);
        }
        else{
        	// If the container is exploding that hand is retracted more
        	// quickly.
	        setOffset( getFullBoundsReference().x, 
	                StatesOfMatterConstants.PARTICLE_CONTAINER_INITIAL_HEIGHT - (containerRect.getHeight() * 2) - 
	                getFullBoundsReference().height);
        }
    }
}
