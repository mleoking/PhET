package edu.colorado.phet.hydrogenatom.util;

import edu.colorado.phet.common.phetcommon.view.controls.valuecontrol.LinearValueControl;
import edu.colorado.phet.common.phetcommon.view.util.BufferedImageUtils;
import edu.colorado.phet.hydrogenatom.view.particle.PhotonNode;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

/**
 * This application can be used to display and save PNG files for photons using PhotonNode.
 *
 * @author Sam Reid
 */
public class PhotonImageGenerator {
    private JFrame frame;
    private JLabel label;
    private LinearValueControl valueControl;

    public PhotonImageGenerator() {
        frame = new JFrame( "Photon Generator" );
        JPanel contentPane = new JPanel();
//        valueControl = new LinearValueControl( 400, 800, "wavelength", "0.0", "nm" );
        valueControl = new LinearValueControl( 568, 590, "wavelength", "0.0", "nm" );
        valueControl.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                updateImage();
            }
        } );
        contentPane.add( valueControl );
        label = new JLabel();
        label.setOpaque( true );
        label.setBackground( Color.black );

        contentPane.add( label );

        JButton save = new JButton( "Save" );
        save.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                JFileChooser fc = new JFileChooser();

                // Show save dialog; this method does not return until the dialog is closed
                fc.showSaveDialog( frame );
                try {
                    ImageIO.write( BufferedImageUtils.toBufferedImage( PhotonNode.createPhotonImage( valueControl.getValue() ) ),
                                   "PNG", fc.getSelectedFile() );
                }
                catch( IOException e1 ) {
                    e1.printStackTrace();
                }
            }
        } );
        contentPane.add( save );
        frame.setContentPane( contentPane );

        updateImage();

        frame.pack();
        frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
    }

    private void updateImage() {
        label.setIcon( new ImageIcon( PhotonNode.createPhotonImage( valueControl.getValue() ) ) );
    }

    public static void main( String[] args ) {
        new PhotonImageGenerator().start();
    }

    private void start() {
        frame.setVisible( true );
    }
}
