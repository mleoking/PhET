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

import edu.colorado.phet.boundstates.model.BSAsymmetricPotential;
import edu.colorado.phet.boundstates.model.BSParticle;

/**
 * BSAsymmetricConfig is a Java Bean used for XML encoding the state 
 * of an asymmetric potential.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class BSAsymmetricConfig implements BSSerializable {

    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private double _offset;
    private double _height;
    private double _width;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    /**
     * Zero-argument constructor for Java Bean compliance.
     */
    public BSAsymmetricConfig() {}
    
    public BSAsymmetricConfig( BSAsymmetricPotential potential ) {
        _offset = potential.getOffset();
        _height = potential.getHeight();
        _width = potential.getWidth();
    }

    //----------------------------------------------------------------------------
    // Accessors
    //----------------------------------------------------------------------------
    
    public double getHeight() {
        return _height;
    }

    public void setHeight( double height ) {
        _height = height;
    }

    public double getOffset() {
        return _offset;
    }

    public void setOffset( double offset ) {
        _offset = offset;
    }

    public double getWidth() {
        return _width;
    }
    
    public void setWidth( double width ) {
        _width = width;
    }
    
    //----------------------------------------------------------------------------
    // Conversions
    //----------------------------------------------------------------------------
    
    public BSAsymmetricPotential toPotential( BSParticle particle ) {
        return new BSAsymmetricPotential( particle, _offset, _height, _width );
    }
}
