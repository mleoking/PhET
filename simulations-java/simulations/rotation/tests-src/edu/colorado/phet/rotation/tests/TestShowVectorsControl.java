package edu.colorado.phet.rotation.tests;

/**
 * User: Sam Reid
 * Date: Dec 28, 2006
 * Time: 10:20:15 PM
 * Copyright (c) Dec 28, 2006 by Sam Reid
 */

import edu.colorado.phet.rotation.controls.ShowVectorsControl;
import edu.colorado.phet.rotation.controls.VectorViewModel;

import javax.swing.*;

public class TestShowVectorsControl {
    private JFrame frame;

    public TestShowVectorsControl() {
        frame = new JFrame();
        frame.setSize( 600, 600 );
        frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );

        final VectorViewModel vectorViewModel = new VectorViewModel();
        vectorViewModel.addListener( new VectorViewModel.Listener() {
            public void visibilityChanged() {
                System.out.println( "v_vis=" + vectorViewModel.isVelocityVisible() + ", " + " vectorViewModel.isAccelerationVisible() = " + vectorViewModel.isAccelerationVisible() );
            }
        } );
        frame.setContentPane( new ShowVectorsControl( vectorViewModel ) );
    }

    public static void main( String[] args ) {
        new TestShowVectorsControl().start();
    }

    private void start() {
        frame.setVisible( true );
    }
}
