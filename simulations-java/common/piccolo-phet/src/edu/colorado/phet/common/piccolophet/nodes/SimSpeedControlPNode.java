// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.common.piccolophet.nodes;

import java.awt.*;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.colorado.phet.common.phetcommon.model.clock.ConstantDtClock;
import edu.colorado.phet.common.phetcommon.model.property.ObservableProperty;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.common.phetcommon.view.clock.SimSpeedControl;
import edu.colorado.phet.common.phetcommon.view.util.SwingUtils;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolox.pswing.PSwing;

/**
 * This class should be used when adding a speed slider to the floating clock control.
 * It provides the wrapper around a swing slider, hooks up the clock, and sets the initial position.
 * It can be used directly in the play area (doesn't need to be embedded in a swing control panel.
 *
 * @author Sam Reid
 */
public class SimSpeedControlPNode extends PNode {

    /*
     * This assumes a default range of clock speeds based on the current (which is presumably the default) clock dt setting, provided through a Property<Double> interface.
     *
     * @param maxPosX - The maximum x value within the floating clock control node, which may be the left edge of the rewind button (if present) or the left edge of the play button.
     * @param labelColors - The colors to show the labels, to add support for changing background colors.
     */
    public SimSpeedControlPNode( double min, final Property<Double> dt, double max, final double maxPosX, ObservableProperty<Color> labelColors ) {
        //SimSpeedControl requires a ConstantDtClock, so we create a dummy one that we can use to pass our Property<Double> dt through
        final ConstantDtClock clock = new ConstantDtClock( 30, dt.getValue() ) {{
            dt.addObserver( new VoidFunction1<Double>() {
                public void apply( Double aDouble ) {
                    setDt( dt.getValue() );
                }
            } );
            addConstantDtClockListener( new ConstantDtClockAdapter() {
                @Override public void dtChanged( ConstantDtClockEvent event ) {
                    dt.setValue( event.getClock().getDt() );
                }
            } );
        }};
        final SimSpeedControl simSpeedControl = new SimSpeedControl( min, max, clock ) {{
            SwingUtils.setBackgroundDeep( this, new Color( 0, 0, 0, 0 ) );
        }};
        addChild( new PSwing( simSpeedControl ) {{
            setOffset( maxPosX - getFullBoundsReference().width, 0 );
        }} );
        //Have to manually add a change listener to the sim speed control since it only reads values from the speed slider instead of writing values to it, see #2798
        simSpeedControl.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                clock.setDt( simSpeedControl.getValue() );
            }
        } );
    }
}