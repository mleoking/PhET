package edu.colorado.phet.workenergy.test;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Ellipse2D;

import javax.swing.*;

import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;

/**
 * @author Sam Reid
 */
public class TestSmoothAnimation {
    public static void main( String[] args ) {
        SwingUtilities.invokeLater( new Runnable() {
            public void run() {
                new JFrame() {{
                    setContentPane( new PhetPCanvas() {{
                        addScreenChild( new PhetPPath( new Ellipse2D.Double( 0, 0, 100, 100 ), Color.blue, new BasicStroke( 2 ), Color.black ) {{
                            new Timer( 30, new ActionListener() {
                                double velocity = 4;

                                public void actionPerformed( ActionEvent e ) {
                                    translate( velocity, 0 );
                                    if ( getFullBoundsReference().getCenterX() > 800 ) {
                                        velocity = -Math.abs( velocity );
                                    }
                                    else if ( getFullBoundsReference().getCenterX() < 0 ) {
                                        velocity = Math.abs( velocity );
                                    }
                                }
                            } ).start();
                        }} );
                    }} );
                    setSize( 800, 600 );
                    setDefaultCloseOperation( EXIT_ON_CLOSE );
                }}.setVisible( true );
            }
        } );
    }
}
