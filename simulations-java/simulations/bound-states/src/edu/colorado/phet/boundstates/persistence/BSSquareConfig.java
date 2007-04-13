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

import edu.colorado.phet.boundstates.model.BSParticle;
import edu.colorado.phet.boundstates.model.BSSquarePotential;

/**
 * BSSquareConfig is a Java Bean used for XML encoding the state 
 * of a square potential.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class BSSquareConfig implements BSSerializable {
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private int _numberOfWells;
    private double _offset;
    private double _height;
    private double _width;
    private double _separation;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    /**
     * Zero-argument constructor for Java Bean compliance.
     */
    public BSSquareConfig() {}
    
    public BSSquareConfig( BSSquarePotential potential ) {
        _numberOfWells = potential.getNumberOfWells();
        _offset = potential.getOffset();
        _height =  potential.getHeight();
        _width = potential.getWidth();
        _separation = potential.getSeparation();
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

    public double getSeparation() {
        return _separation;
    }
    
    public void setSeparation( double separation ) {
        _separation = separation;
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
    
    public BSSquarePotential toPotential( BSParticle particle ) {
        return new BSSquarePotential( particle, _numberOfWells, _offset, _height, _width, _separation );
    }
}
