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

import edu.colorado.phet.piccolo.PhetPNode;


public class AbstractAtomNode extends PhetPNode {

    public AbstractAtomNode() {
        super();
        setPickable( false );
        setChildrenPickable( false );
    }
}
