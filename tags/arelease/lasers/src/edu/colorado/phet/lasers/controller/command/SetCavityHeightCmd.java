/**
 * Class: SetCavityHeightCmd
 * Package: edu.colorado.phet.lasers.controller.command
 * Author: Another Guy
 * Date: Apr 17, 2003
 */
package edu.colorado.phet.lasers.controller.command;

import edu.colorado.phet.lasers.physics.ResonatingCavity;
import edu.colorado.phet.lasers.physics.photon.CollimatedBeam;
import edu.colorado.phet.physics.Vector2D;

public class SetCavityHeightCmd extends LaserSystemCommand {

    private float height;

    public SetCavityHeightCmd( float height ) {
        this.height = height;
    }

    public Object doIt() {
        ResonatingCavity cavity = getLaserSystem().getResonatingCavity();
        float cavityHeight =  cavity.getHeight();
        Vector2D cavityPos = cavity.getPosition();
        float yNew = cavityPos.getY() + cavityHeight / 2 - height / 2;
        cavity.setPosition( cavityPos.getX(), yNew );
        cavity.setHeight( height );
        CollimatedBeam stimulatingBeam = getLaserSystem().getStimulatingBeam();
        stimulatingBeam.setPosition( stimulatingBeam.getPosition().getX(),
                                     cavity.getPosition().getY() );
        stimulatingBeam.setHeight( height );

        return null;
    }
}
