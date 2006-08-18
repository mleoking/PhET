/**
 * Class: TestBufferedAP
 * Package: edu.colorado.phet.common.examples.fastpaint
 * Author: Another Guy
 * Date: May 21, 2004
 */
package edu.colorado.phet.common.examples.fastpaint;

import edu.colorado.phet.common.view.ApparatusPanel;
import edu.colorado.phet.common.view.graphics.Graphic;
import edu.colorado.phet.common.view.graphics.ShapeGraphic;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

public class TestBufferedAP {
    public static class BufferedApparatusPanel extends ApparatusPanel {
        BufferedImage buffer;

        public BufferedApparatusPanel() {
            addComponentListener( new ComponentAdapter() {
                public void componentResized( ComponentEvent e ) {
                    init();
                }
            } );
            init();
        }

        public void repaint() {
            super.repaint();
        }

        void init() {
            buffer = (BufferedImage)createImage( getWidth(), getHeight() );
        }

        protected void paintComponent( Graphics graphics ) {
            Graphics2D bufferGraphics = buffer.createGraphics();
            super.paintComponent( bufferGraphics );
            bufferGraphics.drawRenderedImage( buffer, new AffineTransform() );
            bufferGraphics.dispose();
        }
    }

    public static void main( String[] args ) {
        BufferedApparatusPanel bap = new BufferedApparatusPanel();
        Graphic g = new ShapeGraphic( new Rectangle( 20, 30, 40, 50 ), Color.blue );
        bap.addGraphic( g );
        JFrame jf = new JFrame( "Test Buffered AP" );
        jf.setSize( 600, 600 );

        jf.setContentPane( bap );

        jf.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
        jf.setVisible( true );
    }
}
