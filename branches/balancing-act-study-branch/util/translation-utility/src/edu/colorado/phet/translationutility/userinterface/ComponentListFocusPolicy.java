// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.translationutility.userinterface;

import java.awt.Component;
import java.awt.Container;
import java.awt.FocusTraversalPolicy;
import java.awt.Window;
import java.util.ArrayList;

/**
 * ComponentListFocusPolicy is a focus traversal policy that moves focus 
 * through a provided list of Components.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
/* package private */ class ComponentListFocusPolicy extends FocusTraversalPolicy {

    private ArrayList<Component> components;
    
    public ComponentListFocusPolicy( ArrayList<Component> components ) {
        assert( components.size() > 0 );
        this.components = components;
    }
    
    public Component getComponentAfter( Container focusCycleRoot, Component aComponent ) {
        Component componentAfter = null;
        int index = components.indexOf( aComponent );
        if ( index != -1 ) {
            int indexAfter = index + 1;
            if ( indexAfter >= components.size() ) {
                componentAfter = getFirstComponent( focusCycleRoot );
            }
            else {
                componentAfter = (Component) components.get( indexAfter );
            }
        }
        return componentAfter;
    }

    public Component getComponentBefore( Container focusCycleRoot, Component aComponent ) {
        Component componentAfter = null;
        int index = components.indexOf( aComponent );
        if ( index != -1 ) {
            int indexBefore = index - 1;
            if ( indexBefore < 0 ) {
                componentAfter = getLastComponent( focusCycleRoot );
            }
            else {
                componentAfter = (Component) components.get( indexBefore );
            }
        }
        return componentAfter;
    }

    public Component getDefaultComponent( Container focusCycleRoot ) {
        return getFirstComponent( focusCycleRoot );
    }

    public Component getFirstComponent( Container focusCycleRoot ) {
        return (Component) components.get( 0 );
    }

    public Component getLastComponent( Container focusCycleRoot ) {
        return (Component) components.get( components.size() - 1 );
    }
    
    public Component getInitialComponent( Window window ) {
        return getFirstComponent( window );
    }
    
}