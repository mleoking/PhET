// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.solublesalts.model.ion;

/**
 * ConfigurableAnion
 *
 * @author Ron LeMaster
 */
public class ConfigurableAnion extends Ion {

    public static final double RADIUS = 8;
    private static final double MASS = 11;
    private static int CHARGE = -1;

    public static void setClassCharge( int charge ) {
        if ( charge >= 0 ) {
            throw new IllegalArgumentException( "charge must be < 0" );
        }
        CHARGE = charge;
    }

    public static int getClassCharge() {
        return CHARGE;
    }

    public ConfigurableAnion() {
        super( new IonProperties( MASS, CHARGE, RADIUS ) );
    }
}