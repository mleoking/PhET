package edu.colorado.phet.common.motion.tests;

/**
 * User: Sam Reid
 * Date: Dec 29, 2006
 * Time: 9:05:50 AM
 *
 */

import edu.colorado.phet.common.piccolophet.BufferedPhetPCanvas;
import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.common.motion.model.SimulationVariable;
import edu.colorado.phet.common.motion.graphs.ControlGraph;

import javax.swing.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

public class TestControlGraph {
    private JFrame frame;
    private ControlGraph controlGraph;
    private PhetPCanvas phetPCanvas;

    public TestControlGraph() {
        frame = new JFrame();
        frame.setSize( 600, 600 );
        frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );

        phetPCanvas = new BufferedPhetPCanvas();
        controlGraph = new ControlGraph( phetPCanvas, new SimulationVariable(), "abbrev", "title", -10, 10 );
//        controlGraph.setLayout( new ControlGraph.AlignedLayout( ) );
        controlGraph.addValue( 0, 0 );
        controlGraph.addValue( 600, 10 );
        controlGraph.addValue( 800, -3 );
//        controlGraph.addValue( 600,10);
        phetPCanvas.addScreenChild( controlGraph );
        phetPCanvas.addComponentListener( new ComponentAdapter() {
            public void componentResized( ComponentEvent e ) {
                relayout();
            }
        } );
        frame.setContentPane( phetPCanvas );
        relayout();
    }

    private void relayout() {
        controlGraph.setBounds( 0, 0, phetPCanvas.getWidth(), phetPCanvas.getHeight() );
    }

    public static void main( String[] args ) {
        new TestControlGraph().start();
    }

    private void start() {
        frame.setVisible( true );
    }
}
