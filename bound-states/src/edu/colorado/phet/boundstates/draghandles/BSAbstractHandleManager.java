/* Copyright 2006, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.boundstates.draghandles;

import edu.colorado.phet.boundstates.color.BSColorScheme;
import edu.umd.cs.piccolo.PNode;

/**
 * BSAbstractHandleManager is the base class for all drag handle managers.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public abstract class BSAbstractHandleManager extends PNode {

    public abstract void updateDragBounds();
    
    public abstract PNode getHelpNode();
    
    public abstract void setColorScheme( BSColorScheme colorScheme );
    
    public void setVisible( boolean visible ) {
        if ( visible ) {
            updateDragBounds();
        }
        super.setVisible( visible );
    }
    
}
