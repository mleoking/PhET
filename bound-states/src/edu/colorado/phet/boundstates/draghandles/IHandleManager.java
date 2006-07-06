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
 * IHandleManager is the interface implemented by all drag handle managers.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public interface IHandleManager {

    public void updateDragBounds();
    
    public PNode getHelpNode();
    
    public void setColorScheme( BSColorScheme colorScheme );
}
