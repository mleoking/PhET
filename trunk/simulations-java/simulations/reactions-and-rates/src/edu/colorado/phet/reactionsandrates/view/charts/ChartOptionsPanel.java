/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.reactionsandrates.view.charts;

import edu.colorado.phet.reactionsandrates.MRConfig;
import edu.colorado.phet.reactionsandrates.model.Selectable;
import edu.colorado.phet.reactionsandrates.modules.ComplexModule;
import edu.colorado.phet.reactionsandrates.util.ControlBorderFactory;
import edu.colorado.phet.reactionsandrates.util.Resetable;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * ChartOptionsPanel
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class ChartOptionsPanel extends JPanel implements Resetable {

    public static class Option {
        private Option() {
        }
    }

    public static final Option BAR_CHART_OPTION = new Option();
    public static final Option PIE_CHART_OPTION = new Option();
    public static final Option STRIP_CHART_OPTION = new Option();
    public static final Option NO_CHART_OPTION = new Option();


    private ComplexModule module;
    private JToggleButton showBondsBtn;
    private JToggleButton showStopwatchBtn;
    private JToggleButton showStripChartBtn;
    private JToggleButton showPieChartBtn;
    private JToggleButton showBarChartBtn;
    private JToggleButton showNoneBtn;

    /**
     * @param module
     */
    public ChartOptionsPanel( final ComplexModule module ) {
        this.module = module;

        showBondsBtn = new JCheckBox( MRConfig.RESOURCES.getLocalizedString( "Control.showBonds" ) );
        showBondsBtn.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                module.setGraphicTypeVisible( showBondsBtn.isSelected() );
            }
        } );
        showBondsBtn.setSelected( false );
        module.setGraphicTypeVisible( showBondsBtn.isSelected() );

        showStopwatchBtn = new JCheckBox( MRConfig.RESOURCES.getLocalizedString( "Control.showStopwatch" ) );
        showStopwatchBtn.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                module.setStopwatchVisible( showStopwatchBtn.isSelected() );
            }
        } );
        showBondsBtn.setSelected( false );
        module.setStopwatchVisible( showStopwatchBtn.isSelected() );

        //--------------------------------------------------------------------------------------------------
        // The buttons that put things on the top pane of the energy view
        //--------------------------------------------------------------------------------------------------

        JPanel chartOptionsPanel = new JPanel( new GridBagLayout() );
        {
            chartOptionsPanel.setBorder( ControlBorderFactory.createSecondaryBorder( MRConfig.RESOURCES.getLocalizedString( "Control.chartOptions" ) ) );
            GridBagConstraints gbc = new GridBagConstraints( 0, GridBagConstraints.RELATIVE,
                                                             1, 1, 1, 1,
                                                             GridBagConstraints.WEST,
                                                             GridBagConstraints.NONE,
                                                             new Insets( 0, 0, 0, 0 ), 0, 0 );
            final ButtonGroup chartOptionsBG = new ButtonGroup();

            showBarChartBtn = new JRadioButton( MRConfig.RESOURCES.getLocalizedString( "Control.showBarChart" ) );
            showBarChartBtn.addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    setEnergyViewChartOptions();
                }
            } );
            chartOptionsBG.add( showBarChartBtn );

            showPieChartBtn = new JRadioButton( MRConfig.RESOURCES.getLocalizedString( "Control.showPieChart" ) );
            showPieChartBtn.addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    setEnergyViewChartOptions();
                }
            } );
            chartOptionsBG.add( showPieChartBtn );

            showStripChartBtn = new JRadioButton( MRConfig.RESOURCES.getLocalizedString( "Control.showStripChart" ) );
            showStripChartBtn.addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    setEnergyViewChartOptions();
                }
            } );
            chartOptionsBG.add( showStripChartBtn );

            showNoneBtn = new JRadioButton( MRConfig.RESOURCES.getLocalizedString( "Control.none" ) );
            showNoneBtn.addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    setEnergyViewChartOptions();
                }
            } );
            showNoneBtn.setSelected( true );
            chartOptionsBG.add( showNoneBtn );

            gbc.gridy = 0;
            chartOptionsPanel.add( showBarChartBtn, gbc );
            gbc.gridx = 1;
            chartOptionsPanel.add( showStripChartBtn, gbc );
            gbc.gridx = 0;
            gbc.gridy = 1;
            chartOptionsPanel.add( showPieChartBtn, gbc );
            gbc.gridx = 1;
            chartOptionsPanel.add( showNoneBtn, gbc );
        }

        //--------------------------------------------------------------------------------------------------
        // Lay out the panel
        //--------------------------------------------------------------------------------------------------

        setBorder( ControlBorderFactory.createPrimaryBorder( MRConfig.RESOURCES.getLocalizedString( "Control.options" ) ) );
        setLayout( new GridBagLayout() );
        Insets insets = new Insets( 0, 0, 0, 0 );
        GridBagConstraints gbc = new GridBagConstraints( 0, GridBagConstraints.RELATIVE,
                                                         1, 1, 1, 1,
                                                         GridBagConstraints.CENTER,
                                                         GridBagConstraints.HORIZONTAL,
                                                         insets, 0, 0 );
        add( chartOptionsPanel, gbc );
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.WEST;
        add( showBondsBtn, gbc );
        add( showStopwatchBtn, gbc );
    }

    /**
     * Shows/hides charts in the EnergyView based on the state of the radio buttons
     */
    private void setEnergyViewChartOptions() {
        module.setBarChartVisible( showBarChartBtn.isSelected() );
        module.setPieChartVisible( showPieChartBtn.isSelected() );
        module.setStripChartVisible( showStripChartBtn.isSelected() );
        if( showStripChartBtn.isSelected() ) {
//            module.setStripChartRecording( true );
        }
        if( showNoneBtn.isSelected() && module.getMRModel().getMoleculeBeingTracked() != null ) {
            module.getMRModel().getMoleculeBeingTracked().setSelectionStatus( Selectable.NOT_SELECTED );
        }
        module.setStopwatchVisible( showStopwatchBtn.isSelected() );
    }

    public void reset() {
        showStripChartBtn.setSelected( false );
        showNoneBtn.setSelected( true );
        showBondsBtn.setSelected( false );
        showStopwatchBtn.setSelected( false );
        setEnergyViewChartOptions();
    }

    public void setDefaultSelection( Option defaultChartOption ) {
        showBarChartBtn.setSelected( defaultChartOption == BAR_CHART_OPTION );
        showPieChartBtn.setSelected( defaultChartOption == PIE_CHART_OPTION );
        showStripChartBtn.setSelected( defaultChartOption == STRIP_CHART_OPTION );
        showNoneBtn.setSelected( defaultChartOption == NO_CHART_OPTION );
        setEnergyViewChartOptions();
    }
}

