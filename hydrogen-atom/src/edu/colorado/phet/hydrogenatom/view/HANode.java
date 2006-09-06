/* Copyright 2006, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.hydrogenatom.view;

import edu.umd.cs.piccolo.PNode;

/**
 * HANode is an extension of PNode with some generally useful features.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class HANode extends PNode {

    public boolean _wasPickable;
    public boolean _wereChildrenPickable;
    
    /**
     * Constructor.
     */
    public HANode() {
        super();
        _wasPickable = getPickable();
        _wereChildrenPickable = getChildrenPickable();
    }
    
    /**
     * When this node is made invisible, turn off mouse handling.
     * When this node is made visible, restore mouse handling.
     * 
     * @parma visible true or false
     */
    public void setVisible( boolean visible ) {
        super.setVisible( visible );
        if ( visible ) {
            super.setPickable( _wasPickable );
            super.setChildrenPickable( _wereChildrenPickable );
        }
        else {
            super.setPickable( false );
            super.setChildrenPickable( false );
        }
    }
    
    /**
     * PNode is missing the isVisible method.
     * @return true or false
     */
    public boolean isVisible() {
        return super.getVisible();
    }

    /**
     * Sets the pickable property and remembers it when
     * we change visibility.
     * 
     * @param pickable true or false
     */
    public void setPickable( boolean pickable ) {
        if ( isVisible() ) {
            super.setPickable( pickable );
        }
        _wasPickable = pickable;
    }
    
    /**
     * Sets the pickable property for children and remembers it when
     * we change visibility.
     * 
     * @param pickable true or false
     */
    public void setChildrenPickable( boolean pickable ) {
        if ( isVisible() ) {
            super.setChildrenPickable( pickable );
        }
        _wereChildrenPickable = pickable;
    }
}
