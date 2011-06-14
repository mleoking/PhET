// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.rotation.tests;

/**
 * User: Sam Reid
 * Date: Jan 9, 2007
 * Time: 7:56:54 AM
 *
 */

import javax.swing.*;

import edu.colorado.phet.common.motion.graphs.GraphSetModel;
import edu.colorado.phet.common.motion.graphs.GraphSuite;
import edu.colorado.phet.common.motion.graphs.GraphSuiteSet;
import edu.colorado.phet.common.phetcommon.model.clock.ConstantDtClock;
import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.common.piccolophet.nodes.RulerNode;
import edu.colorado.phet.rotation.RotationModule;
import edu.colorado.phet.rotation.controls.RotationControlPanel;
import edu.colorado.phet.rotation.controls.VectorViewModel;
import edu.colorado.phet.rotation.graphs.RotationGraphSet;
import edu.colorado.phet.rotation.model.AngleUnitModel;
import edu.colorado.phet.rotation.model.RotationModel;

public class TestRotationControlPanel {
    private JFrame frame;

    public TestRotationControlPanel() {
        frame = new JFrame();
        frame.setSize( 600, 600 );
        frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );

        PhetPCanvas phetPCanvas = new PhetPCanvas();
        RotationModel rotationModel = new RotationModel( new ConstantDtClock( 30, 1 ) );
        GraphSuiteSet rotationGraphSet = new RotationGraphSet( phetPCanvas, rotationModel, new AngleUnitModel( true ) );
        GraphSuite graphSuite = new RotationGraphSet( phetPCanvas, rotationModel, new AngleUnitModel( true ) ).getGraphSuite( 0 );
        GraphSetModel graphSetModel = new GraphSetModel( graphSuite );
        VectorViewModel vectorViewModel = new VectorViewModel();
        frame.setContentPane( new RotationControlPanel( new RulerNode( 10, 10, new String[0], "units.units", 3, 14 ), rotationGraphSet, graphSetModel, vectorViewModel, frame, rotationModel.getRotationBody( 0 ), rotationModel.getRotationBody( 1 ), new RotationModule( frame ), new AngleUnitModel( true ), rotationModel.getRotationPlatform() ) );
    }

    public static void main( String[] args ) {
        new TestRotationControlPanel().start();
    }

    private void start() {
        frame.setVisible( true );
    }
}
