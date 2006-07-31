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

import edu.colorado.phet.common.view.ControlPanel;
import edu.colorado.phet.mri.model.GradientElectromagnet;
import edu.colorado.phet.mri.model.MriModel;
import edu.colorado.phet.mri.view.GradientMagnetControlPanel;
import edu.colorado.phet.mri.view.MonitorPanel;

import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

/**
 * MriControlPanel
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class HeadModuleControlPanel extends ControlPanel {

    /**
     * Constructor
     *
     * @param module
     */
    public HeadModuleControlPanel( HeadModule module,
                                   GradientElectromagnet horizontalGradientMagnet,
                                   GradientElectromagnet verticalGradientMagnet ) {
        MriModel model = (MriModel)module.getModel();

        MonitorPanel monitorPanel = new MonitorPanel( model );
        monitorPanel.setPreferredSize( new Dimension( 200, 200 ) );

        addControl( new FadingMagnetControl( model ) );
        addControl( new GradientMagnetControlPanel( horizontalGradientMagnet, verticalGradientMagnet ) );
//        addControl( new BFieldGraphicPanel( model ) );
//        addControl( new TumorSelector( module.getHead(), model ) );
        addControlFullWidth( new HeadControl( module ) );
        addControlFullWidth( new EmRepSelector( module ) );
//        addControl( new DetectorControl( module ));

        addComponentListener( new ComponentAdapter() {
            public void componentResized( ComponentEvent e ) {
                setPreferredSize( new Dimension( 250, (int)getPreferredSize().getHeight() ) );
            }
        } );
    }
}
