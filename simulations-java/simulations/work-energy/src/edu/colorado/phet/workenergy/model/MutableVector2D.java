package edu.colorado.phet.workenergy.model;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.model.Observable;

/**
 * @author Sam Reid
 */
public class MutableVector2D extends Observable<ImmutableVector2D> {
    public MutableVector2D(double x, double y) {
        this(new ImmutableVector2D(x, y));
    }

    public MutableVector2D(ImmutableVector2D value) {
        super(value);
    }
}
