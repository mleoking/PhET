/**
 * Class: SingleAtomApparatusPanel
 * Class: edu.colorado.phet.lasers.view
 * User: Ron LeMaster
 * Date: Mar 28, 2003
 * Time: 1:24:50 PM
 */
package edu.colorado.phet.lasers.controller.module;

import edu.colorado.phet.common.application.PhetApplication;
import edu.colorado.phet.common.model.clock.AbstractClock;
import edu.colorado.phet.lasers.controller.ApparatusConfiguration;
import edu.colorado.phet.lasers.controller.LaserConfig;
import edu.colorado.phet.lasers.controller.TwoLevelControlPanel;
import edu.colorado.phet.lasers.model.LaserModel;
import edu.colorado.phet.lasers.model.atom.Atom;
import edu.colorado.phet.lasers.view.EnergyLevelsDialog;
import edu.colorado.phet.lasers.view.TwoEnergyLevelMonitorPanel;

public class OneAtomTwoLevelsModule extends SingleAtomBaseModule {

    private TwoEnergyLevelMonitorPanel monitorPanel;
    private Atom atom;
    private EnergyLevelsDialog energyLevelsDialog;

    /**
     *
     */
    public OneAtomTwoLevelsModule( AbstractClock clock ) {
        super( "One Atom / Two Energy Levels" );

        setEnergyMonitorPanel( new TwoEnergyLevelMonitorPanel( (LaserModel)getModel() ) );
        //        monitorPanel = new TwoEnergyLevelMonitorPanel( (LaserModel)getModel() );
        //        setMonitorPanel( monitorPanel );
        setControlPanel( new TwoLevelControlPanel( this, clock ) );
    }

    public void activate( PhetApplication app ) {
        super.activate( app );
        ApparatusConfiguration config = new ApparatusConfiguration();
        config.setStimulatedPhotonRate( 1 );
        //        config.setStimulatedPhotonRate( LaserConfig.DEFAULT_STIMULATING_PHOTON_RATE );
        config.setMiddleEnergySpontaneousEmissionTime( LaserConfig.DEFAULT_SPONTANEOUS_EMISSION_TIME );
        config.setPumpingPhotonRate( 0.0f );
        config.setReflectivity( 0.7f );
        config.configureSystem( getLaserModel() );
    }
    //
    //    public void deactivate( PhetApplication app ) {
    //        super.deactivate( app );
    //        getLaserModel().removeModelElement( atom );
    //        atom.removeFromSystem();
    ////
    ////        energyLevelsDialog.setVisible( false );
    //    }
}
