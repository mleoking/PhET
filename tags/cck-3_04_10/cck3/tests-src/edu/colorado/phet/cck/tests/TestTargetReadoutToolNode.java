package edu.colorado.phet.cck.tests;

/**
 * User: Sam Reid
 * Date: Sep 29, 2006
 * Time: 2:16:40 PM
 * Copyright (c) Sep 29, 2006 by Sam Reid
 */

import edu.colorado.phet.cck.piccolo_cck.TargetReadoutToolNode;
import edu.umd.cs.piccolo.PCanvas;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Date;

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
                targetReadoutToolNode.setText( new String[]{"the time is now", new Date().toString()} );
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
