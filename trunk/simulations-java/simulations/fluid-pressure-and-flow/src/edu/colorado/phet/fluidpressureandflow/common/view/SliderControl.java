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
import edu.colorado.phet.common.piccolophet.nodes.slider.HSliderNode;
import edu.colorado.phet.fluidpressureandflow.pressure.view.FluidPressureCanvas;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PText;
import edu.umd.cs.piccolox.pswing.PSwing;

/**
 * Slider control customized with tick labels and using the Property[Double] interface
 *
 * @author Sam Reid
 */
public class SliderControl extends PNode {

    public SliderControl( IUserComponent userComponent, String title, String units, final double min, final double max, final Property<Double> property, final HashMap<Double, String> tickLabels, final DecimalFormat format ) {
        HSliderNode slider = new HSliderNode( userComponent, min, max, property );
        for ( Double key : tickLabels.keySet() ) {
            slider.addLabel( key, new PText( tickLabels.get( key ) ) );
        }
        HTMLNode unitsNode = new HTMLNode( units ) {{
            setFont( FluidPressureCanvas.CONTROL_FONT );
        }};

        //Create the top component which has: title text field units
        final PSwing textField = new PSwing( new DoubleTextField( format, property, min, max ) {{
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