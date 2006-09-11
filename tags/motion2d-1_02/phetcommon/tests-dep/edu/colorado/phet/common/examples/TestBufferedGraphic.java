/** Sam Reid*/
package edu.colorado.phet.common.examples;

import edu.colorado.phet.common.view.ApparatusPanel;
import edu.colorado.phet.common.view.BasicGraphicsSetup;
import edu.colorado.phet.common.view.GraphicsSetup;
import edu.colorado.phet.common.view.graphics.BufferedGraphic;
import edu.colorado.phet.common.view.phetgraphics.PhetBufferedGraphic;
import edu.colorado.phet.common.view.phetgraphics.PhetShapeGraphic;

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
        PhetShapeGraphic sg = new PhetShapeGraphic( ap, ellipse, Color.blue );
        BufferedImage buffer = new BufferedImage( 50, 50, BufferedImage.TYPE_INT_RGB );
        GraphicsSetup setup = new BasicGraphicsSetup();
        PhetBufferedGraphic bufferedGraphic = new PhetBufferedGraphic( ap, 200, 200, sg );
        bufferedGraphic.repaintBuffer();
        ap.addGraphic( bufferedGraphic );
        JFrame jf = new JFrame();
        jf.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
        jf.setContentPane( ap );
        jf.setSize( 400, 400 );
        jf.setVisible( true );
    }
}
