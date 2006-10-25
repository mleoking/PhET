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
import edu.umd.cs.piccolo.PNode;

/**
 * HAModelViewManager is an extension of ModelViewManager that
 * constains node factories specific to this simulation.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class HAModelViewManager extends ModelViewManager {

    public HAModelViewManager( Model model, PNode parentNode ) {
        super( model );
        
        // Particles
        addNodeFactory( new PhotonNodeFactory( parentNode ) );
        addNodeFactory( new AlphaParticleNodeFactory( parentNode ) );
        
        // Atoms
        addNodeFactory( new ExperimentNodeFactory( parentNode ) );
        addNodeFactory( new BilliardBallNodeFactory( parentNode ) );
        addNodeFactory( new BohrNodeFactory( parentNode ) );
        addNodeFactory( new DeBroglieNodeFactory( parentNode ) );
        addNodeFactory( new PlumPuddingNodeFactory( parentNode ) );
        addNodeFactory( new SchrodingerNodeFactory( parentNode ) );
        addNodeFactory( new SolarSystemNodeFactory( parentNode ) );
    }
}
