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
import edu.colorado.phet.mri.view.BFieldArrowGraphic;
import edu.colorado.phet.mri.view.MonitorPanel;
import edu.colorado.phet.mri.controller.AbstractMriModule;
import edu.colorado.phet.mri.controller.EmRepSelector;
import edu.colorado.phet.mri.controller.FadingMagnetControl;

import javax.swing.*;
import java.awt.*;

/**
 * MriControlPanel
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class MriControlPanel extends ControlPanel {

    /**
     * Constructor
     *
     * @param module
     */
    public MriControlPanel( AbstractMriModule module ) {
        MriModel model = (MriModel)module.getModel();

//        JComponent fadingMagnetsControl = new FadingMagnetControl( model );
        MonitorPanel monitorPanel = new MonitorPanel( model );
        monitorPanel.setPreferredSize( new Dimension( 200, 200 ) );

        addControl( new FadingMagnetControl( model ) );
        addControl( monitorPanel );

        // These lines should be in a class that wraps around the PhetPCanvas in BFieldArrowGraphic
        {
            final BFieldArrowGraphic fieldGraphic = new BFieldArrowGraphic( (GradientElectromagnet)model.getLowerMagnet() );
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
