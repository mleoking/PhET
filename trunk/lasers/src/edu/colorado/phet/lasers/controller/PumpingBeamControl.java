/**
 * Class: PumpingBeamControl
 * Class: ${PACKAGE}
 * User: Ron LeMaster
 * Date: Mar 28, 2003
 * Time: 9:18:40 AM
 *      $Author$
 *      $Date$
 *      $Name$
 *      $Revision$
 */
package edu.colorado.phet.lasers.controller;

import edu.colorado.phet.common.util.SimpleObserver;
import edu.colorado.phet.common.view.util.SimStrings;
import edu.colorado.phet.lasers.model.photon.CollimatedBeam;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;

public class PumpingBeamControl extends JPanel implements SimpleObserver {

    private JTextField pumpingRateTF;
    private JSlider pumpingRateSlider;

    public PumpingBeamControl( final CollimatedBeam collimatedBeam ) {

        if( collimatedBeam != null ) {
            collimatedBeam.addObserver( this );
        }

        JPanel photonRateReadoutPanel = new JPanel( new BorderLayout() );
        pumpingRateTF = new JTextField( 4 );
        pumpingRateTF.setEditable( false );
        pumpingRateTF.setHorizontalAlignment( JTextField.RIGHT );
        Font clockFont = pumpingRateTF.getFont();
        pumpingRateTF.setFont( new Font( clockFont.getName(),
                                         LaserConfig.CONTROL_FONT_STYLE,
                                         LaserConfig.CONTROL_FONT_SIZE ) );
        pumpingRateTF.setText( Double.toString( LaserConfig.DEFAULT_PUMPING_PHOTON_RATE ) );
//        pumpingRateTF.setText( Double.toString( LaserConfig.DEFAULT_PUMPING_PHOTON_RATE ) + " photon/sec" );

        pumpingRateSlider = new JSlider( JSlider.VERTICAL,
                                         LaserConfig.MINIMUM_PUMPING_PHOTON_RATE,
                                         LaserConfig.MAXIMUM_PUMPING_PHOTON_RATE,
                                         10 );

        pumpingRateSlider.setPreferredSize( new Dimension( 20, 50 ) );
        pumpingRateSlider.setPaintTicks( true );
        pumpingRateSlider.setMajorTickSpacing( 10 );
        pumpingRateSlider.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                if( !pumpingRateSlider.getValueIsAdjusting() ) {
                    collimatedBeam.setPhotonsPerSecond( pumpingRateSlider.getValue() );
                    //                    updatePumpingRate( pumpingRateSlider.getValue() );
                    pumpingRateTF.setText( Integer.toString( pumpingRateSlider.getValue() ) );
                }
            }
        } );

        this.setLayout( new GridBagLayout() );
        GridBagConstraints gbc = new GridBagConstraints( 0,0, 1,1,0,0,
                                                         GridBagConstraints.CENTER,
                                                         GridBagConstraints.HORIZONTAL,
                                                         new Insets( 5,5,5,5 ), 20, 0 );
        this.add( pumpingRateSlider, gbc );
        gbc.gridx++;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        this.add( pumpingRateTF, gbc );
        Border frequencyBorder = new TitledBorder( SimStrings.get( "PumpingBeamControl.BorderTitle" ) );
        this.setBorder( frequencyBorder );
    }

    //    private void updatePumpingRate( int rate ) {
    //
    //        PhetApplication.instance().getPhysicalSystem().addPrepCmd( new SetPumpingRateCmd( rate ));
    //    }


    public void update() {
        //            SwingUtilities.invokeLater( new UpdateControl( collimatedBeam ));
        //        }
    }

    //    public void update( Observable o, Object arg ) {
    //        if( o instanceof CollimatedBeam ) {
    //            CollimatedBeam collimatedBeam = (CollimatedBeam)o;
    ////            SwingUtilities.invokeLater( new UpdateControl( collimatedBeam ));
    //        }
    //    }
    //
    //
    // Inner Classes
    //
    private class UpdateControl implements Runnable {

        private CollimatedBeam collimatedBeam;

        public UpdateControl( CollimatedBeam collimatedBeam ) {
            this.collimatedBeam = collimatedBeam;
        }

        public void run() {
            int a = (int)collimatedBeam.getPhotonsPerSecond();
            int b = pumpingRateSlider.getValue();
            if( pumpingRateSlider.getValue() != (int)collimatedBeam.getPhotonsPerSecond() ) {
                pumpingRateTF.setText( Double.toString( collimatedBeam.getPhotonsPerSecond() ) );
                pumpingRateSlider.setValue( (int)collimatedBeam.getPhotonsPerSecond() );
            }
        }
    }
}
