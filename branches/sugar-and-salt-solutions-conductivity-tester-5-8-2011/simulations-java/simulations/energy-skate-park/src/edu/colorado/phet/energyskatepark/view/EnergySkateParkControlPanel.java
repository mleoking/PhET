// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.energyskatepark.view;

import edu.colorado.phet.common.phetcommon.view.AdvancedPanel;
import edu.colorado.phet.common.phetcommon.view.ControlPanel;
import edu.colorado.phet.common.phetcommon.view.VerticalLayoutPanel;
import edu.colorado.phet.common.phetcommon.view.util.BufferedImageUtils;
import edu.colorado.phet.common.phetcommon.view.util.ImageLoader;
import edu.colorado.phet.energyskatepark.EnergySkateParkModule;
import edu.colorado.phet.energyskatepark.EnergySkateParkStrings;
import edu.colorado.phet.energyskatepark.common.IconComponent;
import edu.colorado.phet.energyskatepark.view.swing.*;
import edu.umd.cs.piccolo.PNode;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.text.DecimalFormat;

/**
 * User: Sam Reid
 * Date: Sep 30, 2005
 * Time: 2:37:21 PM
 */

public class EnergySkateParkControlPanel extends ControlPanel {
    private EnergySkateParkModule module;
    private PieChartControlPanel piePanel;
    private PathRecordContol pathRecordContol;
    private LocationControlPanel locationControlPanel;
    private AdvancedPanel advancedFrictionPanel;
    private EditSkaterPanel editSkaterPanel;

//    public static boolean PLANET_CENTERED = false;
//    public static LocationControlPanel.PlanetButtonLayout PLANET_LAYOUT = new LocationControlPanel.TwoColumnLayout();

    public EnergySkateParkControlPanel( final EnergySkateParkModule module ) {
        this.module = module;
        JButton reset = new JButton( EnergySkateParkStrings.getString( "controls.reset" ) );
        reset.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                doReset();
            }
        } );
        addControl( reset );

//        JButton button = new JButton("Reset 100x");
//        button.addActionListener(new ActionListener() { public void actionPerformed(ActionEvent e) { for (int i=0;i<100;i++) module.reset(); } });
//        addControl(button);

        {
            JButton resetSkater = new JButton( EnergySkateParkStrings.getString( "controls.reset-character" ) );
            resetSkater.addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    module.returnOrResetSkater();
                }
            } );
            addControl( resetSkater );
        }

