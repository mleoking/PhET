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
import edu.colorado.phet.fluidpressureandflow.fluidpressure.view.FluidPressureCanvas;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PText;
import edu.umd.cs.piccolox.pswing.PSwing;

/**
 * @author Sam Reid
 */
public class SliderControl extends PNode {
    public SliderControl( String title, String units, double min, double max, final Property<Double> property, final HashMap<Double, TickLabel> tickLabels ) {
        //Even though we only use the slider component of this linear value control, it is easier to create the whole LinearValueControl so that
        //We can use its facilities for settings ticks.
        //A better design would have been to move tickmark functionality to LinearSlider so we could just us it directly, see #2837
        final PSwing slider = new PSwing( new LinearValueControl( min, max, property.get(), "", "0.00", "" ) {
            {
                setTickLabels( new Hashtable<Object, Object>() {{
                    for ( Double s : tickLabels.keySet() ) {
                        put( s, tickLabels.get( s ) );
                    }
                }} );
                setMajorTicksVisible( false );
                setMinorTicksVisible( false );
                setFont( FluidPressureCanvas.CONTROL_FONT );
                getTextField().setColumns( 5 );
                setTextFieldVisible( false );

                FluidPressureCanvas.makeTransparent( this );
                addChangeListener( new ChangeListener() {
                    public void stateChanged( ChangeEvent e ) {
                        property.set( getValue() );
                    }
                } );
                property.addObserver( new SimpleObserver() {
                    public void update() {
                        setValue( property.get() );
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

        final HTMLNode htmlNode = new HTMLNode( units ) {{
            setFont( FluidPressureCanvas.CONTROL_FONT );
        }};

        //Create the top component which has: title textfield units
        final HBox topComponent = new HBox( new PText( title ) {{setFont( FluidPressureCanvas.CONTROL_FONT );}},
                                            new PSwing( new DoubleTextField( new DecimalFormat( "0.00" ), property, min, max ) {{
                                                setColumns( 6 );
                                                setFont( FluidPressureCanvas.CONTROL_FONT );
                                            }} ),
                                            htmlNode );

        //Top component goes over the slider
        addChild( new VBox( topComponent,
                            slider ) );

        //Move the HTML node up a bit since the <sup> superscript makes it off centered
        //This must be done after constructing the HBox because the HBox factors out its local offset in the layout.
        //Note: if HBox is rewritten to maintain its layout throughout child bound changes this will break
        //A better long term solution might be to make an HBox variant (or setting) that aligns baselines instead of centers (even though that might not work perfectly since HTMLNode bounds are a bit off)
        htmlNode.translate( 0, -3 );
    }
}