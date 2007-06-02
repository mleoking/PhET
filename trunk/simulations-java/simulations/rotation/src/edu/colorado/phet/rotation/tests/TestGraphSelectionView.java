package edu.colorado.phet.rotation.tests;

import edu.colorado.phet.common.phetcommon.view.PhetLookAndFeel;
import edu.colorado.phet.common.phetcommon.model.clock.SwingClock;
import edu.colorado.phet.common.piccolophet.BufferedPhetPCanvas;
import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.common.motion.graphs.GraphSelectionControl;
import edu.colorado.phet.common.motion.graphs.GraphSetModel;
import edu.colorado.phet.common.motion.graphs.GraphSetNode;
import edu.colorado.phet.rotation.graphs.RotationGraphSet;
import edu.colorado.phet.common.motion.graphs.GraphSuiteSet;
import edu.colorado.phet.rotation.model.RotationModel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

public class TestGraphSelectionView {
    private JFrame suiteSelectionFrame;
    private JFrame plotFrame;
    private GraphSetNode graphSetNode;
    private PhetPCanvas phetPCanvas;

    public TestGraphSelectionView() {
        new PhetLookAndFeel().initLookAndFeel();
        suiteSelectionFrame = new JFrame();
        suiteSelectionFrame.setSize( 600, 600 );
        suiteSelectionFrame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );

        phetPCanvas = new BufferedPhetPCanvas();
        phetPCanvas.setBackground( new Color( 200, 240, 200 ) );
        GraphSuiteSet rotationGraphSet = new RotationGraphSet( phetPCanvas, new RotationModel(new SwingClock( 30,1) ) );
        GraphSetModel graphSetModel = new GraphSetModel( rotationGraphSet.getGraphSuite( 0 ) );

        GraphSelectionControl graphSelectionControl = new GraphSelectionControl( rotationGraphSet, graphSetModel );
        suiteSelectionFrame.getContentPane().add( graphSelectionControl );

        plotFrame = new JFrame();
        graphSetNode = new GraphSetNode( graphSetModel );

        phetPCanvas.addScreenChild( graphSetNode );
        plotFrame.setContentPane( phetPCanvas );
        plotFrame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );

        plotFrame.setSize( 400, 400 );
        plotFrame.setLocation( suiteSelectionFrame.getX(), suiteSelectionFrame.getY() + suiteSelectionFrame.getHeight() );
        phetPCanvas.addComponentListener( new ComponentAdapter() {
            public void componentResized( ComponentEvent e ) {
                relayout();
            }
        } );
        relayout();
    }

    private void relayout() {
        graphSetNode.setBounds( 0, 0, phetPCanvas.getWidth(), phetPCanvas.getHeight() );
    }

    public static void main( String[] args ) {
        new TestGraphSelectionView().start();
    }

    private void start() {
        suiteSelectionFrame.setVisible( true );
        plotFrame.setVisible( true );

    }
}
