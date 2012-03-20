// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.common.piccolophet.nodes.slider;

import java.awt.Paint;
import java.util.ArrayList;
import java.util.Collections;

import edu.colorado.phet.common.phetcommon.model.property.SettableProperty;
import edu.colorado.phet.common.phetcommon.simsharing.SimSharingManager;
import edu.colorado.phet.common.phetcommon.simsharing.messages.IUserComponent;
import edu.colorado.phet.common.phetcommon.simsharing.messages.ParameterKeys;
import edu.colorado.phet.common.phetcommon.simsharing.messages.UserComponentTypes;
import edu.umd.cs.piccolo.PNode;

import static edu.colorado.phet.common.phetcommon.simsharing.messages.ParameterKeys.*;
import static edu.colorado.phet.common.phetcommon.simsharing.messages.ParameterSet.parameterSet;
import static edu.colorado.phet.common.phetcommon.simsharing.messages.UserActions.endDrag;
import static edu.colorado.phet.common.phetcommon.simsharing.messages.UserActions.startDrag;

/**
 * Rewrite for SliderNode, should work at different orientations and support tick labels, etc.
 * See #1767
 *
 * @author Sam Reid
 */
public abstract class SliderNode extends PNode {

    private final IUserComponent userComponent;
    public final double min;
    public final double max;
    public final SettableProperty<Double> value;
    private final ArrayList<Double> dragValues = new ArrayList<Double>(); // accumulation of values during dragging

    public SliderNode( IUserComponent userComponent, double min, double max, SettableProperty<Double> value ) {
        this.userComponent = userComponent;
        this.min = min;
        this.max = max;
        this.value = value;
    }

    protected void dragStarted() {
        SimSharingManager.sendUserMessage( userComponent, UserComponentTypes.slider, startDrag, parameterSet( ParameterKeys.value, value.get() ) );
        dragValues.clear();
        dragValues.add( value.get() );
    }

    protected void dragged() {
        dragValues.add( value.get() );
    }

    protected void dragEnded() {
        dragValues.add( value.get() );
        SimSharingManager.sendUserMessage( userComponent, UserComponentTypes.slider, endDrag,
                                           parameterSet( ParameterKeys.value, value.get() ).
                                                   add( numberDragEvents, dragValues.size() ).
                                                   add( minValue, Collections.min( dragValues ) ).
                                                   add( maxValue, Collections.max( dragValues ) ).
                                                   add( averageValue, average( dragValues ) ) );
        dragValues.clear();
    }

    private static double average( ArrayList<Double> v ) {
        double sum = 0;
        for ( Double entry : v ) {
            sum += entry;
        }
        return sum / v.size();
    }

    public abstract void setTrackFillPaint( final Paint paint );
}