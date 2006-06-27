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

import edu.colorado.phet.boundstates.model.BSCoulomb1DWells;
import edu.colorado.phet.boundstates.model.BSParticle;

/**
 * BSCoulomb1DConfig is a Java Bean used for XML encoding the state 
 * of a 1D Coulomb potential.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class BSCoulomb1DConfig implements BSSerializable {

    private int _numberOfWells;
    private double _offset;
    private double _spacing;
    
    public BSCoulomb1DConfig() {}

    public BSCoulomb1DConfig( BSCoulomb1DWells potential ) {
        _numberOfWells = potential.getNumberOfWells();
        _offset = potential.getOffset();
        _spacing = potential.getSpacing();
    }

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
    
    public BSCoulomb1DWells toPotential( BSParticle particle ) {
        return new BSCoulomb1DWells( particle, _numberOfWells, _offset, _spacing );
    }
}
