// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.rotation.graphs;

import java.util.Arrays;

import edu.colorado.phet.common.motion.graphs.MinimizableControlGraph;
import edu.colorado.phet.common.phetcommon.model.clock.ConstantDtClock;
import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.rotation.model.AngleUnitModel;
import edu.colorado.phet.rotation.model.RotationModel;

/**
 * Author: Sam Reid
 * Aug 8, 2007, 5:23:54 PM
 */
public class RotationGraphSet extends AbstractRotationGraphSet {
    public RotationGraphSet( PhetPCanvas pSwingCanvas, RotationModel model, AngleUnitModel angleUnitModel ) {
        super( pSwingCanvas, model, angleUnitModel );

        RotationMinimizableControlGraph angleGraph = createAngleGraph();
        RotationMinimizableControlGraph angVelGraph = createAngVelGraph();
        RotationMinimizableControlGraph angAccelGraph = createAngAccelGraph( true );
        RotationMinimizableControlGraph xGraph = createXGraph();
        RotationMinimizableControlGraph vGraph = createVGraph();
        RotationMinimizableControlGraph aGraph = createAGraph();

        addGraphSuite( new RotationMinimizableControlGraph[]{angleGraph, angVelGraph, xGraph} );
        addGraphSuite( new RotationMinimizableControlGraph[]{angleGraph, angVelGraph, angAccelGraph} );
        addGraphSuite( new RotationMinimizableControlGraph[]{angleGraph, angVelGraph, vGraph} );
        addGraphSuite( new RotationMinimizableControlGraph[]{angleGraph, angVelGraph, aGraph} );

        addSeriesSelectionPanels();
        updateBody1Series();
    }

    public static void main( String[] args ) {
        AbstractRotationGraphSet abstractRotationGraphSet = new RotationGraphSet( new PhetPCanvas(), new RotationModel( new ConstantDtClock( 30, 1 ) ), new AngleUnitModel( false ) );
        MinimizableControlGraph[] graphs = abstractRotationGraphSet.getAllGraphs();
        System.out.println( "graphs.length = " + graphs.length );
        System.out.println( "Arrays.asList( graphs ) = " + Arrays.asList( graphs ) );
    }
}
