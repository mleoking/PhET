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

    private JSlider spontaneousEmissionTimeSlider;
    private JTextField spontaneousEmissionTimeTF;

    public MiddleEnergyHalfLifeControl( final LaserModel model ) {

        spontaneousEmissionTimeTF = new JTextField( 4 );
        spontaneousEmissionTimeTF.setEditable( false );
        spontaneousEmissionTimeTF.setHorizontalAlignment( JTextField.RIGHT );
        Font clockFont = spontaneousEmissionTimeTF.getFont();
        spontaneousEmissionTimeTF.setFont( new Font( clockFont.getName(),
                                                     LaserConfig.CONTROL_FONT_STYLE,
                                                     LaserConfig.CONTROL_FONT_SIZE ) );
        spontaneousEmissionTimeTF.setText( Double.toString( LaserConfig.DEFAULT_SPONTANEOUS_EMISSION_TIME ) );
        spontaneousEmissionTimeSlider = new JSlider( JSlider.VERTICAL,
                                                     LaserConfig.MINIMUM_SPONTANEOUS_EMISSION_TIME,
                                                     LaserConfig.MAXIMUM_SPONTANEOUS_EMISSION_TIME,
                                                     LaserConfig.DEFAULT_SPONTANEOUS_EMISSION_TIME );

        spontaneousEmissionTimeSlider.setPreferredSize( new Dimension( 20, 50 ) );
        spontaneousEmissionTimeSlider.setPaintTicks( true );
        spontaneousEmissionTimeSlider.setMajorTickSpacing( 100 );
        spontaneousEmissionTimeSlider.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                model.setMiddleEnergyMeanLifetime( spontaneousEmissionTimeSlider.getValue() );
                spontaneousEmissionTimeTF.setText( Double.toString( spontaneousEmissionTimeSlider.getValue() ) );
            }
        } );
        model.setMiddleEnergyMeanLifetime( LaserConfig.DEFAULT_SPONTANEOUS_EMISSION_TIME );

        this.setLayout( new GridBagLayout() );
        GridBagConstraints gbc = new GridBagConstraints( 0, 0, 1, 1, 1, 1,
                                                         GridBagConstraints.CENTER,
                                                         GridBagConstraints.HORIZONTAL,
                                                         new Insets( 0, 5, 0, 5 ), 20, 0 );
        this.add( spontaneousEmissionTimeSlider, gbc );
        gbc.gridx++;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        this.add( spontaneousEmissionTimeTF, gbc );

        Border frequencyBorder = new TitledBorder( "Middle Energy Lifetime" );
        this.setBorder( frequencyBorder );
    }
}
