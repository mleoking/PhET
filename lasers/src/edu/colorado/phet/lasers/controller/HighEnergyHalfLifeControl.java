/**
 * Class: SpontaneousEmissionTimeControlPanel
 * Class: ${PACKAGE}
 * User: Ron LeMaster
 * Date: Mar 28, 2003
 * Time: 9:40:53 AM
 */
package edu.colorado.phet.lasers.controller;

import edu.colorado.phet.lasers.view.BaseLaserModule;
import edu.colorado.phet.lasers.model.LaserModel;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;

public class HighEnergyHalfLifeControl extends JPanel {

    private JSlider highEnergySpontaneousEmissionTimeSlider;
    private JTextField highEnergySpontaneousEmissionTimeTF;

    public HighEnergyHalfLifeControl( LaserModel model ) {
        this.setLayout( new GridLayout( 2, 1 ));
        addHighEnergyControls( model );
    }

    /**
     *
     */
    private void addHighEnergyControls( final LaserModel model ) {
        JPanel controlPanel = new JPanel( new GridLayout( 1, 2 ) );
        controlPanel.setPreferredSize( new Dimension( 125, 70 ) );

        JPanel timeReadoutPanel = new JPanel( new BorderLayout() );
        highEnergySpontaneousEmissionTimeTF = new JTextField( 4 );
        highEnergySpontaneousEmissionTimeTF.setEditable( false );
        highEnergySpontaneousEmissionTimeTF.setHorizontalAlignment( JTextField.RIGHT );
        Font clockFont = highEnergySpontaneousEmissionTimeTF.getFont();
        highEnergySpontaneousEmissionTimeTF.setFont( new Font( clockFont.getName(),
                                                               LaserConfig.CONTROL_FONT_STYLE,
                                                               LaserConfig.CONTROL_FONT_SIZE  ) );

        highEnergySpontaneousEmissionTimeTF.setText( Float.toString( LaserConfig.DEFAULT_SPONTANEOUS_EMISSION_TIME ) + " msec" );

        highEnergySpontaneousEmissionTimeSlider = new JSlider( JSlider.VERTICAL,
                                        LaserConfig.MINIMUM_SPONTANEOUS_EMISSION_TIME,
                                        LaserConfig.MAXIMUM_SPONTANEOUS_EMISSION_TIME,
                                        LaserConfig.DEFAULT_SPONTANEOUS_EMISSION_TIME );

        highEnergySpontaneousEmissionTimeSlider.setPreferredSize( new Dimension( 20, 50 ) );
        highEnergySpontaneousEmissionTimeSlider.setPaintTicks( true );
        highEnergySpontaneousEmissionTimeSlider.setMajorTickSpacing( 100 );
        highEnergySpontaneousEmissionTimeSlider.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
//                updateHighEnergySpontaneousEmissionTime( ((float)highEnergySpontaneousEmissionTimeSlider.getValue()) / 1000 );
                model.setHighEnergySpontaneousEmissionTime( ((float)highEnergySpontaneousEmissionTimeSlider.getValue()) / 1000  );
                highEnergySpontaneousEmissionTimeTF.setText( Float.toString( highEnergySpontaneousEmissionTimeSlider.getValue() ) );
            }
        } );

        timeReadoutPanel.add( highEnergySpontaneousEmissionTimeTF, BorderLayout.CENTER );
        controlPanel.add( timeReadoutPanel );
        controlPanel.add( highEnergySpontaneousEmissionTimeSlider );

        Border frequencyBorder = new TitledBorder( "High Energy Lifetime" );
        controlPanel.setBorder( frequencyBorder );
        this.add( controlPanel );

    }

//    private void updateHighEnergySpontaneousEmissionTime( float time ) {
//        new SetHighEnergySpontaneousEmissionTimeCmd( time ).doIt();
//    }
}
