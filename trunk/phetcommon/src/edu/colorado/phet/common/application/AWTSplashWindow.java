package edu.colorado.phet.common.application;

import edu.colorado.phet.common.view.PhetFrame;
import edu.colorado.phet.common.view.util.ImageLoader;
import edu.colorado.phet.common.view.util.SimStrings;
import edu.colorado.phet.common.view.util.SwingUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.text.MessageFormat;

/**
 * User: Sam Reid
 * Date: May 31, 2006
 * Time: 8:57:09 AM
 * Copyright (c) May 31, 2006 by Sam Reid
 */

public class AWTSplashWindow extends Window {

    private static Frame owner;
    private boolean done = false;
    private String labelString;

    private Component imageComponent;
    private Component textComponent;
    private Component animationComponent;

    public AWTSplashWindow( final Image image, String title ) {
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
        textComponent = new Label( labelString );
        animationComponent = new AnimationComponent();

        final GridBagConstraints gbc = new GridBagConstraints( 0, 0, 1, 1, 1, 1, GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets( 0, 20, 0, 10 ), 0, 0 );

        final Panel panel = new Panel( new GridBagLayout() ) {
            public void paint( Graphics g ) {
                if( g != null ) {
                    super.paint( g );
                    drawBorder( this, g );
                }
            }

            public void update( Graphics g ) {
                if( g != null ) {
                    paint( g );
                }
            }
        };
        add( panel );
        gbc.gridheight = 2;
        panel.add( imageComponent, gbc );
        gbc.gridx = 1;
        gbc.gridheight = 1;
        gbc.insets = new Insets( 20, 10, 10, 20 );
        panel.add( textComponent, gbc );
        gbc.gridy++;
        gbc.fill = GridBagConstraints.REMAINDER;
        gbc.insets = new Insets( 10, 10, 20, 20 );
        panel.add( animationComponent, gbc );
        panel.setBackground( new Color( 200, 240, 200 ) );  // light green );

        Thread t = new Thread( new Runnable() {
            public void run() {
                while( !done ) {
                    try {
                        Thread.sleep( 30 );
                        panel.invalidate();
                        panel.repaint();
                        panel.update( panel.getGraphics() );
                    }
                    catch( InterruptedException e ) {
                        e.printStackTrace();
                    }
                }
            }
        } );
        t.start();

        invalidate();
        pack();
        SwingUtils.centerWindowOnScreen( this );
    }

    public AWTSplashWindow( PhetFrame phetFrame, String title ) {
        this( getPhetImage(), title );
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
        int blockWidth = 20;
        int blockHeight = 14;

        public Dimension getPreferredSize() {
            return getMinimumSize();
        }

        public Dimension getMinimumSize() {
            return new Dimension( 250, blockHeight );
        }

        public void update( Graphics g ) {
            paint( g );
        }

        public void paint( Graphics g ) {
//            System.out.println( "getSize( ) = " + getSize() );
            if( g == null ) {
                return;
            }
//            System.out.println( "AWTSplashWindow$AnimationComponent.paint" );
            g.setColor( Color.lightGray );
            g.fillRect( 0, 0, getWidth(), getHeight() );
            g.setColor( Color.black );
//            double freqHZ = 0.5;
            double freqHZ = 0.25;
            double angularfrequency = freqHZ * 2 * Math.PI;
            int x = (int)( Math.sin( System.currentTimeMillis() / 1000.0 * angularfrequency ) * getWidth() / 2.0 + getWidth() / 2.0 );
            int a = x - blockWidth / 2;

            g.fillRect( a, 0, blockWidth, blockHeight );
            g.drawRect( 0, 0, getWidth() - 1, getHeight() - 1 );
        }
    }

    private static class ImageComponent extends Component {
        Image image;

        public ImageComponent( Image image ) {
            this.image = image;
        }

        public Dimension getMinimumSize() {
            return new Dimension( image.getWidth( this ), image.getHeight( this ) );
        }

        public Dimension getPreferredSize() {
            return getMinimumSize();
        }

        public void paint( Graphics g ) {
            super.paint( g );
            g.drawImage( image, 0, 0, null );
        }
    }

    public void hideSplash() {
        if( !done ) {
//            System.out.println( "AWTSplashScreen2.hideSplash" );
            super.setVisible( false );
            super.hide();
            super.dispose();
            owner.dispose();
            owner = null;
            done = true;
        }
    }

    private void showSplashScreen() {
        setVisible( true );
    }

    private static BufferedImage getPhetImage() {
        try {
            return ImageLoader.loadBufferedImage( "images/Phet-Flatirons-logo-3-small.gif" );
        }
        catch( IOException e ) {
            e.printStackTrace();
            return null;
        }
    }

    public static void main( String[] args ) throws InterruptedException, InvocationTargetException {
        SwingUtilities.invokeAndWait( new Runnable() {
            public void run() {
                try {
                    runTest();
                }
                catch( InterruptedException e ) {
                    e.printStackTrace();
                }
                catch( InvocationTargetException e ) {
                    e.printStackTrace();
                }
            }
        } );
//        runTest();
    }

    private static void runTest() throws InterruptedException, InvocationTargetException {
        AWTSplashWindow awtSplashWindow = new AWTSplashWindow( getPhetImage(), "Energy Skate Park" );
        awtSplashWindow.showSplashScreen();
        Thread.sleep( 1000 );
//        SwingUtilities.invokeAndWait( new Runnable() {
//            public void run() {
//                try {
        Thread.sleep( 5000 );
//                }
//                catch( InterruptedException e ) {
//                    e.printStackTrace();
//                }
//            }
//        } );
        Thread.sleep( 1000 );
        awtSplashWindow.hideSplash();
    }

}
