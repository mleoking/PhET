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
import edu.colorado.phet.common.view.util.SimStrings;
import edu.colorado.phet.lasers.model.atom.GroundState;
import edu.colorado.phet.lasers.model.photon.CollimatedBeam;
import edu.colorado.phet.lasers.model.photon.Photon;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;

/**
 *
 */
public class BeamControl extends JPanel implements SimpleObserver {

    private JSlider photonRateSlider;
    //    private JTextField photonRateTF;
    private CollimatedBeam beam;
    private JSlider wavelengthSlider;
    private Dimension sliderDimension = new Dimension( 50, 30 );

    public BeamControl( final CollimatedBeam beam ) {
        this.beam = beam;

        //        photonRateTF = new JTextField( 3 );
        //        photonRateTF.setEditable( false );
        //        photonRateTF.setHorizontalAlignment( JTextField.RIGHT );
        //        Font clockFont = photonRateTF.getFont();
        //        photonRateTF.setFont( new Font( clockFont.getName(),
        //                                        LaserConfig.CONTROL_FONT_STYLE,
        //                                        LaserConfig.CONTROL_FONT_SIZE ) );
        //
        //        photonRateTF.setText( Double.toString( LaserConfig.DEFAULT_STIMULATING_PHOTON_RATE ) );

        // Create the intesity slider
        photonRateSlider = new JSlider( JSlider.HORIZONTAL,
                                        LaserConfig.MINIMUM_STIMULATING_PHOTON_RATE,
                                        LaserConfig.MAXIMUM_STIMULATING_PHOTON_RATE,
                                        0 );

        photonRateSlider.setPreferredSize( sliderDimension );
        photonRateSlider.setPaintTicks( true );
        photonRateSlider.setMajorTickSpacing( 2 );
        photonRateSlider.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                beam.setPhotonsPerSecond( photonRateSlider.getValue() );
            }
        } );
        photonRateSlider.setValue( (int)beam.getPhotonsPerSecond() );
        beam.addListener( new CollimatedBeam.RateChangeListener() {
            public void rateChangeOccurred( CollimatedBeam.RateChangeEvent event ) {
                photonRateSlider.setEnabled( false );
                photonRateSlider.setValue( (int)event.getRate() );
                photonRateSlider.setEnabled( true );
            }
        } );

        // Create the wavelength slider
        // The wavelength has to be inverted to be used as the min and max for the slider
        // if we want red to be on the left and blue to be on the right. The scale factor
        // is needed to make things usable integers.
        final double scaleFactor = 1E6;
        wavelengthSlider = new JSlider( (int)( scaleFactor / GroundState.instance().getWavelength() ),
                                        (int)( scaleFactor / LaserConfig.MIN_WAVELENGTH ),
                                        (int)( scaleFactor / GroundState.instance().getWavelength() ) );
        wavelengthSlider.setPreferredSize( sliderDimension );
        wavelengthSlider.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                beam.setWavelength( (int)( scaleFactor / wavelengthSlider.getValue() ) );
            }
        } );
        wavelengthSlider.setValue( (int)( scaleFactor / beam.getWavelength() ));


        // Lay out the panel
        this.setLayout( new GridBagLayout() );
        GridBagConstraints gbc = new GridBagConstraints( 0, 0, 1, 1, 1, 1,
                                                         GridBagConstraints.CENTER,
                                                         GridBagConstraints.HORIZONTAL,
                                                         new Insets( 0, 5, 0, 5 ), 20, 0 );
        this.add( photonRateSlider, gbc );
        gbc.gridy++;
        //        gbc.anchor = GridBagConstraints.WEST;
        //        gbc.fill = GridBagConstraints.HORIZONTAL;
        this.add( wavelengthSlider, gbc );
        //        this.add( photonRateTF, gbc );
        Border frequencyBorder = new TitledBorder( SimStrings.get( "StimulatingBeamControl.BorderTitle" ) );
        this.setBorder( frequencyBorder );
    }

    public void update() {
        if( photonRateSlider.getValue() != (int)beam.getPhotonsPerSecond() ) {
            //            photonRateTF.setText( Double.toString( beam.getPhotonsPerSecond() ) );
            photonRateSlider.setValue( (int)beam.getPhotonsPerSecond() );
        }
    }

    public void setMaxPhotonRate( double photonsPerSecond ) {
        photonRateSlider.setMaximum( (int)photonsPerSecond );
    }
}
