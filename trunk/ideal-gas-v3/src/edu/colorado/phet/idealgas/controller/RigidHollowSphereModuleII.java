/**
 * Created by IntelliJ IDEA.
 * User: Another Guy
 * Date: Feb 13, 2003
 * Time: 2:20:47 PM
 * To change this template use Options | File Templates.
 */
package edu.colorado.phet.idealgas.controller;

import edu.colorado.phet.common.model.clock.AbstractClock;
import edu.colorado.phet.idealgas.model.LightSpecies;

import java.util.ResourceBundle;

public class RigidHollowSphereModuleII extends RigidHollowSphereModule {

    private static ResourceBundle localizedStrings;
    static {
        localizedStrings = ResourceBundle.getBundle( "localization/RigidHollowSphereModuleII" );
    }

    public RigidHollowSphereModuleII( AbstractClock clock ) {
        super( clock, localizedStrings.getString( "Rigid_Hollow_Sphere_2" ), LightSpecies.class );
    }
}
