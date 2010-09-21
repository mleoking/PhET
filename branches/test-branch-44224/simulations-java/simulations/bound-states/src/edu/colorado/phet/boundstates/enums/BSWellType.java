/* Copyright 2006, University of Colorado */

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
    public static final BSWellType ASYMMETRIC = new BSWellType( "asymmetric" );
    public static final BSWellType COULOMB_1D = new BSWellType( "coulomb1D" );
    public static final BSWellType COULOMB_3D = new BSWellType( "coulomb3D" );
    public static final BSWellType HARMONIC_OSCILLATOR = new BSWellType( "harmonicOscillator" );
    public static final BSWellType SQUARE = new BSWellType( "square" );
    
    /**
     * Retrieves a well type by name.
     * This is used primarily in XML encoding.
     * 
     * @param name
     * @return the well type that corresponds to name, possibly null
     */
    public static BSWellType getByName( String name ) {
        BSWellType wellType = null;
        if ( ASYMMETRIC.isNamed( name ) ) {
            wellType = ASYMMETRIC;
        }
        else if ( COULOMB_1D.isNamed( name ) ) {
            wellType = COULOMB_1D;
        }
        else if ( COULOMB_3D.isNamed( name ) ) {
            wellType = COULOMB_3D;
        }
        else if ( HARMONIC_OSCILLATOR.isNamed( name ) ) {
            wellType = HARMONIC_OSCILLATOR;
        }
        else if ( SQUARE.isNamed( name ) ) {
            wellType = SQUARE;
        }
        return wellType;
    }
}
