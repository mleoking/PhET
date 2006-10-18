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
import edu.colorado.phet.molecularreactions.util.Resetable;
import edu.colorado.phet.molecularreactions.controller.SelectMoleculeAction;
import edu.colorado.phet.common.view.ModelSlider;
import edu.colorado.phet.common.view.util.SimStrings;

import javax.swing.*;
import javax.swing.event.ChangeListener;
import javax.swing.event.ChangeEvent;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

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
        JButton resetBtn = new JButton( SimStrings.get( "Control.reset") );
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
        private JCheckBox showBondsCB;
        private JCheckBox showStripChartCB;
        private JCheckBox nearestNeighborCB;
        private ComplexModule module;
        private JCheckBox showBarChartCB;

        public OptionsPanel( final ComplexModule module ) {
            this.module = module;

            showBondsCB = new JCheckBox( SimStrings.get("Control.showBonds") );
            showBondsCB.addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    module.setGraphicTypeVisible( showBondsCB.isSelected() );
                }
            } );
            showBondsCB.setSelected( true );

            showStripChartCB = new JCheckBox( SimStrings.get("Control.showStripChart"));
            showStripChartCB.addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    module.setStripChartVisible( showStripChartCB.isSelected() );
                }
            } );

            showBarChartCB = new JCheckBox( SimStrings.get( "Control.showBarChart"));
            showBarChartCB.addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    module.setBarChartVisible( showBarChartCB.isSelected() );
                }
            } );

            nearestNeighborCB = new JCheckBox( SimStrings.get( "Control.nearestNeighbor" ) );

            setBorder( ControlBorderFactory.createPrimaryBorder( SimStrings.get( "Control.options" ) ) );
            setLayout( new GridBagLayout() );
            Insets insets = new Insets( 0, 10, 0, 5 );
            GridBagConstraints gbc = new GridBagConstraints( 0, GridBagConstraints.RELATIVE,
                                                             1, 1, 1, 1,
                                                             GridBagConstraints.WEST,
                                                             GridBagConstraints.NONE,
                                                             insets, 0, 0 );
            add( showStripChartCB, gbc );
            add( showBarChartCB, gbc );
            add( showBondsCB, gbc );
            add( nearestNeighborCB, gbc );
        }

        public void reset() {
            showStripChartCB.setSelected( false );
            showBondsCB.setSelected( true  );
            nearestNeighborCB.setSelected( false );

            module.setStripChartVisible( showStripChartCB.isSelected() );
            module.setBarChartVisible( showBarChartCB.isSelected() );
            module.setGraphicTypeVisible( showBondsCB.isSelected() );
        }
    }

}
