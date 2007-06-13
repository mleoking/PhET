/* Copyright 2007, University of Colorado */
package edu.umd.cs.piccolox.pswing.tests;

import edu.umd.cs.piccolox.pswing.PSwingCanvas;
import edu.umd.cs.piccolox.pswing.PSwing;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.event.PDragEventHandler;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.nodes.PText;

import javax.swing.*;
import java.awt.*;

public class TestEmbeddedPSwing {
    private JFrame frame;

    public TestEmbeddedPSwing() {
        frame = new JFrame();
        frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
        PSwingCanvas parentCanvas = new PSwingCanvas();
        parentCanvas.setPanEventHandler( null);
        frame.setContentPane( parentCanvas );
        PSwingCanvas childCanvas = new PSwingCanvas();
        childCanvas.setPreferredSize( new Dimension( 200,200) );

        childCanvas.setPanEventHandler( null);
        childCanvas.setBackground( Color.green );

        PNode childNode = new PText( "Child Node" );
        childNode.addInputEventListener( new PBasicInputEventHandler() {

            public void mousePressed( PInputEvent event ) {
                System.out.println( "TestEmbeddedPSwing.mousePressed" );
            }
        });
        childNode.addInputEventListener( new PDragEventHandler() );
        childCanvas.getLayer().addChild( childNode );
        PSwing child1 = new PSwing( new JSlider() );
        child1.setOffset( -50,50);
//        childCanvas.getLayer().addChild( child1 );
        PSwing child = new PSwing( childCanvas );
        child.setOffset( 100,100);
        parentCanvas.getLayer().addChild( child );


    }

    public static void main( String[] args ) {

        new TestEmbeddedPSwing().start();
    }

    private void start() {
        frame.setSize( 400, 400 );
        frame.show();
    }
}
