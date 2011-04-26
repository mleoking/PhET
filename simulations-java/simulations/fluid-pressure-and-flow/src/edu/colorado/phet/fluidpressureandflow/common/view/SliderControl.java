// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.fluidpressureandflow.common.view;

import java.util.HashMap;
import java.util.Hashtable;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.view.controls.valuecontrol.LinearValueControl;
import edu.colorado.phet.fluidpressureandflow.fluidpressure.FluidPressureCanvas;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolox.pswing.PSwing;

/**
 * @author Sam Reid
 */
public class SliderControl extends PNode {

    public SliderControl( String title, String units, double min, double max, final Property<Double> property, final HashMap<Double, TickLabel> tickLabels ) {
        final PSwing pswing = new PSwing( new LinearValueControl( min, max, property.getValue(), title, "0.00", units ) {
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
                FluidPressureCanvas.makeTransparent( this );
                addChangeListener( new ChangeListener() {
                    public void stateChanged( ChangeEvent e ) {
                        property.setValue( getValue() );
                    }
                } );
                property.addObserver( new SimpleObserver() {
                    public void update() {
                        setValue( property.getValue() );
                    }
                } );
            }

            @Override
            protected void updateTickLabels() {
                super.updateTickLabels();
                getSlider().setPaintLabels( true );
            }
        } ) {{
            scale( 1.2 );
        }};
        addChild( pswing );
    }

}
