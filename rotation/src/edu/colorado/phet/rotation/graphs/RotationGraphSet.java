package edu.colorado.phet.rotation.graphs;

import edu.colorado.phet.rotation.model.RotationModel;
import edu.colorado.phet.rotation.util.UnicodeUtil;
import edu.umd.cs.piccolox.pswing.PSwingCanvas;

import java.awt.*;

/**
 * User: Sam Reid
 * Date: Dec 28, 2006
 * Time: 8:23:39 AM
 * Copyright (c) Dec 28, 2006 by Sam Reid
 */

public class RotationGraphSet {
    private GraphComponent angleGraph;
    private GraphComponent angularVelocityGraph;
    private GraphComponent angularAccelerationGraph;
    private GraphComponent positionGraph;
    private GraphComponent velocityGraph;
    private GraphComponent accelerationGraph;
    private GraphSuite[] suites;

    public RotationGraphSet( PSwingCanvas pSwingCanvas, final RotationModel rotationModel ) {
        angleGraph = new GraphComponent( pSwingCanvas, UnicodeUtil.THETA, "Angular Position", Math.PI, Color.blue, rotationModel.getXVariable() );
        angularVelocityGraph = new GraphComponent( pSwingCanvas, UnicodeUtil.OMEGA, "Angular Velocity", 2, Color.red, rotationModel.getVVariable() );
        angularAccelerationGraph = new GraphComponent( pSwingCanvas, UnicodeUtil.ALPHA, "Angular Acceleration", 0.01, Color.green, rotationModel.getAVariable() );

        positionGraph = new GraphComponent( pSwingCanvas, "x,y", "Position", 1, Color.blue, rotationModel.getXPositionVariable() );
        velocityGraph = new GraphComponent( pSwingCanvas, "vx,vy", "Linear Velocity", 5, Color.red, rotationModel.getLinearVelocity() );
        accelerationGraph = new GraphComponent( pSwingCanvas, "a", "Centripetal Acceleration", 2, Color.green, rotationModel.getCentripetalAcceleration() );

        suites = new GraphSuite[]{
                new GraphSuite( new GraphComponent[]{getAngleGraph(), getAngularVelocityGraph(), getPositionGraph()} ),
                new GraphSuite( new GraphComponent[]{getAngleGraph(), getAngularVelocityGraph(), getAngularAccelerationGraph()} ),
                new GraphSuite( new GraphComponent[]{getAngleGraph(), getAngularVelocityGraph(), getVelocityGraph()} ),
                new GraphSuite( new GraphComponent[]{getAngleGraph(), getAngularVelocityGraph(), getAccelerationGraph()} ),
        };

        rotationModel.addListener( new RotationModel.Listener() {
            public void steppedInTime() {
                angleGraph.addValue( rotationModel.getLastState().getTime(), rotationModel.getLastState().getAngle() );
                angularVelocityGraph.addValue( rotationModel.getLastState().getTime(), rotationModel.getLastState().getAngularVelocity() );
                angularAccelerationGraph.addValue( rotationModel.getLastState().getTime(), rotationModel.getLastState().getAngularAcceleration() );

                positionGraph.addValue( rotationModel.getLastState().getTime(), rotationModel.getLastState().getBody( 0 ).getX( rotationModel.getLastState() ) );
                velocityGraph.addValue( rotationModel.getLastState().getTime(), rotationModel.getLastState().getBody( 0 ).getVelocity( rotationModel.getLastState() ).getMagnitude() );
                accelerationGraph.addValue( rotationModel.getLastState().getTime(), rotationModel.getLastState().getBody( 0 ).getAcceleration( rotationModel.getLastState() ).getMagnitude() );
            }
        } );
        angleGraph.addControlGraphListener( new ControlGraph.Listener() {
            public void mousePressed() {
                rotationModel.setPositionDriven();
            }

            public void valueChanged() {
            }
        } );
        angularVelocityGraph.addControlGraphListener( new ControlGraph.Listener() {
            public void mousePressed() {
                rotationModel.setVelocityDriven();
            }

            public void valueChanged() {
            }
        } );
        angularAccelerationGraph.addControlGraphListener( new ControlGraph.Listener() {
            public void mousePressed() {
                rotationModel.setAccelerationDriven();
            }

            public void valueChanged() {
            }
        } );
    }

    public GraphComponent getAngleGraph() {
        return angleGraph;
    }

    public GraphComponent getAngularVelocityGraph() {
        return angularVelocityGraph;
    }

    public GraphComponent getAngularAccelerationGraph() {
        return angularAccelerationGraph;
    }

    public GraphComponent getPositionGraph() {
        return positionGraph;
    }

    public GraphComponent getVelocityGraph() {
        return velocityGraph;
    }

    public GraphComponent getAccelerationGraph() {
        return accelerationGraph;
    }

    public GraphSuite[] getGraphSuites() {
        return suites;
    }

    public GraphSuite getGraphSuite( int i ) {
        return suites[i];
    }
}
