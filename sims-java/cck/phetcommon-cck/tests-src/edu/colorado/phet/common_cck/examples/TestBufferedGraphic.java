/** Sam Reid*/
package edu.colorado.phet.common_cck.examples;

import edu.colorado.phet.common_cck.view.ApparatusPanel;
import edu.colorado.phet.common_cck.view.BasicGraphicsSetup;
import edu.colorado.phet.common_cck.view.GraphicsSetup;
import edu.colorado.phet.common_cck.view.graphics.BufferedGraphic;
import edu.colorado.phet.common_cck.view.graphics.ShapeGraphic;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;

/**
 * User: Sam Reid
 * Date: May 26, 2004
 * Time: 11:50:17 AM
 * Copyright (c) May 26, 2004 by Sam Reid
 */
public class TestBufferedGraphic {
    public static void main( String[] args ) {
        ApparatusPanel ap = new ApparatusPanel();
        Ellipse2D.Double ellipse = new Ellipse2D.Double( 10, 10, 40, 40 );
        ShapeGraphic sg = new ShapeGraphic( ellipse, Color.blue );
        BufferedImage buffer = new BufferedImage( 50, 50, BufferedImage.TYPE_INT_RGB );
        GraphicsSetup setup = new BasicGraphicsSetup();
        BufferedGraphic bufferedGraphic = new BufferedGraphic( buffer, sg, Color.white, setup );
        bufferedGraphic.repaintBuffer();
        ap.addGraphic( bufferedGraphic );
        JFrame jf = new JFrame();
        jf.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
        jf.setContentPane( ap );
        jf.setSize( 400, 400 );
        jf.setVisible( true );
    }
}
