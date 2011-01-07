// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.energyskatepark.view.swing;

import edu.colorado.phet.common.phetcommon.view.HorizontalLayoutPanel;
import edu.colorado.phet.energyskatepark.EnergySkateParkModule;
import edu.colorado.phet.energyskatepark.EnergySkateParkStrings;
import edu.colorado.phet.energyskatepark.model.EnergySkateParkModel;
import edu.colorado.phet.energyskatepark.view.EnergySkateParkControlPanel;
import edu.colorado.phet.energyskatepark.view.EnergySkateParkSimulationPanel;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

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
                updateShowThermalEnabled();
                module.setPieChartVisible( showPieChartCheckBox.isSelected() );
            }
        } );
        add( showPieChartCheckBox );

        showThermal = new JCheckBox( EnergySkateParkStrings.getString( "piechart.show-thermal" ), !module.getEnergySkateParkSimulationPanel().getRootNode().getIgnoreThermal() );
        showThermal.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                module.getEnergySkateParkSimulationPanel().setIgnoreThermal( !showThermal.isSelected() );
            }
        } );
        module.getEnergySkateParkSimulationPanel().addListener( new EnergySkateParkSimulationPanel.Adapter() {
            public void ignoreThermalChanged() {
                showThermal.setSelected( !module.getEnergySkateParkSimulationPanel().getIgnoreThermal() );
            }
        } );
        add( showThermal );
        module.getEnergySkateParkModel().addEnergyModelListener( new EnergySkateParkModel.EnergyModelListenerAdapter() {
            public void primaryBodyChanged() {
                update();
            }
        } );
        module.getEnergySkateParkSimulationPanel().addListener( new EnergySkateParkSimulationPanel.Adapter() {
            public void pieChartVisibilityChanged() {
                showPieChartCheckBox.setSelected( module.isPieChartVisible() );
            }
        } );
        setAnchor( GridBagConstraints.WEST );
        updateShowThermalEnabled();
        showPieChartCheckBox.addPropertyChangeListener( "enabled", new PropertyChangeListener() {//todo: this there a way to do this without hard-coding string value 'enabled'?

            public void propertyChange( PropertyChangeEvent evt ) {
                updateShowThermalEnabled();
            }
        } );
        showPieChartCheckBox.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                updateShowThermalEnabled();
            }
        } );
    }

    private void updateShowThermalEnabled() {
        showThermal.setEnabled( showPieChartCheckBox.isEnabled() && showPieChartCheckBox.isSelected() );
    }

    private void update() {
        EnergySkateParkModel energySkateParkModel = module.getEnergySkateParkModel();
        boolean enabled = energySkateParkModel.getNumBodies() > 0 && energySkateParkModel.getBody( 0 ).getPotentialEnergy() >= 0.0;
        showPieChartCheckBox.setEnabled( enabled );
    }
}
