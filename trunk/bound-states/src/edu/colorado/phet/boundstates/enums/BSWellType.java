/* Copyright 2005, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.boundstates.enums;


/**
 * BSWellType is an enumeration of potential well types.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class BSWellType extends AbstractEnum {

    /* This class is not intended for instantiation. */
    private BSWellType( String name ) {
        super( name );
    }
    
    // Well type values
    public static final BSWellType COULOMB_1D = new BSWellType( "coulomb1D" );
    public static final BSWellType HARMONIC_OSCILLATOR = new BSWellType( "harmonicOscillator" );
    public static final BSWellType SQUARE = new BSWellType( "square" );
    public static final BSWellType ASYMMETRIC = new BSWellType( "asymmetric" );
    
    /**
     * Retrieves a well type by name.
     * This is used primarily in XML encoding.
     * 
     * @param name
     * @return
     */
    public static BSWellType getByName( String name ) {
        BSWellType wellType = null;
        if ( COULOMB_1D.isNamed( name ) ) {
            wellType = COULOMB_1D;
        }
        else if ( HARMONIC_OSCILLATOR.isNamed( name ) ) {
            wellType = HARMONIC_OSCILLATOR;
        }
        else if ( SQUARE.isNamed( name ) ) {
            wellType = SQUARE;
        }
        else if ( ASYMMETRIC.isNamed( name ) ) {
            wellType = ASYMMETRIC;
        }
        return wellType;
    }
}
