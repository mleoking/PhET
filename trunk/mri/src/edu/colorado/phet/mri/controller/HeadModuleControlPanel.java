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
import edu.colorado.phet.mri.model.MriModel;
import edu.colorado.phet.mri.model.GradientElectromagnet;
import edu.colorado.phet.mri.view.MonitorPanel;
import edu.colorado.phet.mri.view.TumorSelector;
import edu.colorado.phet.mri.view.BFieldGraphicPanel;
import edu.colorado.phet.mri.controller.EmRepSelector;
import edu.colorado.phet.mri.controller.FadingMagnetControl;
import edu.colorado.phet.mri.controller.GradientMagnetControl;

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
    public HeadModuleControlPanel( HeadModule module, GradientElectromagnet gradientMagnet ) {
        MriModel model = (MriModel)module.getModel();

        MonitorPanel monitorPanel = new MonitorPanel( model );
        monitorPanel.setPreferredSize( new Dimension( 200, 200 ) );

        addControl( new FadingMagnetControl( model ) );
        addControl( new GradientMagnetControl( gradientMagnet ) );

        addControl( new BFieldGraphicPanel( model ) );

        addControl( new TumorSelector( module.getHead(), model ) );
        addControl( new EmRepSelector( module ) );
//        addControl( new SampleMaterialSelector( model ) );

//        addControl( new PrecessionControl( model ) );
//        addControl( new SpinDeterminationControl( model ) );
//        addControl( new MonitorPanelRepControl( monitorPanel ) );

        addComponentListener( new ComponentAdapter() {
            public void componentResized( ComponentEvent e ) {
                setPreferredSize( new Dimension( 250, (int)getPreferredSize().getHeight() ) );
            }
        } );
    }
}
