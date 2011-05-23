// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.common.piccolophet.test;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;

import edu.colorado.phet.common.phetcommon.view.util.PhetFont;

import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.common.piccolophet.nodes.ButtonNode;

/**
 * Class for testing the centering of text in the ButtonNode class, see #2780.
 *
 * Author: John Blanco
 */
public class TestButtonTextCentering {

    public static final String TEST_TEXT_STRING = "This is the Test Caption";

    public static void main( String[] args ) {
        ActionListener listener = new ActionListener() {
            public void actionPerformed( ActionEvent event ) {
                System.out.println( "actionPerformed event= " + event );
            }
        };

        ButtonNode button1 = new ButtonNode( TEST_TEXT_STRING ) {{
            setCornerRadius( 20 );
            setImageTextGap( 20 );
            setMargin( 20, 10, 20, 10 );
            setFont( new PhetFont( 16 ) );
            setBackground( Color.GREEN );
        }};
        button1.setOffset( 5, 5 );
        button1.addActionListener( listener );

        PhetPCanvas canvas = new PhetPCanvas();
        canvas.addScreenChild( button1 );

        JFrame frame = new JFrame( ButtonNode.class.getName() );
        frame.setContentPane( canvas );
        frame.setSize( 525, 600 );
        frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
        frame.setVisible( true );
    }
}
