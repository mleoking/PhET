/* Copyright 2004, Sam Reid */
package edu.colorado.phet.ec3.controls;

import edu.colorado.phet.common.view.VerticalLayoutPanel;
import edu.colorado.phet.ec3.EC3Module;
import edu.colorado.phet.ec3.EnergyPanel;

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
    private EC3Module module;
    private EnergyPanel energyPanel;
    private JCheckBox mechOnly;

    public PieChartControlPanel( final EC3Module module, EnergyPanel energyPanel ) {
        this.module = module;
        this.energyPanel = energyPanel;
        setBorder( BorderFactory.createTitledBorder( "Energy Pie Chart" ) );
        final JCheckBox pieChart = new JCheckBox( "Show", module.isPieChartVisible() );
        pieChart.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                mechOnly.setEnabled( pieChart.isSelected() );
                module.setPieChartVisible( pieChart.isSelected() );
            }
        } );
        add( pieChart );

        mechOnly = new JCheckBox( "Ignore Thermal", module.getEnergyConservationCanvas().getRootNode().getIgnoreThermal() );
        mechOnly.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                module.getEnergyConservationCanvas().getRootNode().setIgnoreThermal( mechOnly.isSelected() );
            }
        } );
        add( mechOnly );
        mechOnly.setEnabled( pieChart.isSelected() );
    }
}
