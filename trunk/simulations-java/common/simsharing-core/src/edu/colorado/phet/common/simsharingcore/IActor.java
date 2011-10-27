// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.common.simsharingcore;

import java.io.IOException;

/**
 * Primary abstraction for sending one-way and reply-oriented messages.
 *
 * @author Sam Reid
 */
public interface IActor {
    Object ask( Object question ) throws IOException, ClassNotFoundException;

    void tell( Object statement ) throws IOException;
}