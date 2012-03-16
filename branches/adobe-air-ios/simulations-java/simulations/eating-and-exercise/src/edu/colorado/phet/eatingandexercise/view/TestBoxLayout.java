// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.eatingandexercise.view;

import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.*;

public class TestBoxLayout extends JFrame {
    public TestBoxLayout() {
        super( "Demostrating BoxLayout" );
        final int SIZE = 3;

        Container c = getContentPane();
        c.setLayout( new BorderLayout( 30, 30 ) );

        Box boxes[] = new Box[4];

        boxes[0] = Box.createHorizontalBox();
        boxes[1] = Box.createVerticalBox();
        boxes[2] = Box.createHorizontalBox();
        boxes[3] = Box.createVerticalBox();

        // add buttons to boxes[ 0 ]
        for ( int i = 0; i < SIZE; i++ ) {
            boxes[0].add( new JButton( "boxes[0]: " + i ) );
        }

        // create strut and add buttons to boxes[ 1 ]
        for ( int i = 0; i < SIZE; i++ ) {
            boxes[1].add( Box.createVerticalStrut( 25 ) );
            boxes[1].add( new JButton( "boxes[1]: " + i ) );
        }

        // create horizontal glue and add buttons to boxes[ 2 ]
        for ( int i = 0; i < SIZE; i++ ) {
            boxes[2].add( Box.createHorizontalGlue() );
            boxes[2].add( new JButton( "boxes[2]: " + i ) );
        }

        // create rigid area and add buttons to boxes[ 3 ]
        for ( int i = 0; i < SIZE; i++ ) {
            boxes[3].add( Box.createRigidArea( new Dimension( 12, 8 ) ) );
            boxes[3].add( new JButton( "boxes[3]: " + i ) );
        }

        // create horizontal glue and add buttons to panel
        JPanel panel = new JPanel();
        panel.setLayout(
                new BoxLayout( panel, BoxLayout.Y_AXIS ) );

        for ( int i = 0; i < SIZE; i++ ) {
            panel.add( Box.createGlue() );
            panel.add( new JButton( "panel: " + i ) );
        }

        // place panels on frame
        c.add( boxes[0], BorderLayout.NORTH );
        c.add( boxes[1], BorderLayout.EAST );
        c.add( boxes[2], BorderLayout.SOUTH );
        c.add( boxes[3], BorderLayout.WEST );
        c.add( panel, BorderLayout.CENTER );

        setSize( 350, 300 );
        show();
    }

    public static void main( String args[] ) {
        TestBoxLayout app = new TestBoxLayout();

        app.addWindowListener(
                new WindowAdapter() {
                    public void windowClosing( WindowEvent e ) {
                        System.exit( 0 );
                    }
                }
        );
    }
}