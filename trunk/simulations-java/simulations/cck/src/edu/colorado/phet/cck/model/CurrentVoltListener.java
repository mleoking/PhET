package edu.colorado.phet.cck.model;

import edu.colorado.phet.cck.model.components.Branch;

/**
 * User: Sam Reid
 * Date: Jun 14, 2004
 * Time: 1:46:20 AM
 *
 */
public interface CurrentVoltListener {
    void currentOrVoltageChanged( Branch branch );
}
