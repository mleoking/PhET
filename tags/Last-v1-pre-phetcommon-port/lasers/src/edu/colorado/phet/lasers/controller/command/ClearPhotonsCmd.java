/**
 * Class: ClearPhotonsCmd
 * Class: edu.colorado.phet.lasers.controller.command
 * User: Ron LeMaster
 * Date: Mar 31, 2003
 * Time: 7:58:36 PM
 */
package edu.colorado.phet.lasers.controller.command;


public class ClearPhotonsCmd extends LaserApplicationCmd {

    public Object doIt() {
        getLaserSystem().removePhotons();
        return null;
    }
}
