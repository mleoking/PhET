/* Copyright 2006, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.hydrogenatom.test;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Rectangle;

import javax.swing.JFrame;
import javax.swing.WindowConstants;

import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolox.pswing.PSwingCanvas;


public class TestFullBounds extends JFrame {

    public static void main( String[] args ) {
        JFrame frame = new TestFullBounds();
        frame.show();
    }
    
    public TestFullBounds() {
        
        PSwingCanvas canvas = new PSwingCanvas();

        PNode node = new TestNode();
        canvas.getLayer().addChild( node );
        node.setOffset( 50, 50 );
        
        System.out.println( "node.getFullBounds = " + node.getFullBounds() );
        
        getContentPane().add( canvas );
        setSize( 300, 300 );
        setDefaultCloseOperation( WindowConstants.EXIT_ON_CLOSE );
    }
    
    private static class TestNode extends PNode {
        public TestNode() {
            super();
            PPath pathNode = new PPath();
            addChild( pathNode );
            pathNode.setPathTo( new Rectangle.Double( 0, 0, 200, 100 ) );
            pathNode.setPaint( Color.RED );
            pathNode.setStroke( null );
            
            System.out.println( "pathNode.getFullBounds = " + pathNode.getFullBounds() );
        }
    }

}
