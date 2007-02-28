package edu.colorado.phet.cck.model;

import edu.colorado.phet.cck.model.components.Branch;

/**
 * User: Sam Reid
 * Date: Jun 14, 2004
 * Time: 1:46:20 AM
 * Copyright (c) Jun 14, 2004 by Sam Reid
 */
public interface CurrentVoltListener {
    void currentOrVoltageChanged( Branch branch );
}
