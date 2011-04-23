// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.fluidpressureandflow.common.view;

import java.awt.*;

import javax.swing.*;

import edu.colorado.phet.common.piccolophet.nodes.ControlPanelNode;
import edu.colorado.phet.fluidpressureandflow.fluidpressure.FluidPressureControlPanel;
import edu.umd.cs.piccolox.pswing.PSwing;

/**
 * @author Sam Reid
 */
public class FluidPressureAndFlowControlPanelNode extends ControlPanelNode {
    public FluidPressureAndFlowControlPanelNode( JComponent controlPanel ) {
        super( new PSwing( controlPanel ), FluidPressureControlPanel.BACKGROUND, new BasicStroke( 1 ), Color.gray, 4 );
    }
}
