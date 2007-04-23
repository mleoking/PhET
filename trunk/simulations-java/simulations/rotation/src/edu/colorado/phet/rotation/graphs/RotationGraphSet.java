package edu.colorado.phet.rotation.graphs;

import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.common.piccolophet.nodes.ZoomControlNode;
import edu.colorado.phet.common.piccolophet.util.PImageFactory;
import edu.colorado.phet.rotation.model.RotationModel;
import edu.colorado.phet.rotation.model.SimulationVariable;
import edu.colorado.phet.rotation.util.UnicodeUtil;
import edu.umd.cs.piccolo.PNode;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;

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
    private GraphComponent speedGraph;
    private GraphComponent accelerationGraph;
    private ArrayList graphComponents = new ArrayList();
    private GraphSuite[] suites;

    public RotationGraphSet( PhetPCanvas pSwingCanvas, final RotationModel rotationModel ) {
        angleGraph = new GraphComponent( UnicodeUtil.THETA, toControlGraph( pSwingCanvas, UnicodeUtil.THETA, "Angular Position", -Math.PI * 3, Math.PI * 3, Color.blue, rotationModel.getXVariable(), PImageFactory.create( "images/blue-arrow.png" ), true ) );
        angularVelocityGraph = new GraphComponent( UnicodeUtil.OMEGA, toControlGraph( pSwingCanvas, UnicodeUtil.OMEGA, "Angular Velocity", -0.1, 0.1, Color.red, rotationModel.getVVariable(), PImageFactory.create( "images/red-arrow.png" ), true ) );
        angularAccelerationGraph = new GraphComponent( UnicodeUtil.ALPHA, toControlGraph( pSwingCanvas, UnicodeUtil.ALPHA, "Angular Acceleration", -0.001, 0.001, Color.green, rotationModel.getAVariable(), PImageFactory.create( "images/green-arrow.png" ), true ) );

        ControlGraph positionControlGraph = toControlGraph( pSwingCanvas, "x", "Position", -1.2, 1.2, Color.blue, rotationModel.getXPositionVariable(), PImageFactory.create( "images/blue-arrow.png" ), false );
        positionControlGraph.addSeries( "Position", Color.red, "y", rotationModel.getYPositionVariable() );

        positionGraph = new GraphComponent( "x,y", positionControlGraph );
        ControlGraph speedControlGraph = toControlGraph( pSwingCanvas, "|v|", "Linear Speed", 0, 0.1, Color.red, rotationModel.getSpeedVariable(), PImageFactory.create( "images/red-arrow.png" ), false );
        speedGraph = new GraphComponent( "vx, vy", speedControlGraph );

        accelerationGraph = new GraphComponent( "a", toControlGraph( pSwingCanvas, "a", "Centripetal Acceleration", 0, 0.001, Color.green, rotationModel.getCentripetalAcceleration(), PImageFactory.create( "images/green-arrow.png" ), false ) );
        graphComponents.addAll( Arrays.asList( new GraphComponent[]{angleGraph, angularVelocityGraph, angularAccelerationGraph, positionGraph, speedGraph, accelerationGraph} ) );
        suites = new GraphSuite[]{
                new GraphSuite( new GraphComponent[]{getAngleGraph(), getAngularVelocityGraph(), getPositionGraph()} ),
                new GraphSuite( new GraphComponent[]{getAngleGraph(), getAngularVelocityGraph(), getAngularAccelerationGraph()} ),
                new GraphSuite( new GraphComponent[]{getAngleGraph(), getAngularVelocityGraph(), getSpeedGraph()} ),
                new GraphSuite( new GraphComponent[]{getAngleGraph(), getAngularVelocityGraph(), getAccelerationGraph()} ),
                new GraphSuite( new GraphComponent[]{getAngleGraph(), getAngularVelocityGraph(), getAngularAccelerationGraph(), positionGraph, speedGraph, accelerationGraph} )
        };

        rotationModel.addListener( new RotationModel.Listener() {
            public void steppedInTime() {
                angleGraph.addValue( rotationModel.getLastState().getTime(), rotationModel.getLastState().getAngle() );
                angularVelocityGraph.addValue( rotationModel.getLastState().getTime(), rotationModel.getLastState().getAngularVelocity() );
                angularAccelerationGraph.addValue( rotationModel.getLastState().getTime(), rotationModel.getLastState().getAngularAcceleration() );

                positionGraph.addValue( 0, rotationModel.getLastState().getTime(), rotationModel.getLastState().getBody( 0 ).getX( rotationModel.getLastState() ) );
                positionGraph.addValue( 1, rotationModel.getLastState().getTime(), rotationModel.getLastState().getBody( 0 ).getY( rotationModel.getLastState() ) );
                speedGraph.addValue( rotationModel.getLastState().getTime(), rotationModel.getLastState().getBody( 0 ).getVelocity( rotationModel.getLastState() ).getMagnitude() );
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

    ControlGraph toControlGraph( PhetPCanvas pSwingCanvas, String label, String title, double min, double max, Color color, SimulationVariable simulationVariable, PNode thumb, boolean editable ) {
        final ControlGraph controlGraph = new ControlGraph( pSwingCanvas, simulationVariable, label, title, min, max, color, thumb );
        controlGraph.addHorizontalZoomListener( new ZoomControlNode.ZoomListener() {
            public void zoomedOut() {
                horizontalZoomChanged( controlGraph.getMaxDataX() );
            }

            public void zoomedIn() {
                horizontalZoomChanged( controlGraph.getMaxDataX() );
            }
        } );
        controlGraph.setEditable( editable );
        return controlGraph;
    }

    private void horizontalZoomChanged( double maxDataX ) {
        for( int i = 0; i < graphComponents.size(); i++ ) {
            GraphComponent graphComponent = (GraphComponent)graphComponents.get( i );
            graphComponent.getControlGraph().setDomainUpperBound( maxDataX );
        }
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

    public GraphComponent getSpeedGraph() {
        return speedGraph;
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

    public void clear() {
        angleGraph.clear();
        angularVelocityGraph.clear();
        angularAccelerationGraph.clear();

        positionGraph.clear();
        positionGraph.clear();
        speedGraph.clear();
        accelerationGraph.clear();
    }
}
