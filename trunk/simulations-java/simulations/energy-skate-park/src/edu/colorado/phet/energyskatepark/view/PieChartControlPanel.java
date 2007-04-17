/* Copyright 2007, University of Colorado */
package edu.colorado.phet.energyskatepark.view;

import edu.colorado.phet.common.phetcommon.view.VerticalLayoutPanel;
import edu.colorado.phet.energyskatepark.EnergySkateParkModule;
import edu.colorado.phet.energyskatepark.EnergySkateParkStrings;
import edu.colorado.phet.energyskatepark.model.EnergySkateParkModel;

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
    private EnergySkateParkControlPanel energySkateParkControlPanel;
    //    private JCheckBox ignoreThermal;
    private JCheckBox showThermal;
    public JCheckBox showPieChartCheckBox;

    public PieChartControlPanel( final EnergySkateParkModule module, EnergySkateParkControlPanel energySkateParkControlPanel ) {
        this.module = module;
        this.energySkateParkControlPanel = energySkateParkControlPanel;
        setBorder( BorderFactory.createTitledBorder( EnergySkateParkStrings.getString( "energy.pie.chart" ) ) );
        showPieChartCheckBox = new JCheckBox( EnergySkateParkStrings.getString( "show.pie.chart" ), module.isPieChartVisible() );
        showPieChartCheckBox.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
//                ignoreThermal.setEnabled( pieChart.isSelected() );
                showThermal.setEnabled( showPieChartCheckBox.isSelected() );
                module.setPieChartVisible( showPieChartCheckBox.isSelected() );
            }
        } );
        add( showPieChartCheckBox );

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
        showThermal.setEnabled( showPieChartCheckBox.isSelected() );
    }

    public void update() {
        EnergySkateParkModel energySkateParkModel = module.getEnergySkateParkModel();
        boolean enabled = energySkateParkModel.getNumBodies() > 0 && energySkateParkModel.getBody( 0 ).getPotentialEnergy() >= 0.0;
        showPieChartCheckBox.setEnabled( enabled );
    }
}
