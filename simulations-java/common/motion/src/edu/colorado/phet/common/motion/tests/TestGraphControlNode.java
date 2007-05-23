package edu.colorado.phet.common.motion.tests;

/**
 * User: Sam Reid
 * Date: Dec 28, 2006
 * Time: 10:38:40 PM
 *
 */

import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.common.motion.graphs.DefaultGraphTimeSeries;
import edu.colorado.phet.common.motion.graphs.GraphControlsNode;
import edu.colorado.phet.common.motion.model.SimulationVariable;

import javax.swing.*;

public class TestGraphControlNode {
    private JFrame frame;

    public TestGraphControlNode() {
        frame = new JFrame();
        frame.setSize( 600, 600 );
        frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );

        PhetPCanvas phetPCanvas = new PhetPCanvas();
        phetPCanvas.addScreenChild( new GraphControlsNode( "title", new SimulationVariable(), new DefaultGraphTimeSeries() ) );
        frame.setContentPane( phetPCanvas );
    }

    public static void main( String[] args ) {
        new TestGraphControlNode().start();
    }

    private void start() {
        frame.setVisible( true );
    }
}
