/** Sam Reid*/
package edu.colorado.phet.cck3.common;

import edu.colorado.phet.common.model.clock.AbstractClock;
import edu.colorado.phet.common.model.clock.ClockTickListener;
import edu.colorado.phet.common.view.util.RescaleOp;
import fmsware.AnimatedGifEncoder;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

/**
 * User: Sam Reid
 * Date: May 28, 2004
 * Time: 11:24:13 PM
 * Copyright (c) May 28, 2004 by Sam Reid
 */
public class MovieRecorder implements ClockTickListener, KeyListener {
    JFrame frame;
    String name;
    int count = 0;
    ArrayList images = new ArrayList();
    static int index = 0;
    private boolean recording;

    public MovieRecorder( JFrame frame, String name ) {
        this.frame = frame;
        this.name = name;
    }

    public void reset() {
        images.clear();
    }

    public void setRecording( boolean recording ) {
        this.recording = recording;
    }

    public void exportAll() {

        BufferedImage[] im = (BufferedImage[])images.toArray( new BufferedImage[0] );

        String filename = ( name + "_" + index + ".gif" );
        System.out.println( "Exporting movie, filename=" + filename + ", framecount=" + im.length );
        index++;
        AnimatedGifEncoder age = new AnimatedGifEncoder();
        age.setQuality( 1 );
        age.start( filename );

        for( int i = 0; i < im.length; i++ ) {
            final BufferedImage image = im[i];
            age.addFrame( image );
        }
        age.finish();

    }

    static final void show( final BufferedImage image ) {
        JFrame frame = new JFrame();
        JPanel panel = new JPanel() {
            protected void paintComponent( Graphics g ) {
                super.paintComponent( g );
                Graphics2D g2 = (Graphics2D)g;
                g2.drawRenderedImage( image, new AffineTransform() );
            }
        };
        frame.setContentPane( panel );
        frame.setSize( image.getWidth() + 20, image.getHeight() + 20 );
        frame.setVisible( true );
    }

    public void clockTicked( AbstractClock c, double dt ) {
        if( !recording ) {
            return;
        }
        if( count++ % 2 != 0 ) {
            return;
        }
        BufferedImage im = new BufferedImage( frame.getWidth(), frame.getHeight(), BufferedImage.TYPE_INT_RGB );
        Graphics2D g2 = im.createGraphics();
        frame.paint( g2 );
        g2.dispose();

        im = RescaleOp.rescaleXMaintainAspectRatio( im, 600 );
        System.out.println( "images.size() = " + images.size() );
        images.add( im );
    }

    public void keyPressed( KeyEvent e ) {
    }

    public void keyReleased( KeyEvent e ) {
        if( e.getKeyCode() == KeyEvent.VK_RIGHT ) {
            reset();
            setRecording( true );
            System.out.println( "Started recording." );
        }
        else if( e.getKeyCode() == KeyEvent.VK_LEFT ) {
            setRecording( false );
            reset();
            System.out.println( "Stopped recording." );
        }
        else if( e.getKeyCode() == KeyEvent.VK_SPACE ) {
            System.out.println( "Exporting movie." );
            exportAll();
            reset();
            setRecording( false );
            System.out.println( "Finished Exporting movie." );
        }
    }

    public void keyTyped( KeyEvent e ) {
    }
}
