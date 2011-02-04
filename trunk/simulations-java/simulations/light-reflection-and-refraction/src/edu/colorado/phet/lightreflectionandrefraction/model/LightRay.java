// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.lightreflectionandrefraction.model;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.model.Property;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;

/**
 * @author Sam Reid
 */
public class LightRay {
    public final Property<ImmutableVector2D> tip;
    public final Property<ImmutableVector2D> tail;

    public LightRay( Property<ImmutableVector2D> tip, Property<ImmutableVector2D> tail ) {
        this.tip = tip;
        this.tail = tail;
    }

    public void addObserver( SimpleObserver simpleObserver ) {
        tip.addObserver( simpleObserver );
        tail.addObserver( simpleObserver );
    }
}
