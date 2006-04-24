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
import edu.colorado.phet.mri.view.BFieldGraphicPanel;
import edu.colorado.phet.mri.controller.AbstractMriModule;
import edu.colorado.phet.mri.controller.EmRepSelector;
import edu.colorado.phet.mri.controller.FadingMagnetControl;
import edu.colorado.phet.mri.controller.GradientMagnetControl;

import javax.swing.*;
import java.awt.*;

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
    public HeadModuleControlPanel( AbstractMriModule module, GradientElectromagnet gradientMagnet ) {
        MriModel model = (MriModel)module.getModel();

//        JComponent fadingMagnetsControl = new FadingMagnetControl( model );
        MonitorPanel monitorPanel = new MonitorPanel( model );
        monitorPanel.setPreferredSize( new Dimension( 200, 200 ) );

        addControl( new FadingMagnetControl( model ) );
        addControl( new GradientMagnetControl( gradientMagnet ));
        addControl( monitorPanel );

        // These lines should be in a class that wraps around the PhetPCanvas in BFieldGraphicPanel
        {
            final BFieldGraphicPanel fieldGraphic = new BFieldGraphicPanel( model.getLowerMagnet() );
            final JPanel fieldGraphicPanel = new JPanel();
            fieldGraphicPanel.setBorder( BorderFactory.createTitledBorder( BorderFactory.createEtchedBorder( ),"Magnetic Field") );
            fieldGraphicPanel.add( fieldGraphic );
            addControl( fieldGraphicPanel );
        }

        addControl( new EmRepSelector( module ) );
//        addControl( new SampleMaterialSelector( model ) );

//        addControl( new PrecessionControl( model ) );
//        addControl( new SpinDeterminationControl( model ) );
//        addControl( new MonitorPanelRepControl( monitorPanel ) );
    }
}
