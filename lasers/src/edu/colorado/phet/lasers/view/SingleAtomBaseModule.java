/**
 * Class: SingleAtomBaseModule
 * Package: edu.colorado.phet.lasers.view
 * Author: Another Guy
 * Date: Apr 1, 2003
 */
package edu.colorado.phet.lasers.view;

import edu.colorado.phet.common.application.PhetApplication;
import edu.colorado.phet.lasers.model.LaserModel;
import edu.colorado.phet.lasers.model.atom.Atom;
import edu.colorado.phet.lasers.model.photon.CollimatedBeam;
import edu.colorado.phet.lasers.controller.LaserConfig;

import java.awt.geom.Point2D;

public class SingleAtomBaseModule extends BaseLaserModule {
    private Atom atom;
    private CollimatedBeam stimulatingBeam;
    private CollimatedBeam pumpingBeam;

    public SingleAtomBaseModule( String title ) {
        super( title );

        atom = new Atom();
        atom.setPosition( getLaserOrigin().getX() + s_boxWidth / 2,
                          getLaserOrigin().getY() + s_boxHeight / 2  );
        atom.setVelocity( 0, 0 );
        addAtom( atom );

        stimulatingBeam = ((LaserModel)getModel()).getStimulatingBeam();
        stimulatingBeam.setHeight( s_boxHeight );
//        stimulatingBeam.setHeight( 10 );
        stimulatingBeam.setOrigin( new Point2D.Double( s_origin.getX(), s_origin.getY()  + s_boxHeight / 2));
        stimulatingBeam.setPosition( new Point2D.Double( s_origin.getX(), s_origin.getY() + s_boxHeight / 2 ));

        pumpingBeam = ((LaserModel)getModel()).getPumpingBeam();
        pumpingBeam.setWidth( 10 );
        pumpingBeam.setPosition( pumpingBeam.getPosition().getX() + s_boxWidth / 2,
                                 pumpingBeam.getPosition().getY() );
//        pumpingBeam.getPosition().setX( pumpingBeam.getPosition().getX() + s_boxWidth / 2 );
    }
}
