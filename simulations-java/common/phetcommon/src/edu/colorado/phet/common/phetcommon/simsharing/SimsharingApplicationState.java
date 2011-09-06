// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.common.phetcommon.simsharing;

/**
 * @author Sam Reid
 */
public interface SimsharingApplicationState<T extends SimsharingApplication> {
    public void apply( T application );
}