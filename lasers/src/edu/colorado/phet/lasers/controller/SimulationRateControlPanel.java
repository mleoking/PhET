/**
 * Class: SimulationRateControlPanel
 * Class: ${PACKAGE}
 * User: Ron LeMaster
 * Date: Mar 28, 2003
 * Time: 9:44:21 AM
 */
package edu.colorado.phet.lasers.controller;

import edu.colorado.phet.controller.command.SetClockDtCmd;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;

public class SimulationRateControlPanel extends JPanel {

    private JSlider simulationRateSlider;
    private JTextField simulationRateTF;

    public SimulationRateControlPanel( int minValue, int maxValue, int defaultValue ) {
        JPanel controlPanel = new JPanel( new GridLayout( 1, 2 ) );
        controlPanel.setPreferredSize( new Dimension( 125, 70 ) );

        JPanel timeReadoutPanel = new JPanel( new BorderLayout() );
        simulationRateTF = new JTextField( 4 );
        simulationRateTF.setEditable( false );
        simulationRateTF.setHorizontalAlignment( JTextField.RIGHT );
        Font clockFont = simulationRateTF.getFont();
        simulationRateTF.setFont( new Font( clockFont.getName(),
                                            LaserConfig.CONTROL_FONT_STYLE,
                                            LaserConfig.CONTROL_FONT_SIZE ));
        simulationRateTF.setText( Float.toString( 10 ) );

        simulationRateSlider = new JSlider( JSlider.VERTICAL,
                                        minValue,
                                        maxValue,
                                        defaultValue );

        simulationRateSlider.setPreferredSize( new Dimension( 20, 50 ) );
        simulationRateSlider.setPaintTicks( true );
        simulationRateSlider.setMajorTickSpacing( 5 );
        simulationRateSlider.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                updateSimulationRate( ((float)simulationRateSlider.getValue()) / 1000 );
                simulationRateTF.setText( Float.toString( simulationRateSlider.getValue() ) );
            }
        } );

        timeReadoutPanel.add( simulationRateTF, BorderLayout.CENTER );
        controlPanel.add( timeReadoutPanel );
        controlPanel.add( simulationRateSlider );

        Border border = new TitledBorder( "Simulation rate" );
        controlPanel.setBorder( border );
        this.add( controlPanel );
    }

    private void updateSimulationRate( float time ) {
        new SetClockDtCmd( time ).doIt();
    }
}