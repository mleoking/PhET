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

import edu.colorado.phet.common.model.clock.AbstractClock;
import edu.colorado.phet.lasers.controller.ApparatusConfiguration;
import edu.colorado.phet.lasers.controller.LaserConfig;
import edu.colorado.phet.lasers.controller.LaserControlPanel;

public class OneAtomModule extends SingleAtomBaseModule {

    public OneAtomModule( AbstractClock clock ) {
        super( "One Atom" );

        LaserControlPanel controlPanel = new LaserControlPanel( this, clock );
        controlPanel.setMaxPhotonRate( 5 );
        setControlPanel( controlPanel );

        ApparatusConfiguration config = new ApparatusConfiguration();
        config.setStimulatedPhotonRate( 1 );
        config.setMiddleEnergySpontaneousEmissionTime( LaserConfig.DEFAULT_SPONTANEOUS_EMISSION_TIME );
        config.setPumpingPhotonRate( 1 );
        config.setReflectivity( 0.7 );
        config.configureSystem( getLaserModel() );
    }
}
