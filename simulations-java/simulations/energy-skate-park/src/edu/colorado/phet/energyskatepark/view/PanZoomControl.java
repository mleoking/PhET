package edu.colorado.phet.energyskatepark.view;

import edu.colorado.phet.common.phetcommon.view.util.ImageLoader;
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
    private EnergySkateParkSimulationPanel simulationPanel;
    private double zoomScale = 1.1;
    private int zoomOutCount = 0;
    private JButton zoomIn;
    private JButton zoomOut;

    public PanZoomControl( EnergySkateParkSimulationPanel phetPCanvas ) {
        this.simulationPanel = phetPCanvas;
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
//        phetPCanvas.addComponentListener( new ComponentAdapter() {
//            public void componentResized( ComponentEvent e ) {
//                updateZoom();
//            }
//        } );
    }

//    private void updateZoom() {
//        Point2D point = simulationPanel.getCamera().getBounds().getCenter2D();
//        simulationPanel.getCamera().localToGlobal( point );
//        simulationPanel.getPhetRootNode().globalToWorld( point );
//        simulationPanel.getPhetRootNode().scaleWorldAboutPoint( zoomScale, point );
//        simulationPanel.fireZoomEvent();
//        zoomIn.setEnabled( zoomOutCount > 0 );
//    }

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

        Point2D point = simulationPanel.getCamera().getBounds().getCenter2D();
        simulationPanel.getCamera().localToGlobal( point );
        simulationPanel.getPhetRootNode().globalToWorld( point );
        simulationPanel.getPhetRootNode().scaleWorldAboutPoint( zoomScale, point );

        //translate vertically so the ground is a fixed fraction of the way up the screen
        Point2D origin = new Point2D.Double( 0, 0 );
        simulationPanel.getPhetRootNode().worldToScreen( origin );

        //or use fixed fraction
//        double fraction = 0.1;
        //desired screen y value
        double desiredScreenY = simulationPanel.getHeight() * ( 1.0 - fractionToGround );
        double screenDY = origin.getY() - desiredScreenY;
        translateWorld( screenDY );

        simulationPanel.fireZoomEvent();
        zoomIn.setEnabled( zoomOutCount > 0 );
    }

    private void translateWorld( double screenDY ) {
        PDimension dy = new PDimension( 0, screenDY );
        simulationPanel.getPhetRootNode().screenToWorld( dy );
        simulationPanel.getPhetRootNode().translateWorld( dy.width, -dy.height );
    }

    private double getScreenFractionToGround() {
        Point2D origin = new Point2D.Double( 0, 0 );
        simulationPanel.getPhetRootNode().worldToScreen( origin );
        return 1.0 - origin.getY() / simulationPanel.getHeight();
//        return 0;
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
