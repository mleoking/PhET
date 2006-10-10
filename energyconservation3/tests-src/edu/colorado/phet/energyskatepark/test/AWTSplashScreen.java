/* Copyright 2004, Sam Reid */
package edu.colorado.phet.energyskatepark.test;

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

public class AWTSplashScreen extends Window {

    private static Frame owner;
    private boolean done = false;
    private String labelString;

    private Component imageComponent;
    private Component textComponent;
    private Component animationComponent;

    public AWTSplashScreen( final Image image, String title ) {
        super( initOwner() );
        addMouseListener( new MouseAdapter() {
            public void mousePressed( MouseEvent e ) {
                hideSplash();
            }
        } );
        setCursor( Cursor.getPredefinedCursor( Cursor.WAIT_CURSOR ) );
        labelString = MessageFormat.format( SimStrings.get( "PhetApplication.StartupDialog.message" ), new Object[]{title} );
        imageComponent = new ImageComponent( image ) {
            public void paint( Graphics g ) {
                super.paint( g );
                drawBorder( this, g );
            }
        };
        textComponent = new Label( labelString ) {
            public void paint( Graphics g ) {
                super.paint( g );
                drawBorder( this, g );
            }
        };
        animationComponent = new AnimationComponent();

        setLayout( new BorderLayout() );
        add( imageComponent, BorderLayout.CENTER );

        add( textComponent, BorderLayout.EAST );
        add( animationComponent, BorderLayout.SOUTH );
    }

    private static Frame initOwner() {
        if( owner == null ) {
            owner = new Frame();
        }
        return owner;
    }

    private static void drawBorder( Component component, Graphics g ) {
        g.setColor( Color.black );
        g.drawRect( 0, 0, component.getWidth() - 1, component.getHeight() - 1 );
    }

    private class AnimationComponent extends Component {
        int blockWidth = 12;
        int blockHeight = 3;

        public AnimationComponent() {
            Thread t = new Thread( new Runnable() {
                public void run() {
                    while( !done ) {
                        try {
                            Thread.sleep( 30 );
                            repaint();
                        }
                        catch( InterruptedException e ) {
                            e.printStackTrace();
                        }
                    }
                }
            } );
            t.start();
        }

        public Dimension getPreferredSize() {
            return new Dimension( super.getPreferredSize().width, blockHeight );
        }

        public void update( Graphics g ) {
            paint( g );
        }

        public void paint( Graphics g ) {
            g.setColor( Color.lightGray );
            g.fillRect( 0, 0, getWidth(), getHeight() );
            g.setColor( Color.black );
            double freqHZ = 0.5;
            double angularfrequency = freqHZ * 2 * Math.PI;
            int x = (int)( Math.sin( System.currentTimeMillis() / 1000.0 * angularfrequency ) * getWidth() / 2.0 + getWidth() / 2.0 );
            int a = x - blockWidth / 2;

            g.fillRect( a, 0, blockWidth, blockHeight );
            g.drawRect( 0, -1, getWidth() - 1, getHeight() );
        }
    }

    private static class ImageComponent extends Component {
        Image image;

        public ImageComponent( Image image ) {
            this.image = image;
        }

        public Dimension getPreferredSize() {
            return new Dimension( image.getWidth( this ), image.getHeight( this ) );
        }

        public void paint( Graphics g ) {
            super.paint( g );
            g.drawImage( image, 0, 0, this );
        }
    }

    public void hideSplash() {
        System.out.println( "AWTSplashScreen2.hideSplash" );
        super.setVisible( false );
        super.hide();
        super.dispose();
        owner.dispose();
        owner = null;
        done = true;
    }

    private void showSplashScreen() {
        pack();
        SwingUtils.centerWindowOnScreen( this );
        setVisible( true );
    }

    public static void main( String[] args ) throws IOException, InterruptedException {
        AWTSplashScreen awtSplashScreen = new AWTSplashScreen( ImageLoader.loadBufferedImage( "images/Phet-Flatirons-logo-3-small.gif" ), "Energy Skate Park" );
        awtSplashScreen.showSplashScreen();
        Thread.sleep( 5000 );
        awtSplashScreen.hideSplash();
    }
}
