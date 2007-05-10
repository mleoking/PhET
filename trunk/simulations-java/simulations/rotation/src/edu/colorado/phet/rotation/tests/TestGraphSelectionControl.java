package edu.colorado.phet.rotation.tests;

import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.rotation.controls.GraphSelectionControl;
import edu.colorado.phet.rotation.graphs.GraphSetModel;
import edu.colorado.phet.rotation.graphs.RotationGraphSet;
import edu.colorado.phet.rotation.model.RotationModel;

import javax.swing.*;

/**
 * User: Sam Reid
 * Date: Dec 28, 2006
 * Time: 8:42:32 AM
 */

public class TestGraphSelectionControl {
    private JFrame frame;

    public TestGraphSelectionControl() {
        frame = new JFrame();
        frame.setSize( 600, 600 );
        frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );

        RotationGraphSet rotationGraphSet = new RotationGraphSet( new PhetPCanvas(), new RotationModel() );
        GraphSetModel graphSetPanel = new GraphSetModel( rotationGraphSet.getGraphSuite( 0 ) );
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

