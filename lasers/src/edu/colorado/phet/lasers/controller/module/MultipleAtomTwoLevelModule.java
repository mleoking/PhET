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
import edu.colorado.phet.lasers.view.BaseLaserModule;
import edu.colorado.phet.lasers.view.TwoEnergyLevelMonitorPanel;

import java.awt.geom.Point2D;
import java.util.ArrayList;

public class MultipleAtomTwoLevelModule extends BaseLaserModule {

    private double s_maxSpeed = .5;
    private ArrayList atoms;

    /**
     *
     */
    public MultipleAtomTwoLevelModule( AbstractClock clock) {
        super( "Multiple Atoms / Two Levels" );

//        double newHeight = 100;
//        setCavityHeight( newHeight );

//        Atom atom = null;
//        atoms = new ArrayList();
//        for( int i = 0; i < 20; i++ ) {
//            atom = new Atom();
//            boolean placed = false;
//
//            // Place atoms so they don't overlap
//            do {
//                placed = true;
//                atom.setPosition( ( getLaserOrigin().getX() + ( Math.random() ) * ( s_boxWidth - atom.getRadius() * 2 ) + atom.getRadius() ),
//                                  ( getLaserOrigin().getY() + ( Math.random() ) * ( s_boxHeight - atom.getRadius() * 2 ) ) + atom.getRadius() );
////                                  ( getLaserOrigin().getY() + ( Math.random() ) * ( newHeight - atom.getRadius() * 2 ) ) + atom.getRadius() );
//                atom.setVelocity( ( Math.random() - 0.5 ) * s_maxSpeed,
//                                  ( Math.random() - 0.5 ) * s_maxSpeed );
//                for( int j = 0; j < atoms.size(); j++ ) {
//                    Atom atom2 = (Atom)atoms.get( j );
//                    double d = atom.getPosition().distance( atom2.getPosition() );
//                    if( d <= atom.getRadius() + atom2.getRadius() ) {
////                    if( ContactDetector.areContacting( atom, atom2 )) {
//                        placed = false;
//                        break;
//                    }
//                }
//            } while( !placed );
//            atoms.add( atom );
//            addAtom( atom );
////            new AddAtomCmd( atom ).doIt();
//        }
//
//        ApparatusConfiguration config = new ApparatusConfiguration();
//        config.setStimulatedPhotonRate( 2.0f );
//        config.setMiddleEnergySpontaneousEmissionTime( 0.500f );
//        config.setPumpingPhotonRate( 0f );
//        config.setHighEnergySpontaneousEmissionTime( 0.05f );
//        config.setReflectivity( 0.7f );
//        config.configureSystem( (LaserModel)getModel() );

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
//                    if( ContactDetector.areContacting( atom, atom2 )) {
                        placed = false;
                        break;
                    }
                }
            } while( !placed );
            atoms.add( atom );
            addAtom( atom );
//            new AddAtomCmd( atom ).doIt();
        }

        ApparatusConfiguration config = new ApparatusConfiguration();
        config.setStimulatedPhotonRate( 2.0f );
        config.setMiddleEnergySpontaneousEmissionTime( 0.500f );
        config.setPumpingPhotonRate( 0f );
        config.setHighEnergySpontaneousEmissionTime( 0.05f );
        config.setReflectivity( 0.7f );
        config.configureSystem( (LaserModel)getModel() );

//        PhetApplication.instance().getPhetMainPanel().setMonitorPanel( new ThreeEnergyLevelMonitorPanel() );
//        PhetApplication.instance().getPhetMainPanel().setControlPanel( new ThreeLevelControlPanel() );

//
//        double newHeight = 100;
//        new SetCavityHeightCmd( newHeight ).doIt();
//
//        Atom atom = null;
//        atoms = new ArrayList();
//        for( int i = 0; i < 20; i++ ) {
//            atom = new Atom();
//            boolean placed = false;
//
//            // Place atoms so they don't overlap
//            do {
//                placed = true;
//                atom.setPosition( (double)( getLaserOrigin().getX() + ( Math.random() ) * ( s_boxWidth - atom.getRadius() * 2 ) + atom.getRadius() ),
//                                  (double)( getLaserOrigin().getY() + ( Math.random() ) * ( newHeight - atom.getRadius() * 2 ) ) + atom.getRadius() );
//                atom.setVelocity( (double)( Math.random() - 0.5 ) * s_maxSpeed,
//                                  (double)( Math.random() - 0.5 ) * s_maxSpeed );
//                for( int j = 0; j < atoms.size(); j++ ) {
//                    Atom atom2 = (Atom)atoms.get( j );
//                    if( ContactDetector.areContacting( atom, atom2 )) {
//                        placed = false;
//                        break;
//                    }
//                }
//            } while( !placed );
//            atoms.add( atom );
//            new AddAtomCmd( atom ).doIt();
//        }
//
//        ApparatusConfiguration config = new ApparatusConfiguration();
//        config.setStimulatedPhotonRate( 2.0f );
//        config.setMiddleEnergySpontaneousEmissionTime( 0.500f );
//        config.setPumpingPhotonRate( 0f );
//        config.setHighEnergySpontaneousEmissionTime( 0.05f );
//        config.setReflectivity( 0.7f );
//        config.configureSystem( (LaserModel)getModel() );
//
    }

    /**
     *
     */
    public void deactivate( PhetApplication app ) {
        super.deactivate( app );
        atoms.clear();
    }

    private void setCavityHeight( double height ) {
        ResonatingCavity cavity = getLaserModel().getResonatingCavity();
        double cavityHeight =  cavity.getHeight();
        Point2D cavityPos = cavity.getPosition();
        double yNew = cavityPos.getY() + cavityHeight / 2 - height / 2;
        cavity.setPosition( cavityPos.getX(), yNew );
        cavity.setHeight( height );
        CollimatedBeam stimulatingBeam = getLaserModel().getStimulatingBeam();
        stimulatingBeam.setPosition( stimulatingBeam.getPosition().getX(),
                                     cavity.getPosition().getY() );
        stimulatingBeam.setHeight( height );
    }
}
