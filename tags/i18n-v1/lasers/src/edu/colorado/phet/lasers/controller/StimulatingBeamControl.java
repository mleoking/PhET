/**
 * Class: StimulatingBeamControl
 * Class: edu.colorado.phet.lasers.view
 * User: Ron LeMaster
 * Date: Mar 28, 2003
 * Time: 9:16:02 AM
 *      $Author$
 *      $Date$
 *      $Name$
 *      $Revision$
 */
package edu.colorado.phet.lasers.controller;

import edu.colorado.phet.common.util.SimpleObserver;
import edu.colorado.phet.lasers.model.LaserModel;
import edu.colorado.phet.lasers.model.photon.CollimatedBeam;
import edu.colorado.phet.common.view.util.SimStrings;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;

/**
 *
 */
public class StimulatingBeamControl extends JPanel implements SimpleObserver {

    private JSlider photonRateSlider;
    private JTextField photonRateTF;
    private CollimatedBeam collimatedBeam;

    public StimulatingBeamControl( final LaserModel model ) {

        collimatedBeam = model.getStimulatingBeam();
        if( collimatedBeam != null ) {
            collimatedBeam.addObserver( this );
        }
        this.collimatedBeam = collimatedBeam;

        photonRateTF = new JTextField( 3 );
        photonRateTF.setEditable( false );
        photonRateTF.setHorizontalAlignment( JTextField.RIGHT );
        Font clockFont = photonRateTF.getFont();
        photonRateTF.setFont( new Font( clockFont.getName(),
                                        LaserConfig.CONTROL_FONT_STYLE,
                                        LaserConfig.CONTROL_FONT_SIZE ) );

        photonRateTF.setText( Double.toString( LaserConfig.DEFAULT_STIMULATING_PHOTON_RATE ) );
        photonRateSlider = new JSlider( JSlider.VERTICAL,
                                        LaserConfig.MINIMUM_STIMULATING_PHOTON_RATE,
                                        LaserConfig.MAXIMUM_STIMULATING_PHOTON_RATE,
                                        0 );

        photonRateSlider.setPreferredSize( new Dimension( 20, 50 ) );
        photonRateSlider.setPaintTicks( true );
        photonRateSlider.setMajorTickSpacing( 2 );
        photonRateSlider.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                //                updatePhotonRate( photonRateSlider.getValue() );
                CollimatedBeam beam = model.getStimulatingBeam();
                beam.setPhotonsPerSecond( photonRateSlider.getValue() );
                photonRateTF.setText( Integer.toString( photonRateSlider.getValue() ) );
            }
        } );

        this.setLayout( new GridBagLayout() );
        GridBagConstraints gbc = new GridBagConstraints( 0, 0, 1, 1, 1, 1,
                                                         GridBagConstraints.CENTER,
                                                         GridBagConstraints.HORIZONTAL,
                                                         new Insets( 0, 5, 0, 5 ), 20, 0 );
        this.add( photonRateSlider, gbc );
        gbc.gridx++;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        this.add( photonRateTF, gbc );
        Border frequencyBorder = new TitledBorder( SimStrings.get( "StimulatingBeamControl.BorderTitle" ) );
        this.setBorder( frequencyBorder );
    }

    public void update() {
        if( photonRateSlider.getValue() != (int)collimatedBeam.getPhotonsPerSecond() ) {
            photonRateTF.setText( Double.toString( collimatedBeam.getPhotonsPerSecond() ) );
            photonRateSlider.setValue( (int)collimatedBeam.getPhotonsPerSecond() );
        }
    }

    public void setMaxPhotonRate( double photonsPerSecond ) {
        photonRateSlider.setMaximum( (int)photonsPerSecond );
    }
}
