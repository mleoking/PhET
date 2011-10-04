// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.fluidpressureandflow.common.view;

import edu.umd.cs.piccolo.PNode;

//REVIEW purpose of this class? doesn't even seem necessary as a marker class.

/**
 * Tool box node that contains the velocity and pressure sensor nodes
 *
 * @author Sam Reid
 */
public class SensorToolBoxNode extends FluidPressureAndFlowControlPanelNode {
    public SensorToolBoxNode( final PNode content ) {
        super( content );
    }
}
