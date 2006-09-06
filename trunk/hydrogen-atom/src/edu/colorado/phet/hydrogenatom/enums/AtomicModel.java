/* Copyright 2006, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.hydrogenatom.enums;


/**
 * AtomicModel is an enumeration of atomic models.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class AtomicModel extends AbstractEnum {

    /* This class is not intended for instantiation. */
    private AtomicModel( String name ) {
        super( name );
    }
    
    // Well type values
    public static final AtomicModel BILLIARD_BALL = new AtomicModel( "billiardBall" );
    public static final AtomicModel PLUM_PUDDING = new AtomicModel( "plumPudding" );
    public static final AtomicModel SOLAR_SYSTEM = new AtomicModel( "solarSystem" );
    public static final AtomicModel BOHR = new AtomicModel( "bohr" );
    public static final AtomicModel DEBROGLIE = new AtomicModel( "deBroglie" );
    public static final AtomicModel SCHRODINGER = new AtomicModel( "schrodinger" );
    
    /**
     * Retrieves a well type by name.
     * This is used primarily in XML encoding.
     * 
     * @param name
     * @return the well type that corresponds to name, possibly null
     */
    public static AtomicModel getByName( String name ) {
        AtomicModel wellType = null;
        if ( BILLIARD_BALL.isNamed( name ) ) {
            wellType = BILLIARD_BALL;
        }
        else if ( PLUM_PUDDING.isNamed( name ) ) {
            wellType = PLUM_PUDDING;
        }
        else if ( SOLAR_SYSTEM.isNamed( name ) ) {
            wellType = SOLAR_SYSTEM;
        }
        else if ( BOHR.isNamed( name ) ) {
            wellType = BOHR;
        }
        else if ( DEBROGLIE.isNamed( name ) ) {
            wellType = DEBROGLIE;
        }
        else if ( SCHRODINGER.isNamed( name ) ) {
            wellType = SCHRODINGER;
        }
        return wellType;
    }
}
