// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.common.phetcommon.simsharing;

import java.io.IOException;

/**
 * A NullClient can be substituted for a real client for offline testing.
 *
 * @author Sam Reid
 */
public class NullClient implements IActor {

    public String ask( String question ) throws IOException, ClassNotFoundException {
        return null;
    }

    public void tell( String statement ) throws IOException {
    }
}