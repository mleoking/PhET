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
import edu.colorado.phet.mri.model.*;
import edu.colorado.phet.piccolo.util.PImageFactory;
import edu.umd.cs.piccolo.PNode;

import java.awt.*;
import java.awt.geom.Ellipse2D;

/**
 * MriModuleA
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class HeadModule extends AbstractMriModule {

    //----------------------------------------------------------------
    // Class fields and methods
    //----------------------------------------------------------------

    private static String name = "The Head";

    //----------------------------------------------------------------
    // Instance methods and fields
    //----------------------------------------------------------------

    /**
     * Constructor
     */
    public HeadModule() {
        super( HeadModule.name, new SwingClock( delay, dt ) );

        // Control panel
        HeadModuleControlPanel controlPanel = new HeadModuleControlPanel( this );
        setControlPanel( controlPanel );

        double earOffsetX = 60;
        Head head = new Head( new Ellipse2D.Double( MriConfig.SAMPLE_CHAMBER_LOCATION.getX() + earOffsetX,
                                                    MriConfig.SAMPLE_CHAMBER_LOCATION.getY() + 25,
                                                    MriConfig.SAMPLE_CHAMBER_WIDTH - earOffsetX * 2,
                                                    MriConfig.SAMPLE_CHAMBER_HEIGHT + 100) );

        // Make some dipoles
        head.createDipoles( (MriModel)getModel(), 60);

        // Add the head graphic
        PNode headGraphic = PImageFactory.create( MriConfig.HEAD_IMAGE, new Dimension( (int)MriConfig.SAMPLE_CHAMBER_WIDTH,
                                                                                       (int)MriConfig.SAMPLE_CHAMBER_WIDTH) );
        headGraphic.setOffset( MriConfig.SAMPLE_CHAMBER_LOCATION );
        getWorldNode().addChild( headGraphic );

        // Set the initial view
        setEmRep( HeadModule.WAVE_VIEW );

    }

}
