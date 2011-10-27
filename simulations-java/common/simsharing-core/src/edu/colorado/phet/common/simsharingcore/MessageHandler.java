// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.common.simsharingcore;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * @author Sam Reid
 */
public interface MessageHandler {
    void handle( Object message, ObjectOutputStream writeToClient, ObjectInputStream readFromClient ) throws IOException;
}
