/** Sam Reid*/
package edu.colorado.phet.common_cck.tests.basicgraphics;

import edu.colorado.phet.common_cck.view.ApparatusPanel;
import edu.colorado.phet.common_cck.view.basicgraphics.BasicMultiLineTextGraphic;
import edu.colorado.phet.common_cck.view.basicgraphics.RenderedGraphic;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

/**
 * User: Sam Reid
 * Date: Sep 10, 2004
 * Time: 8:03:51 AM
 * Copyright (c) Sep 10, 2004 by Sam Reid
 */
public class TestFonts {
    private JFrame frame;
    private ApparatusPanel panel;
    private Thread thread;
    int x = 0;
    int y = 50;
    private RenderedGraphic renderedGraphic;

    public TestFonts() {
        frame = new JFrame();
        panel = new ApparatusPanel() {
            public void paintComponent( Graphics graphics ) {
                Graphics2D g2 = (Graphics2D)graphics;
                g2.setRenderingHint( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON );
                super.paintComponent( graphics );
            }
        };
        frame.setContentPane( panel );
        frame.setSize( 400, 400 );
        frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );

        Font font = new Font( "Lucida Sans", 0, 54 );
        FontMetrics fontMetrics = panel.getFontMetrics( font );
        final BasicMultiLineTextGraphic bmltg = new BasicMultiLineTextGraphic( new String[]{"multi-line", "Text graphic is working!!"},
                                                                               font, fontMetrics, 50, 50, Color.red, 2, 2, Color.black );
        renderedGraphic = new RenderedGraphic( bmltg, panel );
        panel.addGraphic( renderedGraphic, 0 );

        //or just do the following...
//        RenderedGraphic hg = new RenderedGraphic( shapeGraphic, panel );//this should be automated in ApparatusPanel.add(Lightweight...)
//        panel.addGraphic( hg, 0 );


        thread = new Thread( new Runnable() {
            public void run() {
                while( true ) {
                    try {
                        Thread.sleep( 30 );
                        if( bmltg.getBounds().x > panel.getWidth() ) {
                            x = -bmltg.getBounds().width;
                        }
                        x += 3;
//                        if( x > 100 ) {
//                            renderedGraphic.setVisible( false );
//                        }
                        bmltg.setPosition( x, y );
                    }
                    catch( InterruptedException e ) {
                        e.printStackTrace();
                    }
                }
            }
        } );
        panel.addKeyListener( new KeyListener() {
            public void keyTyped( KeyEvent e ) {
                if( e.getKeyChar() == 's' ) {
                    renderedGraphic.setVisible( true );
                }
                else if( e.getKeyChar() == 'h' ) {
                    renderedGraphic.setVisible( false );
                }
            }

            public void keyPressed( KeyEvent e ) {
            }

            public void keyReleased( KeyEvent e ) {
            }
        } );
    }

    public static void main( String[] args ) {
        new TestFonts().start();
    }

    private void start() {
        frame.setVisible( true );
        panel.repaint();
        thread.start();
        panel.requestFocus();
    }
}
