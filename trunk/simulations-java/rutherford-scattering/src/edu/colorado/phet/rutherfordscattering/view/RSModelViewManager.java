/* Copyright 2007, University of Colorado */

package edu.colorado.phet.rutherfordscattering.view;

import edu.colorado.phet.rutherfordscattering.factory.AlphaParticleNodeFactory;
import edu.colorado.phet.rutherfordscattering.model.Model;
import edu.umd.cs.piccolo.PLayer;

/**
 * RSModelViewManager is an extension of ModelViewManager that
 * contains node factories specific to this simulation.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class RSModelViewManager extends ModelViewManager {

    public RSModelViewManager( Model model, AnimationBoxNode animationBoxNode ) {
        super( model );
        
        // Particles are dynamically added to the middle layer of the animation box
        PLayer middleLayer = animationBoxNode.getParticleLayer();
        addNodeFactory( new AlphaParticleNodeFactory( middleLayer ) );
    }
}
