package edu.colorado.phet.cck.elements.branch;

import edu.colorado.phet.cck.elements.junction.Junction;


/**
 * User: Sam Reid
 * Date: Aug 30, 2003
 * Time: 1:26:23 PM
 * Copyright (c) Aug 30, 2003 by Sam Reid
 */
public interface BranchObserver {
    void junctionMoved(Branch branch2, Junction junction);

    void currentOrVoltageChanged(Branch branch2);
}
