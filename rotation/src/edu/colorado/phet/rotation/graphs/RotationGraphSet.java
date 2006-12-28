package edu.colorado.phet.rotation.graphs;

import edu.colorado.phet.rotation.util.UnicodeUtil;
import edu.umd.cs.piccolox.pswing.PSwingCanvas;

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

    public RotationGraphSet( PSwingCanvas pSwingCanvas ) {
        angleGraph = new GraphComponent( pSwingCanvas, UnicodeUtil.THETA );
        angularVelocityGraph = new GraphComponent( pSwingCanvas, UnicodeUtil.OMEGA );
        angularAccelerationGraph = new GraphComponent( pSwingCanvas, UnicodeUtil.ALPHA );
        positionGraph = new GraphComponent( pSwingCanvas, "x,y" );
        velocityGraph = new GraphComponent( pSwingCanvas, "vx,vy" );
        accelerationGraph = new GraphComponent( pSwingCanvas, "a" );

        suites = new GraphSuite[]{
                new GraphSuite( new GraphComponent[]{getAngleGraph(), getAngularVelocityGraph(), getPositionGraph()} ),
                new GraphSuite( new GraphComponent[]{getAngleGraph(), getAngularVelocityGraph(), getAngularAccelerationGraph()} ),
                new GraphSuite( new GraphComponent[]{getAngleGraph(), getAngularVelocityGraph(), getVelocityGraph()} ),
                new GraphSuite( new GraphComponent[]{getAngleGraph(), getAngularVelocityGraph(), getAccelerationGraph()} ),
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

    public GraphSuite[] getGraphSuites() {
        return suites;
    }

    public GraphSuite getGraphSuite( int i ) {
        return suites[i];
    }
}
