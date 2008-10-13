/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.lasers.controller;

import java.awt.*;

import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.colorado.phet.common.phetcommon.model.clock.Clock;
import edu.colorado.phet.common.phetcommon.model.clock.IClock;
import edu.colorado.phet.common.phetcommon.model.clock.TimingStrategy;
import edu.colorado.phet.lasers.LasersResources;

/**
 * A panel that provides control over the clock rate
 */
public class SimulationRateControlPanel extends JPanel {

    private JSlider simulationRateSlider;
    private JTextField simulationRateTF;
    private IClock clock;

    public SimulationRateControlPanel( IClock clock, int minValue, int maxValue, int defaultValue ) {

        this.clock = clock;
        simulationRateTF = new JTextField( 3 );
        simulationRateTF.setEditable( false );
        simulationRateTF.setHorizontalAlignment( JTextField.RIGHT );
        Font clockFont = simulationRateTF.getFont();
        simulationRateTF.setFont( new Font( clockFont.getName(),
                                            LasersConfig.CONTROL_FONT_STYLE,
                                            LasersConfig.CONTROL_FONT_SIZE ) );
        simulationRateTF.setText( Double.toString( 10 ) );

        simulationRateSlider = new JSlider( JSlider.VERTICAL,
                                            minValue,
                                            maxValue,
                                            defaultValue );

        simulationRateSlider.setPreferredSize( new Dimension( 20, 50 ) );
        simulationRateSlider.setPaintTicks( true );
        simulationRateSlider.setMajorTickSpacing( 5 );
        simulationRateSlider.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                updateSimulationRate( simulationRateSlider.getValue() / 1000 );
                simulationRateTF.setText( Double.toString( simulationRateSlider.getValue() ) );
            }
        } );

        this.setLayout( new GridBagLayout() );
        GridBagConstraints gbc = new GridBagConstraints( 0, 0, 1, 1, 1, 1,
                                                         GridBagConstraints.CENTER,
                                                         GridBagConstraints.HORIZONTAL,
                                                         new Insets( 0, 5, 0, 5 ), 20, 0 );
        this.add( simulationRateSlider, gbc );
        gbc.gridx++;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        this.add( simulationRateTF, gbc );

        Border border = new TitledBorder( LasersResources.getString( "SimulationRateControlPanel.BorderTitle" ) );
        this.setBorder( border );
    }

    private void updateSimulationRate( double time ) {
        ( (Clock) clock ).setTimingStrategy( new TimingStrategy.Constant( time ) );
    }
}