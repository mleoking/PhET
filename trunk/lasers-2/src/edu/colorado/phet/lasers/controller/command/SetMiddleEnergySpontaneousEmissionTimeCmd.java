package edu.colorado.phet.lasers.controller.command;


/**
 * Created by IntelliJ IDEA.
 * User: Ron LeMaster
 * Date: Mar 27, 2003
 * Time: 9:59:09 AM
 * To change this template use Options | File Templates.
 */
public class SetMiddleEnergySpontaneousEmissionTimeCmd extends LaserApplicationCmd {

    private float time;

    public SetMiddleEnergySpontaneousEmissionTimeCmd( float time ) {
        this.time = time;
    }

    public Object doIt() {
        getLaserSystem().setMiddleEnergySpontaneousEmissionTime( time );
        return null;
    }
}
