// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.sugarandsaltsolutions.common.util;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JComponent;
import javax.swing.JFrame;

/**
 * This is a utility class that allows the user to use a screenshot to pick 2D projected coordinates from a rasterized 3D representation.
 * We are using this to pick good 2D positions for the 3D sucrose structure.
 *
 * @author Sam Reid
 */
public class ProjectorUtil {

    private JFrame frame;

    public ProjectorUtil( final BufferedImage image ) {
        frame = new JFrame( getClass().getName() ) {{
            setContentPane( new JComponent() {
                {
                    setPreferredSize( new Dimension( image.getWidth(), image.getHeight() ) );
                    addMouseListener( new MouseAdapter() {
                        @Override public void mousePressed( MouseEvent e ) {
                            System.out.println( e.getX() + ", " + e.getY() );
                        }
                    } );
                }

                @Override protected void paintComponent( Graphics g ) {
                    super.paintComponent( g );
                    g.drawImage( image, 0, 0, null );
                }
            } );
            pack();
        }};
    }

    public static void main( String[] args ) throws IOException {
        new ProjectorUtil( ImageIO.read( new File( args[0] ) ) ).start();
    }

    private void start() {
        frame.setVisible( true );
    }
}
