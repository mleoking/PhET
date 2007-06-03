/* Copyright 2007, University of Colorado */
package edu.colorado.phet.energyskatepark.view.swing;

import edu.colorado.phet.common.phetcommon.view.HorizontalLayoutPanel;
import edu.colorado.phet.energyskatepark.EnergySkateParkModule;
import edu.colorado.phet.energyskatepark.EnergySkateParkStrings;
import edu.colorado.phet.energyskatepark.model.EnergySkateParkModel;
import edu.colorado.phet.energyskatepark.view.EnergySkateParkControlPanel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * User: Sam Reid
 * Date: Oct 31, 2005
 * Time: 12:56:48 AM
 */

public class PieChartControlPanel extends HorizontalLayoutPanel {
    private EnergySkateParkModule module;
    private EnergySkateParkControlPanel energySkateParkControlPanel;
    private JCheckBox showThermal;
    private JCheckBox showPieChartCheckBox;

    public PieChartControlPanel( final EnergySkateParkModule module, EnergySkateParkControlPanel energySkateParkControlPanel ) {
        this.module = module;
        this.energySkateParkControlPanel = energySkateParkControlPanel;
        showPieChartCheckBox = new JCheckBox( EnergySkateParkStrings.getString( "piechart.show" ), module.isPieChartVisible() );
        showPieChartCheckBox.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                showThermal.setEnabled( showPieChartCheckBox.isSelected() );
                module.setPieChartVisible( showPieChartCheckBox.isSelected() );
            }
        } );
        add( showPieChartCheckBox );

        showThermal = new JCheckBox( EnergySkateParkStrings.getString( "piechart.show-thermal" ), !module.getEnergySkateParkSimulationPanel().getRootNode().getIgnoreThermal() );
        showThermal.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                module.getEnergySkateParkSimulationPanel().getRootNode().setIgnoreThermal( !showThermal.isSelected() );
            }
        } );
        add( showThermal );
        module.getEnergySkateParkModel().addEnergyModelListener( new EnergySkateParkModel.EnergyModelListenerAdapter() {
            public void primaryBodyChanged() {
                update();
            }
        } );
        setAnchor( GridBagConstraints.WEST );
        showThermal.setEnabled( showPieChartCheckBox.isSelected() );
    }

    private void update() {
        EnergySkateParkModel energySkateParkModel = module.getEnergySkateParkModel();
        boolean enabled = energySkateParkModel.getNumBodies() > 0 && energySkateParkModel.getBody( 0 ).getPotentialEnergy() >= 0.0;
        showPieChartCheckBox.setEnabled( enabled );
    }
}
