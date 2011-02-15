// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.lightreflectionandrefraction.modules.prisms;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;

/**
 * @author Sam Reid
 */
public class Intersection {
    private ImmutableVector2D unitNormal;
    private ImmutableVector2D point;

    public Intersection( ImmutableVector2D unitNormal, ImmutableVector2D point ) {
        this.unitNormal = unitNormal;
        this.point = point;
    }

    public ImmutableVector2D getPoint() {
        return point;
    }

    public ImmutableVector2D getUnitNormal() {
        return unitNormal;
    }
}
