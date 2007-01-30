/* Copyright 2004, Sam Reid */
package edu.colorado.phet.ec3.controls;

import edu.colorado.phet.common.view.VerticalLayoutPanel;
import edu.colorado.phet.ec3.EnergySkateParkControlPanel;
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
    private EnergySkateParkControlPanel EnergySkateParkControlPanel;
    //    private JCheckBox ignoreThermal;
    private JCheckBox showThermal;

    public PieChartControlPanel( final EnergySkateParkModule module, EnergySkateParkControlPanel EnergySkateParkControlPanel ) {
        this.module = module;
        this.EnergySkateParkControlPanel = EnergySkateParkControlPanel;
        setBorder( BorderFactory.createTitledBorder( EnergySkateParkStrings.getString( "energy.pie.chart" ) ) );
        final JCheckBox pieChart = new JCheckBox( EnergySkateParkStrings.getString( "show.pie.chart" ), module.isPieChartVisible() );
        pieChart.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
//                ignoreThermal.setEnabled( pieChart.isSelected() );
                showThermal.setEnabled( pieChart.isSelected() );
                module.setPieChartVisible( pieChart.isSelected() );
            }
        } );
        add( pieChart );

//        ignoreThermal = new JCheckBox( EnergySkateParkStrings.getString( "ignore.thermal" ), module.getEnergyConservationCanvas().getRootNode().getIgnoreThermal() );
//        ignoreThermal.addActionListener( new ActionListener() {
//            public void actionPerformed( ActionEvent e ) {
//                module.getEnergyConservationCanvas().getRootNode().setIgnoreThermal( ignoreThermal.isSelected() );
//            }
//        } );
//        add( ignoreThermal );
//        ignoreThermal.setEnabled( pieChart.isSelected() );

        showThermal = new JCheckBox( EnergySkateParkStrings.getString( "show.thermal" ), !module.getEnergyConservationCanvas().getRootNode().getIgnoreThermal() );
        showThermal.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                module.getEnergyConservationCanvas().getRootNode().setIgnoreThermal( !showThermal.isSelected() );
            }
        } );
        add( showThermal );
        showThermal.setEnabled( pieChart.isSelected() );
    }
}
