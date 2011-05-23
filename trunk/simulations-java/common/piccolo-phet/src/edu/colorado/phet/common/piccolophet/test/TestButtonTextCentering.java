// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.common.piccolophet.test;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Dimension2D;

import javax.swing.*;

import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.common.piccolophet.nodes.ButtonNode;
import edu.umd.cs.piccolo.util.PDebug;
import edu.umd.cs.piccolo.util.PDimension;

/**
 * Class for testing the centering of text in the ButtonNode class, see #2780.
 * <p/>
 * Author: John Blanco
 */
public class TestButtonTextCentering {

    private static final Dimension2D STAGE_SIZE = new PDimension( 1008, 679 );

    public static void main( String[] args ) {

        PDebug.debugBounds = false;
        ActionListener listener = new ActionListener() {
            public void actionPerformed( ActionEvent event ) {
                System.out.println( "actionPerformed event= " + event );
            }
        };

        ButtonNode button1 = new ButtonNode( "xxx", new PhetFont( Font.BOLD, 18 ), Color.ORANGE );
        button1.setOffset( 500, 500 );
        button1.addActionListener( listener );

        PhetPCanvas canvas = new PhetPCanvas();
        canvas.setWorldTransformStrategy( new PhetPCanvas.CenteredStage( canvas, STAGE_SIZE ) );
        canvas.addWorldChild( button1 );

        JFrame frame = new JFrame( ButtonNode.class.getName() );
        frame.setContentPane( canvas );
        frame.setSize( (int) STAGE_SIZE.getWidth(), (int) STAGE_SIZE.getHeight() );
        frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
        frame.setVisible( true );
    }
}
