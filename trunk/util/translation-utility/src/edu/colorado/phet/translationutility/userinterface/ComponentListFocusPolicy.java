/* Copyright 2007, University of Colorado */

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
public class ComponentListFocusPolicy extends FocusTraversalPolicy {

    private ArrayList _components; // array of Component
    
    public ComponentListFocusPolicy( ArrayList components ) {
        assert( components.size() > 0 );
        _components = components;
    }
    
    public Component getComponentAfter( Container focusCycleRoot, Component aComponent ) {
        Component componentAfter = null;
        int index = _components.indexOf( aComponent );
        if ( index != -1 ) {
            int indexAfter = index + 1;
            if ( indexAfter >= _components.size() ) {
                componentAfter = getFirstComponent( focusCycleRoot );
            }
            else {
                componentAfter = (Component) _components.get( indexAfter );
            }
        }
        return componentAfter;
    }

    public Component getComponentBefore( Container focusCycleRoot, Component aComponent ) {
        Component componentAfter = null;
        int index = _components.indexOf( aComponent );
        if ( index != -1 ) {
            int indexBefore = index - 1;
            if ( indexBefore < 0 ) {
                componentAfter = getLastComponent( focusCycleRoot );
            }
            else {
                componentAfter = (Component) _components.get( indexBefore );
            }
        }
        return componentAfter;
    }

    public Component getDefaultComponent( Container focusCycleRoot ) {
        return getFirstComponent( focusCycleRoot );
    }

    public Component getFirstComponent( Container focusCycleRoot ) {
        return (Component) _components.get( 0 );
    }

    public Component getLastComponent( Container focusCycleRoot ) {
        return (Component) _components.get( _components.size() - 1 );
    }
    
    public Component getInitialComponent( Window window ) {
        return getFirstComponent( window );
    }
    
}