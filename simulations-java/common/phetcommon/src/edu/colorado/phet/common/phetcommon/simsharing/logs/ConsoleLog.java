// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.common.phetcommon.simsharing.logs;

import edu.colorado.phet.common.phetcommon.simsharing.Log;
import edu.colorado.phet.common.phetcommon.simsharing.SimSharingMessage;

/**
 * @author Sam Reid
 */
public class ConsoleLog implements Log {
    public void addMessage( SimSharingMessage message ) {
        System.out.println( message );
    }

    public String getName() {
        return "console";
    }
}
