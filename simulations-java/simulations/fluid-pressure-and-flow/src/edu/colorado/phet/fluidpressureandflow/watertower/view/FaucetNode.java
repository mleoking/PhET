// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.fluidpressureandflow.watertower.view;

import java.awt.*;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.colorado.phet.common.phetcommon.model.property.SettableProperty;
import edu.colorado.phet.common.phetcommon.util.PhetUtilities;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.view.VerticalLayoutPanel;
import edu.colorado.phet.common.phetcommon.view.controls.PropertyRadioButton;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.fluidpressureandflow.FluidPressureAndFlowApplication;
import edu.colorado.phet.fluidpressureandflow.common.FPAFStrings;
import edu.colorado.phet.fluidpressureandflow.watertower.model.FaucetFlowRate;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PImage;
import edu.umd.cs.piccolox.pswing.PSwing;

import static edu.colorado.phet.common.phetcommon.model.property.SettableNot.not;

/**
 * @author Sam Reid
 */
public class FaucetNode extends PNode {
    final static Color TRANSPARENT = new Color( 0, 0, 0, 0 );

    public static class FaucetRadioButton extends PropertyRadioButton<Boolean> {
        public FaucetRadioButton( String name, SettableProperty<Boolean> selected ) {
            super( name, selected, true );
            setBackground( TRANSPARENT );
            setFont( new PhetFont( 18, true ) );
        }
    }

    public FaucetNode( ModelViewTransform transform, final FaucetFlowRate faucetFlowLevel ) {
        addChild( new PImage( FluidPressureAndFlowApplication.RESOURCES.getImage( "faucet.png" ) ) {{
            setScale( 0.75 );
            setOffset( -27, 0 );
            final PSwing sliderNode = new PSwing( new JSlider( 0, 100 ) {{
                setBackground( TRANSPARENT );
                setPaintTicks( true );//to make the slider thumb wider on Windows 7
                setPreferredSize( new Dimension( 120, getPreferredSize().height ) );
                addChangeListener( new ChangeListener() {
                    public void stateChanged( ChangeEvent e ) {
                        faucetFlowLevel.flow.set( getValue() / 100.0 );
                    }
                } );
                faucetFlowLevel.flow.addObserver( new SimpleObserver() {
                    public void update() {
                        setValue( (int) ( faucetFlowLevel.flow.get() * 100 ) );
                    }
                } );
            }} ) {{
                translate( 186, -2 + ( PhetUtilities.isMacintosh() ? -8 : 0 ) );//Mac sliders render lower than windows slider, so have to compensate
                //Faucet slider should be invisible when in "auto" mode
                faucetFlowLevel.automatic.addObserver( new SimpleObserver() {
                    public void update() {
                        setVisible( !faucetFlowLevel.automatic.get() );
                    }
                } );
            }};
            addChild( sliderNode );
            addChild( new PSwing( new VerticalLayoutPanel() {{
                setInsets( new Insets( -6, 0, 0, 0 ) );//Bring the radio buttons a bit closer together
                add( new FaucetRadioButton( FPAFStrings.MATCH_LEAKAGE, faucetFlowLevel.automatic ) );
                add( new FaucetRadioButton( FPAFStrings.MANUAL, not( faucetFlowLevel.automatic ) ) );
                setBackground( TRANSPARENT );
            }} ) {{
                setOffset( sliderNode.getFullBounds().getMaxX() - getFullBounds().getWidth(),//Right align with slider
                           45 );
            }} );
        }} );
        setOffset( 20, 10 );
    }
}