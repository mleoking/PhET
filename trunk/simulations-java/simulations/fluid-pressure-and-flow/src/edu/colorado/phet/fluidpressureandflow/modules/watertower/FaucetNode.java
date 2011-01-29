// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.fluidpressureandflow.modules.watertower;

import java.awt.*;

import javax.swing.*;

import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
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
            setScale( 0.75 );
            setOffset( -27, 0 );
            final Color TRANSPARENT = new Color( 0, 0, 0, 0 );
            addChild( new PSwing( new JSlider() {{
                setBackground( TRANSPARENT );
                setPaintTicks( true );
                setPreferredSize( new Dimension( 120, getPreferredSize().height ) );
            }} ) {{
                translate( 186, 0 );
            }} );
            addChild( new PSwing( new JPanel() {{
                add( new JRadioButton( "Manual", true ) {{
                    setBackground( TRANSPARENT );
                    setFont( new PhetFont( 14, true ) );
                }} );
                add( new JRadioButton( "Auto", false ) {{
                    setBackground( TRANSPARENT );
                    setFont( new PhetFont( 14, true ) );
                }} );
                setBackground( TRANSPARENT );
            }} ) {{
                setScale( 1.4 );
                setOffset( 110, 45 );
            }} );
        }} );
        setOffset( 20, 10 );
    }
}