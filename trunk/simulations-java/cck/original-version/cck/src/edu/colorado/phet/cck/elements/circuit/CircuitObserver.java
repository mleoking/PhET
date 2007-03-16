package edu.colorado.phet.cck.elements.circuit;

import edu.colorado.phet.cck.elements.branch.Branch;

/**
 * User: Sam Reid
 * Date: Aug 28, 2003
 * Time: 2:14:50 AM
 * Copyright (c) Aug 28, 2003 by Sam Reid
 */
public interface CircuitObserver {
    void branchAdded( Circuit circuit2, Branch branch );

    void branchRemoved( Circuit circuit2, Branch branch );

    void connectivityChanged( Circuit circuit2 );

}
