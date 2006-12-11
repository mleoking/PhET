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

import edu.colorado.phet.boundstates.model.BSCoulomb1DPotential;
import edu.colorado.phet.boundstates.model.BSParticle;

/**
 * BSCoulomb1DConfig is a Java Bean used for XML encoding the state 
 * of a 1D Coulomb potential.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class BSCoulomb1DConfig implements BSSerializable {

    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private int _numberOfWells;
    private double _offset;
    private double _spacing;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    /**
     * Zero-argument constructor for Java Bean compliance.
     */
    public BSCoulomb1DConfig() {}

    public BSCoulomb1DConfig( BSCoulomb1DPotential potential ) {
        _numberOfWells = potential.getNumberOfWells();
        _offset = potential.getOffset();
        _spacing = potential.getSpacing();
    }

    //----------------------------------------------------------------------------
    // Accessors
    //----------------------------------------------------------------------------
    
    public int getNumberOfWells() {
        return _numberOfWells;
    }

    public void setNumberOfWells( int numberOfWells ) {
        _numberOfWells = numberOfWells;
    }
    
    public double getOffset() {
        return _offset;
    }
    
    public void setOffset( double offset ) {
        _offset = offset;
    }
    
    public double getSpacing() {
        return _spacing;
    }
    
    public void setSpacing( double spacing ) {
        _spacing = spacing;
    }
    
    //----------------------------------------------------------------------------
    // Conversions
    //----------------------------------------------------------------------------
    
    public BSCoulomb1DPotential toPotential( BSParticle particle ) {
        return new BSCoulomb1DPotential( particle, _numberOfWells, _offset, _spacing );
    }
}
