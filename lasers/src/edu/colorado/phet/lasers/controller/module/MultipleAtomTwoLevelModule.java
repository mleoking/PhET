/**
 * Class: MultipleAtomThreeLevelModule
 * Class: edu.colorado.phet.lasers.view
 * User: Ron LeMaster
 * Date: Mar 28, 2003
 * Time: 1:36:10 PM
 * Latest Change:
 *      $Author$
 *      $Date$
 *      $Name$
 *      $Revision$
 */
package edu.colorado.phet.lasers.controller.module;

import edu.colorado.phet.collision.ContactDetector;
import edu.colorado.phet.common.application.PhetApplication;
import edu.colorado.phet.common.model.clock.AbstractClock;
import edu.colorado.phet.lasers.controller.ApparatusConfiguration;
import edu.colorado.phet.lasers.controller.TwoLevelControlPanel;
import edu.colorado.phet.lasers.model.LaserModel;
import edu.colorado.phet.lasers.model.ResonatingCavity;
import edu.colorado.phet.lasers.model.atom.Atom;
import edu.colorado.phet.lasers.model.photon.CollimatedBeam;
import edu.colorado.phet.lasers.view.TwoEnergyLevelMonitorPanel;

import java.awt.geom.Point2D;
import java.util.ArrayList;

public class MultipleAtomTwoLevelModule extends BaseLaserModule {

    private double s_maxSpeed = .1;
    private ArrayList atoms;

    /**
     *
     */
    public MultipleAtomTwoLevelModule( AbstractClock clock) {
        super( "Multiple Atoms / Two Levels" );
        setMonitorPanel(new TwoEnergyLevelMonitorPanel( (LaserModel)getModel() ));
        setControlPanel( new TwoLevelControlPanel( this, clock ) );
    }

    /**
     *
     */
    public void activate( PhetApplication app ) {
        super.activate( app );

        Atom atom = null;
        atoms = new ArrayList();
        for( int i = 0; i < 20; i++ ) {
            atom = new Atom();
            boolean placed = false;

            // Place atoms so they don't overlap
            do {
                placed = true;
                atom.setPosition( ( getLaserOrigin().getX() + ( Math.random() ) * ( s_boxWidth - atom.getRadius() * 2 ) + atom.getRadius() ),
                                  ( getLaserOrigin().getY() + ( Math.random() ) * ( s_boxHeight - atom.getRadius() * 2 ) ) + atom.getRadius() );

                atom.setVelocity( ( Math.random() - 0.5 ) * s_maxSpeed,
                                  ( Math.random() - 0.5 ) * s_maxSpeed );
                for( int j = 0; j < atoms.size(); j++ ) {
                    Atom atom2 = (Atom)atoms.get( j );
                    double d = atom.getPosition().distance( atom2.getPosition() );
                    if( d <= atom.getRadius() + atom2.getRadius() ) {
                        placed = false;
                        break;
                    }
                }
            } while( !placed );
            atoms.add( atom );
            addAtom( atom );
        }

        ApparatusConfiguration config = new ApparatusConfiguration();
        config.setStimulatedPhotonRate( 2.0f );
        config.setMiddleEnergySpontaneousEmissionTime( 0.500f );
        config.setPumpingPhotonRate( 0f );
        config.setHighEnergySpontaneousEmissionTime( 0.05f );
        config.setReflectivity( 0.7f );
        config.configureSystem( (LaserModel)getModel() );
    }

    /**
     *
     */
    public void deactivate( PhetApplication app ) {
        super.deactivate( app );
        for( int i = 0; i < atoms.size(); i++ ) {
            Atom atom = (Atom)atoms.get( i );
            getLaserModel().removeModelElement( atom );
            atom.removeFromSystem();
        }
        atoms.clear();
    }
}
