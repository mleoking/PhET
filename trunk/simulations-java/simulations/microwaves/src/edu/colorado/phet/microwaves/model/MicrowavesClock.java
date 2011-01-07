// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.microwaves.model;

import edu.colorado.phet.common.phetcommon.model.clock.ConstantDtClock;

public class MicrowavesClock extends ConstantDtClock {
    
    public MicrowavesClock() {
        super( 50, 20 );
    }
}
