// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.fractions.intro.matchinggame.model;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.model.property.BooleanProperty;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.fractions.intro.intro.model.Fraction;
import edu.umd.cs.piccolo.PNode;

/**
 * @author Sam Reid
 */
public abstract class Representation {
    public final Fraction fraction;
    public final Property<ImmutableVector2D> velocity = new Property<ImmutableVector2D>( new ImmutableVector2D( 0, 0 ) );
    public final double mass = 1;
    public final ImmutableVector2D force = new ImmutableVector2D( 0, 9.8 * mass * 300 );
    public final BooleanProperty dropped = new BooleanProperty( false );
    public final PNode node;
    public final BooleanProperty dragging = new BooleanProperty( false ) {{
        addObserver( new VoidFunction1<Boolean>() {
            public void apply( Boolean dragging ) {
                if ( dragging ) {
                    setOverPlatform( null );
                }
            }
        } );
    }};
    private PNode platform;

    protected Representation( ModelViewTransform transform, Fraction fraction, double x, double y ) {
        this.fraction = fraction;
        node = toNode( transform );
        setOffset( new ImmutableVector2D( x, y ) );
    }

    protected abstract PNode toNode( ModelViewTransform transform );

    public void setOffset( ImmutableVector2D immutableVector2D ) {
        node.setOffset( immutableVector2D.getX(), immutableVector2D.getY() );
    }

    public ImmutableVector2D getOffset() {
        return new ImmutableVector2D( node.getOffset() );
    }

    public void setOverPlatform( PNode platform ) {
        this.platform = platform;
    }

    public PNode getOverPlatform() {
        return platform;
    }

    public double getWeight() {
        return fraction.getValue();
    }
}