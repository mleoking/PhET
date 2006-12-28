package edu.colorado.phet.rotation.graphs;

import edu.colorado.phet.rotation.util.UnicodeUtil;

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
    private RotationGraphSuite[] suites;

    public RotationGraphSet() {
        angleGraph = new GraphComponent( UnicodeUtil.THETA );
        angularVelocityGraph = new GraphComponent( UnicodeUtil.OMEGA );
        angularAccelerationGraph = new GraphComponent( UnicodeUtil.ALPHA );
        positionGraph = new GraphComponent( "x,y" );
        velocityGraph = new GraphComponent( "vx,vy" );
        accelerationGraph = new GraphComponent( "a" );

        suites = new RotationGraphSuite[]{
                new RotationGraphSuite( new GraphComponent[]{getAngleGraph(), getAngularVelocityGraph(), getPositionGraph()} ),
                new RotationGraphSuite( new GraphComponent[]{getAngleGraph(), getAngularVelocityGraph(), getAngularAccelerationGraph()} ),
                new RotationGraphSuite( new GraphComponent[]{getAngleGraph(), getAngularVelocityGraph(), getVelocityGraph()} ),
                new RotationGraphSuite( new GraphComponent[]{getAngleGraph(), getAngularVelocityGraph(), getAccelerationGraph()} ),
        };
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

    public RotationGraphSuite[] getRotationGraphSuites() {
        return suites;
    }
}
