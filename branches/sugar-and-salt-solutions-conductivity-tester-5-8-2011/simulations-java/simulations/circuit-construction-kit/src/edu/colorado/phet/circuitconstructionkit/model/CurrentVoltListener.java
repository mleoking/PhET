// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.circuitconstructionkit.model;

import edu.colorado.phet.circuitconstructionkit.model.components.Branch;

/**
 * User: Sam Reid
 * Date: Jun 14, 2004
 * Time: 1:46:20 AM
 */
public interface CurrentVoltListener {
    void currentOrVoltageChanged(Branch branch);
}
