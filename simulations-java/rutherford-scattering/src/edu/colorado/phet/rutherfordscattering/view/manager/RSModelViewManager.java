/* Copyright 2007, University of Colorado */

package edu.colorado.phet.rutherfordscattering.view.manager;

import edu.colorado.phet.rutherfordscattering.model.Model;
import edu.colorado.phet.rutherfordscattering.view.AnimationBoxNode;
import edu.umd.cs.piccolo.PLayer;

/**
 * RSModelViewManager is an extension of ModelViewManager that
 * contains node factories specific to this simulation.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class RSModelViewManager extends ModelViewManager {

    public RSModelViewManager( Model model, AnimationBoxNode animationBoxNode ) {
        super( model );
        
        // Add particles to the animation box's particle layer
        PLayer particleLayer = animationBoxNode.getParticleLayer();
        addNodeFactory( new AlphaParticleNodeFactory( particleLayer ) );
    }
}
