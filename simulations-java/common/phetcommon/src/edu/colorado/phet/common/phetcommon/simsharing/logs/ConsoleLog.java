// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.common.phetcommon.simsharing.logs;

import edu.colorado.phet.common.phetcommon.simsharing.AugmentedMessage;
import edu.colorado.phet.common.phetcommon.simsharing.Log;

/**
 * @author Sam Reid
 */
public class ConsoleLog implements Log {
    public void addMessage( AugmentedMessage message ) {
        System.out.println( message );
    }
}
