package edu.colorado.phet.rotation.tests;
/**
 * User: Sam Reid
 * Date: Dec 28, 2006
 * Time: 8:42:32 AM
 * Copyright (c) Dec 28, 2006 by Sam Reid
 */


import edu.colorado.phet.rotation.controls.GraphSelectionControl;
import edu.colorado.phet.rotation.graphs.GraphSetPanel;
import edu.colorado.phet.rotation.graphs.RotationGraphSet;

import javax.swing.*;

public class TestGraphSelectionControl {
    private JFrame frame;

    public TestGraphSelectionControl() {
        frame = new JFrame();
        frame.setSize( 600, 600 );
        frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );

        RotationGraphSet rotationGraphSet = new RotationGraphSet();
        GraphSetPanel graphSetPanel = new GraphSetPanel( rotationGraphSet.getGraphSuite( 0 ) );
        GraphSelectionControl graphSelectionControl = new GraphSelectionControl( rotationGraphSet, graphSetPanel );
        frame.getContentPane().add( graphSelectionControl );
    }

    public static void main( String[] args ) {
        new TestGraphSelectionControl().start();
    }

    private void start() {
        frame.setVisible( true );
    }
}

