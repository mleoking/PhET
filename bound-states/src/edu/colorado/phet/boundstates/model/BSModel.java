/* Copyright 2006, University of Colorado */

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
    
    public static final String PROPERTY_PARTICLE = "particle";
    public static final String PROPERTY_POTENTIAL = "potential";
    public static final String PROPERTY_HILITED_EIGENSTATE_INDEX = "hilitedEnergy";
    public static final String PROPERTY_SUPERPOSITION_COEFFICIENTS_COUNT = "superpositionCoefficientsCount";
    public static final String PROPERTY_SUPERPOSITION_COEFFICIENTS_VALUES = "superpositionCoefficientsValues"; // implies that count may have changed
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private BSParticle _particle;
    private BSAbstractPotential _potential;
    private BSSuperpositionCoefficients _superpositionCoefficients;
    private BSEigenstate[] _eigenstates;
    private int _hilitedEigenstateIndex;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    public BSModel( BSParticle particle, BSAbstractPotential potential, BSSuperpositionCoefficients superpositionCoefficients ) {
        _particle = particle;
        _particle.addObserver( this );
        _potential = potential;
        _potential.addObserver( this );
        _superpositionCoefficients = superpositionCoefficients;
        _superpositionCoefficients.addObserver( this );
        syncWithPotential();
    }

    //----------------------------------------------------------------------------
    // Accessors
    //----------------------------------------------------------------------------
    
    public void setParticle( BSParticle particle ) {
        if ( particle != _particle ) {
            if ( _particle != null ) {
                _particle.deleteObserver( this );
            }
            _particle = particle;
            _particle.addObserver( this );
            notifyObservers( PROPERTY_PARTICLE );
        }
    }
    
    public BSParticle getParticle() {
        return _particle;
    }
    
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
        assert( superpositionCoefficients.getNumberOfCoefficients() == _eigenstates.length );
        if ( superpositionCoefficients != _superpositionCoefficients ) {
            if ( _superpositionCoefficients != null ) {
                _superpositionCoefficients.deleteObserver( this );
            }
            _superpositionCoefficients = superpositionCoefficients;
            _superpositionCoefficients.addObserver( this );
            syncWithPotential();
            notifyObservers( PROPERTY_SUPERPOSITION_COEFFICIENTS_VALUES );
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
    
    /**
     * Gets the eigenstate that is closest to some energy value,
     * within some threshold.  If there is no eigenstate within
     * the threshold, then BSEigenstate.INDEX_UNDEFINED is returned.
     * 
     * @param energy
     * @param threshold
     * @return eigenstate index, possibly BSEigenstate.INDEX_UNDEFINED
     */
    public int getClosestEigenstateIndex( final double energy, final double threshold ) {
        int index = BSEigenstate.INDEX_UNDEFINED;
        if ( _eigenstates != null && _eigenstates.length > 0 ) {
            if ( _eigenstates.length == 1 ) {
                if ( Math.abs( _eigenstates[0].getEnergy() - energy ) <= threshold ) {
                    index = 0;
                }
            }
            else {
                for ( int i = 1; i < _eigenstates.length; i++ ) {
                    final double currentEnergy = _eigenstates[i].getEnergy();
                    if ( energy == currentEnergy ) {
                        index = i;
                        break;
                    }
                    else if ( energy < currentEnergy ) {
                        final double lowerEnergy = _eigenstates[i - 1].getEnergy();
                        final double currentEnergyDifference = Math.abs( currentEnergy - energy );
                        final double lowerEnergyDifference = Math.abs( energy - lowerEnergy );
                        if ( currentEnergyDifference <= lowerEnergyDifference && currentEnergyDifference <= threshold ) {
                            index = i;
                            break;
                        }
                        else if ( currentEnergyDifference > lowerEnergyDifference && lowerEnergyDifference <= threshold ) {
                            index = i - 1;
                            break;
                        }
                    }
                }
            }
        }
        return index;
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
        if ( o == _particle ) {
            notifyObservers( PROPERTY_PARTICLE );
        }
        else if ( o == _potential ) {
            syncWithPotential();
            notifyObservers( PROPERTY_POTENTIAL );
        }
        else if ( o == _superpositionCoefficients ) {
            notifyObservers( arg );
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
    
    public boolean isSuperpositionState() {
        return _superpositionCoefficients.isSuperpositionState();
    }
}
