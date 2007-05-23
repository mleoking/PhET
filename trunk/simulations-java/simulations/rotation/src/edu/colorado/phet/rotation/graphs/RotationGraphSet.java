package edu.colorado.phet.rotation.graphs;

import edu.colorado.phet.common.motion.graphs.*;
import edu.colorado.phet.common.motion.model.MotionModel;
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
    private MinimizableControlGraph angleMinimizableControlGraph;
    private MinimizableControlGraph angularVelocityMinimizableControlGraph;
    private MinimizableControlGraph angularAccelerationMinimizableControlGraph;
    private MinimizableControlGraph positionMinimizableControlGraph;
    private MinimizableControlGraph speedMinimizableControlGraph;
    private MinimizableControlGraph accelerationMinimizableControlGraph;

    public RotationGraphSet( PhetPCanvas pSwingCanvas, final RotationModel rotationModel ) {
        this( pSwingCanvas, rotationModel, new CursorModel( rotationModel.getTimeSeriesModel() ) );
    }

    public RotationGraphSet( PhetPCanvas pSwingCanvas, final RotationModel rotationModel, CursorModel cursorModel ) {
        super( cursorModel );
        angleMinimizableControlGraph = new MinimizableControlGraph( UnicodeUtil.THETA, toControlGraph( pSwingCanvas, UnicodeUtil.THETA, "Angular Position", -Math.PI * 3, Math.PI * 3, Color.blue, rotationModel.getXVariable(), new PImage( loadArrow( "blue-arrow.png" ) ), true, cursorModel, rotationModel ) );
        angularVelocityMinimizableControlGraph = new MinimizableControlGraph( UnicodeUtil.OMEGA, toControlGraph( pSwingCanvas, UnicodeUtil.OMEGA, "Angular Velocity", -0.1, 0.1, Color.red, rotationModel.getVVariable(), new PImage( loadArrow( "red-arrow.png" ) ), true, cursorModel, rotationModel ) );
        angularAccelerationMinimizableControlGraph = new MinimizableControlGraph( UnicodeUtil.ALPHA, toControlGraph( pSwingCanvas, UnicodeUtil.ALPHA, "Angular Acceleration", -0.001, 0.001, Color.green, rotationModel.getAVariable(), new PImage( loadArrow( "green-arrow.png" ) ), true, cursorModel, rotationModel ) );

        ControlGraph positionControlGraph = toControlGraph( pSwingCanvas, "x", "Position", 0, 500, Color.blue, rotationModel.getXPositionVariable(), new PImage( loadArrow( "blue-arrow.png" ) ), false, cursorModel, rotationModel );
        positionControlGraph.addSeries( "Position", Color.red, "y", rotationModel.getYPositionVariable() );

        positionMinimizableControlGraph = new MinimizableControlGraph( "x,y", positionControlGraph );
        ControlGraph speedControlGraph = toControlGraph( pSwingCanvas, "|v|", "Linear Speed", 0, 0.1, Color.red, rotationModel.getSpeedVariable(), new PImage( loadArrow( "red-arrow.png" ) ), false, cursorModel, rotationModel );
        speedMinimizableControlGraph = new MinimizableControlGraph( "vx, vy", speedControlGraph );

        accelerationMinimizableControlGraph = new MinimizableControlGraph( "a", toControlGraph( pSwingCanvas, "a", "Centripetal Acceleration", 0, 0.001, Color.green, rotationModel.getCentripetalAcceleration(), new PImage( loadArrow( "green-arrow.png" ) ), false, cursorModel, rotationModel ) );

        //new GraphSuite( new GraphComponent[]{getAngleGraph(), getAngularVelocityGraph(), getPositionGraph()} ),//todo: remove after testing
        addGraphSuite( new GraphSuite( new MinimizableControlGraph[]{getAngleGraph(), getAngularVelocityGraph(), getPositionGraph(), getAngularAccelerationGraph()} ) );
        addGraphSuite( new GraphSuite( new MinimizableControlGraph[]{getAngleGraph(), getAngularVelocityGraph(), getAngularAccelerationGraph()} ) );
        addGraphSuite( new GraphSuite( new MinimizableControlGraph[]{getAngleGraph(), getAngularVelocityGraph(), getSpeedGraph()} ) );
        addGraphSuite( new GraphSuite( new MinimizableControlGraph[]{getAngleGraph(), getAngularVelocityGraph(), getAccelerationGraph()} ) );
        addGraphSuite( new GraphSuite( new MinimizableControlGraph[]{getAngleGraph(), getAngularVelocityGraph(), getAngularAccelerationGraph(), positionMinimizableControlGraph, speedMinimizableControlGraph, accelerationMinimizableControlGraph} ) );

        //todo: generalize this adapter code
        rotationModel.addListener( new MotionModel.Listener() {
            public void steppedInTime() {
                angleMinimizableControlGraph.addValue( rotationModel.getTime(), rotationModel.getPosition() );
                angularVelocityMinimizableControlGraph.addValue( rotationModel.getTime(), rotationModel.getVelocity() );
                angularAccelerationMinimizableControlGraph.addValue( rotationModel.getTime(), rotationModel.getAcceleration() );

                positionMinimizableControlGraph.addValue( 0, rotationModel.getTime(), rotationModel.getRotationBody( 0 ).getX() );
                positionMinimizableControlGraph.addValue( 1, rotationModel.getTime(), rotationModel.getRotationBody( 0 ).getY() );
                speedMinimizableControlGraph.addValue( rotationModel.getTime(), rotationModel.getRotationBody( 0 ).getVelocity().getMagnitude() );
                accelerationMinimizableControlGraph.addValue( rotationModel.getTime(), rotationModel.getRotationBody( 0 ).getAcceleration().getMagnitude() );
            }
        } );
        angleMinimizableControlGraph.addControlGraphListener( new ControlGraph.Adapter() {
            public void controlFocusGrabbed() {
                rotationModel.setPositionDriven();
            }
        } );
        angularVelocityMinimizableControlGraph.addControlGraphListener( new ControlGraph.Adapter() {
            public void controlFocusGrabbed() {
                rotationModel.setVelocityDriven();
            }
        } );
        angularAccelerationMinimizableControlGraph.addControlGraphListener( new ControlGraph.Adapter() {
            public void controlFocusGrabbed() {
                rotationModel.setAccelerationDriven();
            }
        } );
    }

    public MinimizableControlGraph getAngleGraph() {
        return angleMinimizableControlGraph;
    }

    public MinimizableControlGraph getAngularVelocityGraph() {
        return angularVelocityMinimizableControlGraph;
    }

    public MinimizableControlGraph getAngularAccelerationGraph() {
        return angularAccelerationMinimizableControlGraph;
    }

    public MinimizableControlGraph getPositionGraph() {
        return positionMinimizableControlGraph;
    }

    public MinimizableControlGraph getSpeedGraph() {
        return speedMinimizableControlGraph;
    }

    public MinimizableControlGraph getAccelerationGraph() {
        return accelerationMinimizableControlGraph;
    }

    public static void main( String[] args ) {
        RotationGraphSet rotationGraphSet = new RotationGraphSet( new PhetPCanvas(), new RotationModel() );
        MinimizableControlGraph[] graphs = rotationGraphSet.getAllGraphs();
        System.out.println( "graphs.length = " + graphs.length );
        System.out.println( "Arrays.asList( graphs ) = " + Arrays.asList( graphs ) );
    }
}
