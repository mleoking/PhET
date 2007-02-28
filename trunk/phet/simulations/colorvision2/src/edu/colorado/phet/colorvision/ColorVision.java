/**
 * Class: ColorVision
 * Package: edu.colorado.phet.colorvision
 * Author: Another Guy
 * Date: Feb 24, 2004
 */
package edu.colorado.phet.colorvision;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;

public class ColorVision {

    public static void main( String[] args ) {
        JFrame frame = new JFrame( "Color test" );
        Container contentPane = frame.getContentPane();
        CvPanel photonBeamPanel = new CvPanel();
        photonBeamPanel.setPreferredSize( new Dimension( 700, 600 ) );

        PhotonBeam redBeam = new PhotonBeam();
        redBeam.setLocation( Config.beamX, 130 );
        redBeam.setTheta( 30 );
        redBeam.setColor( Color.red );
        redBeam.setRate( 16 );
        redBeam.start();
        photonBeamPanel.addPhotonBeam( redBeam );

        PhotonBeam greenBeam = new PhotonBeam();
        greenBeam.setLocation( Config.beamX, 300 );
        greenBeam.setTheta( 0 );
        greenBeam.setColor( Color.green );
        greenBeam.setRate( 16 );
        greenBeam.start();
        photonBeamPanel.addPhotonBeam( greenBeam );

        PhotonBeam blueBeam = new PhotonBeam();
        blueBeam.setLocation( Config.beamX, 470 );
        blueBeam.setTheta( -30 );
        blueBeam.setColor( Color.blue );
        blueBeam.setRate( 16 );
        blueBeam.start();
        photonBeamPanel.addPhotonBeam( blueBeam );
        contentPane.add( photonBeamPanel );

        JButton stopBtn = new JButton( "Stop" );
        JPanel southPanel = new JPanel();
        southPanel.add( stopBtn );
        stopBtn.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                System.exit( 0 );
            }
        } );

        contentPane.add( southPanel, BorderLayout.SOUTH );
        frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
        frame.pack();
        frame.setVisible( true );
        //How awesome
    }

}
