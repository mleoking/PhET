/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.mri.controller;

import edu.colorado.phet.common.model.clock.SwingClock;
import edu.colorado.phet.mri.MriConfig;
import edu.colorado.phet.mri.controller.AbstractMriModule;
import edu.colorado.phet.mri.controller.MriControlPanel;
import edu.colorado.phet.mri.model.*;

import java.awt.geom.Rectangle2D;

/**
 * MriModuleA
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class MriModuleA extends AbstractMriModule {

    //----------------------------------------------------------------
    // Class fields and methods
    //----------------------------------------------------------------

    private static String name = "Simplified NMR";

    //----------------------------------------------------------------
    // Instance methods and fields
    //----------------------------------------------------------------

    /**
     * Constructor
     */
    public MriModuleA() {
        super( name, new SwingClock( delay, dt ) );

        // Control panel
        setControlPanel( new MriControlPanel( this ) );

        // Sample Chamber
        SampleChamber sampleChamber = new SampleChamber( new Rectangle2D.Double( MriConfig.SAMPLE_CHAMBER_LOCATION.getX(),
                                                                                                             MriConfig.SAMPLE_CHAMBER_LOCATION.getY(),
                                                                                                             MriConfig.SAMPLE_CHAMBER_WIDTH,
                                                                                                             MriConfig.SAMPLE_CHAMBER_HEIGHT ) );
        addModelElement( sampleChamber );
        sampleChamber.createDipoles( (MriModel)getModel(), 20 );

        // Set the initial view
        setEmRep( MriModuleA.WAVE_VIEW );
    }

}
