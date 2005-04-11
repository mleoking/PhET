/**
 * Class: SingleAtomBaseApparatusPanel
 * Package: edu.colorado.phet.lasers.view
 * Author: Another Guy
 * Date: Apr 1, 2003
 */
package edu.colorado.phet.lasers.view;

import edu.colorado.phet.lasers.physics.atom.Atom;
import edu.colorado.phet.lasers.physics.LaserSystem;
import edu.colorado.phet.lasers.physics.photon.CollimatedBeam;
import edu.colorado.phet.lasers.controller.command.AddAtomCmd;
import edu.colorado.phet.lasers.controller.ApparatusConfiguration;
import edu.colorado.phet.controller.PhetApplication;

import java.awt.geom.Point2D;

public class SingleAtomBaseApparatusPanel extends BaseLaserApparatusPanel {
    private Atom atom;
    private CollimatedBeam stimulatingBeam;
    private CollimatedBeam pumpingBeam;

    public SingleAtomBaseApparatusPanel( String title ) {
        super( title );
    }

    /**
     *
     */
    public void activate() {
        super.activate();

        atom = new Atom();
        atom.setPosition( (float) ( getLaserOrigin().getX() + s_boxWidth / 2 ),
                          (float) ( getLaserOrigin().getY() + s_boxHeight / 2  ) );
        atom.setVelocity( 0, 0 );
        new AddAtomCmd( atom ).doIt();

        stimulatingBeam = ((LaserSystem)PhetApplication.instance().getPhysicalSystem()).getStimulatingBeam();
        stimulatingBeam.setHeight( 10 );
        stimulatingBeam.setOrigin( new Point2D.Float( (float)s_origin.getX(), (float)s_origin.getY()  + s_boxHeight / 2));
        stimulatingBeam.setPosition( new Point2D.Float( (float)s_origin.getX(), (float)s_origin.getY() + s_boxHeight / 2 ));

        pumpingBeam = ((LaserSystem)PhetApplication.instance().getPhysicalSystem()).getPumpingBeam();
        pumpingBeam.setWidth( 10 );
        pumpingBeam.getPosition().setX( pumpingBeam.getPosition().getX() + s_boxWidth / 2 );
    }

    /**
     *
     */
    public void deactivate() {
        super.deactivate();
    }
}
