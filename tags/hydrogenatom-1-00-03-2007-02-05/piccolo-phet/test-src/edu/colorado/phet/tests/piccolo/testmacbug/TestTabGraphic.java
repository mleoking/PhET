package edu.colorado.phet.tests.piccolo.testmacbug;

import edu.umd.cs.piccolo.PCanvas;
import edu.umd.cs.piccolo.nodes.PPath;

import javax.swing.*;
import java.awt.*;

/**
 * Created by IntelliJ IDEA.
 * User: reids
 * Date: Mar 9, 2006
 * Time: 5:21:00 PM
 * To change this template use File | Settings | File Templates.
 */
public class TestTabGraphic {
    public static void main( String[] args ) {
        JFrame frame = new JFrame();
        PCanvas pCanvas = new PCanvas();

        PPath tabNode = new PPath( new Rectangle( 20, 20 ) );
        tabNode.setPaint( new GradientPaint( 0, 0, Color.blue, 0, 10, Color.blue ) );

        pCanvas.getLayer().addChild( tabNode );
        frame.setContentPane( pCanvas );
        frame.setSize( 200, 200 );
        frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );

        frame.setVisible( true );
    }

}
