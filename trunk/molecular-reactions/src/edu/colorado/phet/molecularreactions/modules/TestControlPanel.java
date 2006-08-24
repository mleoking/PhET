/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.molecularreactions.modules;

import edu.colorado.phet.common.view.ModelSlider;
import edu.colorado.phet.molecularreactions.MRConfig;
import edu.colorado.phet.molecularreactions.model.MRModel;

import javax.swing.*;
import javax.swing.event.ChangeListener;
import javax.swing.event.ChangeEvent;
import java.awt.*;

/**
 * TestControlPanel
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class TestControlPanel extends JPanel {

    public TestControlPanel( MRModule module ) {
        super( new GridBagLayout() );

        final MRModel model = (MRModel)module.getModel();
        GridBagConstraints gbc = new GridBagConstraints( 0,0,1,1,1,1,
                                                         GridBagConstraints.NORTH,
                                                         GridBagConstraints.NONE,
                                                         new Insets( 0,0,0,0),0,0 );

        final ModelSlider thresholdEnergySlider = new ModelSlider( "Threshold energy",
                                                             "",
                                                             0,
                                                             MRConfig.REACTION_THRESHOLD * 2,
                                                             model.getEnergyProfile().getPeakLevel() );
        thresholdEnergySlider.setNumMajorTicks( 0 );
        thresholdEnergySlider.setNumMinorTicks( 0 );
        thresholdEnergySlider.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                model.getEnergyProfile().setPeakLevel(thresholdEnergySlider.getValue());
            }
        } );
        model.getEnergyProfile().setPeakLevel(thresholdEnergySlider.getValue());

        add( thresholdEnergySlider, gbc );
    }
}
