/**
 * Class: ClearSystemCmd
 * Class: edu.colorado.phet.lasers.controller.command
 * User: Ron LeMaster
 * Date: Mar 28, 2003
 * Time: 3:44:52 PM
 */
package edu.colorado.phet.lasers.controller.command;

import edu.colorado.phet.physics.body.Particle;

import java.util.List;

public class ClearPhysicalEntitiesCmd extends LaserApplicationCmd {

    public Object doIt() {
        List enitities = getLaserSystem().getBodies();
        for( int i = 0; i < enitities.size(); i++ ) {
            Particle physicalEntity = (Particle)enitities.get( i );
            edu.colorado.phet.controller.command.Command cmd = new edu.colorado.phet.controller.command.RemoveParticleCmd( physicalEntity );
            getLaserSystem().addPrepCmd( cmd );
        }
        return null;
    }
}
