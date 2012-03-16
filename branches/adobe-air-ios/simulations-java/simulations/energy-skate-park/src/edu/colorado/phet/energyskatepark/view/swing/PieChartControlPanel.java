// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.energyskatepark.view.swing;

import java.awt.GridBagConstraints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JCheckBox;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.colorado.phet.common.phetcommon.util.function.VoidFunction0;
import edu.colorado.phet.common.phetcommon.view.HorizontalLayoutPanel;
import edu.colorado.phet.energyskatepark.AbstractEnergySkateParkModule;
import edu.colorado.phet.energyskatepark.EnergySkateParkResources;
import edu.colorado.phet.energyskatepark.model.EnergySkateParkModel;
import edu.colorado.phet.energyskatepark.view.EnergySkateParkControlPanel;
import edu.colorado.phet.energyskatepark.view.EnergySkateParkSimulationPanel;

/**
 * User: Sam Reid
 * Date: Oct 31, 2005
 * Time: 12:56:48 AM
 */

public class PieChartControlPanel extends HorizontalLayoutPanel {
    private final AbstractEnergySkateParkModule module;
    private final EnergySkateParkControlPanel energySkateParkControlPanel;
    private final JCheckBox showThermal;
    private final JCheckBox showPieChartCheckBox;

    public PieChartControlPanel( final AbstractEnergySkateParkModule module, EnergySkateParkControlPanel energySkateParkControlPanel ) {
        this.module = module;
        this.energySkateParkControlPanel = energySkateParkControlPanel;
        showPieChartCheckBox = new JCheckBox( EnergySkateParkResources.getString( "piechart.show" ), module.pieChartVisible.get() );
        showPieChartCheckBox.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                updateShowThermalEnabled();
                module.pieChartVisible.set( showPieChartCheckBox.isSelected() );
            }
        } );
        add( showPieChartCheckBox );

        final boolean defaultShowThermal = !module.getEnergySkateParkSimulationPanel().getRootNode().getIgnoreThermal();
        showThermal = new JCheckBox( EnergySkateParkResources.getString( "piechart.show-thermal" ), defaultShowThermal );
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
                showPieChartCheckBox.setSelected( module.pieChartVisible.get() );
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

        module.addResetListener( new VoidFunction0() {
            public void apply() {
                showPieChartCheckBox.setSelected( false );
                showThermal.setSelected( defaultShowThermal );
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
