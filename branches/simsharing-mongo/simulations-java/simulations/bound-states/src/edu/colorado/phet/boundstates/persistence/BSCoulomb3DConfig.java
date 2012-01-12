// Copyright 2002-2011, University of Colorado

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.boundstates.persistence;

import edu.colorado.phet.boundstates.model.BSCoulomb3DPotential;
import edu.colorado.phet.boundstates.model.BSParticle;
import edu.colorado.phet.common.phetcommon.util.IProguardKeepClass;

/**
 * BSCoulomb3DConfig is a Java Bean used for XML encoding the state 
 * of a 3D Coulomb potential.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class BSCoulomb3DConfig implements IProguardKeepClass {
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private double _offset;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    /**
     * Zero-argument constructor for Java Bean compliance.
     */
    public BSCoulomb3DConfig() {}
    
    public BSCoulomb3DConfig( BSCoulomb3DPotential potential ) {
        _offset = potential.getOffset();
    }

    //----------------------------------------------------------------------------
    // Accessors
    //----------------------------------------------------------------------------
    
    public double getOffset() {
        return _offset;
    }
    
    public void setOffset( double offset ) {
        _offset = offset;
    }
    
    //----------------------------------------------------------------------------
    // Conversions
    //----------------------------------------------------------------------------
    
    public BSCoulomb3DPotential toPotential( BSParticle particle ) {
        return new BSCoulomb3DPotential( particle, _offset );
    }
}
