/* Copyright 2004, Sam Reid */
package edu.colorado.phet.tests.piccolo.experimental.tests;

import edu.colorado.phet.tests.piccolo.experimental.LayeredPNode;
import edu.umd.cs.piccolo.PCanvas;
import edu.umd.cs.piccolo.nodes.PPath;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Line2D;

/**
 * User: Sam Reid
 * Date: Nov 12, 2005
 * Time: 8:08:35 AM
 * Copyright (c) Nov 12, 2005 by Sam Reid
 */

public class TestLayeredPNode {
    private JFrame frame;
    private LayeredPNode layeredPNode;
    private Color[] colors;
    private JTextField[] textFields;

    public TestLayeredPNode() {
        frame = new JFrame( getClass().getName() );
        PCanvas pCanvas = new PCanvas();
        JPanel contentPane = new JPanel( new BorderLayout() );
        contentPane.add( pCanvas, BorderLayout.CENTER );
        frame.setContentPane( contentPane );

        layeredPNode = new LayeredPNode();
        colors = new Color[]{Color.red, Color.blue, Color.green, Color.black};

        pCanvas.getLayer().addChild( layeredPNode );
        frame.setSize( 400, 400 );

        JPanel controls = new JPanel();
        controls.setLayout( new BoxLayout( controls, BoxLayout.Y_AXIS ) );
        textFields = new JTextField[colors.length];
        for( int i = 0; i < 4; i++ ) {
            textFields[i] = new JTextField( "0.0",10 );
            textFields[i].setBorder( new LineBorder( colors[i], 2 ) );
            controls.add( textFields[i] );
            textFields[i].addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    TestLayeredPNode.this.update();
                }
            } );
        }
        controls.setBorder( BorderFactory.createTitledBorder( "Layers for each bar." ) );
        contentPane.add( controls, BorderLayout.SOUTH );
        update();
    }

    private void update() {
        layeredPNode.removeAllLayeredChildren();

        layeredPNode.addLayerChild( new ExampleGraphic( 100, 50, 100, 250, colors[0] ), getLayer( 0 ) );
        layeredPNode.addLayerChild( new ExampleGraphic( 200, 50, 200, 250, colors[1] ), getLayer( 1 ) );
        layeredPNode.addLayerChild( new ExampleGraphic( 50, 100, 250, 100, colors[2] ), getLayer( 2 ) );
        layeredPNode.addLayerChild( new ExampleGraphic( 50, 200, 250, 200, colors[3] ), getLayer( 3 ) );
    }

    private double getLayer( int i ) {
        return Double.parseDouble( textFields[i].getText() );
    }

    private static class ExampleGraphic extends PPath {
        public ExampleGraphic( double x1, double y1, double x2, double y2, Color color ) {
            super( new Line2D.Double( x1, y1, x2, y2 ) );
            setStrokePaint( color );
            setStroke( new BasicStroke( 10 ) );
        }
    }

    public static void main( String[] args ) {
        new TestLayeredPNode().start();
    }

    private void start() {
        frame.setVisible( true );
    }
}
