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

import edu.colorado.phet.molecularreactions.view.MoleculeInstanceControlPanel;
import edu.colorado.phet.molecularreactions.view.ExperimentSetupPanel;
import edu.colorado.phet.molecularreactions.view.charts.ChartOptionsPanel;
import edu.colorado.phet.molecularreactions.model.*;
import edu.colorado.phet.molecularreactions.model.reactions.Profiles;
import edu.colorado.phet.molecularreactions.controller.SelectMoleculeAction;
import edu.colorado.phet.molecularreactions.util.Resetable;
import edu.colorado.phet.molecularreactions.util.DialogCheckBox;
import edu.colorado.phet.molecularreactions.util.ControlBorderFactory;
import edu.colorado.phet.common.view.ControlPanel;
import edu.colorado.phet.common.view.util.SimStrings;
import edu.colorado.phet.common.model.ModelElement;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

/**
 * ComplexMRControlPanel
 * <p>
 * The control panel for the ComplexModule
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class RateExperimentsMRControlPanel extends MRControlPanel {
    private MoleculeInstanceControlPanel moleculeInstanceControlPanel;
    private ChartOptionsPanel optionsPanel;


    public RateExperimentsMRControlPanel( final RateExperimentsModule module ) {
        super( new GridBagLayout() );

        final MRModel model = (MRModel)module.getModel();
        GridBagConstraints gbc = new GridBagConstraints( 0, GridBagConstraints.RELATIVE,
                                                         1, 1, 1, 1,
                                                         GridBagConstraints.NORTH,
                                                         GridBagConstraints.NONE,
                                                         new Insets( 5, 0, 0, 0 ), 0, 0 );

        // Controls for setting up and running experiments
        ExperimentSetupPanel experimentSetupPanel = new ExperimentSetupPanel( module );

        // Controls for adding and removing molecules
        moleculeInstanceControlPanel = new MoleculeInstanceControlPanel( model );

        // Options
        optionsPanel = new ChartOptionsPanel( module );

        // Reset button
        JButton resetBtn = new JButton( SimStrings.get( "Control.reset" ) );
        resetBtn.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                module.reset();
            }
        } );

        // Lay out the controls
        gbc.fill = GridBagConstraints.HORIZONTAL;
        add( experimentSetupPanel, gbc );
        add( moleculeInstanceControlPanel, gbc );
        add( optionsPanel, gbc );

        gbc.fill = GridBagConstraints.NONE;
        add( resetBtn, gbc );

        reset();
    }

    public MoleculeInstanceControlPanel getMoleculeInstanceControlPanel() {
        return moleculeInstanceControlPanel;
    }

    public void reset() {
        optionsPanel.reset();
        optionsPanel.setDefaultSelection( ChartOptionsPanel.STRIP_CHART_OPTION );
        setExperimentRunning( false );
    }

    public void setExperimentRunning( boolean running ) {
        getMoleculeInstanceControlPanel().setCountersEditable( running );        
    }

}
