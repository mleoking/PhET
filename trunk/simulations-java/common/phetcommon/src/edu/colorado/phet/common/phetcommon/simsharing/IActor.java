// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.common.phetcommon.simsharing;

import java.io.IOException;

/**
 * Primary abstraction for sending one-way and reply-oriented messages.
 *
 * @author Sam Reid
 */
public interface IActor {

    //Send an object and wait for a response object
    Object ask( Object question ) throws IOException, ClassNotFoundException;

    //Send a one-way message
    void tell( Object statement ) throws IOException;
}