/**
 * Class: SingleAtomApparatusPanel
 * Class: edu.colorado.phet.lasers.view
 * User: Ron LeMaster
 * Date: Mar 28, 2003
 * Time: 1:24:50 PM
 * Latest Change:
 *      $Author$
 *      $Date$
 *      $Name$
 *      $Revision$
 */
package edu.colorado.phet.lasers.controller.module;

import edu.colorado.phet.common.application.PhetApplication;
import edu.colorado.phet.common.model.clock.AbstractClock;
import edu.colorado.phet.lasers.controller.ApparatusConfiguration;
import edu.colorado.phet.lasers.controller.ThreeLevelControlPanel;
import edu.colorado.phet.lasers.model.LaserModel;
import edu.colorado.phet.lasers.model.ResonatingCavity;
import edu.colorado.phet.lasers.model.atom.Atom;
import edu.colorado.phet.lasers.model.photon.CollimatedBeam;
import edu.colorado.phet.lasers.view.*;

import java.awt.geom.Point2D;

public class OneAtomThreeLevelsModule extends BaseLaserModule {

    private ThreeEnergyLevelMonitorPanel monitorPanel;
    private Atom atom;

    public OneAtomThreeLevelsModule( AbstractClock clock ) {
        super( "One Atom / Three Energy Levels" );

        monitorPanel = new ThreeEnergyLevelMonitorPanel( (LaserModel)getModel() );
        setMonitorPanel( monitorPanel  );
        setControlPanel( new ThreeLevelControlPanel( this, clock ) );
    }

    public void activate( PhetApplication app ) {
        super.activate( app );
        atom = new Atom();
        atom.setPosition( getLaserOrigin().getX() + s_boxWidth / 2,
                          getLaserOrigin().getY() + s_boxHeight / 2  );
        atom.setVelocity( 0, 0 );
        addAtom( atom );
    }

    public void deactivate( PhetApplication app ) {
        super.deactivate( app );
        getLaserModel().removeModelElement( atom );
        atom.removeFromSystem();
    }
}
