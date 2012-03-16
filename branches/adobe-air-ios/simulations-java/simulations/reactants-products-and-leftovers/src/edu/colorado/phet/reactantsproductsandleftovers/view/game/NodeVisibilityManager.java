// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.reactantsproductsandleftovers.view.game;

import java.util.*;

import edu.umd.cs.piccolo.PNode;

/**
 * Manages the visibility of a collection of PNodes.
 * Subsets of the collection can be named, and those subsets can then be made visible.
 * All nodes managed but not in a named subset will be invisible.
 * 
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class NodeVisibilityManager {
    
    private final List<PNode> managedNodes;
    private final HashMap<String,List<PNode>> visibleNodesMap; // name -> list
    
    /**
     * Constructor.
     * @param managedNodes the set of nodes whose visibility will be managed
     */
    public NodeVisibilityManager( PNode[] managedNodes ) {
        this.managedNodes = Arrays.asList( managedNodes );
        visibleNodesMap = new HashMap<String,List<PNode>>();
    }
    
    /**
     * Adds a named collection.
     * @param name
     * @param nodes
     */
    public void add( String name, PNode... visibleNodes ) {
        if ( exists( name ) ) {
            throw new IllegalArgumentException( "a collection already exists named " + name );
        }
        for ( PNode node : visibleNodes ) {
            if ( !managedNodes.contains( node ) ) {
                throw new IllegalArgumentException( "attempt to add an unmanaged node, were all nodes specified in the constructor?" );
            }
        }
        visibleNodesMap.put( name, Arrays.asList( visibleNodes ) );
    }
    
    /**
     * Removes a named collection.
     * If the collection is currently visible, it is made invisible. 
     * @param name
     */
    public void remove( String name ) {
        if ( !exists( name ) ) {
            throw new IllegalArgumentException( "there is no collection named " + name );
        }
        visibleNodesMap.remove( name );
    }
    
    /**
     * Shows the nodes associated with a collection name.
     * @param name
     */
    public void setVisibility( String name ) {
        if ( !exists( name ) ) {
            throw new IllegalArgumentException( "there is no collection named " + name );
        }
        setVisible( managedNodes, false ); // first set all managed nodes to invisible...
        setVisible( visibleNodesMap.get( name ), true ); // ... then make nodes in the specified collection visible
    }
    
    /*
     * Sets the visibility of an array of nodes.
     */
    private void setVisible( List<PNode> nodes, boolean visible ) {
        if ( nodes != null ) {
            for ( PNode node : nodes ) {
                node.setVisible( visible );
            }
        } 
    }
    
    /*
     * Checks for the existence of a named collection.
     */
    private boolean exists( String name ) {
        Set<String> keys = visibleNodesMap.keySet();
        return keys.contains( name );
    }
}
