/* Copyright 2006, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.hydrogenatom.view;

import edu.colorado.phet.hydrogenatom.factory.*;
import edu.colorado.phet.hydrogenatom.model.Model;
import edu.umd.cs.piccolo.PLayer;

/**
 * HAModelViewManager is an extension of ModelViewManager that
 * constains node factories specific to this simulation.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class HAModelViewManager extends ModelViewManager {

    public HAModelViewManager( Model model, AnimationBoxNode animationBoxNode ) {
        super( model );
        
        PLayer bottomLayer = animationBoxNode.getBottomLayer();
        PLayer middleLayer = animationBoxNode.getMiddleLayer();
        PLayer topLayer = animationBoxNode.getTopLayer();
        
        // Particles are in the middle layer
        addNodeFactory( new PhotonNodeFactory( middleLayer ) );
        addNodeFactory( new AlphaParticleNodeFactory( middleLayer ) );
        
        // "Experiement" atom is in front of particles
        addNodeFactory( new ExperimentNodeFactory( topLayer ) );
        
        // All other atoms are behind particles
        addNodeFactory( new BilliardBallNodeFactory( bottomLayer ) );
        addNodeFactory( new BohrNodeFactory( bottomLayer ) );
        addNodeFactory( new DeBroglieNodeFactory( bottomLayer ) );
        addNodeFactory( new PlumPuddingNodeFactory( bottomLayer ) );
        addNodeFactory( new SchrodingerNodeFactory( bottomLayer ) );
        addNodeFactory( new SolarSystemNodeFactory( bottomLayer ) );
    }
}
