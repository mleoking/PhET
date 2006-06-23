/**
 * Class: DecayListener
 * Class: edu.colorado.phet.nuclearphysics.model
 * User: Ron LeMaster
 * Date: Feb 29, 2004
 * Time: 11:34:41 AM
 */
package edu.colorado.phet.nuclearphysics.model;

public interface DecayListener {
    void alphaDecay( AlphaDecayProducts decayProducts, AlphaDecaySnapshot alphaDecaySnapshot );
}
