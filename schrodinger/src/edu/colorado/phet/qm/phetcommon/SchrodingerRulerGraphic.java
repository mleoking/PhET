/* Copyright 2004, Sam Reid */
package edu.colorado.phet.qm.phetcommon;

import edu.colorado.phet.common.view.util.ImageLoader;
import edu.colorado.phet.piccolo.CursorHandler;
import edu.colorado.phet.qm.SchrodingerLookAndFeel;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PDragEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolox.pswing.PSwing;
import edu.umd.cs.piccolox.pswing.PSwingCanvas;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

/**
 * User: Sam Reid
 * Date: Jun 23, 2005
 * Time: 6:35:56 PM
 * Copyright (c) Jun 23, 2005 by Sam Reid
 */

public class SchrodingerRulerGraphic extends PNode {
    public RulerGraphic rulerGraphic;
    boolean horizontal = true;
//    public BufferedImage horiz;
//    public BufferedImage vert;

    public SchrodingerRulerGraphic( PSwingCanvas component, RulerGraphic rulerGraphic ) {
        this.rulerGraphic = rulerGraphic;
//        horiz = BufferedImageUtils.toBufferedImage( imageGraphic.getImage() );
//        vert = BufferedImageUtils.getRotatedImage( horiz, 3 * Math.PI / 2 );
        addChild( this.rulerGraphic );

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
                super.setDraggedNode( SchrodingerRulerGraphic.this );
                super.drag( event );    //Slight hack to make controls drag with the ruler
            }
        } );
    }

    protected void layoutChildren() {
        super.layoutChildren();    //To change body of overridden methods use File | Settings | File Templates.
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
        this.horizontal = !horizontal;
        if( horizontal ) {
            rulerGraphic.setRotation( 0 );
            rulerGraphic.setOffset( 0, 0 );
        }
        else {
            rulerGraphic.setRotation( Math.PI / 2 );
            rulerGraphic.setOffset( rulerGraphic.getHeight(), 0 );
        }
//        if( imageGraphic.getImage() == horiz ) {
//            imageGraphic.setImage( vert );
//        }
//        else {
//            imageGraphic.setImage( horiz );
//        }
    }

    public void setUnits( String units ) {
        rulerGraphic.setUnits( units );
    }
}
