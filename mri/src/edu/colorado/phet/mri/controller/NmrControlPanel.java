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
import edu.colorado.phet.mri.view.MonitorPanel;
import edu.colorado.phet.mri.view.MriLegend;

import java.awt.*;

/**
 * MriControlPanel
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class NmrControlPanel extends ControlPanel {

    /**
     * Constructor
     *
     * @param module
     */
    public NmrControlPanel( AbstractMriModule module ) {
        MriModel model = (MriModel)module.getModel();

        MonitorPanel monitorPanel = new MonitorPanel( model );
        monitorPanel.setPreferredSize( new Dimension( 200, 200 ) );

        addControlFullWidth( new MriLegend() );
        addControlFullWidth( monitorPanel );
        addControlFullWidth( new FadingMagnetControl( model ) );
//        addControlFullWidth( new BFieldGraphicPanel( model ) );
//        addControlFullWidth( new EmRepSelector( module ) );
        addControlFullWidth( new SampleMaterialSelector( model ) );

//        addControl( new PrecessionControl( model ) );
//        addControl( new SpinDeterminationControl( model ) );
//        addControl( new MonitorPanelRepControl( monitorPanel ) );
    }
}
