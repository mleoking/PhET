/**
 * Class: SingleAtomBaseModule
 * Package: edu.colorado.phet.lasers.view
 * Author: Another Guy
 * Date: Apr 1, 2003
 */
package edu.colorado.phet.lasers.view;

import edu.colorado.phet.lasers.physics.atom.Atom;
import edu.colorado.phet.lasers.physics.LaserSystem;
import edu.colorado.phet.lasers.physics.photon.CollimatedBeam;
import edu.colorado.phet.lasers.controller.command.AddAtomCmd;
import edu.colorado.phet.common.application.PhetApplication;

import java.awt.geom.Point2D;

public class SingleAtomBaseModule extends BaseLaserModule {
    private Atom atom;
    private CollimatedBeam stimulatingBeam;
    private CollimatedBeam pumpingBeam;

    public SingleAtomBaseModule( String title ) {
        super( title );
    }

    /**
     *
     */
    public void activate( PhetApplication app ) {
        super.activate( app );

        atom = new Atom();
        atom.setPosition( (float) ( getLaserOrigin().getX() + s_boxWidth / 2 ),
                          (float) ( getLaserOrigin().getY() + s_boxHeight / 2  ) );
        atom.setVelocity( 0, 0 );
        new AddAtomCmd( atom ).doIt();

        stimulatingBeam = ((LaserSystem)getModel()).getStimulatingBeam();
        stimulatingBeam.setHeight( 10 );
        stimulatingBeam.setOrigin( new Point2D.Float( (float)s_origin.getX(), (float)s_origin.getY()  + s_boxHeight / 2));
        stimulatingBeam.setPosition( new Point2D.Float( (float)s_origin.getX(), (float)s_origin.getY() + s_boxHeight / 2 ));

        pumpingBeam = ((LaserSystem)getModel()).getPumpingBeam();
        pumpingBeam.setWidth( 10 );
        pumpingBeam.setPosition( pumpingBeam.getPosition().getX() + s_boxWidth / 2,
                                 pumpingBeam.getPosition().getY() );
//        pumpingBeam.getPosition().setX( pumpingBeam.getPosition().getX() + s_boxWidth / 2 );
    }

    /**
     *
     */
    public void deactivate( PhetApplication app ) {
        super.deactivate( app );
    }
}
