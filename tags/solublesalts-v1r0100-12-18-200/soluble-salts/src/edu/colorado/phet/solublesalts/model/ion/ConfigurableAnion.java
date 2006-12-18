/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.solublesalts.model.ion;

import edu.colorado.phet.common.math.Vector2D;

import java.awt.geom.Point2D;

/**
 * ConfigurableAnion
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class ConfigurableAnion extends Ion {

    public static final double RADIUS = 8;
    private static final double MASS = 11;
    private static int CHARGE = -1;

    public static void setClassCharge( int charge ) {
        if( charge >= 0 ) {
            throw new IllegalArgumentException( "charge must be < 0");
        }
        CHARGE = charge;
    }

    public static int getClassCharge() {
        return CHARGE;
    }

    public ConfigurableAnion() {
        super( new IonProperties( MASS, CHARGE, RADIUS ));
    }

    public ConfigurableAnion( Point2D position, Vector2D velocity, Vector2D acceleration ) {
        super( position,
               velocity,
               acceleration,
               new IonProperties( MASS, CHARGE, RADIUS) );
    }
}
