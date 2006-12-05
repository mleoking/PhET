/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.molecularreactions.view.charts;

import edu.colorado.phet.molecularreactions.util.Resetable;
import edu.colorado.phet.molecularreactions.util.DialogCheckBox;
import edu.colorado.phet.molecularreactions.util.ControlBorderFactory;
import edu.colorado.phet.molecularreactions.modules.ComplexModule;
import edu.colorado.phet.molecularreactions.model.*;
import edu.colorado.phet.common.view.util.SimStrings;

import javax.swing.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.*;

/**
 * ChartOptionsPanel
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class ChartOptionsPanel extends JPanel implements Resetable {
    private ComplexModule module;
    private JToggleButton showBondsBtn;
    private DialogCheckBox showStripChartBtn;
    private JToggleButton showPieChartBtn;
    private JToggleButton showBarChartBtn;
    private JToggleButton trackMoleculeBtn;
    private JToggleButton showNoneBtn;
    private JToggleButton designReactionBtn;

    /**
     *
     * @param module
     */
    public ChartOptionsPanel( final ComplexModule module ) {
        this.module = module;

        showBondsBtn = new JCheckBox( SimStrings.get( "Control.showBonds" ) );
        showBondsBtn.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                module.setGraphicTypeVisible( showBondsBtn.isSelected() );
            }
        } );
        showBondsBtn.setSelected( false );
        module.setGraphicTypeVisible( showBondsBtn.isSelected() );

        showStripChartBtn = new DialogCheckBox( SimStrings.get( "Control.showStripChart" ) );
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
            chartOptionsPanel.setBorder( ControlBorderFactory.createSecondaryBorder( SimStrings.get( "Control.chartOptions" ) ) );
            GridBagConstraints gbc = new GridBagConstraints( 0, GridBagConstraints.RELATIVE,
                                                             1, 1, 1, 1,
                                                             GridBagConstraints.WEST,
                                                             GridBagConstraints.NONE,
                                                             new Insets( 0, 0, 0, 0 ), 0, 0 );
            final ButtonGroup chartOptionsBG = new ButtonGroup();
            trackMoleculeBtn = new JRadioButton( SimStrings.get( "Control.trackMolecule" ) );
            trackMoleculeBtn.addActionListener( new ActionListener() {
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

            gbc.gridy = 0;
            chartOptionsPanel.add( showBarChartBtn, gbc );
            gbc.gridx = 1;
            chartOptionsPanel.add( showNoneBtn, gbc );
            gbc.gridx = 0;
            gbc.gridy = 1;
            chartOptionsPanel.add( showPieChartBtn, gbc );
        }

        //--------------------------------------------------------------------------------------------------
        // The check box that enables the manipulation of the energy profile with the mouse
        //--------------------------------------------------------------------------------------------------

        designReactionBtn = new JCheckBox( SimStrings.get( "Control.designReaction"));
        designReactionBtn.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                module.getEnergyView().setProfileManipulable( designReactionBtn.isSelected() );
            }
        } );

        //--------------------------------------------------------------------------------------------------
        // Lay out the panel
        //--------------------------------------------------------------------------------------------------

        setBorder( ControlBorderFactory.createPrimaryBorder( SimStrings.get( "Control.options" ) ) );
        setLayout( new GridBagLayout() );
        Insets insets = new Insets( 0, 0, 0, 0 );
        GridBagConstraints gbc = new GridBagConstraints( 0, GridBagConstraints.RELATIVE,
                                                         1, 1, 1, 1,
                                                         GridBagConstraints.WEST,
                                                         GridBagConstraints.HORIZONTAL,
                                                         insets, 0, 0 );
        add( chartOptionsPanel, gbc );
        gbc.insets = new Insets( 0, 10, 0, 5 );
        add( showStripChartBtn, gbc );
        add( showBondsBtn, gbc );
    }

    /**
     * Shows/hides charts in the EnergyView based on the state of the radio buttons
     *
     * @param module
     */
    private void setEnergyViewChartOptions( ComplexModule module ) {
        module.setBarChartVisible( showBarChartBtn.isSelected() );
        module.setPieChartVisible( showPieChartBtn.isSelected() );
        module.getEnergyView().hideSelectedMolecule( !trackMoleculeBtn.isSelected() );
        if( showNoneBtn.isSelected() && module.getMRModel().getMoleculeBeingTracked() != null ) {
            module.getMRModel().getMoleculeBeingTracked().setSelectionStatus( Selectable.NOT_SELECTED );
        }
    }

    public void reset() {
        showStripChartBtn.setSelected( false );
        showNoneBtn.setSelected( true );
        showBondsBtn.setSelected( false );

        module.setStripChartVisible( showStripChartBtn.isSelected(), showStripChartBtn );
        module.setBarChartVisible( showBarChartBtn.isSelected() );
        module.setPieChartVisible( showPieChartBtn.isSelected() );
        module.setGraphicTypeVisible( showBondsBtn.isSelected() );
    }
}

