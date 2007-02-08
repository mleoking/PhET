package edu.colorado.phet.tests.piccolo.testmacbug;

import javax.swing.*;
import java.awt.*;

/**
 * Created by IntelliJ IDEA.
 * User: reids
 * Date: Mar 9, 2006
 * Time: 4:51:44 PM
 * To change this template use File | Settings | File Templates.
 */
public class TestGradientPaint {
    public static void main( String[] args ) {
        final GradientPaint gradientPaint = new GradientPaint( 0, 0, Color.blue, 0, 10, Color.blue );
        JFrame frame = new JFrame();
        frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
        final Rectangle rectangle = new Rectangle( 20, 20 );
        frame.setContentPane( new JPanel() {
            protected void paintComponent( Graphics graphics ) {
                super.paintComponent( graphics );    //To change body of overridden methods use File | Settings | File Templates.
                Graphics2D g2 = (Graphics2D)graphics;
                g2.setRenderingHint( RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY );
                g2.setPaint( gradientPaint );
                g2.fill( rectangle );
            }
        } );
        frame.setSize( 200, 200 );
        frame.setVisible( true );
    }

}
