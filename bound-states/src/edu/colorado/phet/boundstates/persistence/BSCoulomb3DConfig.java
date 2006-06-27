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

import edu.colorado.phet.boundstates.model.BSCoulomb3DWell;
import edu.colorado.phet.boundstates.model.BSParticle;

/**
 * BSCoulomb3DConfig is a Java Bean used for XML encoding the state 
 * of a 3D Coulomb potential.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class BSCoulomb3DConfig implements BSSerializable {
    
    private double _offset;
    
    public BSCoulomb3DConfig() {}
    
    public BSCoulomb3DConfig( BSCoulomb3DWell potential ) {
        _offset = potential.getOffset();
    }

    public double getOffset() {
        return _offset;
    }
    
    public void setOffset( double offset ) {
        _offset = offset;
    }
    
    public BSCoulomb3DWell toPotential( BSParticle particle ) {
        return new BSCoulomb3DWell( particle, _offset );
    }
}
