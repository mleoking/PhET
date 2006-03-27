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
 * WellType is an enumeration of potential well types.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class WellType extends AbstractEnum {

    /* This class is not intended for instantiation. */
    private WellType( String name ) {
        super( name );
    }
    
    // Well type values
    public static final WellType COULOMB = new WellType( "coulomb" );
    public static final WellType HARMONIC_OSCILLATOR = new WellType( "harmonicOscillator" );
    public static final WellType SQUARE = new WellType( "square" );
    public static final WellType ASYMMETRIC = new WellType( "asymmetric" );
    
    /**
     * Retrieves a well type by name.
     * This is used primarily in XML encoding.
     * 
     * @param name
     * @return
     */
    public static WellType getByName( String name ) {
        WellType wellType = null;
        if ( COULOMB.isNamed( name ) ) {
            wellType = COULOMB;
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
