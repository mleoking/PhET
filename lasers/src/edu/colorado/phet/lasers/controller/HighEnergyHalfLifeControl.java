/**
 * Class: SpontaneousEmissionTimeControlPanel
 * Class: ${PACKAGE}
 * User: Ron LeMaster
 * Date: Mar 28, 2003
 * Time: 9:40:53 AM
 *      $Author$
 *      $Date$
 *      $Name$
 *      $Revision$
 */
package edu.colorado.phet.lasers.controller;

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

    public HighEnergyHalfLifeControl( final LaserModel model ) {

        JPanel timeReadoutPanel = new JPanel( new BorderLayout() );
        highEnergySpontaneousEmissionTimeTF = new JTextField( 4 );
        highEnergySpontaneousEmissionTimeTF.setEditable( false );
        highEnergySpontaneousEmissionTimeTF.setHorizontalAlignment( JTextField.RIGHT );
        Font clockFont = highEnergySpontaneousEmissionTimeTF.getFont();
        highEnergySpontaneousEmissionTimeTF.setFont( new Font( clockFont.getName(),
                                                               LaserConfig.CONTROL_FONT_STYLE,
                                                               LaserConfig.CONTROL_FONT_SIZE  ) );

        highEnergySpontaneousEmissionTimeTF.setText( Double.toString( LaserConfig.DEFAULT_SPONTANEOUS_EMISSION_TIME ) );
//        highEnergySpontaneousEmissionTimeTF.setText( Double.toString( LaserConfig.DEFAULT_SPONTANEOUS_EMISSION_TIME ) + " msec" );

        highEnergySpontaneousEmissionTimeSlider = new JSlider( JSlider.VERTICAL,
                                        LaserConfig.MINIMUM_SPONTANEOUS_EMISSION_TIME,
                                        LaserConfig.MAXIMUM_SPONTANEOUS_EMISSION_TIME,
                                        LaserConfig.DEFAULT_SPONTANEOUS_EMISSION_TIME );

        highEnergySpontaneousEmissionTimeSlider.setPreferredSize( new Dimension( 20, 50 ) );
        highEnergySpontaneousEmissionTimeSlider.setPaintTicks( true );
        highEnergySpontaneousEmissionTimeSlider.setMajorTickSpacing( 100 );
        highEnergySpontaneousEmissionTimeSlider.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
//                updateHighEnergySpontaneousEmissionTime( ((double)highEnergySpontaneousEmissionTimeSlider.getValue()) / 1000 );
                model.setHighEnergySpontaneousEmissionTime( highEnergySpontaneousEmissionTimeSlider.getValue()  );
//                model.setHighEnergySpontaneousEmissionTime( highEnergySpontaneousEmissionTimeSlider.getValue() / 1000  );
                highEnergySpontaneousEmissionTimeTF.setText( Double.toString( highEnergySpontaneousEmissionTimeSlider.getValue() ) );
            }
        } );

        this.setLayout( new GridBagLayout() );
        GridBagConstraints gbc = new GridBagConstraints( 0,0, 1,1,1,1,
                                                         GridBagConstraints.CENTER,
                                                         GridBagConstraints.HORIZONTAL,
                                                         new Insets( 0,5,0,5 ), 20, 0 );
        this.add( highEnergySpontaneousEmissionTimeSlider, gbc );
        gbc.gridx++;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        this.add( highEnergySpontaneousEmissionTimeTF, gbc );

        Border frequencyBorder = new TitledBorder( "High Energy Lifetime" );
        this.setBorder( frequencyBorder );
    }

//    private void updateHighEnergySpontaneousEmissionTime( double time ) {
//        new SetHighEnergySpontaneousEmissionTimeCmd( time ).doIt();
//    }
}
