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

public class RigidSphereModuleI extends RigidHollowSphereModule {

    public RigidSphereModuleI( AbstractClock clock ) {
        super( clock, "Rigid Hollow Sphere", HeavySpecies.class );
    }
}
