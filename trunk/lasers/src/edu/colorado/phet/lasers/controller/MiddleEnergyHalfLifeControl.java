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

public class MiddleEnergyHalfLifeControl extends JPanel {

    private JSlider middleEnergySpontaneousEmissionTimeSlider;
    private JTextField middleEnergySpontaneousEmissionTimeTF;

    public MiddleEnergyHalfLifeControl( final LaserModel model ) {

        middleEnergySpontaneousEmissionTimeTF = new JTextField( 4 );
        middleEnergySpontaneousEmissionTimeTF.setEditable( false );
        middleEnergySpontaneousEmissionTimeTF.setHorizontalAlignment( JTextField.RIGHT );
        Font clockFont = middleEnergySpontaneousEmissionTimeTF.getFont();
        middleEnergySpontaneousEmissionTimeTF.setFont( new Font( clockFont.getName(),
                                                                 LaserConfig.CONTROL_FONT_STYLE,
                                                                 LaserConfig.CONTROL_FONT_SIZE ) );
        middleEnergySpontaneousEmissionTimeTF.setText( Double.toString( LaserConfig.DEFAULT_SPONTANEOUS_EMISSION_TIME ) );
//        middleEnergySpontaneousEmissionTimeTF.setText( Double.toString( LaserConfig.DEFAULT_SPONTANEOUS_EMISSION_TIME ) + " msec" );
        middleEnergySpontaneousEmissionTimeSlider = new JSlider( JSlider.VERTICAL,
                                                                 LaserConfig.MINIMUM_SPONTANEOUS_EMISSION_TIME,
                                                                 LaserConfig.MAXIMUM_SPONTANEOUS_EMISSION_TIME,
                                                                 LaserConfig.DEFAULT_SPONTANEOUS_EMISSION_TIME );

        middleEnergySpontaneousEmissionTimeSlider.setPreferredSize( new Dimension( 20, 50 ) );
        middleEnergySpontaneousEmissionTimeSlider.setPaintTicks( true );
        middleEnergySpontaneousEmissionTimeSlider.setMajorTickSpacing( 100 );
        middleEnergySpontaneousEmissionTimeSlider.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
//                model.setMiddleEnergySpontaneousEmissionTime( middleEnergySpontaneousEmissionTimeSlider.getValue() / 1000 );
                model.setMiddleEnergySpontaneousEmissionTime( middleEnergySpontaneousEmissionTimeSlider.getValue() );

                //                updateMiddleEnergySpontaneousEmissionTime( ((float)middleEnergySpontaneousEmissionTimeSlider.getValue()) / 1000 );
                middleEnergySpontaneousEmissionTimeTF.setText( Double.toString( middleEnergySpontaneousEmissionTimeSlider.getValue() ) );
            }
        } );

        this.setLayout( new GridBagLayout() );
        GridBagConstraints gbc = new GridBagConstraints( 0,0, 1,1,1,1,
                                                         GridBagConstraints.CENTER,
                                                         GridBagConstraints.HORIZONTAL,
                                                         new Insets( 0,5,0,5 ), 20, 0 );
        this.add( middleEnergySpontaneousEmissionTimeSlider, gbc );
        gbc.gridx++;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        this.add( middleEnergySpontaneousEmissionTimeTF, gbc );

        Border frequencyBorder = new TitledBorder( "Middle Energy Lifetime" );
        this.setBorder( frequencyBorder );
    }

//    private void updateMiddleEnergySpontaneousEmissionTime( float time ) {
//        new SetMiddleEnergySpontaneousEmissionTimeCmd( time ).doIt();
//    }
}
