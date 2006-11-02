package edu.colorado.phet.tests.piccolo;

/**
 * User: Sam Reid
 * Date: Sep 11, 2006
 * Time: 12:35:45 PM
 * Copyright (c) Sep 11, 2006 by Sam Reid
 */

import edu.umd.cs.piccolo.PCanvas;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.nodes.PText;

import javax.swing.*;
import java.awt.*;

public class TestPiccoloDrag {
    private JFrame frame;

    public TestPiccoloDrag() {
        frame = new JFrame();
        frame.setSize( 600, 600 );
        frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );

        PCanvas pc = new PCanvas();
        pc.setPanEventHandler( null );
        final MyChild child = new MyChild();
        pc.getLayer().addChild( child );
        child.addInputEventListener( new PBasicInputEventHandler() {
            public void mouseDragged( PInputEvent event ) {
                child.translate( event.getCanvasDelta().width, event.getCanvasDelta().height );
            }
        } );
        frame.setContentPane( pc );
    }

    static class MyChild extends PNode {
        public MyChild() {
            PText child = new PText( "push this button" );
            child.addInputEventListener( new PBasicInputEventHandler() {
                public void mouseReleased( PInputEvent event ) {
                    System.out.println( "TestPiccoloDrag$MyChild.mouseReleased" );
                }
            } );
            PPath rect = new PPath( new Rectangle( 0, 0, 100, 100 ) );
            rect.setPaint( Color.yellow );
            addChild( rect );
            addChild( new PText( "There" ) );
            addChild( child );
            child.setOffset( 0, 80 );
        }
    }

    public static void main( String[] args ) {
        new TestPiccoloDrag().start();
    }

    private void start() {
        frame.setVisible( true );
    }
}
