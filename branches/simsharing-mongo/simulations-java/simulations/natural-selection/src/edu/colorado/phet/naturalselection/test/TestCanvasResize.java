// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.naturalselection.test;

import java.awt.*;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;

import javax.swing.*;

import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.umd.cs.piccolo.PCanvas;
import edu.umd.cs.piccolo.PNode;

public class TestCanvasResize {
    public static void main( String[] args ) {
        final int WIDTH = 640;


        JFrame frame = new JFrame( "Test" );
        PCanvas contentPane = new PCanvas();
        contentPane.setPreferredSize( new Dimension( WIDTH, 480 ) );
        final PNode rootNode = new PNode();

        contentPane.getLayer().addChild( rootNode );

        PhetPPath blueBox = new PhetPPath( new Rectangle( -25, 0, 50, 50 ), Color.blue );
        rootNode.addChild( blueBox );

        JPanel panel = new JPanel( new GridBagLayout() );
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 0;
        panel.add( contentPane, c );
        final JScrollBar horizBar = new JScrollBar( JScrollBar.HORIZONTAL );
        c.gridy = 1;
        c.fill = GridBagConstraints.BOTH;
        panel.add( horizBar, c );

        frame.setContentPane( panel );
        //frame.setSize( 400, 500 );
        frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
        frame.pack();
        frame.setVisible( true );

        System.out.println( "rootNode bounds: " + rootNode.computeFullBounds( null ) );

        System.out.println( "rootNode bounds: " + rootNode.computeFullBounds( null ) );

        rootNode.setOffset( WIDTH / 2, 0 );

        horizBar.setMaximum( 1000 );
        horizBar.setMinimum( -1000 );
        horizBar.setValue( 0 );

        horizBar.addAdjustmentListener( new AdjustmentListener() {
            public void adjustmentValueChanged( AdjustmentEvent adjustmentEvent ) {
                rootNode.setOffset( WIDTH / 2 + horizBar.getValue(), 0 );
            }
        } );


    }
}
