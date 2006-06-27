/* Copyright 2006, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.boundstates.persistence;

import edu.colorado.phet.boundstates.BSConstants;
import edu.colorado.phet.boundstates.model.BSParticle;

/**
 * BSParticleConfig is a Java Bean used for XML encoding the state of a particle.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class BSParticleConfig implements BSSerializable {

    private double _mass;
    
    public BSParticleConfig() {
        _mass = BSConstants.ELECTRON_MASS;
    }

    public BSParticleConfig( BSParticle particle ) {
        _mass = particle.getMass();
    }
    
    public double getMass() {
        return _mass;
    }

    public void setMass( double mass ) {
        _mass = mass;
    }
    
    public BSParticle toParticle() {
        return new BSParticle( _mass );
    }
}
