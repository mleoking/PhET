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

import edu.colorado.phet.common.view.util.SimStrings;
import edu.colorado.phet.lasers.model.photon.CollimatedBeam;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;

/**
 *
 */
public class BeamControl extends JPanel implements CollimatedBeam.WavelengthChangeListener,
                                                   CollimatedBeam.RateChangeListener {

    private JSlider photonRateSlider;
    //    private JTextField photonRateTF;
    private CollimatedBeam beam;
    private JSlider wavelengthSlider;
    private Dimension sliderDimension = new Dimension( 50, 30 );
    private double wavelengthSliderScaleFactor = 1E6;
    // A beam whose wavelength this control must remain longer than.
    private CollimatedBeam wavelengthLimitingBeam;

    public BeamControl( final CollimatedBeam beam ) {
        this( beam, LaserConfig.MINIMUM_SEED_PHOTON_RATE, LaserConfig.MAXIMUM_SEED_PHOTON_RATE );
    }

    public BeamControl( final CollimatedBeam beam, int minRate, int maxRate ) {
        this.beam = beam;
        beam.addListener( this );

        //        photonRateTF = new JTextField( 3 );
        //        photonRateTF.setEditable( false );
        //        photonRateTF.setHorizontalAlignment( JTextField.RIGHT );
        //        Font clockFont = photonRateTF.getFont();
        //        photonRateTF.setFont( new Font( clockFont.getName(),
        //                                        LaserConfig.CONTROL_FONT_STYLE,
        //                                        LaserConfig.CONTROL_FONT_SIZE ) );
        //
        //        photonRateTF.setText( Double.toString( LaserConfig.DEFAULT_SEED_PHOTON_RATE ) );

        // Create the intesity slider
        photonRateSlider = new JSlider( JSlider.HORIZONTAL, minRate, maxRate, 0 );

        photonRateSlider.setPreferredSize( sliderDimension );
        photonRateSlider.setPaintTicks( true );
        photonRateSlider.setMajorTickSpacing( 2 );
        photonRateSlider.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                beam.setPhotonsPerSecond( photonRateSlider.getValue() );
            }
        } );
        photonRateSlider.setValue( (int)beam.getPhotonsPerSecond() );

        // Create the wavelength slider
        // The wavelength has to be inverted to be used as the min and max for the slider
        // if we want red to be on the left and blue to be on the right. The scale factor
        // is needed to make things usable integers.
        wavelengthSlider = new JSlider( (int)( LaserConfig.MIN_WAVELENGTH ),
                                        (int)( LaserConfig.MAX_WAVELENGTH ),
                                        (int)( LaserConfig.MIN_WAVELENGTH ) );
        wavelengthSlider.setPreferredSize( sliderDimension );
        wavelengthSlider.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                int value = wavelengthSlider.getValue();
                // The wavelength may be limited by the wavelength of another beam
                if( wavelengthLimitingBeam != null && wavelengthLimitingBeam.isEnabled() ) {
                    double limitingWavelength = wavelengthLimitingBeam.getWavelength();
                    int limitingValue = (int)( limitingWavelength );
                    value = Math.min( value, limitingValue );
                }
                beam.setWavelength( (int)( value ) );
            }
        } );
        wavelengthSlider.setValue( (int)( beam.getWavelength() ) );

        // Lay out the panel
        this.setLayout( new GridBagLayout() );
        GridBagConstraints gbc = new GridBagConstraints( 0, 0, 1, 1, 1, 1,
                                                         GridBagConstraints.CENTER,
                                                         GridBagConstraints.HORIZONTAL,
                                                         new Insets( 0, 5, 0, 5 ), 20, 0 );
        this.add( new JLabel( SimStrings.get( "StimulatingBeamControl.IntensitySliderLabel" ) ), gbc );
        gbc.gridy++;
        this.add( photonRateSlider, gbc );
        gbc.gridy++;
        //        gbc.anchor = GridBagConstraints.WEST;
        //        gbc.fill = GridBagConstraints.HORIZONTAL;
        this.add( new JLabel( SimStrings.get( "StimulatingBeamControl.WavelengthSliderLabel" ) ), gbc );
        gbc.gridy++;
        this.add( wavelengthSlider, gbc );
        //        this.add( photonRateTF, gbc );
        Border frequencyBorder = new TitledBorder( SimStrings.get( "StimulatingBeamControl.BorderTitle" ) );
        this.setBorder( frequencyBorder );
    }

    public void setMaxPhotonRate( double photonsPerSecond ) {
        photonRateSlider.setMaximum( (int)photonsPerSecond );
    }

    public void setWavelengthLimitingBeam( CollimatedBeam wavelengthLimitingBeam ) {
        this.wavelengthLimitingBeam = wavelengthLimitingBeam;
        wavelengthLimitingBeam.addListener( new LimitingWavelengthChangeListener() );
    }

    public class LimitingWavelengthChangeListener implements CollimatedBeam.WavelengthChangeListener {
        public void wavelengthChanged( CollimatedBeam.WavelengthChangeEvent event ) {
            int wavelengthValue = (int)( wavelengthSliderScaleFactor / event.getWavelength() );
            wavelengthValue = Math.min( wavelengthValue, wavelengthSlider.getValue() );
            wavelengthSlider.setEnabled( false );
            wavelengthSlider.setValue( wavelengthValue );
            wavelengthSlider.setEnabled( true );
        }
    }

    public void wavelengthChanged( CollimatedBeam.WavelengthChangeEvent event ) {
        wavelengthSlider.setEnabled( false );
        wavelengthSlider.setValue( (int)( beam.getWavelength() ) );
        wavelengthSlider.setEnabled( true );
    }

    public void rateChangeOccurred( CollimatedBeam.RateChangeEvent event ) {
        photonRateSlider.setEnabled( false );
        photonRateSlider.setValue( (int)event.getRate() );
        photonRateSlider.setEnabled( true );
    }
}
