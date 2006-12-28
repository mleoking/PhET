package edu.colorado.phet.rotation.tests;

import edu.colorado.phet.piccolo.PhetPCanvas;
import edu.colorado.phet.rotation.controls.GraphSelectionControl;
import edu.colorado.phet.rotation.graphs.GraphSetModel;
import edu.colorado.phet.rotation.graphs.GraphSetPanel;
import edu.colorado.phet.rotation.graphs.RotationGraphSet;

import javax.swing.*;

public class TestGraphSelectionView {
    private JFrame frame;
    private JFrame f2;

    public TestGraphSelectionView() {
        frame = new JFrame();
        frame.setSize( 600, 600 );
        frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );

        PhetPCanvas pane = new PhetPCanvas();
        RotationGraphSet rotationGraphSet = new RotationGraphSet( pane );
        GraphSetModel graphSetPanel = new GraphSetModel( rotationGraphSet.getGraphSuite( 0 ) );
        GraphSelectionControl graphSelectionControl = new GraphSelectionControl( rotationGraphSet, graphSetPanel );
        frame.getContentPane().add( graphSelectionControl );

        f2 = new JFrame();

        pane.addScreenChild( new GraphSetPanel( graphSetPanel ) );
        f2.setContentPane( pane );
        f2.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );

        f2.setSize( 400, 400 );
        f2.setLocation( frame.getX(), frame.getY() + frame.getHeight() );
    }

    public static void main( String[] args ) {
        new TestGraphSelectionView().start();
    }

    private void start() {
        frame.setVisible( true );
        f2.setVisible( true );

    }
}
