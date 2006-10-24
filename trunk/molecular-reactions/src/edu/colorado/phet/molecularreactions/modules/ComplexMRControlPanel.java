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

import edu.colorado.phet.common.view.util.SimStrings;
import edu.colorado.phet.molecularreactions.controller.SelectMoleculeAction;
import edu.colorado.phet.molecularreactions.model.MRModel;
import edu.colorado.phet.molecularreactions.util.ControlBorderFactory;
import edu.colorado.phet.molecularreactions.util.Resetable;
import edu.colorado.phet.molecularreactions.util.DialogCheckBox;
import edu.colorado.phet.molecularreactions.view.MoleculeInstanceControlPanel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

/**
 * ComplexMRControlPanel
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class ComplexMRControlPanel extends MRControlPanel {
    private MoleculeInstanceControlPanel moleculeInstanceControlPanel;
    private OptionsPanel optionsPanel;

    public ComplexMRControlPanel( final ComplexModule module ) {
        super( new GridBagLayout() );

        final MRModel model = (MRModel)module.getModel();
        GridBagConstraints gbc = new GridBagConstraints( 0, GridBagConstraints.RELATIVE,
                                                         1, 1, 1, 1,
                                                         GridBagConstraints.NORTH,
                                                         GridBagConstraints.NONE,
                                                         new Insets( 10, 0, 0, 0 ), 0, 0 );

        // Legend
//        Legend legend = new Legend();

        // Button to pause and select a molecule
        JButton selectMoleculeBtn = new JButton( "<html><center>Select molecule<br>to track" );
        selectMoleculeBtn.addActionListener( new SelectMoleculeAction( module.getClock(), model ) );

        // Controls for adding and removing molecules
        moleculeInstanceControlPanel = new MoleculeInstanceControlPanel( model );

        // Options
        optionsPanel = new OptionsPanel( module );

        // Reset button
        JButton resetBtn = new JButton( SimStrings.get( "Control.reset" ) );
        resetBtn.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                module.reset();
            }
        } );

        // Lay out the controls
        add( selectMoleculeBtn, gbc );
        gbc.fill = GridBagConstraints.HORIZONTAL;
        add( moleculeInstanceControlPanel, gbc );
        add( optionsPanel, gbc );
        gbc.fill = GridBagConstraints.NONE;
        add( resetBtn, gbc );
    }

    public MoleculeInstanceControlPanel getMoleculeInstanceControlPanel() {
        return moleculeInstanceControlPanel;
    }

    public void reset() {
        optionsPanel.reset();
    }

    //--------------------------------------------------------------------------------------------------
    // Inner classes
    //--------------------------------------------------------------------------------------------------

    private class OptionsPanel extends JPanel implements Resetable {
        private ComplexModule module;
        private JToggleButton showBondsBtn;
        private DialogCheckBox showStripChartBtn;
        private JToggleButton showPieChartBtn;
        private JToggleButton nearestNeighborBtn;
        private JToggleButton showBarChartBtn;
        private JToggleButton trackMoleculeBtn;
        private JToggleButton showNoneBtn;

        public OptionsPanel( final ComplexModule module ) {
            this.module = module;

            showBondsBtn = new JCheckBox( SimStrings.get( "Control.showBonds" ) );
            showBondsBtn.addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    module.setGraphicTypeVisible( showBondsBtn.isSelected() );
                }
            } );
            showBondsBtn.setSelected( true );

            showStripChartBtn = new DialogCheckBox( SimStrings.get( "Control.showStripChart" ) );
//            showStripChartBtn = new JCheckBox( SimStrings.get( "Control.showStripChart" ) );
            showStripChartBtn.addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    module.setStripChartVisible( showStripChartBtn.isSelected(), showStripChartBtn );
                }
            } );

            //--------------------------------------------------------------------------------------------------
            // The buttons that put things on the top pane of the energy view
            //--------------------------------------------------------------------------------------------------

            JPanel chartOptionsPanel = new JPanel( new GridBagLayout() );
            {
                chartOptionsPanel.setBorder( ControlBorderFactory.createSecondaryBorder( SimStrings.get("Control.chartOptions")));
                GridBagConstraints gbc = new GridBagConstraints( 0, GridBagConstraints.RELATIVE,
                                                                 1, 1, 1, 1,
                                                                 GridBagConstraints.WEST,
                                                                 GridBagConstraints.NONE,
                                                                 new Insets( 0, 0, 0, 0 ), 0, 0 );
                final ButtonGroup chartOptionsBG = new ButtonGroup();
                trackMoleculeBtn = new JRadioButton( SimStrings.get( "Control.trackMolecule" ) );
                trackMoleculeBtn .addActionListener( new ActionListener() {
                    public void actionPerformed( ActionEvent e ) {
                        setEnergyViewChartOptions( module );
                    }
                } );
                chartOptionsBG.add( trackMoleculeBtn );

                showBarChartBtn = new JRadioButton( SimStrings.get( "Control.showBarChart" ) );
                showBarChartBtn.addActionListener( new ActionListener() {
                    public void actionPerformed( ActionEvent e ) {
                        setEnergyViewChartOptions( module );
                    }
                } );
                chartOptionsBG.add( showBarChartBtn );

                showPieChartBtn = new JRadioButton( SimStrings.get( "Control.showPieChart" ) );
                showPieChartBtn.addActionListener( new ActionListener() {
                    public void actionPerformed( ActionEvent e ) {
                        setEnergyViewChartOptions( module );
                    }
                } );
                chartOptionsBG.add( showPieChartBtn );

                showNoneBtn = new JRadioButton( SimStrings.get( "Control.none" ) );
                showNoneBtn.addActionListener( new ActionListener() {
                    public void actionPerformed( ActionEvent e ) {
                        setEnergyViewChartOptions( module );
                    }
                } );
                showNoneBtn.setSelected( true );
                chartOptionsBG.add( showNoneBtn );

                chartOptionsPanel.add( trackMoleculeBtn, gbc );
                chartOptionsPanel.add( showBarChartBtn, gbc );
                chartOptionsPanel.add( showPieChartBtn, gbc );
                chartOptionsPanel.add( showNoneBtn, gbc );
            }

            nearestNeighborBtn = new JCheckBox( SimStrings.get( "Control.nearestNeighbor" ) );

            setBorder( ControlBorderFactory.createPrimaryBorder( SimStrings.get( "Control.options" ) ) );
            setLayout( new GridBagLayout() );
            Insets insets = new Insets( 0, 10, 0, 5 );
            GridBagConstraints gbc = new GridBagConstraints( 0, GridBagConstraints.RELATIVE,
                                                             1, 1, 1, 1,
                                                             GridBagConstraints.WEST,
                                                             GridBagConstraints.NONE,
                                                             insets, 0, 0 );
//            add( trackMoleculeBtn, gbc );
//            add( showBarChartBtn, gbc );
//            add( showPieChartBtn, gbc );
//            add( showNoneBtn, gbc );
            add( chartOptionsPanel, gbc );
            add( showStripChartBtn, gbc );
            add( showBondsBtn, gbc );
//            add( nearestNeighborBtn, gbc );
        }

        /**
         * Shows/hides charts in the EnergyView based on the state of the radio buttons
         *
         * @param module
         */
        private void setEnergyViewChartOptions( ComplexModule module ) {
            module.setBarChartVisible( showBarChartBtn.isSelected() );
            module.setPieChartVisible( showPieChartBtn.isSelected() );
        }

        public void reset() {
            showStripChartBtn.setSelected( false );
            showBondsBtn.setSelected( true );
            nearestNeighborBtn.setSelected( false );

            module.setStripChartVisible( showStripChartBtn.isSelected(), showStripChartBtn );
            module.setBarChartVisible( showBarChartBtn.isSelected() );
            module.setGraphicTypeVisible( showBondsBtn.isSelected() );
        }

    }
}
