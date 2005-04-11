/**
 * Class: ClearSystemCmd
 * Class: edu.colorado.phet.lasers.controller.command
 * User: Ron LeMaster
 * Date: Mar 28, 2003
 * Time: 3:44:52 PM
 */
package edu.colorado.phet.lasers.controller.command;


public class ClearSystemCmd extends LaserApplicationCmd {

    public Object doIt() {
        getLaserSystem().clear();
        return null;
    }
}
