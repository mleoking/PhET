// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.fluidpressureandflow.common.view;

import java.text.DecimalFormat;
import java.util.HashMap;

import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.simsharing.messages.IUserComponent;
import edu.colorado.phet.common.piccolophet.nodes.HTMLNode;
import edu.colorado.phet.common.piccolophet.nodes.PhetPText;
import edu.colorado.phet.common.piccolophet.nodes.layout.HBox;
import edu.colorado.phet.common.piccolophet.nodes.layout.VBox;
import edu.colorado.phet.common.piccolophet.nodes.slider.HSliderNode2;
import edu.colorado.phet.common.piccolophet.nodes.slider.KnobNode2;
import edu.colorado.phet.common.piccolophet.nodes.slider.KnobNode2.Style;
import edu.colorado.phet.common.piccolophet.nodes.slider.VSliderNode;
import edu.colorado.phet.common.piccolophet.nodes.slider.VSliderNode2;
import edu.colorado.phet.fluidpressureandflow.FPAFSimSharing.UserComponents;
import edu.colorado.phet.fluidpressureandflow.pressure.view.FluidPressureCanvas;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PText;
import edu.umd.cs.piccolox.pswing.PSwing;

import static edu.colorado.phet.common.phetcommon.simsharing.messages.UserComponentChain.chain;

/**
 * Slider control customized with tick labels and using the Property[Double] interface
 *
 * @author Sam Reid
 */
public class SliderControl extends PNode {

    public SliderControl( IUserComponent userComponent, String title, String units, final double min, final double max, final Property<Double> property, final HashMap<Double, String> tickLabels, final DecimalFormat format, Style style ) {
        HSliderNode2 slider = new HSliderNode2( chain( userComponent, UserComponents.slider ), min, max, new KnobNode2( style ),
                                                VSliderNode.DEFAULT_TRACK_THICKNESS, VSliderNode.DEFAULT_TRACK_LENGTH, property, new Property<Boolean>( true ), VSliderNode2.GRADIENT_TRACK_PAINT );

        for ( Double key : tickLabels.keySet() ) {
            slider.addLabel( key, new PText( tickLabels.get( key ) ) );
        }
        HTMLNode unitsNode = new HTMLNode( units ) {{
            setFont( FluidPressureCanvas.CONTROL_FONT );
        }};

        //Create the top component which has: title text field units
        final PSwing textField = new PSwing( new DoubleTextField( chain( userComponent, UserComponents.textField ), format, property, min, max ) {{
            setColumns( 6 );
            setFont( FluidPressureCanvas.CONTROL_FONT );
        }} );
        final HBox topComponent = new HBox( new PhetPText( title, FluidPressureCanvas.CONTROL_FONT ),
                                            textField,
                                            unitsNode );

        //Top component goes over the slider
        addChild( new VBox( topComponent, slider ) );

        //Move the HTML node up a bit since the <sup> superscript makes it off centered
        //This must be done after constructing the HBox because the HBox factors out its local offset in the layout.
        //Note: if HBox is rewritten to maintain its layout throughout child bound changes this will break
        //A better long term solution might be to make an HBox variant (or setting) that aligns baselines instead of centers (even though that might not work perfectly since HTMLNode bounds are a bit off)
        unitsNode.translate( 0, -3 );
    }
}