// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.common.phetcommon.simsharing;

import java.io.IOException;

/**
 * @author Sam Reid
 */
public interface Log {
    public void addMessage( AugmentedMessage augmentedMessage ) throws IOException;
}