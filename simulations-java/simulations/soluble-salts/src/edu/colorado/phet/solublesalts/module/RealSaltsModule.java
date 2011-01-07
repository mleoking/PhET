// Copyright 2002-2011, University of Colorado

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.solublesalts.module;

import edu.colorado.phet.common.phetcommon.model.clock.IClock;
import edu.colorado.phet.solublesalts.SolubleSaltResources;
import edu.colorado.phet.solublesalts.SolubleSaltsConfig;
import edu.colorado.phet.solublesalts.control.RealSaltsControlPanel;

/**
 * SolubleSaltsModule
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class RealSaltsModule extends SolubleSaltsModule {
    public RealSaltsModule( IClock clock ) {
        super( SolubleSaltResources.getString( "Module.title" ),
               clock,
               new SolubleSaltsConfig.Calibration( 1.5E-16 / 500,
                                                   1E-16,
                                                   0.5E-16,
                                                   0.1E-16 ) );
        // Set up the control panel
        setControlPanel( new RealSaltsControlPanel( this ) );
    }
}
