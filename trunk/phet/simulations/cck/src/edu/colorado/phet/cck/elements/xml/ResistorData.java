/*Copyright, University of Colorado, 2004.*/
package edu.colorado.phet.cck.elements.xml;

import edu.colorado.phet.cck.elements.branch.Branch;
import edu.colorado.phet.cck.elements.branch.components.Resistor;
import edu.colorado.phet.cck.elements.circuit.Circuit;

/**
 * User: Sam Reid
 * Date: Nov 23, 2003
 * Time: 12:06:57 AM
 * Copyright (c) Nov 23, 2003 by Sam Reid
 */
public class ResistorData extends BranchData {
    public ResistorData() {
    }

    public ResistorData( Branch b ) {
        super( b );
    }

    public Branch toBranch( Circuit parent ) {
        return new Resistor( parent, x0, y0, x1, y1, resistance );
    }
}
