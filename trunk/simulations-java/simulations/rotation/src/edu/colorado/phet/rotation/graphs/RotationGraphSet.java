package edu.colorado.phet.rotation.graphs;

import edu.colorado.phet.common.motion.graphs.CursorModel;
import edu.colorado.phet.common.motion.graphs.GraphSuiteSet;
import edu.colorado.phet.common.motion.graphs.MinimizableControlGraph;
import edu.colorado.phet.common.motion.graphs.MotionControlGraph;
import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.rotation.model.RotationModel;
import edu.colorado.phet.rotation.util.UnicodeUtil;
import edu.umd.cs.piccolo.nodes.PImage;

import java.awt.*;
import java.util.Arrays;

/**
 * User: Sam Reid
 * Date: Dec 28, 2006
 * Time: 8:23:39 AM
 */

public class RotationGraphSet extends GraphSuiteSet {

    public RotationGraphSet( PhetPCanvas pSwingCanvas, final RotationModel rotationModel ) {
        this( pSwingCanvas, rotationModel, new CursorModel( rotationModel.getTimeSeriesModel() ) );
    }

    public RotationGraphSet( PhetPCanvas pSwingCanvas, final RotationModel rotationModel, CursorModel cursorModel ) {
        super( cursorModel );
        MinimizableControlGraph angleGraph = new MinimizableControlGraph( UnicodeUtil.THETA, new MotionControlGraph( pSwingCanvas, rotationModel.getXVariable(), UnicodeUtil.THETA, "Angular Position", -Math.PI * 3, Math.PI * 3, Color.blue, new PImage( loadArrow( "blue-arrow.png" ) ), rotationModel, true, cursorModel, rotationModel.getPositionDriven() ) );
        MinimizableControlGraph velocityGraph = new MinimizableControlGraph( UnicodeUtil.OMEGA, new MotionControlGraph( pSwingCanvas, rotationModel.getVVariable(), UnicodeUtil.OMEGA, "Angular Velocity", -0.1, 0.1, Color.red, new PImage( loadArrow( "red-arrow.png" ) ), rotationModel, true, cursorModel, rotationModel.getVelocityDriven() ) );
        MinimizableControlGraph accelGraph = new MinimizableControlGraph( UnicodeUtil.ALPHA, new MotionControlGraph( pSwingCanvas, rotationModel.getAVariable(), UnicodeUtil.ALPHA, "Angular Acceleration", -0.001, 0.001, Color.green, new PImage( loadArrow( "green-arrow.png" ) ), rotationModel, true, cursorModel, rotationModel.getAccelDriven() ) );
        MinimizableControlGraph linearPositionGraph = new MinimizableControlGraph( "x,y", new MotionControlGraph( pSwingCanvas, rotationModel.getXPositionVariable(), "x", "Position", 0, 500, Color.blue, new PImage( loadArrow( "blue-arrow.png" ) ), rotationModel, false, cursorModel, null ) );
        linearPositionGraph.getControlGraph().addSeries( "Position", Color.red, "y", rotationModel.getYPositionVariable() );
        MinimizableControlGraph speedGraph = new MinimizableControlGraph( "vx, vy", new MotionControlGraph( pSwingCanvas, rotationModel.getSpeedVariable(), "|v|", "Linear Speed", 0, 0.1, Color.red, new PImage( loadArrow( "red-arrow.png" ) ), rotationModel, false, cursorModel, null ) );
        MinimizableControlGraph centripetalAccelGraph = new MinimizableControlGraph( "a", new MotionControlGraph( pSwingCanvas, rotationModel.getCentripetalAcceleration(), "a", "Centripetal Acceleration", 0, 0.001, Color.green, new PImage( loadArrow( "green-arrow.png" ) ), rotationModel, false, cursorModel, null ) );

        //new GraphSuite( new GraphComponent[]{getAngleGraph(), getAngularVelocityGraph(), getPositionGraph()} ),//todo: remove after testing
        addGraphSuite( new MinimizableControlGraph[]{angleGraph, velocityGraph, linearPositionGraph, accelGraph} );
        addGraphSuite( new MinimizableControlGraph[]{angleGraph, velocityGraph, accelGraph} );
        addGraphSuite( new MinimizableControlGraph[]{angleGraph, velocityGraph, speedGraph} );
        addGraphSuite( new MinimizableControlGraph[]{angleGraph, velocityGraph, centripetalAccelGraph} );
        addGraphSuite( new MinimizableControlGraph[]{angleGraph, velocityGraph, accelGraph, linearPositionGraph, speedGraph, centripetalAccelGraph} );
    }

    public static void main( String[] args ) {
        RotationGraphSet rotationGraphSet = new RotationGraphSet( new PhetPCanvas(), new RotationModel() );
        MinimizableControlGraph[] graphs = rotationGraphSet.getAllGraphs();
        System.out.println( "graphs.length = " + graphs.length );
        System.out.println( "Arrays.asList( graphs ) = " + Arrays.asList( graphs ) );
    }
}
