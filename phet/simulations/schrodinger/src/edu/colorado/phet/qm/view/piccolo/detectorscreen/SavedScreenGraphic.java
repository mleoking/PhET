/* Copyright 2004, Sam Reid */
package edu.colorado.phet.qm.view.piccolo.detectorscreen;

import edu.colorado.phet.common.view.util.ImageLoader;
import edu.colorado.phet.piccolo.event.CursorHandler;
import edu.colorado.phet.qm.view.QWIPanel;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.nodes.PImage;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolox.pswing.PSwing;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.IOException;

/**
 * User: Sam Reid
 * Date: Jul 8, 2005
 * Time: 6:47:28 PM
 * Copyright (c) Jul 8, 2005 by Sam Reid
 */

public class SavedScreenGraphic extends PNode {
    private QWIPanel QWIPanel;
    private BufferedImage image;
    private Insets m = new Insets( 2, 2, 2, 2 );

    public SavedScreenGraphic( final QWIPanel QWIPanel, BufferedImage image ) {
        this.QWIPanel = QWIPanel;
        this.image = image;
        PPath border = new PPath( new Rectangle2D.Double( 0, 0, image.getWidth(), image.getHeight() ) );
        border.setStroke( new BasicStroke( 2 ) );
        border.setStrokePaint( Color.lightGray );

        PImage imageGraphic = new PImage( image );
        addChild( imageGraphic );
        addChild( border );
        addInputEventListener( new PBasicInputEventHandler() {
            public void mouseDragged( PInputEvent event ) {
                super.mouseDragged( event );
                translate( event.getDelta().width, event.getDelta().height );
            }
        } );
        addInputEventListener( new CursorHandler( Cursor.HAND_CURSOR ) );

        try {
            BufferedImage closeImage = ImageLoader.loadBufferedImage( "images/x-14.jpg" );
            JButton closeButton = new JButton( new ImageIcon( closeImage ) );
            closeButton.setMargin( m );
            PSwing button = new PSwing( QWIPanel, closeButton );
            addChild( button );
            button.setOffset( -button.getWidth() - 2, 0 );
            closeButton.addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    QWIPanel.getSchrodingerScreenNode().removeSavedScreenGraphic( SavedScreenGraphic.this );
                }
            } );
        }
        catch( IOException e ) {
            e.printStackTrace();
        }
    }
}
