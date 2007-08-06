package edu.colorado.phet.common.motion.tests;

import edu.colorado.phet.common.motion.MotionResources;
import edu.colorado.phet.common.phetcommon.view.util.MakeDuotoneImageOp;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;

/**
 * Author: Sam Reid
 * Aug 6, 2007, 2:41:56 PM
 */
public class TestDuotoneImageOp {
    public static void main( String[] args ) throws IOException {
        BufferedImage image = MotionResources.loadBufferedImage( "gray-arrow.png" );
        MakeDuotoneImageOp duotoneImageOp = new MakeDuotoneImageOp( Color.blue );
        JFrame frame = new JFrame( "Test Duotone Image Op" );
        frame.getContentPane().setLayout( new BoxLayout( frame.getContentPane(), BoxLayout.Y_AXIS ) );
        frame.getContentPane().add( new JLabel( new ImageIcon( image ) ) );
        frame.getContentPane().add( new JLabel( new ImageIcon( new MakeDuotoneImageOp( Color.green ).filter( image, null ) ) ) );
        frame.getContentPane().add( new JLabel( new ImageIcon( new MakeDuotoneImageOp( Color.red ).filter( image, null ) ) ) );
        frame.getContentPane().add( new JLabel( new ImageIcon( new MakeDuotoneImageOp( Color.blue ).filter( image, null ) ) ) );
        frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
        frame.pack();
        frame.show();
    }
}
