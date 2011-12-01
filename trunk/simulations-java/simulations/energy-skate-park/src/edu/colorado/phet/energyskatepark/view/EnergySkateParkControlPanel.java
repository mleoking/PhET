// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.energyskatepark.view;

import java.awt.GridBagConstraints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;

import edu.colorado.phet.common.phetcommon.view.AdvancedPanel;
import edu.colorado.phet.common.phetcommon.view.ControlPanel;
import edu.colorado.phet.common.phetcommon.view.VerticalLayoutPanel;
import edu.colorado.phet.common.phetcommon.view.util.BufferedImageUtils;
import edu.colorado.phet.energyskatepark.AbstractEnergySkateParkModule;
import edu.colorado.phet.energyskatepark.EnergySkateParkResources;
import edu.colorado.phet.energyskatepark.common.IconComponent;
import edu.colorado.phet.energyskatepark.view.swing.ChooseCharacterDialog;
import edu.colorado.phet.energyskatepark.view.swing.ClearHeatButton;
import edu.colorado.phet.energyskatepark.view.swing.EditSkaterPanel;
import edu.colorado.phet.energyskatepark.view.swing.FrictionControl;
import edu.colorado.phet.energyskatepark.view.swing.GridLinesCheckBox;
import edu.colorado.phet.energyskatepark.view.swing.LocationControlPanel;
import edu.colorado.phet.energyskatepark.view.swing.PathRecordContol;
import edu.colorado.phet.energyskatepark.view.swing.PieChartControlPanel;
import edu.umd.cs.piccolo.PNode;

/**
 * User: Sam Reid
 * Date: Sep 30, 2005
 * Time: 2:37:21 PM
 */

public class EnergySkateParkControlPanel extends ControlPanel {
    private final AbstractEnergySkateParkModule module;
    private final PieChartControlPanel piePanel;
    private final PathRecordContol pathRecordContol;
    private final LocationControlPanel locationControlPanel;
    private final AdvancedPanel advancedFrictionPanel;
    private final EditSkaterPanel editSkaterPanel;

    public EnergySkateParkControlPanel( final AbstractEnergySkateParkModule module ) {
        this.module = module;
        JButton reset = new JButton( EnergySkateParkResources.getString( "controls.reset" ) );
        reset.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                doReset();
            }
        } );
        addControl( reset );

        {
            JButton resetSkater = new JButton( EnergySkateParkResources.getString( "controls.reset-character" ) );
            resetSkater.addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    module.returnOrResetSkater();
                }
            } );
            addControl( resetSkater );
        }

        JButton chooseCharacter = new JButton( EnergySkateParkResources.getString( "controls.choose-character" ) + "..." );
        chooseCharacter.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                new ChooseCharacterDialog( module ).setVisible( true );
            }
        } );
        addControl( new IconComponent( chooseCharacter, BufferedImageUtils.rescaleYMaintainAspectRatio( EnergySkateParkResources.getImage( "skater3.png" ), chooseCharacter.getPreferredSize().height ) ) );

        BufferedImage measuringTapeIcon = EnergySkateParkResources.getImage( "ruler-thumb.png" );
        final JCheckBox measuringTape = new JCheckBox( EnergySkateParkResources.getString( "controls.measuring-tape" ), module.isMeasuringTapeVisible() );
        measuringTape.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                module.setMeasuringTapeVisible( measuringTape.isSelected() );
            }
        } );
        getMeasuringTapeNode( module ).addPropertyChangeListener( PNode.PROPERTY_VISIBLE, new PropertyChangeListener() {
            public void propertyChange( PropertyChangeEvent evt ) {
                measuringTape.setSelected( getMeasuringTapeNode( module ).getVisible() );
            }
        } );
        addControlFullWidth( new IconComponent( measuringTape, measuringTapeIcon ) );

        final JCheckBox zeroPointPotential = new JCheckBox( EnergySkateParkResources.getString( "label.potential-energy-reference" ) );
        zeroPointPotential.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                module.getEnergySkateParkSimulationPanel().setZeroPointVisible( zeroPointPotential.isSelected() );
            }
        } );
        module.getEnergySkateParkSimulationPanel().addListener( new EnergySkateParkSimulationPanel.Adapter() {
            public void zeroPointEnergyVisibilityChanged() {
                zeroPointPotential.setSelected( module.getEnergySkateParkSimulationPanel().isZeroPointVisible() );
            }
        } );

        BufferedImage potentialIcon = EnergySkateParkResources.getImage( "peicon.png" );
        addControlFullWidth( new IconComponent( zeroPointPotential, potentialIcon ) );
        addControlFullWidth( new GridLinesCheckBox( module ) );
        pathRecordContol = new PathRecordContol( module );
        addControlFullWidth( pathRecordContol );

        piePanel = new PieChartControlPanel( module, this );

        final VerticalLayoutPanel chartPanel = new VerticalLayoutPanel();
        chartPanel.setFillNone();
        chartPanel.setAnchor( GridBagConstraints.WEST );
        chartPanel.setBorder( BorderFactory.createTitledBorder( EnergySkateParkResources.getString( "plots.plot" ) ) );

        chartPanel.add( piePanel );

        final JButton timeChart = new JButton( EnergySkateParkResources.getString( "plots.energy-vs-time" ) );
        timeChart.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                module.showNewEnergyVsTimePlot();
            }
        } );

        final JButton showEnergyPositionPlot = new JButton( EnergySkateParkResources.getString( "plots.energy-vs-position" ) );
        showEnergyPositionPlot.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                module.setEnergyPositionPlotVisible( true );
            }
        } );

        final JButton showBarChart = new JButton( EnergySkateParkResources.getString( "plots.bar-graph" ) );
        showBarChart.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                module.barChartVisible.set( true );
            }
        } );

        chartPanel.add( showBarChart );
        chartPanel.add( showEnergyPositionPlot );
        chartPanel.add( timeChart );

        addControlFullWidth( chartPanel );
        locationControlPanel = new LocationControlPanel( module, module.getOptions().getPlanetButtonLayout(), module.getOptions().getPlanetButtonsCentered() );
        addControl( locationControlPanel );


        addControl( new ClearHeatButton( module.getEnergySkateParkModel() ) );

        advancedFrictionPanel = new AdvancedPanel( EnergySkateParkResources.getString( "controls.show-friction" ) + " >>", EnergySkateParkResources.getString( "controls.hide-friction" ) + " <<" );
        final FrictionControl frictionControl = new FrictionControl( module );
        advancedFrictionPanel.addControl( frictionControl );
        frictionControl.getModelSlider().setBorder( null );

        addControl( advancedFrictionPanel );

        editSkaterPanel = new EditSkaterPanel( module );
        addControl( editSkaterPanel );
    }

    private PNode getMeasuringTapeNode( AbstractEnergySkateParkModule module ) {
        return module.getEnergySkateParkSimulationPanel().getRootNode().getMeasuringTapeNode();
    }

    private void doReset() {
        module.confirmAndReset();
    }

    public void reset() {
        pathRecordContol.reset();
        locationControlPanel.reset();
        advancedFrictionPanel.setAdvancedControlsVisible( false );
        editSkaterPanel.reset();
    }
}
