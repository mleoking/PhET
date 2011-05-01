// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.fluidpressureandflow.common.view;

import java.awt.*;

import javax.swing.*;

import edu.colorado.phet.common.piccolophet.nodes.ControlPanelNode;
import edu.colorado.phet.fluidpressureandflow.fluidpressure.view.FluidPressureControlPanel;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolox.pswing.PSwing;

/**
 * ControlPanelNode used to show controls in the play area of Fluid Pressure and Flow
 *
 * @author Sam Reid
 */
public class FluidPressureAndFlowControlPanelNode extends ControlPanelNode {
    public FluidPressureAndFlowControlPanelNode( JComponent controlPanel ) {
        this( new PSwing( controlPanel ) );
    }

    public FluidPressureAndFlowControlPanelNode( PNode content ) {
        super( content, FluidPressureControlPanel.BACKGROUND, new BasicStroke( 1 ), Color.gray, 4 );
    }
}
