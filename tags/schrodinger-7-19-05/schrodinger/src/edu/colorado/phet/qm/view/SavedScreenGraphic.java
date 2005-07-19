/* Copyright 2004, Sam Reid */
package edu.colorado.phet.qm.view;

import edu.colorado.phet.common.view.graphics.mousecontrols.TranslationEvent;
import edu.colorado.phet.common.view.graphics.mousecontrols.TranslationListener;
import edu.colorado.phet.common.view.phetcomponents.PhetJComponent;
import edu.colorado.phet.common.view.phetgraphics.GraphicLayerSet;
import edu.colorado.phet.common.view.phetgraphics.PhetGraphic;
import edu.colorado.phet.common.view.phetgraphics.PhetImageGraphic;
import edu.colorado.phet.common.view.util.ImageLoader;

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

public class SavedScreenGraphic extends GraphicLayerSet {
    private SchrodingerPanel schrodingerPanel;
    private BufferedImage image;
    private Insets m = new Insets( 2, 2, 2, 2 );

    public SavedScreenGraphic( final SchrodingerPanel schrodingerPanel, BufferedImage image ) {
        this.schrodingerPanel = schrodingerPanel;
        this.image = image;
        PhetImageGraphic imageGraphic = new PhetImageGraphic( schrodingerPanel, image );
        addGraphic( imageGraphic );
        addTranslationListener( new TranslationListener() {
            public void translationOccurred( TranslationEvent translationEvent ) {
                translate( translationEvent.getDx(), translationEvent.getDy() );
            }
        } );
        setCursorHand();

        try {
            BufferedImage closeImage = ImageLoader.loadBufferedImage( "images/x-14.jpg" );
            JButton closeButton = new JButton( new ImageIcon( closeImage ) );
            closeButton.setMargin( m );
            PhetGraphic button = PhetJComponent.newInstance( schrodingerPanel, closeButton );
            addGraphic( button );
            button.setLocation( -button.getWidth() - 2, 0 );
            closeButton.addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    schrodingerPanel.removeGraphic( SavedScreenGraphic.this );
                }
            } );
        }
        catch( IOException e ) {
            e.printStackTrace();
        }
    }
}
