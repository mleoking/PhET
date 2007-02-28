/*Copyright, University of Colorado, 2004.*/
package edu.colorado.phet.cck.elements.xml;

import edu.colorado.phet.cck.elements.branch.Branch;
import edu.colorado.phet.cck.elements.branch.components.Switch;
import edu.colorado.phet.cck.elements.circuit.Circuit;

/**
 * User: Sam Reid
 * Date: Nov 23, 2003
 * Time: 12:11:20 AM
 * Copyright (c) Nov 23, 2003 by Sam Reid
 */
public class SwitchData extends BranchData {
    private boolean open;

    public SwitchData() {
    }

    public SwitchData( Switch aSwitch ) {
        super( aSwitch );
        this.open = aSwitch.isOpen();
    }

    public Branch toBranch( Circuit parent ) {
        Switch s = new Switch( parent, x0, y0, x1, y1 );
        s.setOpen( open );
        return s;
    }
}
