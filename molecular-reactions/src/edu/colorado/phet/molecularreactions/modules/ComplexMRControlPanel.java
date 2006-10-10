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
import edu.colorado.phet.molecularreactions.model.MRModel;
import edu.colorado.phet.molecularreactions.model.EnergyProfile;
import edu.colorado.phet.molecularreactions.MRConfig;
import edu.colorado.phet.molecularreactions.util.ControlBorderFactory;
import edu.colorado.phet.molecularreactions.controller.SelectMoleculeAction;
import edu.colorado.phet.molecularreactions.controller.ResetAllAction;
import edu.colorado.phet.common.view.ModelSlider;
import edu.colorado.phet.common.view.ControlPanel;
import edu.colorado.phet.common.view.util.SimStrings;

import javax.swing.*;
import javax.swing.event.ChangeListener;
import javax.swing.event.ChangeEvent;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

/**
 * TestControlPanel
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
//public class ComplexMRControlPanel extends ControlPanel {
public class ComplexMRControlPanel extends JPanel {
    private MoleculeInstanceControlPanel moleculeInstanceControlPanel;

    public ComplexMRControlPanel( ComplexModule module ) {
        super( new GridBagLayout() );

        final MRModel model = (MRModel)module.getModel();
        GridBagConstraints gbc = new GridBagConstraints( 0, GridBagConstraints.RELATIVE,
                                                         1, 1, 1, 1,
                                                         GridBagConstraints.NORTH,
                                                         GridBagConstraints.NONE,
                                                         new Insets( 0, 0, 0, 0 ), 0, 0 );

//        final ModelSlider thresholdEnergySlider = createThresholdEnergySlider( model );

        // Legend
//        Legend legend = new Legend();

        // Button to pause and select a molecule
        JButton selectMoleculeBtn = new JButton( "<html><center>Select molecule<br>to track" );
        selectMoleculeBtn.addActionListener( new SelectMoleculeAction( module.getClock(), model ) );

        // Controls for adding and removing molecules
        moleculeInstanceControlPanel = new MoleculeInstanceControlPanel( model );

        // Options
        JComponent options = new OptionsPanel( module );

        // Reset button
        JButton resetBtn = new JButton( "Reset All" );
        resetBtn.addActionListener( new ResetAllAction( model ) );

        // Lay out the controls
        add( selectMoleculeBtn, gbc );
//        add( thresholdEnergySlider, gbc );
        gbc.fill = GridBagConstraints.HORIZONTAL;
//        add( legend, gbc );
        add( moleculeInstanceControlPanel, gbc );
        add( options, gbc );
    }

    private ModelSlider createThresholdEnergySlider( final MRModel model ) {
        final ModelSlider thresholdEnergySlider = new ModelSlider( "Threshold energy",
                                                                   "",
                                                                   0,
                                                                   MRConfig.MAX_REACTION_THRESHOLD,
                                                                   model.getEnergyProfile().getPeakLevel() );
        thresholdEnergySlider.setNumMajorTicks( 0 );
        thresholdEnergySlider.setNumMinorTicks( 0 );
        thresholdEnergySlider.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                model.getEnergyProfile().setPeakLevel( thresholdEnergySlider.getValue() );
            }
        } );
        model.getEnergyProfile().setPeakLevel( thresholdEnergySlider.getValue() );
        model.getEnergyProfile().addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                thresholdEnergySlider.setValue( ( (EnergyProfile)e.getSource() ).getPeakLevel() );
            }
        } );
        return thresholdEnergySlider;
    }

    public MoleculeInstanceControlPanel getMoleculeInstanceControlPanel() {
        return moleculeInstanceControlPanel;
    }

    //--------------------------------------------------------------------------------------------------
    // Inner classes
    //--------------------------------------------------------------------------------------------------

    private class OptionsPanel extends JPanel {

        public OptionsPanel( final ComplexModule module ) {

            final JCheckBox showBondsCB = new JCheckBox( SimStrings.get("Control.showbonds") );
            showBondsCB.addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    module.setGraphicTypeVisible( showBondsCB.isSelected() );
                }
            } );
            showBondsCB.setSelected( true );

            final JCheckBox showStripChartCB = new JCheckBox( SimStrings.get("Control.showStripChart"));
            showStripChartCB.addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    module.setStripChartVisible( showStripChartCB.isSelected() );
                }
            } );

            final JCheckBox nearestNeighborCB = new JCheckBox( SimStrings.get( "Control.nearestNeighbor" ) );

            setBorder( ControlBorderFactory.createPrimaryBorder( SimStrings.get( "Control.options" ) ) );
            setLayout( new GridBagLayout() );
            Insets insets = new Insets( 0, 10, 0, 5 );
            GridBagConstraints gbc = new GridBagConstraints( 0, GridBagConstraints.RELATIVE,
                                                             1, 1, 1, 1,
                                                             GridBagConstraints.WEST,
                                                             GridBagConstraints.NONE,
                                                             insets, 0, 0 );
            add( showStripChartCB, gbc );
            add( showBondsCB, gbc );
            add( nearestNeighborCB, gbc );
        }
    }

}
