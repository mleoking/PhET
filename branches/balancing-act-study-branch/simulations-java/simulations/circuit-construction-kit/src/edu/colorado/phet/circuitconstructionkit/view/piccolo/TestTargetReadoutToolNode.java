// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.circuitconstructionkit.view.piccolo;

/**
 * User: Sam Reid
 * Date: Sep 29, 2006
 * Time: 2:16:40 PM
 *
 */

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Date;

import javax.swing.JFrame;
import javax.swing.Timer;

import edu.umd.cs.piccolo.PCanvas;

public class TestTargetReadoutToolNode {
    private JFrame frame;

    public TestTargetReadoutToolNode() {
        frame = new JFrame();
        frame.setSize( 600, 600 );
        frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
        PCanvas pc = new PCanvas();
        final TargetReadoutToolNode targetReadoutToolNode = new TargetReadoutToolNode();
        pc.getLayer().addChild( targetReadoutToolNode );
        frame.setContentPane( pc );
        Timer timer = new Timer( 30, new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                targetReadoutToolNode.setText( new String[] { "the time is now", new Date().toString() } );
            }
        } );
        timer.start();
    }

    public static void main( String[] args ) {
        new TestTargetReadoutToolNode().start();
    }

    private void start() {
        frame.setVisible( true );
    }
}
