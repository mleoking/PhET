// Copyright 2002-2011, University of Colorado

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.hydrogenatom.view.atom;

import edu.colorado.phet.common.piccolophet.PhetPNode;

/**
 * AbstractHydrogenAtomNode is the base class for all hydrogen atoms.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class AbstractHydrogenAtomNode extends PhetPNode {

    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    public AbstractHydrogenAtomNode() {
        super();
        setPickable( false );
        setChildrenPickable( false );
    }
}
