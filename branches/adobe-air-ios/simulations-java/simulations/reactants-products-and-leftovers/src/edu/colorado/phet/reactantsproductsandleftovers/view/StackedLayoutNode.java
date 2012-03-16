// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.reactantsproductsandleftovers.view;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.HashMap;

import edu.colorado.phet.reactantsproductsandleftovers.controls.ValueNode;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PImage;
import edu.umd.cs.piccolo.util.PDimension;

/**
 * Stacks images vertically in a box.
 * 
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class StackedLayoutNode extends ImageLayoutNode {

    private static final double Y_MARGIN = 7;
    private static final double Y_SPACING = 28;
    
    private final PropertyChangeListener imageChangeListener;
    private final HashMap<String,ArrayList<SubstanceImageNode>> stacks; // sustance name -> stack of images
    private final HashMap<String,ValueNode> valueNodes; // substance name -> ValueNode (for alignment)
    
    public StackedLayoutNode( PDimension boxSize ) {
        super( boxSize );
        imageChangeListener = new PropertyChangeListener() {
            public void propertyChange( PropertyChangeEvent event ) {
                // if an image changes, update the layout of stacks
                if ( event.getPropertyName().equals( PImage.PROPERTY_IMAGE ) ) {
                    updateLayout();
                }
            }
        };
        stacks = new HashMap<String,ArrayList<SubstanceImageNode>>();
        valueNodes = new HashMap<String,ValueNode>();
    }
    
    /**
     * @param node the node to be added
     * @param referenceNode the node currently at the top of the stack, used for y offset.
     * @param valueNode ValueNode below the box, used for x offset of each stack
     */
    public void addNode( SubstanceImageNode node, ValueNode valueNode ) {
        
        // get the substance name, it's the key for our data structures
        String substanceName = node.getSubstance().getName();
        
        // add the node to the scenegraph
        addChild( node );
        
        // add the node to the proper stack
        ArrayList<SubstanceImageNode> stack = stacks.get( substanceName );
        if ( stack == null ) {
            stack = new ArrayList<SubstanceImageNode>();
            stacks.put( substanceName, stack );
        }
        stack.add( node );
        
        // remember the control node for this substance type
        if ( valueNodes.get( substanceName ) == null ) {
            valueNodes.put( substanceName, valueNode );
        }
        
        // listen for changes to the node's image
        node.addPropertyChangeListener( imageChangeListener );
        
        updateLayout();
    }
    
    public void removeNode( SubstanceImageNode node ) {
        // remove from the scenegraph
        removeChild( node );
        // remove image listener
        node.removePropertyChangeListener( imageChangeListener );
        // remove from stack
        ArrayList<SubstanceImageNode> stack = stacks.get( node.getSubstance().getName() );
        stack.remove( node );
    }
    
    /*
     * Adjust the layout all images, in all stacks.
     */
    private void updateLayout() {
        for ( String substanceName : stacks.keySet() ){
            ArrayList<SubstanceImageNode> stack = stacks.get( substanceName );
            PNode controlNode = valueNodes.get( substanceName );
            for ( int i = 0; i < stack.size(); i++ ) {
                SubstanceImageNode node = stack.get( i );
                double x = controlNode.getXOffset() - ( node.getFullBoundsReference().getWidth() / 2 );
                double y = getBoxHeight() - node.getFullBoundsReference().getHeight() - Y_MARGIN - ( i * Y_SPACING );
                node.setOffset( x, y );
            }
        }
    }
}