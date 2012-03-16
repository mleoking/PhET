// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.rotation.tests;

import java.awt.*;

import javax.swing.*;

import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.common.piccolophet.nodes.HandleNode;

/**
 * Created by: Sam
 * Nov 7, 2007 at 12:18:46 AM
 */
public class TestHandleNode {
    public static void main( String[] args ) {
        JFrame frame = new JFrame();
        PhetPCanvas phetPCanvas = new PhetPCanvas();
        frame.setContentPane( phetPCanvas );
        HandleNode handleNode = new HandleNode( 100, 200, Color.gray );
        handleNode.setStroke( new BasicStroke( 2 ) );
        handleNode.setOffset( 100, 100 );
        phetPCanvas.addScreenChild( handleNode );
        frame.setSize( 800, 600 );
        frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
        frame.show();
    }
}
