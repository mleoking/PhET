// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.common.piccolophet.nodes.slider.simsharing;

import java.util.ArrayList;
import java.util.Collections;

import edu.colorado.phet.common.phetcommon.model.property.ObservableProperty;
import edu.colorado.phet.common.phetcommon.model.property.SettableProperty;
import edu.colorado.phet.common.phetcommon.simsharing.SimSharingManager;
import edu.colorado.phet.common.phetcommon.simsharing.messages.IUserComponent;
import edu.colorado.phet.common.phetcommon.simsharing.messages.ParameterKeys;
import edu.colorado.phet.common.phetcommon.simsharing.messages.UserComponentTypes;
import edu.colorado.phet.common.piccolophet.nodes.slider.HSliderNode;

import static edu.colorado.phet.common.phetcommon.simsharing.messages.ParameterKeys.*;
import static edu.colorado.phet.common.phetcommon.simsharing.messages.ParameterSet.parameterSet;
import static edu.colorado.phet.common.phetcommon.simsharing.messages.UserActions.endDrag;
import static edu.colorado.phet.common.phetcommon.simsharing.messages.UserActions.startDrag;

/**
 * HSliderNode that also sends message about drag events.
 *
 * @author Sam Reid
 */
public class SimSharingHSliderNode extends HSliderNode {

    private final IUserComponent userComponent;
    private final ArrayList<Double> dragValues = new ArrayList<Double>();

    public SimSharingHSliderNode( IUserComponent userComponent, final double min, final double max, final SettableProperty<Double> value ) {
        super( min, max, value );
        this.userComponent = userComponent;
    }

    public SimSharingHSliderNode( IUserComponent userComponent, final double min, final double max, final SettableProperty<Double> value, final ObservableProperty<Boolean> enabled ) {
        super( min, max, value, enabled );
        this.userComponent = userComponent;
    }

    public SimSharingHSliderNode( IUserComponent userComponent, final double min, final double max, double trackWidth, double trackHeight, final SettableProperty<Double> value, final ObservableProperty<Boolean> enabled ) {
        super( min, max, trackWidth, trackHeight, value, enabled );
        this.userComponent = userComponent;
    }

    @Override protected void dragStarted() {
        SimSharingManager.sendUserMessage( userComponent, UserComponentTypes.slider, startDrag, parameterSet( ParameterKeys.value, value.get() ) );
        dragValues.clear();
        dragValues.add( value.get() );
    }

    @Override protected void dragged() {
        super.dragged();
        dragValues.add( value.get() );
    }

    @Override protected void dragEnded() {
        dragValues.add( value.get() );
        SimSharingManager.sendUserMessage( userComponent, UserComponentTypes.slider, endDrag,
                                           parameterSet( ParameterKeys.value, value.get() ).
                                                   add( numberDragEvents, dragValues.size() ).
                                                   add( minValue, Collections.min( dragValues ) ).
                                                   add( maxValue, Collections.max( dragValues ) ).
                                                   add( averageValue, average( dragValues ) ) );
        dragValues.clear();
    }

    public static double average( ArrayList<Double> v ) {
        double sum = 0;
        for ( Double entry : v ) {
            sum += entry;
        }
        return sum / v.size();
    }
}
