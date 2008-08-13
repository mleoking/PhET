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

/**
 * This class represents a node that looks like a large finger, which can be
 * used to push down on things.
 */
public class FingerNode extends PNode {

    //----------------------------------------------------------------------------
    // Class Data
    //----------------------------------------------------------------------------
    
    // Width of the finger node as a proportion of the width of the particle
    // container.
    private static final double NODE_WIDTH_PROPORTION = 0.65;
    
    // Initial and max distance of the node above the particle container as a
    // function of the container height.
    private static final double NODE_MIN_Y_POS_PROPORTION = 0.1;
    
    // Horizontal position of the node as function of the container width.
    private static final double NODE_X_POS_PROPORTION = 0.30;
    
    // File name of the primary image.
    public static final String PRIMARY_IMAGE = "finger-2.png";
    
    //----------------------------------------------------------------------------
    // Instance Data
    //----------------------------------------------------------------------------
    
    private PImage m_fingerImageNode;
    private MultipleParticleModel m_model;
    private double m_minLowerEdgeYPos;     // Minimum Y position for the lower edge of this node.
    private double m_scale;

    //----------------------------------------------------------------------------
    // Constructor
    //----------------------------------------------------------------------------

    public FingerNode(MultipleParticleModel model){

        m_model = model;
        Rectangle2D containerRect = m_model.getParticleContainerRect();
        double nodeWidth = containerRect.getWidth() * NODE_WIDTH_PROPORTION;
        
        m_minLowerEdgeYPos = -(containerRect.getMaxY() + (containerRect.getHeight() * NODE_MIN_Y_POS_PROPORTION));
        
        // Listen to the model for notifications of changes to the container size.
        m_model.addListener( new MultipleParticleModel.Adapter(){
            public void containerSizeChanged(){
                handleContainerSizeChanged();
            }
        });
        
        // Load and scale the image that looks like a finger (hopefully).
        m_fingerImageNode = StatesOfMatterResources.getImageNode( PRIMARY_IMAGE );
        m_scale = nodeWidth / m_fingerImageNode.getFullBoundsReference().width;
        m_fingerImageNode.scale( m_scale );
        
        // Set up a cursor handler so that the user will get an indication
        // that the node can be moved.
        m_fingerImageNode.setPickable( true );
        m_fingerImageNode.addInputEventListener( new CursorHandler(Cursor.N_RESIZE_CURSOR) );
        
        // Set ourself up to listen for and handle mouse dragging events.
        m_fingerImageNode.addInputEventListener( new PDragEventHandler(){
            
            public void drag(PInputEvent event){
                handleMouseDragEvent( event );
            }
        });

        // Add the finger node as a child.
        addChild(m_fingerImageNode);

        // Set our initial offset.
//        setOffset( containerRect.getX() + containerRect.getWidth() * NODE_X_POS_PROPORTION,
//                m_minLowerEdgeYPos - m_fingerImageNode.getFullBoundsReference().height);
        setOffset( containerRect.getX() + containerRect.getWidth() * NODE_X_POS_PROPORTION,
                -containerRect.getMaxY() - m_fingerImageNode.getFullBoundsReference().height);
    }

    //----------------------------------------------------------------------------
    // Private Methods
    //----------------------------------------------------------------------------
    
    private void handleMouseDragEvent(PInputEvent event){
        
        double currentLowerEdgePosY = getFullBoundsReference().getMaxY();
        double movementAmount = event.getCanvasDelta().getHeight();
        
        if (currentLowerEdgePosY + movementAmount < m_minLowerEdgeYPos){
            // We are at the top of the allowable range, so only pay attention
            // to this event if we are moving in a downward direction.
            if (movementAmount > 0){
                m_fingerImageNode.translate( 0, movementAmount );
            }
            return;
        }
        
        if (currentLowerEdgePosY + movementAmount < -StatesOfMatterConstants.PARTICLE_CONTAINER_INITIAL_HEIGHT){
            // The node is currently moving in the range where it doesn't
            // affect the size of the container, so go ahead and move it.
            m_fingerImageNode.translate( 0, movementAmount );
            return;
        }
        
        // If the preceding conditions are not met, it means the node is in
        // the range where its motion should affect the size of the container.
        // Hence, we only set the container size here and rely on the
        // notifications of changes to the container size to move the node.
        m_model.setParticleContainerHeight( m_model.getParticleContainerHeight() - ( movementAmount * m_scale ) );
    }
    
    private void handleContainerSizeChanged(){
        Rectangle2D containerRect = m_model.getParticleContainerRect();
        setOffset( getFullBoundsReference().x,
                containerRect.getY() - containerRect.getHeight() - m_fingerImageNode.getFullBoundsReference().height);
    }
}
