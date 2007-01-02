package edu.colorado.phet.rotation.tests;

import edu.colorado.phet.piccolo.PhetPCanvas;
import edu.colorado.phet.rotation.controls.GraphSelectionControl;
import edu.colorado.phet.rotation.graphs.GraphSetModel;
import edu.colorado.phet.rotation.graphs.GraphSetPanel;
import edu.colorado.phet.rotation.graphs.RotationGraphSet;

import javax.swing.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

public class TestGraphSelectionView {
    private JFrame frame;
    private JFrame f2;
    private GraphSetPanel graphSetPanel;
    private PhetPCanvas phetPCanvas;

    public TestGraphSelectionView() {
        frame = new JFrame();
        frame.setSize( 600, 600 );
        frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );

        phetPCanvas = new PhetPCanvas();
        RotationGraphSet rotationGraphSet = new RotationGraphSet( phetPCanvas );
        GraphSetModel graphSetModel = new GraphSetModel( rotationGraphSet.getGraphSuite( 0 ) );
        GraphSelectionControl graphSelectionControl = new GraphSelectionControl( rotationGraphSet, graphSetModel );
        frame.getContentPane().add( graphSelectionControl );

        f2 = new JFrame();

        graphSetPanel = new GraphSetPanel( graphSetModel );

        phetPCanvas.addScreenChild( graphSetPanel );
        f2.setContentPane( phetPCanvas );
        f2.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );

        f2.setSize( 400, 400 );
        f2.setLocation( frame.getX(), frame.getY() + frame.getHeight() );
        phetPCanvas.addComponentListener( new ComponentAdapter() {
            public void componentResized( ComponentEvent e ) {
                relayout();
            }
        } );
        relayout();
    }

    private void relayout() {
        graphSetPanel.setBounds( 0, 0, phetPCanvas.getWidth(), phetPCanvas.getHeight() );

    }

    public static void main( String[] args ) {
        new TestGraphSelectionView().start();
    }

    private void start() {
        frame.setVisible( true );
        f2.setVisible( true );

    }
}
