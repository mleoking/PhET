package edu.colorado.phet.fluidpressureandflow.view;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import edu.colorado.phet.common.piccolophet.nodes.ButtonNode;
import edu.colorado.phet.fluidpressureandflow.FluidPressureAndFlowModule;
import edu.umd.cs.piccolo.PNode;

/**
* @author Sam Reid
*/
public class ResetButton extends PNode {
    public ResetButton( final FluidPressureAndFlowModule module ) {
        addChild( new ButtonNode( "Reset all", (int) ( FluidPressureControlPanel.CONTROL_FONT.getSize() * 1.3 ), FluidPressureControlPanel.BACKGROUND, FluidPressureControlPanel.FOREGROUND ) {{
        addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                module.resetAll();
            }
        } );
    }} );
    }
}
