/* Copyright 2008, University of Colorado */

package edu.colorado.phet.statesofmatter.view;

import java.awt.Cursor;

import edu.colorado.phet.common.piccolophet.event.CursorHandler;
import edu.colorado.phet.statesofmatter.StatesOfMatterConstants;
import edu.colorado.phet.statesofmatter.StatesOfMatterResources;
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
    // Instance Data
    //----------------------------------------------------------------------------
    
    private PImage m_fingerImageNode;

    //----------------------------------------------------------------------------
    // Constructor
    //----------------------------------------------------------------------------

    public FingerNode(double desiredWidth){

        // Load the image that looks like a finger.
        m_fingerImageNode = StatesOfMatterResources.getImageNode( StatesOfMatterConstants.FINGER_IMAGE );
        m_fingerImageNode.scale( desiredWidth / m_fingerImageNode.getFullBoundsReference().width );
        
        // Set the cursor to be the hand.
        m_fingerImageNode.setPickable( true );
        m_fingerImageNode.addInputEventListener( new CursorHandler(Cursor.N_RESIZE_CURSOR) );
        
        // Set ourself up to listen for and handle mouse dragging events.
        m_fingerImageNode.addInputEventListener( new PDragEventHandler(){
            
            public void drag(PInputEvent event){
                double movementAmount = event.getDelta().height;
                m_fingerImageNode.translate( 0, movementAmount );
            }
        });
        
        // Add the finger node as a child.
        addChild(m_fingerImageNode);
    }
}
