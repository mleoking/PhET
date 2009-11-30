package edu.colorado.phet.reactantsproductsandleftovers.view;

import java.util.HashMap;
import java.util.Set;

import edu.umd.cs.piccolo.PNode;

/**
 * Pseudo state machine for controlling visibility of nodes.
 * A state is a named collection of nodes.
 * Nodes may be members of more than one state.
 * When a state is added, all of its nodes are made initially invisible.
 * Only one state may be visible at a time.
 * 
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class VisibilityStateMachine {
    
    private final HashMap<String,PNode[]> states; // state name -> PNode[]
    private String visibleStateName;
    
    public VisibilityStateMachine() {
        states = new HashMap<String,PNode[]>();
        visibleStateName = null;
    }
    
    /**
     * Gets the name of the state that is currently visible.
     * If no state is visible, returns null.
     * @return
     */
    public String getVisibleStateName() {
        return visibleStateName;
    }
    
    /**
     * Adds a state.
     * If a state with this name already exists, it is replaced.
     * @param name
     * @param nodes
     */
    public void addState( String name, PNode[] nodes ) {
        states.put( name, nodes );
        // a state is invisible by default
        setVisible( name, false );
        // in case nodes are shared the visible state
        if ( visibleStateName != null ) {
            setVisible( visibleStateName, true );
        }
    }
    
    /**
     * Removes a state.
     * If the state is currently visible, it is made invisible. 
     * @param name
     */
    public void removeState( String name ) {
        if ( name == visibleStateName ) {
            setVisible( visibleStateName, false );
            visibleStateName = null;
        }
        states.remove( name );
    }
    
    /**
     * Shows the nodes associated with a state.
     * Nodes associated with the previous state are made invisible.
     * @param name
     */
    public void showState( String name ) {
        if ( !hasState( name ) ) {
            throw new IllegalArgumentException( "there is no state named " + name );
        }
        if ( name != visibleStateName ) {
            if ( visibleStateName != null ) {
                setVisible( visibleStateName, false );
            }
            visibleStateName = name;
            setVisible( visibleStateName, true );
        }
    }
    
    /*
     * Checks for the existence of a named state.
     */
    private boolean hasState( String name ) {
        Set<String> keys = states.keySet();
        return keys.contains( name );
    }
    
    /*
     * Sets the visibility of all nodes in a state.
     */
    private void setVisible( String name, boolean visible ) {
        PNode[] nodes = states.get( name );
        for ( PNode node : nodes ) {
            node.setVisible( visible );
        }
    }
}
