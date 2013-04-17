// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.naturalselection.test;

import java.awt.*;

import javax.swing.*;

import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.umd.cs.piccolo.PCanvas;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PText;

public class TestAddChild {
    public static void main( String[] args ) {
        JFrame frame = new JFrame( "Test" );
        PCanvas contentPane = new PCanvas();
        PNode rootNode = new PNode();
        for ( int i = 0; i < 10; i++ ) {
            PText text = new PText( "node " + i );
            rootNode.addChild( text );
            text.setOffset( 100, i * text.getFullBounds().getHeight() + 20 );
        }
        contentPane.getLayer().addChild( rootNode );

        rootNode.addChild( 5, new PhetPPath( new Rectangle( 0, 0, 50, 50 ), Color.blue ) );

        frame.setContentPane( contentPane );
        frame.setSize( 400, 500 );
        frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
        frame.setVisible( true );
    }
}
