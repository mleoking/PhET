/*Copyright, Sam Reid, 2003.*/
package edu.colorado.phet.cck.elements.particles;

/**
 * User: Sam Reid
 * Date: Sep 5, 2003
 * Time: 2:11:52 AM
 * Copyright (c) Sep 5, 2003 by Sam Reid
 */
public interface ParticleSetObserver {
    void particleRemoved(BranchParticle bp);

    void particleAdded(BranchParticle bp);
}
