// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.common.piccolophet.test;

/**
 * User: Sam Reid
 * Date: Mar 8, 2007
 * Time: 4:59:18 PM
 *
 */

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.geom.Line2D;
import java.util.Random;

import javax.swing.JFrame;

import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.common.piccolophet.nodes.BufferedPNode;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PPanEventHandler;
import edu.umd.cs.piccolo.event.PZoomEventHandler;

public class TestBufferedPNode {
    private JFrame frame;
    private PhetPCanvas phetPCanvas;

    public TestBufferedPNode() {
        frame = new JFrame();
        frame.setSize( 600, 600 );
        frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );

        phetPCanvas = new PhetPCanvas();
        phetPCanvas.setPanEventHandler( new PPanEventHandler() );
        phetPCanvas.setZoomEventHandler( new PZoomEventHandler() );

        frame.setContentPane( phetPCanvas );
        final ExpensiveNode expensiveNode = new ExpensiveNode();
        final BufferedPNode bufferedPNode = new BufferedPNode( phetPCanvas, expensiveNode );
//        phetPCanvas.addWorldChild( expensiveNode );
        phetPCanvas.addWorldChild( bufferedPNode );

        phetPCanvas.addKeyListener( new KeyAdapter() {
            public void keyPressed( KeyEvent e ) {
                System.out.println( "TestBufferedPNode.keyPressed" );
                phetPCanvas.getCamera().scaleView( 1.1 );
                phetPCanvas.repaint();
            }
        } );
    }

    static class ExpensiveNode extends PNode {
        Random random = new Random();

        public ExpensiveNode() {
            int w = 500;
            int h = 500;
            for ( int i = 0; i < 2000; i++ ) {
                PhetPPath path = new PhetPPath( new Line2D.Double( random.nextDouble() * w, random.nextDouble() * h, random.nextDouble() * w, random.nextDouble() * h ), new BasicStroke( random.nextFloat() * 3 ), new Color( random.nextFloat(), random.nextFloat(), random.nextFloat() ) );
                addChild( path );
            }
        }
    }

    private void start() {
        frame.setVisible( true );
        phetPCanvas.requestFocus();
    }

    public static void main( String[] args ) {
        new TestBufferedPNode().start();
    }
}
