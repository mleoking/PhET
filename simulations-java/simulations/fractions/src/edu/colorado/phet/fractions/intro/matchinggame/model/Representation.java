// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.fractions.intro.matchinggame.model;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.fractions.intro.intro.model.Fraction;
import edu.umd.cs.piccolo.PNode;

/**
 * @author Sam Reid
 */
public abstract class Representation {
    public final Fraction fraction;
    public final Property<ImmutableVector2D> position;

    protected Representation( Fraction fraction, double x, double y ) {
        this.fraction = fraction;
        this.position = new Property<ImmutableVector2D>( new ImmutableVector2D( x, y ) );
    }

    public abstract PNode toNode( ModelViewTransform transform );
}