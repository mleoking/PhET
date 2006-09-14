package edu.colorado.phet.cck.model.components;

import edu.colorado.phet.cck.model.CircuitChangeListener;
import edu.colorado.phet.cck.model.Junction;

/**
 * User: Sam Reid
 * Date: Sep 14, 2006
 * Time: 5:17:00 PM
 * Copyright (c) Sep 14, 2006 by Sam Reid
 */

public class Wire extends Branch {
    protected Wire( CircuitChangeListener listener ) {
        super( listener );
    }

    public Wire( CircuitChangeListener listener, Junction startJunction, Junction endJunction ) {
        super( listener, startJunction, endJunction );
    }
}
