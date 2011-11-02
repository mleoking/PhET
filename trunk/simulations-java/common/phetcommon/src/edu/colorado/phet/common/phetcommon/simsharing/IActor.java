// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.common.phetcommon.simsharing;

import java.io.IOException;

/**
 * Primary abstraction for sending one-way and reply-oriented messages.
 * This interface and related classes allow clients to connect to the server for data sharing.
 * Note, the classes named *actors are not full-blown-actors in the Erlang, Scala or Akka sense, but minimal representations for sending messages.
 *
 * @author Sam Reid
 */
public interface IActor {

    //Send an object and wait for a response object
    String ask( String question ) throws IOException, ClassNotFoundException;

    //Send a one-way message
    void tell( String statement ) throws IOException;
}