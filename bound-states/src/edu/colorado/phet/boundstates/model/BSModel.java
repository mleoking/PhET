/* Copyright 2005, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.boundstates.model;

import java.util.Observable;
import java.util.Observer;

import edu.colorado.phet.boundstates.enums.BSWellType;


/**
 * BSModel is a composite of several parts of the model,
 * more easily used by the view componennts.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class BSModel extends BSObservable implements Observer {
    
    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------
    
    public static final String PROPERTY_POTENTIAL = "potential";
    public static final String PROPERTY_SUPERPOSITION_COEFFICIENTS = "superpositionCoefficients";
    public static final String PROPERTY_HILITED_EIGENSTATE_INDEX = "hilitedEnergy";
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private BSAbstractPotential _potential;
    private BSSuperpositionCoefficients _superpositionCoefficients;
    private BSEigenstate[] _eigenstates;
    private int _hilitedEigenstateIndex;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    public BSModel( BSAbstractPotential potential, BSSuperpositionCoefficients superpositionCoefficients ) {
        _potential = potential;
        _potential.addObserver( this );
        _superpositionCoefficients = superpositionCoefficients;
        _superpositionCoefficients.addObserver( this );
        syncWithPotential();
    }

    //----------------------------------------------------------------------------
    // Accessors
    //----------------------------------------------------------------------------
    
    public void setPotential( BSAbstractPotential potential ) {
        if ( potential != _potential ) {
            if ( _potential != null ) {
                _potential.deleteObserver( this );
            }
            _potential = potential;
            _potential.addObserver( this );
            syncWithPotential();
            notifyObservers( PROPERTY_POTENTIAL );
        }
    }
    
    public BSAbstractPotential getPotential() {
        return _potential;
    }
    
    public void setSuperpositionCoefficients( BSSuperpositionCoefficients superpositionCoefficients ) {
        if ( superpositionCoefficients != _superpositionCoefficients ) {
            if ( _superpositionCoefficients != null ) {
                _superpositionCoefficients.deleteObserver( this );
            }
            _superpositionCoefficients = superpositionCoefficients;
            _superpositionCoefficients.addObserver( this );
            syncWithPotential();
            notifyObservers( PROPERTY_SUPERPOSITION_COEFFICIENTS );
        }
    }
    
    public BSSuperpositionCoefficients getSuperpositionCoefficients() {
        return _superpositionCoefficients;
    }
    
    public BSEigenstate[] getEigenstates() {
        return _eigenstates;
    }
    
    public void setHilitedEigenstateIndex( final int hilitedEigenstateIndex ) {
        if ( hilitedEigenstateIndex != _hilitedEigenstateIndex ) {
            _hilitedEigenstateIndex = hilitedEigenstateIndex;
            notifyObservers( PROPERTY_HILITED_EIGENSTATE_INDEX );
        }
    }
    
    public int getHilitedEigenstateIndex() {
        return _hilitedEigenstateIndex;
    }
    
    /*
     * Synchronizes the model with the potential.
     * This is called when the potential changes, to ensure that we update the 
     * eigenstates and superposition coefficients.
     */
    private void syncWithPotential() {
        _hilitedEigenstateIndex = BSEigenstate.INDEX_UNDEFINED;
        _eigenstates = _potential.getEigenstates();
        _superpositionCoefficients.setNumberOfCoefficients( _eigenstates.length );
    }
    
    //----------------------------------------------------------------------------
    // Observer implementation
    //----------------------------------------------------------------------------
    
    public void update( Observable o, Object arg ) {
        if ( o == _potential ) {
            syncWithPotential();
            notifyObservers( PROPERTY_POTENTIAL );
        }
        else if ( o == _superpositionCoefficients ) {
            notifyObservers( PROPERTY_SUPERPOSITION_COEFFICIENTS );
        }
    }
    
    //----------------------------------------------------------------------------
    // Convenience methods
    //----------------------------------------------------------------------------
    
    public BSWellType getWellType() {
        return _potential.getWellType();
    }
    
    public int getNumberOfWells() {
        return _potential.getNumberOfWells();
    }
    
    public int getNumberOfEigenstates() {
        int numberOfEigenstates = 0;
        if ( _eigenstates != null ) {
            numberOfEigenstates = _eigenstates.length;
        }
        return numberOfEigenstates;
    }
    
    public BSEigenstate getEigenstate( int index ) {
        if ( index < 0 || index >= _eigenstates.length ) {
            throw new IndexOutOfBoundsException( "index out of bounds: " + index );
        }
        return _eigenstates[ index ];
    }
}
