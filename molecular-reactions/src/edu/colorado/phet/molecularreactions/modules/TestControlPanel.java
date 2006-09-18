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
import edu.colorado.phet.molecularreactions.controller.SelectMoleculeAction;
import edu.colorado.phet.molecularreactions.controller.ResetAllAction;
import edu.colorado.phet.molecularreactions.view.Legend;
import edu.colorado.phet.molecularreactions.view.MoleculeInstanceControlPanel;
import edu.colorado.phet.molecularreactions.model.MRModel;
import edu.colorado.phet.molecularreactions.model.EnergyProfile;

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
        GridBagConstraints gbc = new GridBagConstraints( 0, GridBagConstraints.RELATIVE,
                                                         1,1,1,1,
                                                         GridBagConstraints.NORTH,
                                                         GridBagConstraints.NONE,
                                                         new Insets( 0,0,0,0),0,0 );

        final ModelSlider thresholdEnergySlider = new ModelSlider( "Threshold energy",
                                                             "",
                                                             0,
                                                             MRConfig.MAX_REACTION_THRESHOLD,
                                                             model.getEnergyProfile().getPeakLevel() );
        thresholdEnergySlider.setNumMajorTicks( 0 );
        thresholdEnergySlider.setNumMinorTicks( 0 );
        thresholdEnergySlider.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                model.getEnergyProfile().setPeakLevel(thresholdEnergySlider.getValue());
            }
        } );
        model.getEnergyProfile().setPeakLevel(thresholdEnergySlider.getValue());
        model.getEnergyProfile().addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                thresholdEnergySlider.setValue( (( EnergyProfile )e.getSource()).getPeakLevel() );
            }
        } );

        // Button to pause and select a molecule
        JButton selectMoleculeBtn = new JButton( "<html><center>Select molecule<br>to track");
        selectMoleculeBtn.addActionListener( new SelectMoleculeAction( module.getClock(), model ) );

        // Reset button
        JButton resetBtn = new JButton( "Reset All");
        resetBtn.addActionListener( new ResetAllAction( model ) );


        add( thresholdEnergySlider, gbc );
        add( selectMoleculeBtn, gbc );
        add( new MoleculeInstanceControlPanel( model ), gbc );
    }
}
