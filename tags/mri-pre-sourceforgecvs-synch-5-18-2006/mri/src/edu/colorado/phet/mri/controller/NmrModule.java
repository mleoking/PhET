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
import edu.colorado.phet.common.view.util.SimStrings;
import edu.colorado.phet.mri.MriConfig;
import edu.colorado.phet.mri.view.ModelElementGraphicManager;
import edu.colorado.phet.mri.view.BFieldIndicatorB;
import edu.colorado.phet.mri.controller.AbstractMriModule;
import edu.colorado.phet.mri.controller.NmrControlPanel;
import edu.colorado.phet.mri.model.*;

import java.awt.geom.Rectangle2D;
import java.awt.geom.Point2D;

/**
 * MriModuleA
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class NmrModule extends AbstractMriModule {

    //----------------------------------------------------------------
    // Class fields and methods
    //----------------------------------------------------------------

    private static String name = SimStrings.get("Module.NmrTitle");

    //----------------------------------------------------------------
    // Instance methods and fields
    //----------------------------------------------------------------

    static SampleChamber sampleChamber = new SampleChamber( new Rectangle2D.Double( MriConfig.SAMPLE_CHAMBER_LOCATION.getX(),
                                                                                    MriConfig.SAMPLE_CHAMBER_LOCATION.getY(),
                                                                                    MriConfig.SAMPLE_CHAMBER_WIDTH,
                                                                                    MriConfig.SAMPLE_CHAMBER_HEIGHT ) );

    /**
     * Constructor
     */
    public NmrModule() {
        super( name, new SwingClock( delay, dt ), sampleChamber );

        // Control panel
        setControlPanel( new NmrControlPanel( this ) );

        // Sample Chamber
        sampleChamber.createDipoles( (MriModel)getModel(), 20 );

        // Set the initial view
        setEmRep( NmrModule.WAVE_VIEW );
    }

}
