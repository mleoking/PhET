// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.reactantsproductsandleftovers.view;

/**
 * Interface implemented by all nodes that are intended to be dynamically
 * allocated and deallocated.  The goal is to ensure that we don't leak memory
 * by forgetting to remove listeners, etc.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public interface IDynamicNode {
    
    /**
     * Clean up anything that could create memory leaks.
     * For example, if the class created listener connections, it should remove those connections here.
     * This is *not* intended to do play the role of finalize.
     */
    public void cleanup();
}
