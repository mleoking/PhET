/* Copyright 2004, Sam Reid */
package edu.colorado.phet.qm.phetcommon;

import edu.colorado.phet.common.view.util.ImageLoader;
import edu.colorado.phet.qm.SchrodingerLookAndFeel;
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
 * Date: Jun 23, 2005
 * Time: 6:35:56 PM
 * Copyright (c) Jun 23, 2005 by Sam Reid
 */

public class RulerGraphic extends PNode {
    public PImage phetImageGraphic;
    public BufferedImage horiz;
    public BufferedImage vert;

    public RulerGraphic( Component component ) {
//        super( component );

        //todo piccolo
//        phetImageGraphic = new PImage("images/10-nanometer-stick.png" );
//        horiz = phetImageGraphic.getImage();
//        vert = BufferedImageUtils.getRotatedImage( horiz, 3 * Math.PI / 2 );
//        addChild( phetImageGraphic );
//
//        JButton rotate = createRotateButton();
//        PhetGraphic rotateButton = PhetJComponent.newInstance( component, rotate );
//        addChild( rotateButton );
//        rotateButton.setCursorHand();
//        rotateButton.setLocation( -2 - rotateButton.getWidth(), 0 );
//
//        JButton closeButton = createCloseButton();
//        PhetGraphic closeGraphic = PhetJComponent.newInstance( component, closeButton );
//        addChild( closeGraphic );
//        closeGraphic.setCursorHand();
//        closeGraphic.setLocation( rotateButton.getX(), rotateButton.getY() + rotateButton.getHeight() + 2 );
//
//        setCursorHand();
//        addTranslationListener( new TranslationListener() {
//            public void translationOccurred( TranslationEvent translationEvent ) {
//                translate( translationEvent.getDx(), translationEvent.getDy() );
//            }
//        } );

    }

    private JButton createCloseButton() {
        JButton closeButton = SchrodingerLookAndFeel.createCloseButton();
        closeButton.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                close();
            }
        } );
        return closeButton;
    }

    private void close() {
        setVisible( false );
    }

    private JButton createRotateButton() {
        JButton rotate = null;
        try {
            rotate = new JButton( new ImageIcon( ImageLoader.loadBufferedImage( "images/rot.jpg" ) ) );
            rotate.setMargin( new Insets( 1, 1, 1, 1 ) );
        }
        catch( IOException e ) {
            e.printStackTrace();
        }
        rotate.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                rotate();
            }
        } );
        return rotate;
    }

    private void rotate() {
        if( phetImageGraphic.getImage() == horiz ) {
            phetImageGraphic.setImage( vert );
        }
        else {
            phetImageGraphic.setImage( horiz );
        }
    }
}
