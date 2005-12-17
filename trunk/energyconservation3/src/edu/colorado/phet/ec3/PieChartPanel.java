/* Copyright 2004, Sam Reid */
package edu.colorado.phet.ec3;

import edu.colorado.phet.common.view.VerticalLayoutPanel;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * User: Sam Reid
 * Date: Oct 31, 2005
 * Time: 12:56:48 AM
 * Copyright (c) Oct 31, 2005 by Sam Reid
 */

public class PieChartPanel extends VerticalLayoutPanel {
    private EC3Module module;
    private EnergyPanel energyPanel;
    private JCheckBox mechOnly;

    public PieChartPanel( final EC3Module module, EnergyPanel energyPanel ) {
        this.module = module;
        this.energyPanel = energyPanel;
        setBorder( BorderFactory.createTitledBorder( "Pie Chart" ) );
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
