/**
 * Class: SpontaneousEmissionTimeControlPanel
 * Class: ${PACKAGE}
 * User: Ron LeMaster
 * Date: Mar 28, 2003
 * Time: 9:40:53 AM
 */
package edu.colorado.phet.lasers.controller;

import edu.colorado.phet.lasers.controller.LaserConfig;
import edu.colorado.phet.lasers.controller.command.SetHighEnergySpontaneousEmissionTimeCmd;
import edu.colorado.phet.lasers.controller.command.SetMiddleEnergySpontaneousEmissionTimeCmd;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;

public class MiddleEnergyHalfLifeControl extends JPanel {

    private JSlider middleEnergySpontaneousEmissionTimeSlider;
    private JTextField middleEnergySpontaneousEmissionTimeTF;

    public MiddleEnergyHalfLifeControl() {
        this.setLayout( new GridLayout( 2, 1 ));
        addMiddleEnergyControls();
    }

    /**
     *
     */
    private void addMiddleEnergyControls() {
        JPanel controlPanel = new JPanel( new GridLayout( 1, 2 ) );
        controlPanel.setPreferredSize( new Dimension( 125, 70 ) );

        JPanel timeReadoutPanel = new JPanel( new BorderLayout() );
        middleEnergySpontaneousEmissionTimeTF = new JTextField( 4 );
        middleEnergySpontaneousEmissionTimeTF.setEditable( false );
        middleEnergySpontaneousEmissionTimeTF.setHorizontalAlignment( JTextField.RIGHT );
        Font clockFont = middleEnergySpontaneousEmissionTimeTF.getFont();
        middleEnergySpontaneousEmissionTimeTF.setFont( new Font( clockFont.getName(),
                                                                 LaserConfig.CONTROL_FONT_STYLE,
                                                                 LaserConfig.CONTROL_FONT_SIZE ));
        middleEnergySpontaneousEmissionTimeTF.setText( Float.toString( LaserConfig.DEFAULT_SPONTANEOUS_EMISSION_TIME ) + " msec" );
        middleEnergySpontaneousEmissionTimeSlider = new JSlider( JSlider.VERTICAL,
                                        LaserConfig.MINIMUM_SPONTANEOUS_EMISSION_TIME,
                                        LaserConfig.MAXIMUM_SPONTANEOUS_EMISSION_TIME,
                                        LaserConfig.DEFAULT_SPONTANEOUS_EMISSION_TIME );

        middleEnergySpontaneousEmissionTimeSlider.setPreferredSize( new Dimension( 20, 50 ) );
        middleEnergySpontaneousEmissionTimeSlider.setPaintTicks( true );
        middleEnergySpontaneousEmissionTimeSlider.setMajorTickSpacing( 100 );
        middleEnergySpontaneousEmissionTimeSlider.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                updateMiddleEnergySpontaneousEmissionTime( ((float)middleEnergySpontaneousEmissionTimeSlider.getValue()) / 1000 );
                middleEnergySpontaneousEmissionTimeTF.setText( Float.toString( middleEnergySpontaneousEmissionTimeSlider.getValue() ) );
            }
        } );

        timeReadoutPanel.add( middleEnergySpontaneousEmissionTimeTF, BorderLayout.CENTER );
        controlPanel.add( timeReadoutPanel );
        controlPanel.add( middleEnergySpontaneousEmissionTimeSlider );

        Border frequencyBorder = new TitledBorder( "Middle Energy Lifetime" );
        controlPanel.setBorder( frequencyBorder );
        this.add( controlPanel );

    }

    private void updateMiddleEnergySpontaneousEmissionTime( float time ) {
        new SetMiddleEnergySpontaneousEmissionTimeCmd( time ).doIt();
    }
}
