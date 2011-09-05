// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.simsharing.socket;

import java.io.IOException;

/**
 * @author Sam Reid
 */
public interface IActor {
    Object ask( Object question ) throws IOException, ClassNotFoundException;

    void tell( Object statement ) throws IOException;
}