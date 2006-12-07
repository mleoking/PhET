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
    private JToggleButton showStripChartBtn;
    private JToggleButton showPieChartBtn;
    private JToggleButton showBarChartBtn;
    private JToggleButton showNoneBtn;

    /**
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

            showBarChartBtn = new JRadioButton( SimStrings.get( "Control.showBarChart" ) );
            showBarChartBtn.addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    setEnergyViewChartOptions();
                }
            } );
            chartOptionsBG.add( showBarChartBtn );

            showPieChartBtn = new JRadioButton( SimStrings.get( "Control.showPieChart" ) );
            showPieChartBtn.addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    setEnergyViewChartOptions();
                }
            } );
            chartOptionsBG.add( showPieChartBtn );

            showStripChartBtn = new JRadioButton( SimStrings.get( "Control.showStripChart" ) );
            showStripChartBtn.addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    setEnergyViewChartOptions();
                }
            } );
            chartOptionsBG.add( showStripChartBtn );

            showNoneBtn = new JRadioButton( SimStrings.get( "Control.none" ) );
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

        setBorder( ControlBorderFactory.createPrimaryBorder( SimStrings.get( "Control.options" ) ) );
        setLayout( new GridBagLayout() );
        Insets insets = new Insets( 0, 0, 0, 0 );
        GridBagConstraints gbc = new GridBagConstraints( 0, GridBagConstraints.RELATIVE,
                                                         1, 1, 1, 1,
                                                         GridBagConstraints.CENTER,
                                                         GridBagConstraints.HORIZONTAL,
                                                         insets, 0, 0 );
        add( chartOptionsPanel, gbc );
        gbc.fill = GridBagConstraints.NONE;
//        gbc.insets = new Insets( 0, 10, 0, 5 );
        add( showBondsBtn, gbc );
    }

    /**
     * Shows/hides charts in the EnergyView based on the state of the radio buttons
     */
    private void setEnergyViewChartOptions() {
        module.setBarChartVisible( showBarChartBtn.isSelected() );
        module.setPieChartVisible( showPieChartBtn.isSelected() );
        module.setStripChartVisible( showStripChartBtn.isSelected() );
        if( showStripChartBtn.isSelected() ) {
            module.setStripChartRecording( true );
        }
        if( showNoneBtn.isSelected() && module.getMRModel().getMoleculeBeingTracked() != null ) {
            module.getMRModel().getMoleculeBeingTracked().setSelectionStatus( Selectable.NOT_SELECTED );
        }
    }

    public void reset() {
        showStripChartBtn.setSelected( false );
        showNoneBtn.setSelected( true );
        showBondsBtn.setSelected( false );

        setEnergyViewChartOptions();
    }

    public void setDefaultSelection( Option stripChartOption ) {
        showBarChartBtn.setSelected( stripChartOption == BAR_CHART_OPTION );
        showPieChartBtn.setSelected( stripChartOption == PIE_CHART_OPTION );
        showStripChartBtn.setSelected( stripChartOption == STRIP_CHART_OPTION );
        showNoneBtn.setSelected( stripChartOption == NO_CHART_OPTION );
        setEnergyViewChartOptions();
    }

}

