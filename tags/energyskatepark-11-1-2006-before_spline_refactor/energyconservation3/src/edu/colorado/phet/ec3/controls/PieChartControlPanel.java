/* Copyright 2004, Sam Reid */
package edu.colorado.phet.ec3.controls;

import edu.colorado.phet.common.view.VerticalLayoutPanel;
import edu.colorado.phet.ec3.EC3ControlPanel;
import edu.colorado.phet.ec3.EnergySkateParkModule;
import edu.colorado.phet.ec3.EnergySkateParkStrings;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * User: Sam Reid
 * Date: Oct 31, 2005
 * Time: 12:56:48 AM
 * Copyright (c) Oct 31, 2005 by Sam Reid
 */

public class PieChartControlPanel extends VerticalLayoutPanel {
    private EnergySkateParkModule module;
    private EC3ControlPanel EC3ControlPanel;
    private JCheckBox mechOnly;

    public PieChartControlPanel( final EnergySkateParkModule module, EC3ControlPanel EC3ControlPanel ) {
        this.module = module;
        this.EC3ControlPanel = EC3ControlPanel;
        setBorder( BorderFactory.createTitledBorder( EnergySkateParkStrings.getString( "energy.pie.chart" ) ) );
        final JCheckBox pieChart = new JCheckBox( EnergySkateParkStrings.getString( "show" ), module.isPieChartVisible() );
        pieChart.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                mechOnly.setEnabled( pieChart.isSelected() );
                module.setPieChartVisible( pieChart.isSelected() );
            }
        } );
        add( pieChart );

        mechOnly = new JCheckBox( EnergySkateParkStrings.getString( "ignore.thermal" ), module.getEnergyConservationCanvas().getRootNode().getIgnoreThermal() );
        mechOnly.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                module.getEnergyConservationCanvas().getRootNode().setIgnoreThermal( mechOnly.isSelected() );
            }
        } );
        add( mechOnly );
        mechOnly.setEnabled( pieChart.isSelected() );
    }
}
