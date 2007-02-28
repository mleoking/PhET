/* Copyright 2004, Sam Reid */
package edu.colorado.phet.ec3.test;

import edu.colorado.phet.common.view.util.ImageLoader;
import edu.colorado.phet.common.view.util.SimStrings;
import edu.colorado.phet.common.view.util.SwingUtils;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.text.MessageFormat;

/**
 * User: Sam Reid
 * Date: May 31, 2006
 * Time: 8:57:09 AM
 * Copyright (c) May 31, 2006 by Sam Reid
 */

public class AWTSplashScreenMonolithic extends Window {
    private static Frame owner = new Frame();
    private Image image;
    private boolean done = false;
    private int blockHeight = 2;
    private int blockWidth = 6;
    private String labelString;
    private Label label;

    public AWTSplashScreenMonolithic( final Image image, String title ) {
        super( owner );
        this.image = image;
        addMouseListener( new MouseAdapter() {
            public void mousePressed( MouseEvent e ) {
                hideSplash();
            }
        } );
        setCursor( Cursor.getPredefinedCursor( Cursor.WAIT_CURSOR ) );
        Thread t = new Thread( new Runnable() {
            public void run() {
                while( !done ) {
                    try {
//                        System.out.println( "AWTSplashScreen.run" );
                        Thread.sleep( 30 );
                        repaint( 0, image.getHeight( AWTSplashScreenMonolithic.this ), image.getWidth( AWTSplashScreenMonolithic.this ), blockHeight );
                    }
                    catch( InterruptedException e ) {
                        e.printStackTrace();
                    }
                }
            }
        } );
        t.start();
        String labelFormat = SimStrings.get( "PhetApplication.StartupDialog.message" );
        Object[] args = {title};
        labelString = MessageFormat.format( labelFormat, args );
        label = new Label( labelString );
        add( label );
    }

    public void hideSplash() {
        super.setVisible( false );
        super.hide();
        super.dispose();
        done = true;
    }

    public void update( Graphics g ) {
        paint( g );
    }

    public void paint( Graphics g ) {
//        System.out.println( "AWTSplashScreen.paint" );
        g.drawImage( image, 0, 0, this );
        g.setColor( Color.lightGray );
        g.fillRect( 0, image.getHeight( this ), image.getWidth( this ), blockHeight );
        g.setColor( Color.black );
        double freqHZ = 0.5;
        double angularfrequency = freqHZ * 2 * Math.PI;
        int x = (int)( Math.sin( System.currentTimeMillis() / 1000.0 * angularfrequency ) * image.getWidth( this ) / 2.0 + image.getWidth( this ) / 2.0 );
        int a = x - blockWidth / 2;
        int b = image.getHeight( this );

//        System.out.println( "a = " + a + ", b=" + b );
        g.fillRect( a, b, blockWidth, blockHeight );
        g.drawString( labelString, 50, 50 );
    }

    public static void main( String[] args ) throws IOException {
        AWTSplashScreenMonolithic awtSplashScreenMonolithic = new AWTSplashScreenMonolithic( ImageLoader.loadBufferedImage( "images/Phet-Flatirons-logo-3-small.gif" ), "Energy Skate Park" );
        awtSplashScreenMonolithic.showSplashScreen();
    }

    private void showSplashScreen() {
        setSize( image.getWidth( this ), image.getHeight( this ) + blockHeight );
        SwingUtils.centerWindowOnScreen( this );
        setVisible( true );
    }
}
