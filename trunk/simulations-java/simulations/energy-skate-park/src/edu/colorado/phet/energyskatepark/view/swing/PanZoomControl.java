// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.energyskatepark.view.swing;

import edu.colorado.phet.common.phetcommon.view.util.ImageLoader;
import edu.colorado.phet.energyskatepark.view.EnergySkateParkSimulationPanel;
import edu.umd.cs.piccolo.util.PDimension;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Point2D;
import java.io.IOException;

/**
 * User: Sam Reid
 * Date: Dec 1, 2006
 * Time: 12:35:00 PM
 */

public class PanZoomControl extends JPanel {
    private EnergySkateParkSimulationPanel energySkateParkSimulationPanel;
    private double zoomScale = 1.1;
    private int zoomOutCount = 0;
    private JButton zoomIn;
    private JButton zoomOut;

    public PanZoomControl( EnergySkateParkSimulationPanel energySkateParkSimulationPanel ) {
        this.energySkateParkSimulationPanel = energySkateParkSimulationPanel;
        setLayout( new BoxLayout( this, BoxLayout.Y_AXIS ) );
        try {
            zoomOut = new JButton( new ImageIcon( ImageLoader.loadBufferedImage( "energy-skate-park/images/icons/glass-20-minus.gif" ) ) );
            zoomOut.addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    zoomOutOnce();
                }
            } );
            add( zoomOut );
            zoomIn = new JButton( new ImageIcon( ImageLoader.loadBufferedImage( "energy-skate-park/images/icons/glass-20-plus.gif" ) ) );
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
        //preserve fraction of screen ground takes up
        double fractionToGround = getScreenFractionToGround();
//        double fraction = 0.1;//or use fixed fraction

        Point2D point = energySkateParkSimulationPanel.getCamera().getBounds().getCenter2D();
        energySkateParkSimulationPanel.getCamera().localToGlobal( point );
        energySkateParkSimulationPanel.getPhetRootNode().globalToWorld( point );
        energySkateParkSimulationPanel.getPhetRootNode().scaleWorldAboutPoint( zoomScale, point );

        //translate vertically so the ground is a fixed fraction of the way up the screen
        Point2D origin = new Point2D.Double( 0, 0 );
        energySkateParkSimulationPanel.getPhetRootNode().worldToScreen( origin );

        //desired screen y value
        double desiredScreenY = energySkateParkSimulationPanel.getHeight() * ( 1.0 - fractionToGround );
        double screenDY = origin.getY() - desiredScreenY;
        translateWorld( screenDY );

        energySkateParkSimulationPanel.fireZoomEvent();
        zoomIn.setEnabled( zoomOutCount > 0 );
    }

    private void translateWorld( double screenDY ) {
        PDimension dy = new PDimension( 0, screenDY );
        energySkateParkSimulationPanel.getPhetRootNode().screenToWorld( dy );
        energySkateParkSimulationPanel.getPhetRootNode().translateWorld( dy.width, -dy.height );
    }

    private double getScreenFractionToGround() {
        Point2D origin = new Point2D.Double( 0, 0 );
        energySkateParkSimulationPanel.getPhetRootNode().worldToScreen( origin );
        return 1.0 - origin.getY() / energySkateParkSimulationPanel.getHeight();
    }

    public void reset() {
        while( zoomOutCount > 0 ) {
            zoomInOnce();
        }
    }

    public void updateScale() {
        for( int i = 0; i < zoomOutCount; i++ ) {
            zoom( 1.0 / zoomScale );
        }
    }
}
