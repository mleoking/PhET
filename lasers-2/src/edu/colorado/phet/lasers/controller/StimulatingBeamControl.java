/**
 * Class: StimulatingBeamControl
 * Class: edu.colorado.phet.lasers.view
 * User: Ron LeMaster
 * Date: Mar 28, 2003
 * Time: 9:16:02 AM
 */
package edu.colorado.phet.lasers.controller;

import edu.colorado.phet.common.util.SimpleObserver;
import edu.colorado.phet.lasers.physics.photon.CollimatedBeam;
import edu.colorado.phet.lasers.physics.LaserModel;

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

        JPanel photonRateControlPanel = new JPanel( new GridLayout( 1, 2 ) );
        photonRateControlPanel.setPreferredSize( new Dimension( 125, 70 ) );

        JPanel photonRateReadoutPanel = new JPanel( new BorderLayout() );
        photonRateTF = new JTextField( 4 );
        photonRateTF.setEditable( false );
        photonRateTF.setHorizontalAlignment( JTextField.RIGHT );
        Font clockFont = photonRateTF.getFont();
        photonRateTF.setFont( new Font( clockFont.getName(),
                                        LaserConfig.CONTROL_FONT_STYLE,
                                        LaserConfig.CONTROL_FONT_SIZE ) );

        photonRateTF.setText( Float.toString( LaserConfig.DEFAULT_STIMULATING_PHOTON_RATE ) + " photon/sec" );

        photonRateSlider = new JSlider( JSlider.VERTICAL,
                                        LaserConfig.MINIMUM_STIMULATING_PHOTON_RATE,
                                        LaserConfig.MAXIMUM_STIMULATING_PHOTON_RATE,
                                        10 );

        photonRateSlider.setPreferredSize( new Dimension( 20, 50 ) );
        photonRateSlider.setPaintTicks( true );
        photonRateSlider.setMajorTickSpacing( 10 );
        photonRateSlider.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
//                updatePhotonRate( photonRateSlider.getValue() );
                CollimatedBeam beam = model.getStimulatingBeam();
                beam.setPhotonsPerSecond( photonRateSlider.getValue() );
                photonRateTF.setText( Integer.toString( photonRateSlider.getValue() ) );
            }
        } );

        photonRateReadoutPanel.add( photonRateTF, BorderLayout.CENTER );
        photonRateControlPanel.add( photonRateReadoutPanel );
        photonRateControlPanel.add( photonRateSlider );

        Border frequencyBorder = new TitledBorder( "Stimulating photon rate" );
        photonRateControlPanel.setBorder( frequencyBorder );
        this.add( photonRateControlPanel );
    }

    public void update() {
        if( photonRateSlider.getValue() != (int)collimatedBeam.getPhotonsPerSecond() ) {
            photonRateTF.setText( Float.toString( collimatedBeam.getPhotonsPerSecond() ) );
            photonRateSlider.setValue( (int)collimatedBeam.getPhotonsPerSecond() );
        }
    }
}
