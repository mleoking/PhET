// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.fluidpressureandflow.modules.watertower;

import java.awt.*;

import javax.swing.*;

import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.fluidpressureandflow.FluidPressureAndFlowApplication;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PImage;
import edu.umd.cs.piccolox.pswing.PSwing;

/**
 * @author Sam Reid
 */
public class FaucetNode extends PNode {
    public FaucetNode( ModelViewTransform transform, FaucetFlowLevel faucetFlowLevel ) {
        addChild( new PImage( FluidPressureAndFlowApplication.RESOURCES.getImage( "faucet.png" ) ) {{
            setScale( 0.8 );
            setOffset( -27, 0 );

            addChild( new PSwing( new JSlider() {{
                setBackground( new Color( 0, 0, 0, 0 ) );
                setPaintTicks( true );
                setPreferredSize( new Dimension( 120, getPreferredSize().height ) );
            }} ) {{
                translate( 186, 0 );
            }} );
        }} );
    }
}
