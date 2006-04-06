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
import edu.colorado.phet.common.view.ModelSlider;
import edu.colorado.phet.mri.model.MriModel;
import edu.colorado.phet.mri.model.Electromagnet;
import edu.colorado.phet.mri.MriConfig;

import javax.swing.event.ChangeListener;
import javax.swing.event.ChangeEvent;

/**
 * MriControlPanel
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class MriControlPanel extends ControlPanel {
    private ModelSlider fadingMagnetsSlider;

    public MriControlPanel( MriModuleA module ) {
        final MriModel model = (MriModel)module.getModel();

        fadingMagnetsSlider = new ModelSlider( "Fading Coil Current", "", 0, MriConfig.MAX_FADING_COIL_CURRENT, 0 );
        fadingMagnetsSlider.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                Electromagnet upperMagnet = model.getUpperMagnet();
                upperMagnet.setCurrent( fadingMagnetsSlider.getValue() );
                Electromagnet lowerMagnet = model.getLowerMagnet();
                lowerMagnet.setCurrent( fadingMagnetsSlider.getValue() );
            }
        } );

        layoutPanel();
    }

    private void layoutPanel() {
        addControl( fadingMagnetsSlider );
    }
}
