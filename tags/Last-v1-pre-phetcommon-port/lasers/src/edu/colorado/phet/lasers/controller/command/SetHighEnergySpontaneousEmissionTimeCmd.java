package edu.colorado.phet.lasers.controller.command;


/**
 * Created by IntelliJ IDEA.
 * User: Ron LeMaster
 * Date: Mar 27, 2003
 * Time: 9:59:09 AM
 * To change this template use Options | File Templates.
 */
public class SetHighEnergySpontaneousEmissionTimeCmd extends LaserApplicationCmd {

    private float time;

    public SetHighEnergySpontaneousEmissionTimeCmd( float time ) {
        this.time = time;
    }

    public Object doIt() {
        getLaserSystem().setHighEnergySpontaneousEmissionTime( time );
        return null;
    }
}
