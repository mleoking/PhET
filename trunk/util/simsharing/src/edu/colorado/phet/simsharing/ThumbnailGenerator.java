// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.simsharing;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;

import javax.swing.*;

import edu.colorado.phet.gravityandorbits.GravityAndOrbitsApplication;
import edu.colorado.phet.gravityandorbits.simsharing.gravityandorbits.GravityAndOrbitsApplicationState;

/**
 * @author Sam Reid
 */
public class ThumbnailGenerator {
    protected GravityAndOrbitsApplication application;

    public ThumbnailGenerator() {
        application = GAOHelper.launchApplication( new String[0] );
        application.getPhetFrame().setTitle( application.getPhetFrame().getTitle() + ": Screenshot Generator" );
        application.getGravityAndOrbitsModule().setTeacherMode( true );
        application.getPhetFrame().setState( Frame.ICONIFIED );
    }

    public BufferedImage generateThumbnail( GravityAndOrbitsApplicationState response ) {
        response.apply( application );
        BufferedImage image = new BufferedImage( application.getPhetFrame().getWidth(), application.getPhetFrame().getHeight(), BufferedImage.TYPE_INT_ARGB_PRE );
        Graphics2D g2 = image.createGraphics();
        application.getPhetFrame().getContentPane().paint( g2 );
        g2.dispose();
        return image;
    }

    public static void main( String[] args ) {
        final ThumbnailGenerator thumbnailGenerator = new ThumbnailGenerator();
        final JFrame frame = new JFrame() {{
            pack();
            setVisible( true );
            setDefaultCloseOperation( EXIT_ON_CLOSE );
        }};
        Timer timer = new Timer( 1000, new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                final BufferedImage bufferedImage = thumbnailGenerator.generateThumbnail( new GravityAndOrbitsApplicationState( thumbnailGenerator.application ) );
                frame.setContentPane( new JLabel( new ImageIcon( bufferedImage ) ) );
                frame.pack();
            }
        } );
        timer.start();
    }
}
