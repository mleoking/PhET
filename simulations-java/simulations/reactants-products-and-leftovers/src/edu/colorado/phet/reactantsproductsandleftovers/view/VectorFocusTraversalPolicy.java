/* Copyright 2010, University of Colorado */

package edu.colorado.phet.reactantsproductsandleftovers.view;

import java.awt.Component;
import java.awt.Container;
import java.awt.FocusTraversalPolicy;
import java.util.Vector;

/**
 * Focus traversal policy that visits Components in the order that they appear in a Vector.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class VectorFocusTraversalPolicy extends FocusTraversalPolicy {

    private final Vector<Component> components;

    public VectorFocusTraversalPolicy( Vector<Component> components ) {
        this.components = new Vector<Component>( components.size() );
        this.components.addAll( components );
    }

    public Component getComponentAfter( Container aContainer, Component aComponent ) {
        int index = ( components.indexOf( aComponent ) + 1 ) % components.size();
        return components.get( index );
    }

    public Component getComponentBefore( Container aContainer, Component aComponent ) {
        int index = components.indexOf( aComponent ) - 1;
        if ( index < 0 ) {
            index = components.size() - 1;
        }
        return components.get( index );
    }

    public Component getDefaultComponent( Container aContainer ) {
        return components.get( 0 );
    }

    public Component getLastComponent( Container aContainer ) {
        return components.lastElement();
    }

    public Component getFirstComponent( Container aContainer ) {
        return components.get( 0 );
    }
}
