// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.simeventdatacollectionserver;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * Message handler is an abstraction for how to handle a given message.
 *
 * @author Sam Reid
 */
public interface MessageHandler {
    void handle( Object message, ObjectOutputStream writeToClient, ObjectInputStream readFromClient ) throws IOException;
}
