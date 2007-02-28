/* Copyright 2004, Sam Reid */
package edu.colorado.phet.forces1d.tests;

import edu.colorado.phet.common.view.ApparatusPanel;
import edu.colorado.phet.common.view.BasicGraphicsSetup;
import edu.colorado.phet.common.view.graphics.mousecontrols.TranslationEvent;
import edu.colorado.phet.common.view.graphics.mousecontrols.TranslationListener;
import edu.colorado.phet.common.view.phetcomponents.AffineTransformBuilder;
import edu.colorado.phet.common.view.phetgraphics.PhetImageGraphic;

import javax.swing.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

/**
 * User: Sam Reid
 * Date: Dec 15, 2004
 * Time: 9:11:16 AM
 * Copyright (c) Dec 15, 2004 by Sam Reid
 */

public class TestTransformBuilder {
    public static void main( String[] args ) {
        ApparatusPanel panel = new ApparatusPanel();
        panel.addGraphicsSetup( new BasicGraphicsSetup() );
        final PhetImageGraphic graphic = new PhetImageGraphic( panel, "images/ollie.gif" );
        panel.addGraphic( graphic );
        JFrame frame = new JFrame();
        frame.setContentPane( panel );
        frame.setSize( 600, 600 );
        frame.setVisible( true );
        frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
        final AffineTransformBuilder builder = new AffineTransformBuilder( graphic.getBounds() );
        graphic.addTranslationListener( new TranslationListener() {
            public void translationOccurred( TranslationEvent translationEvent ) {
//                builder.setLocation( translationEvent.getX(), translationEvent.getY() );
                if( SwingUtilities.isLeftMouseButton( translationEvent.getMouseEvent() ) ) {
                    builder.translate( translationEvent.getDx(), translationEvent.getDy() );
                    graphic.setTransform( builder.toAffineTransform() );
                }
                else {
                    builder.rotate( Math.PI / 32 );
                    graphic.setTransform( builder.toAffineTransform() );
                }
            }
        } );

        graphic.setCursorHand();
        panel.addKeyListener( new KeyListener() {
            public void keyTyped( KeyEvent e ) {
            }

            public void keyPressed( KeyEvent e ) {
            }

            public void keyReleased( KeyEvent e ) {
                if( e.getKeyCode() == KeyEvent.VK_RIGHT ) {
                    builder.scale( 1.1, 1.1 );
                    graphic.setTransform( builder.toAffineTransform() );
                }
                if( e.getKeyCode() == KeyEvent.VK_LEFT ) {
                    builder.scale( 1 / 1.1, 1 / 1.1 );
                    graphic.setTransform( builder.toAffineTransform() );
                }
                if( e.getKeyCode() == KeyEvent.VK_X ) {
                    builder.scale( -1, 1 );
                    graphic.setTransform( builder.toAffineTransform() );
                }
            }
        } );
        panel.requestFocus();
    }
}
