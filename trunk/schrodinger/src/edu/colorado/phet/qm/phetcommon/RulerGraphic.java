/* Copyright 2004, Sam Reid */
package edu.colorado.phet.qm.phetcommon;

import edu.colorado.phet.common.view.graphics.mousecontrols.TranslationEvent;
import edu.colorado.phet.common.view.graphics.mousecontrols.TranslationListener;
import edu.colorado.phet.common.view.phetcomponents.PhetJComponent;
import edu.colorado.phet.common.view.phetgraphics.GraphicLayerSet;
import edu.colorado.phet.common.view.phetgraphics.PhetGraphic;
import edu.colorado.phet.common.view.phetgraphics.PhetImageGraphic;
import edu.colorado.phet.common.view.util.BufferedImageUtils;
import edu.colorado.phet.common.view.util.ImageLoader;

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

public class RulerGraphic extends GraphicLayerSet {
    public PhetImageGraphic phetImageGraphic;
    public BufferedImage horiz;
    public BufferedImage vert;

    public RulerGraphic( Component component ) {
        super( component );
        phetImageGraphic = new PhetImageGraphic( component, "images/10-nanometer-stick.png" );
        horiz = phetImageGraphic.getImage();
        vert = BufferedImageUtils.getRotatedImage( horiz, 3 * Math.PI / 2 );
        addGraphic( phetImageGraphic );

        JButton rotate = createRotateButton();
        PhetGraphic rotateButton = PhetJComponent.newInstance( component, rotate );
        addGraphic( rotateButton );
        rotateButton.setCursorHand();
        rotateButton.setLocation( -2 - rotateButton.getWidth(), 0 );

        JButton closeButton = createCloseButton();
        PhetGraphic closeGraphic = PhetJComponent.newInstance( component, closeButton );
        addGraphic( closeGraphic );
        closeGraphic.setCursorHand();
        closeGraphic.setLocation( rotateButton.getX(), rotateButton.getY() + rotateButton.getHeight() + 2 );

        setCursorHand();
        addTranslationListener( new TranslationListener() {
            public void translationOccurred( TranslationEvent translationEvent ) {
                translate( translationEvent.getDx(), translationEvent.getDy() );
            }
        } );

    }

    private JButton createCloseButton() {
        JButton rotate = null;
        try {
            rotate = new JButton( new ImageIcon( ImageLoader.loadBufferedImage( "images/x-14.jpg" ) ) );
            rotate.setMargin( new Insets( 1, 1, 1, 1 ) );
        }
        catch( IOException e ) {
            e.printStackTrace();
        }
        rotate.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                close();
            }
        } );
        return rotate;
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
