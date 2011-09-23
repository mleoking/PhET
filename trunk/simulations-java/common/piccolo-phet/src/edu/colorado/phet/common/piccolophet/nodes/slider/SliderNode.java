// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.common.piccolophet.nodes.slider;

import edu.colorado.phet.common.phetcommon.model.property.BooleanProperty;
import edu.colorado.phet.common.phetcommon.model.property.ObservableProperty;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.model.property.SettableProperty;
import edu.umd.cs.piccolo.PNode;

/**
 * Rewrite for SliderNode, should work at different orientations and support tick labels, etc.
 * See #1767
 *
 * @author Sam Reid
 */
public abstract class SliderNode extends PNode {
    public final SettableProperty<Boolean> enabled = new Property<Boolean>( true );
    public final double min;
    public final double max;
    public final SettableProperty<Double> value;

    public SliderNode( final double min, final double max, final SettableProperty<Double> value ) {
        this( min, max, value, new BooleanProperty( true ) );
    }

    public SliderNode( final double min, final double max, final SettableProperty<Double> value, final ObservableProperty<Boolean> enabled ) {
        this.min = min;
        this.max = max;
        this.value = value;
    }
}