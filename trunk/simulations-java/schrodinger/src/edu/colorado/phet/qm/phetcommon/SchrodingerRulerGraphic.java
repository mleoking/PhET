/* Copyright 2004, Sam Reid */
package edu.colorado.phet.qm.phetcommon;

import edu.colorado.phet.common.view.util.ImageLoader;
import edu.colorado.phet.piccolo.PhetPNode;
import edu.colorado.phet.piccolo.event.CursorHandler;
import edu.colorado.phet.qm.QWILookAndFeel;
import edu.colorado.phet.qm.model.QWIModel;
import edu.umd.cs.piccolox.pswing.PSwing;
import edu.umd.cs.piccolox.pswing.PSwingCanvas;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.geom.Rectangle2D;
import java.io.IOException;

/**
 * User: Sam Reid
 * Date: Jun 23, 2005
 * Time: 6:35:56 PM
 * Copyright (c) Jun 23, 2005 by Sam Reid
 */

public class SchrodingerRulerGraphic extends PhetPNode {
    //public class SchrodingerRulerGraphic extends PComposite {
    private QWIModel QWIModel;
    public RulerGraphic rulerGraphic;
    boolean horizontal = true;
    //    private PPath dragBounds;
    private PSwingCanvas component;
//    public BufferedImage horiz;
//    public BufferedImage vert;

    public SchrodingerRulerGraphic( QWIModel QWIModel, final PSwingCanvas component, final RulerGraphic rulerGraphic ) {
        this.component = component;
        this.QWIModel = QWIModel;
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
        addInputEventListener( new HalfOnscreenDragHandler( component, this ) );
        component.addComponentListener( new ComponentAdapter() {
            public void componentResized( ComponentEvent e ) {
                rotate();
                rotate();
            }
        } );
        component.addComponentListener( new ComponentAdapter() {
            public void componentResized( ComponentEvent e ) {
                Rectangle2D bounds = rulerGraphic.getGlobalFullBounds();
                Rectangle2D screen = new Rectangle2D.Double( 0, 0, component.getWidth(), component.getHeight() );
                if( !screen.intersects( bounds ) ) {
//                    System.out.println( "Moved ruler manually." );
                    setOffset( 100, 100 );
                }
            }
        } );
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
        this.horizontal = !horizontal;
        if( horizontal ) {
            rulerGraphic.setRotation( 0 );
            rulerGraphic.setOffset( 0, 0 );
        }
        else {
            rulerGraphic.setRotation( -Math.PI / 2 );
//            rulerGraphic.setOffset( rulerGraphic.getHeight(), 0 );
            rulerGraphic.setOffset( 0, rulerGraphic.getWidth() );
        }
    }

    public void setUnits( String units ) {
        rulerGraphic.setUnitsText( units );
    }

    public RulerGraphic getRulerGraphic() {
        return rulerGraphic;
    }
}
