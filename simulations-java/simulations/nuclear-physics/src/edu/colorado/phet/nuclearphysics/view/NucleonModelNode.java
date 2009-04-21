/* Copyright 2008, University of Colorado */

package edu.colorado.phet.nuclearphysics.view;

import edu.colorado.phet.nuclearphysics.common.model.Nucleon;


public interface NucleonModelNode {

    /**
     * Get the nucleon that this node represents.
     */
    public Nucleon getNucleon();
    
    /**
     * Cleanup memory references so that this node doesn't cause memory leaks.
     */
    public void cleanup();
}