//        {
//            final JButton returnSkaterButton = new JButton( EnergySkateParkStrings.getString( "controls.return-character" ) );
//            returnSkaterButton.addActionListener( new ActionListener() {
//                public void actionPerformed( ActionEvent e ) {
//                    if( module.getEnergySkateParkModel().getNumBodies() > 0 ) {
//                        module.returnSkateToRestorePoint( module.getEnergySkateParkModel().getBody( 0 ) );
//                    }
//                }
//            } );
////            addControl( returnSkaterButton );
//            module.getEnergySkateParkModel().addEnergyModelListener( new EnergySkateParkModel.EnergyModelListenerAdapter() {
//                public void primaryBodyChanged() {
//                    updateReturnSkaterButton( module, returnSkaterButton );
//                }
//            } );
//            updateReturnSkaterButton( module, returnSkaterButton );
//        }
        try {
            JButton chooseCharacter = new JButton( EnergySkateParkStrings.getString( "controls.choose-character" ) + "..." );
            chooseCharacter.addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    new ChooseCharacterDialog( module ).setVisible( true );
                }
            } );
            addControl( new IconComponent( chooseCharacter, BufferedImageUtils.rescaleYMaintainAspectRatio( ImageLoader.loadBufferedImage( "energy-skate-park/images/skater3.png" ), chooseCharacter.getPreferredSize().height ) ) );
        }
        catch( IOException e ) {
            e.printStackTrace();
        }

        BufferedImage measuringTapeIcon = null;
        try {
            measuringTapeIcon = ImageLoader.loadBufferedImage( "energy-skate-park/images/ruler-thumb.png" );
        }
        catch( IOException e ) {
            e.printStackTrace();
        }

        final JCheckBox measuringTape = new JCheckBox( EnergySkateParkStrings.getString( "controls.measuring-tape" ), module.isMeasuringTapeVisible() );
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

        final JCheckBox zeroPointPotential = new JCheckBox( EnergySkateParkStrings.getString( "label.potential-energy-reference" ) );
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

        BufferedImage potentialIcon = null;
        try {
            potentialIcon = ImageLoader.loadBufferedImage( "energy-skate-park/images/peicon.png" );
        }
        catch( IOException e ) {
            e.printStackTrace();
        }
        addControlFullWidth( new IconComponent( zeroPointPotential, potentialIcon ) );
        addControlFullWidth( new GridLinesCheckBox( module ) );
        pathRecordContol = new PathRecordContol( module );
        addControlFullWidth( pathRecordContol );

        piePanel = new PieChartControlPanel( module, this );

        final VerticalLayoutPanel chartPanel = new VerticalLayoutPanel();
        chartPanel.setFillNone();
        chartPanel.setAnchor( GridBagConstraints.WEST );
        chartPanel.setBorder( BorderFactory.createTitledBorder( EnergySkateParkStrings.getString( "plots.plot" ) ) );

        chartPanel.add( piePanel );

        final JButton timeChart = new JButton( EnergySkateParkStrings.getString( "plots.energy-vs-time" ) );
        timeChart.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                module.showNewEnergyVsTimePlot();
            }
        } );

        final JButton showEnergyPositionPlot = new JButton( EnergySkateParkStrings.getString( "plots.energy-vs-position" ) );
        showEnergyPositionPlot.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                module.setEnergyPositionPlotVisible( true );
            }
        } );

        final JButton showBarChart = new JButton( EnergySkateParkStrings.getString( "plots.bar-graph" ) );
        showBarChart.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                module.setBarChartVisible( true );
            }
        } );

        chartPanel.add( showBarChart );
        chartPanel.add( showEnergyPositionPlot );
        chartPanel.add( timeChart );

        addControlFullWidth( chartPanel );
        locationControlPanel = new LocationControlPanel( module, module.getOptions().getPlanetButtonLayout(), module.getOptions().getPlanetButtonsCentered() );
        addControl( locationControlPanel );


        addControl( new ClearHeatButton( module.getEnergySkateParkModel() ) );

        advancedFrictionPanel = new AdvancedPanel( EnergySkateParkStrings.getString( "controls.show-friction" )+" >>", EnergySkateParkStrings.getString( "controls.hide-friction" )+" <<" );
        final FrictionControl frictionControl = new FrictionControl( module );
        advancedFrictionPanel.addControl( frictionControl );
        frictionControl.getModelSlider().setBorder( null );

        addControl( advancedFrictionPanel );

        editSkaterPanel = new EditSkaterPanel( module );
        addControl( editSkaterPanel );
    }

    DecimalFormat decimalFormat = new DecimalFormat( "0.0" );

    private void setReturnSkateText( JButton returnSkaterButton, String text ) {//for performance reasons
        if( !returnSkaterButton.getText().equals( text ) ) {
            returnSkaterButton.setText( text );
        }
    }

    private void setEnabled( JButton button, boolean enabled ) {
        if( button.isEnabled() != enabled ) {
            button.setEnabled( enabled );
        }
    }

//    private void updateReturnSkaterButton( EnergySkateParkModule module, JButton returnSkater ) {
//        if( module.getEnergySkateParkModel().getNumBodies() > 0 ) {
//            Body body = module.getEnergySkateParkModel().getBody( 0 );
//            if( body.isRestorePointSet() ) {
//
//                String x = decimalFormat.format( body.getRestorePoint().getX() );
//                String y = decimalFormat.format( body.getRestorePoint().getY() );
//                String returnString = getReturnString( x, y );
//                setReturnSkateText( returnSkater, returnString );
//                setEnabled( returnSkater, true );
//            }
//            else {
//                setReturnSkateText( returnSkater, getReturnString( "?", "?" ) );
//                setEnabled( returnSkater, false );
//            }
//        }
//        else {
//            setReturnSkateText( returnSkater, getReturnString( "?", "?" ) );
//            setEnabled( returnSkater, false );
//        }
//    }

//    private String getReturnString( String x, String y ) {
//        String template = EnergySkateParkStrings.getString( "controls.return-character" );
//        template = template.replaceFirst( "@X", x );
//        template = template.replaceFirst( "@Y", y );
//        return template;
//    }

    private PNode getMeasuringTapeNode( EnergySkateParkModule module ) {
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
