/**
 * Created by IntelliJ IDEA.
 * User: Another Guy
 * Date: Feb 13, 2003
 * Time: 2:20:47 PM
 * To change this template use Options | File Templates.
 */
package edu.colorado.phet.idealgas.controller;

import edu.colorado.phet.common.model.clock.AbstractClock;
import edu.colorado.phet.idealgas.model.HeavySpecies;

import java.util.ResourceBundle;

public class RigidSphereModuleI extends RigidHollowSphereModule {

    private static ResourceBundle localizedStrings;
    static {
        localizedStrings = ResourceBundle.getBundle( "localization/RigidHollowSphereModuleI" );
    }

    public RigidSphereModuleI( AbstractClock clock ) {
        super( clock, localizedStrings.getString( "Rigid_Hollow_Sphere_1" ), HeavySpecies.class );
    }
}
