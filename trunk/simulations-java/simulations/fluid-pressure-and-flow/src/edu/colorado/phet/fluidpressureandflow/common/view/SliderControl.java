// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.fluidpressureandflow.common.view;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Hashtable;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.view.controls.valuecontrol.LinearValueControl;
import edu.colorado.phet.common.piccolophet.nodes.HTMLNode;
import edu.colorado.phet.common.piccolophet.nodes.layout.HBox;
import edu.colorado.phet.common.piccolophet.nodes.layout.VBox;
import edu.colorado.phet.fluidpressureandflow.pressure.view.FluidPressureCanvas;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PText;
import edu.umd.cs.piccolox.pswing.PSwing;

import static edu.colorado.phet.common.phetcommon.math.MathUtil.clamp;
import static edu.colorado.phet.fluidpressureandflow.common.view.FluidPressureAndFlowCanvas.makeTransparent;

//REVIEW this slider has issues on Mac, see #3101

/**
 * PSwing slider control customized with tick labels and using the Property[Double] interface
 * Even though we only use the slider component of this linear value control, it is easier to create the whole LinearValueControl so that
 * We can use its facilities for settings ticks.
 * A better design would have been to move tick mark functionality to LinearSlider so we could just us it directly, see #2837
 *
 * @author Sam Reid
 */
public class SliderControl extends PNode {

    public final HTMLNode unitsNode;

    public SliderControl( String title, String units, final double min, final double max, final Property<Double> property, final HashMap<Double, TickLabel> tickLabels ) {
        final PSwing slider = new PSwing( new LinearValueControl( min, max, property.get(), "", "0.00", "" ) {
            {

                //Show the tick labels on the linear value control
                setTickLabels( new Hashtable<Object, Object>() {{
                    for ( Double s : tickLabels.keySet() ) {
                        put( s, tickLabels.get( s ) );
                    }
                }} );
                setMajorTicksVisible( false );
                setMinorTicksVisible( false );

                //Make it look good
                setFont( FluidPressureCanvas.CONTROL_FONT );
                getTextField().setColumns( 5 );
                setTextFieldVisible( false );
                makeTransparent( this );

                //Wire up to property
                addChangeListener( new ChangeListener() {
                    public void stateChanged( ChangeEvent e ) {
                        property.set( getValue() );
                    }
                } );
                //Since ScaledDoubleProperty is rounded, values can get slightly outside min and max, so clamp here
                property.addObserver( new SimpleObserver() {
                    public void update() {
                        setValue( clamp( min, property.get(), max ) );
                    }
                } );
            }

            @Override
            protected void updateTickLabels() {
                super.updateTickLabels();
                getSlider().setPaintLabels( true );
            }
        }.getSlider() ) {{
            scale( 1.2 );
        }};

        unitsNode = new HTMLNode( units ) {{
            setFont( FluidPressureCanvas.CONTROL_FONT );
        }};

        //Create the top component which has: title text field units
        final HBox topComponent = new HBox( new PText( title ) {{setFont( FluidPressureCanvas.CONTROL_FONT );}},
                                            new PSwing( new DoubleTextField( new DecimalFormat( "0" ), property, min, max ) {{
                                                setColumns( 6 );
                                                setFont( FluidPressureCanvas.CONTROL_FONT );
                                            }} ),
                                            unitsNode );

        //Top component goes over the slider
        addChild( new VBox( topComponent,
                            slider ) );

        //Move the HTML node up a bit since the <sup> superscript makes it off centered
        //This must be done after constructing the HBox because the HBox factors out its local offset in the layout.
        //Note: if HBox is rewritten to maintain its layout throughout child bound changes this will break
        //A better long term solution might be to make an HBox variant (or setting) that aligns baselines instead of centers (even though that might not work perfectly since HTMLNode bounds are a bit off)
        unitsNode.translate( 0, -3 );
    }
}