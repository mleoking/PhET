package edu.colorado.phet.reactantsproductsandleftovers.view;

import java.util.HashMap;
import java.util.Set;

import edu.umd.cs.piccolo.PNode;

/**
 * Manages the visibility of named collections of PNodes.
 * A collection consists of a name and array of PNodes.
 * PNodes may be members of more than one collection.
 * When a collection is added, all of its nodes are made initially invisible.
 * Only one collection may be visible at a time.
 * 
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class NodeCollectionVisibilityManager {
    
    private final HashMap<String,PNode[]> collections; // collection name -> PNode[]
    private String visibleName;
    
    public NodeCollectionVisibilityManager() {
        collections = new HashMap<String,PNode[]>();
        visibleName = null;
    }
    
    /**
     * Gets the name of the collection that is currently visible.
     * If no collection is visible, returns null.
     * @return
     */
    public String getVisibleName() {
        return visibleName;
    }
    
    /**
     * Adds a named collection.
     * If a collection with this name already exists, it is replaced.
     * @param name
     * @param nodes
     */
    public void add( String name, PNode[] nodes ) {
        collections.put( name, nodes );
        // a collection is invisible by default
        setVisible( name, false );
        // in case nodes are shared with the visible collection
        if ( visibleName != null ) {
            setVisible( visibleName, true );
        }
    }
    
    /**
     * Removes a named collection.
     * If the collection is currently visible, it is made invisible. 
     * @param name
     */
    public void remove( String name ) {
        if ( name == visibleName ) {
            setVisible( visibleName, false );
            visibleName = null;
        }
        collections.remove( name );
    }
    
    /**
     * Shows the nodes associated with a collection.
     * Nodes associated with the previously-visible collection are made invisible.
     * @param name
     */
    public void show( String name ) {
        if ( !exists( name ) ) {
            throw new IllegalArgumentException( "there is no collection named " + name );
        }
        if ( name != visibleName ) {
            if ( visibleName != null ) {
                setVisible( visibleName, false );
            }
            visibleName = name;
            setVisible( visibleName, true );
        }
    }
    
    /*
     * Checks for the existence of a named collection.
     */
    private boolean exists( String name ) {
        Set<String> keys = collections.keySet();
        return keys.contains( name );
    }
    
    /*
     * Sets the visibility of all nodes in a collection.
     */
    private void setVisible( String name, boolean visible ) {
        PNode[] nodes = collections.get( name );
        for ( PNode node : nodes ) {
            node.setVisible( visible );
        }
    }
}
