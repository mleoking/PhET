/* Copyright 2004, Sam Reid */
package edu.colorado.phet.qm.phetcommon;

import edu.colorado.phet.common.view.util.BufferedImageUtils;
import edu.colorado.phet.common.view.util.ImageLoader;
import edu.colorado.phet.piccolo.event.CursorHandler;
import edu.colorado.phet.piccolo.util.PImageFactory;
import edu.colorado.phet.qm.QWILookAndFeel;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PDragEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.nodes.PImage;
import edu.umd.cs.piccolox.pswing.PSwing;
import edu.umd.cs.piccolox.pswing.PSwingCanvas;

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

public class RulerImageGraphic extends PNode {
    public PImage imageGraphic;
    public BufferedImage horiz;
    public BufferedImage vert;

    public RulerImageGraphic( PSwingCanvas component ) {
        imageGraphic = PImageFactory.create( "images/10-nanometer-stick.png" );
        horiz = BufferedImageUtils.toBufferedImage( imageGraphic.getImage() );
        vert = BufferedImageUtils.getRotatedImage( horiz, 3 * Math.PI / 2 );
        addChild( imageGraphic );

        JButton rotate = createRotateButton();
        PSwing rotateButton = new PSwing( component, rotate );
        addChild( rotateButton );
        rotateButton.addInputEventListener( new CursorHandler( Cursor.HAND_CURSOR ) );
        rotateButton.setOffset( -2 - rotateButton.getWidth(), 0 );

        JButton closeButton = createCloseButton();
        PSwing closeGraphic = new PSwing( component, closeButton );
        addChild( closeGraphic );
        closeGraphic.addInputEventListener( new CursorHandler( Cursor.HAND_CURSOR ) );
        closeGraphic.setOffset( rotateButton.getX(), rotateButton.getY() - rotateButton.getHeight() - 2 );

        addInputEventListener( new CursorHandler( Cursor.HAND_CURSOR ) );
        addInputEventListener( new PDragEventHandler() {
            protected void drag( PInputEvent event ) {
                super.setDraggedNode( RulerImageGraphic.this );
                super.drag( event );    //Slight hack to make controls drag with the ruler
            }
        } );
    }

    protected void layoutChildren() {
        super.layoutChildren();    //To change body of overridden methods use File | Settings | File Templates.
    }

    private JButton createCloseButton() {
        JButton closeButton = QWILookAndFeel.createCloseButton();
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
        if( imageGraphic.getImage() == horiz ) {
            imageGraphic.setImage( vert );
        }
        else {
            imageGraphic.setImage( horiz );
        }
    }
}
