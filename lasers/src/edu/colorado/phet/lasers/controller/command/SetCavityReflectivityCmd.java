/**
 * Class: SetCavityReflectivityCmd
 * Package: edu.colorado.phet.lasers.controller.command
 * Author: Another Guy
 * Date: Apr 1, 2003
 */
package edu.colorado.phet.lasers.controller.command;


public class SetCavityReflectivityCmd extends LaserApplicationCmd{

    private float reflectivity;

    public SetCavityReflectivityCmd( float reflectivity ) {
        this.reflectivity = reflectivity;
    }

    public Object doIt() {
        getLaserSystem().getResonatingCavity().setReflectivity( reflectivity );
        return null;
    }
}
