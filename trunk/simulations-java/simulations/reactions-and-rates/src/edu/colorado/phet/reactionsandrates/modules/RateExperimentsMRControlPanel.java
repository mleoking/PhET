/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.reactionsandrates.modules;

import edu.colorado.phet.common.phetcommon.model.Resettable;
import edu.colorado.phet.common.phetcommon.view.ResetAllButton;
import edu.colorado.phet.reactionsandrates.MRConfig;
import edu.colorado.phet.reactionsandrates.model.MRModel;
import edu.colorado.phet.reactionsandrates.view.ExperimentSetupPanel;
import edu.colorado.phet.reactionsandrates.view.MoleculeInstanceControlPanel;
import edu.colorado.phet.reactionsandrates.view.charts.ChartOptionsPanel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * ComplexMRControlPanel
 * <p/>
 * The control panel for the ComplexModule
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class RateExperimentsMRControlPanel extends MRControlPanel {
    private MoleculeInstanceControlPanel moleculeInstanceControlPanel;
    private ChartOptionsPanel optionsPanel;
    public ExperimentSetupPanel experimentSetupPanel;


    public RateExperimentsMRControlPanel( final RateExperimentsModule module ) {
        super( new GridBagLayout() );

        final MRModel model = (MRModel)module.getModel();
        GridBagConstraints gbc = new GridBagConstraints( 0, GridBagConstraints.RELATIVE,
                                                         1, 1, 1, 1,
                                                         GridBagConstraints.NORTH,
                                                         GridBagConstraints.NONE,
                                                         new Insets( 5, 0, 0, 0 ), 0, 0 );

        // Controls for setting up and running experiments
        experimentSetupPanel = new ExperimentSetupPanel( module );

        // Controls for adding and removing molecules
        moleculeInstanceControlPanel = new MoleculeInstanceControlPanel( model, module.getClock() );
        moleculeInstanceControlPanel.setClearContainerButtonVisible( false );

        // Options
        optionsPanel = new ChartOptionsPanel( module );

        ResetAllButton resetAllBtn = new ResetAllButton( this );
        resetAllBtn.addResettable( new Resettable() {
            public void reset() {
                module.reset();
            }
        });

        // Lay out the controls
        gbc.fill = GridBagConstraints.HORIZONTAL;
        add( experimentSetupPanel, gbc );
        add( moleculeInstanceControlPanel, gbc );
        add( optionsPanel, gbc );

        gbc.fill = GridBagConstraints.NONE;
        add( resetAllBtn, gbc );

        reset();
    }

    public MoleculeInstanceControlPanel getMoleculeInstanceControlPanel() {
        return moleculeInstanceControlPanel;
    }

    public void reset() {
        experimentSetupPanel.reset();
        optionsPanel.reset();
        setExperimentRunning( false );
    }
    
    public void clearExperiment() {
        experimentSetupPanel.endExperiment();
    }

    public void setExperimentRunning( boolean running ) {
        getMoleculeInstanceControlPanel().setCountersEditable( running );
    }

    public boolean isTemperatureBeingAdjusted() {
        boolean adjusting = super.isTemperatureBeingAdjusted();

        if( !adjusting ) {
            adjusting = experimentSetupPanel.isTemperatureBeingAdjusted();
        }

        return adjusting;
    }
}
