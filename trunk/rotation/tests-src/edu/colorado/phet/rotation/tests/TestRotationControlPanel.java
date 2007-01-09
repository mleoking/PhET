package edu.colorado.phet.rotation.tests;

/**
 * User: Sam Reid
 * Date: Jan 9, 2007
 * Time: 7:56:54 AM
 * Copyright (c) Jan 9, 2007 by Sam Reid
 */

import edu.colorado.phet.piccolo.PhetPCanvas;
import edu.colorado.phet.rotation.RotationControlPanel;
import edu.colorado.phet.rotation.controls.VectorViewModel;
import edu.colorado.phet.rotation.graphs.GraphSetModel;
import edu.colorado.phet.rotation.graphs.GraphSuite;
import edu.colorado.phet.rotation.graphs.RotationGraphSet;
import edu.colorado.phet.rotation.model.RotationModel;

import javax.swing.*;

public class TestRotationControlPanel {
    private JFrame frame;

    public TestRotationControlPanel() {
        frame = new JFrame();
        frame.setSize( 600, 600 );
        frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );

        PhetPCanvas phetPCanvas = new PhetPCanvas();
        RotationModel rotationModel = new RotationModel();
        RotationGraphSet rotationGraphSet = new RotationGraphSet( phetPCanvas, rotationModel );
        GraphSuite graphSuite = new RotationGraphSet( phetPCanvas, rotationModel ).getGraphSuite( 0 );
        GraphSetModel graphSetModel = new GraphSetModel( graphSuite );
        VectorViewModel vectorViewModel = new VectorViewModel();
        frame.setContentPane( new RotationControlPanel( rotationGraphSet, graphSetModel, vectorViewModel ) );
    }

    public static void main( String[] args ) {
        new TestRotationControlPanel().start();
    }

    private void start() {
        frame.setVisible( true );
    }
}
