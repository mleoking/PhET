/**
 * Class: SimulationRateControlPanel
 * Class: ${PACKAGE}
 * User: Ron LeMaster
 * Date: Mar 28, 2003
 * Time: 9:44:21 AM
 *      $Author$
 *      $Date$
 *      $Name$
 *      $Revision$
 */
package edu.colorado.phet.lasers.controller;

import edu.colorado.phet.common.model.clock.AbstractClock;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;

public class SimulationRateControlPanel extends JPanel {

    private JSlider simulationRateSlider;
    private JTextField simulationRateTF;
    private AbstractClock clock;

    public SimulationRateControlPanel( AbstractClock clock, int minValue, int maxValue, int defaultValue ) {

        this.clock = clock;
        simulationRateTF = new JTextField( 3 );
        simulationRateTF.setEditable( false );
        simulationRateTF.setHorizontalAlignment( JTextField.RIGHT );
        Font clockFont = simulationRateTF.getFont();
        simulationRateTF.setFont( new Font( clockFont.getName(),
                                            LaserConfig.CONTROL_FONT_STYLE,
                                            LaserConfig.CONTROL_FONT_SIZE ));
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
        GridBagConstraints gbc = new GridBagConstraints( 0,0, 1,1,1,1,
                                                         GridBagConstraints.CENTER,
                                                         GridBagConstraints.HORIZONTAL,
                                                         new Insets( 0,5,0,5 ), 20, 0 );
        this.add( simulationRateSlider, gbc );
        gbc.gridx++;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        this.add( simulationRateTF, gbc );

        Border border = new TitledBorder( "Simulation Rate" );
        this.setBorder( border );
    }

    private void updateSimulationRate( double time ) {
//    private void updateSimulationRate( float time ) {
        clock.setDt( time );
//        new SetClockDtCmd( time ).doIt();
    }
}