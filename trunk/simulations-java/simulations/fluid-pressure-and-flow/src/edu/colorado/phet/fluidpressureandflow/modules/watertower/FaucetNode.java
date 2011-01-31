// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.fluidpressureandflow.modules.watertower;

import java.awt.*;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.colorado.phet.common.phetcommon.model.SettableProperty;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.view.controls.PropertyRadioButton;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.fluidpressureandflow.FluidPressureAndFlowApplication;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PImage;
import edu.umd.cs.piccolox.pswing.PSwing;

import static edu.colorado.phet.common.phetcommon.model.Not.not;

/**
 * @author Sam Reid
 */
public class FaucetNode extends PNode {
    final static Color TRANSPARENT = new Color( 0, 0, 0, 0 );

    public static class RadioButton extends PropertyRadioButton<Boolean> {
        public RadioButton( String name, SettableProperty<Boolean> selected ) {
            super( name, selected, true );
            setBackground( TRANSPARENT );
            setFont( new PhetFont( 14, true ) );
        }
    }

    public FaucetNode( ModelViewTransform transform, final FaucetFlowLevel faucetFlowLevel ) {
        addChild( new PImage( FluidPressureAndFlowApplication.RESOURCES.getImage( "faucet.png" ) ) {{
            setScale( 0.75 );
            setOffset( -27, 0 );
            addChild( new PSwing( new JSlider( 0, 100 ) {{
                //Faucet slider should gray out when in "auto" mode
                faucetFlowLevel.automatic.addObserver( new SimpleObserver() {
                    public void update() {
                        setEnabled( !faucetFlowLevel.automatic.getValue() );
                    }
                } );
                setBackground( TRANSPARENT );
                setPaintTicks( true );//to make the slider thumb wider on Windows 7
                setPreferredSize( new Dimension( 120, getPreferredSize().height ) );
                addChangeListener( new ChangeListener() {
                    public void stateChanged( ChangeEvent e ) {
                        faucetFlowLevel.flow.setValue( getValue() / 100.0 );
                    }
                } );
                faucetFlowLevel.flow.addObserver( new SimpleObserver() {
                    public void update() {
                        setValue( (int) ( faucetFlowLevel.flow.getValue() * 100 ) );
                    }
                } );
            }} ) {{
                translate( 186, 0 );
            }} );
            addChild( new PSwing( new JPanel() {{
                add( new RadioButton( "Manual", not( faucetFlowLevel.automatic ) ) );
                add( new RadioButton( "Auto", faucetFlowLevel.automatic ) );
                setBackground( TRANSPARENT );
            }} ) {{
                setScale( 1.4 );
                setOffset( 110, 45 );
            }} );
        }} );
        setOffset( 20, 10 );
    }
}