/* Copyright 2004, Sam Reid */
package edu.colorado.phet.qm.view;

import edu.colorado.phet.common.view.util.ImageLoader;
import edu.colorado.phet.piccolo.pswing.PSwing;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PImage;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.IOException;

/**
 * User: Sam Reid
 * Date: Jul 8, 2005
 * Time: 6:47:28 PM
 * Copyright (c) Jul 8, 2005 by Sam Reid
 */

public class SavedScreenGraphic extends PNode {
    private SchrodingerPanel schrodingerPanel;
    private BufferedImage image;
    private Insets m = new Insets( 2, 2, 2, 2 );

    public SavedScreenGraphic( final SchrodingerPanel schrodingerPanel, BufferedImage image ) {
        this.schrodingerPanel = schrodingerPanel;
        this.image = image;
        PImage imageGraphic = new PImage( image );
        addChild( imageGraphic );
        //todo piccolo
//        addTranslationListener( new TranslationListener() {
//            public void translationOccurred( TranslationEvent translationEvent ) {
//                translate( translationEvent.getDx(), translationEvent.getDy() );
//            }
//        } );
//        setCursorHand();

        try {
            BufferedImage closeImage = ImageLoader.loadBufferedImage( "images/x-14.jpg" );
            JButton closeButton = new JButton( new ImageIcon( closeImage ) );
            closeButton.setMargin( m );
            PSwing button = new PSwing( schrodingerPanel, closeButton );
            addChild( button );
            button.setOffset( -button.getWidth() - 2, 0 );
            closeButton.addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    schrodingerPanel.removeWorldChild( SavedScreenGraphic.this );
                }
            } );
        }
        catch( IOException e ) {
            e.printStackTrace();
        }
    }
}
