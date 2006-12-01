package edu.colorado.phet.ec3;

import edu.colorado.phet.common.view.util.ImageLoader;
import edu.colorado.phet.piccolo.PhetPCanvas;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Point2D;
import java.io.IOException;

/**
 * User: Sam Reid
 * Date: Dec 1, 2006
 * Time: 12:35:00 PM
 * Copyright (c) Dec 1, 2006 by Sam Reid
 */

public class PanZoomControl extends JPanel {
    private PhetPCanvas phetPCanvas;
    private double zoomScale = 1.1;
    int zoomOutCount = 0;
    private JButton zoomIn;
    private JButton zoomOut;

    public PanZoomControl( PhetPCanvas phetPCanvas ) {
        this.phetPCanvas = phetPCanvas;
        setLayout( new BoxLayout( this, BoxLayout.Y_AXIS ) );
        try {
            zoomOut = new JButton( new ImageIcon( ImageLoader.loadBufferedImage( "images/icons/glass-20-minus.gif" ) ) );
            zoomOut.addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    zoomOutOnce();
                }
            } );
            add( zoomOut );
            zoomIn = new JButton( new ImageIcon( ImageLoader.loadBufferedImage( "images/icons/glass-20-plus.gif" ) ) );
            zoomIn.addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    zoomInOnce();
                }
            } );
            add( zoomIn );
        }
        catch( IOException e ) {
            e.printStackTrace();
        }
        zoomIn.setEnabled( false );
        setOpaque( false );
    }

    private void zoomInOnce() {
        zoomOutCount--;
        zoom( zoomScale );
    }

    private void zoomOutOnce() {
        zoomOutCount++;
        zoom( 1.0 / zoomScale );
    }

    private void zoom( double zoomScale ) {
        //todo zooming is disabled
        Point2D point = phetPCanvas.getCamera().getBounds().getCenter2D();
        phetPCanvas.getCamera().localToGlobal( point );
        phetPCanvas.getPhetRootNode().globalToWorld( point );
        phetPCanvas.getPhetRootNode().scaleWorldAboutPoint( zoomScale, point );

        zoomIn.setEnabled( zoomOutCount > 0 );
    }

    public void reset() {
        while( zoomOutCount > 0 ) {
            zoomInOnce();
        }
    }
}
