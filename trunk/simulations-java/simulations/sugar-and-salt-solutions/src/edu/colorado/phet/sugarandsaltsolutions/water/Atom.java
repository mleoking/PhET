// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.sugarandsaltsolutions.water;

import org.jbox2d.collision.CircleDef;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;

/**
 * @author Sam Reid
 */
public class Atom {
    public final CircleDef circleDef;
    public final Property<ImmutableVector2D> position;
    public final Particle particle;
    public final ImmutableVector2D modelOffset;

    public Atom( double x, double y, final ModelViewTransform transform, final double r, final Body body, final double localOffsetX, final double localOffsetY, final double charge, final boolean origin ) {
        modelOffset = new ImmutableVector2D( localOffsetX, localOffsetY );
        position = new Property<ImmutableVector2D>( new ImmutableVector2D( x, y ) );

        //Make it a bouncy circle
        circleDef = new CircleDef() {{
            restitution = 0.4f;
            density = 1;
            ImmutableVector2D boxOffset = transform.modelToViewDelta( new ImmutableVector2D( localOffsetX, localOffsetY ) );
            localPosition = new Vec2( (float) boxOffset.getX(), (float) boxOffset.getY() );
            radius = (float) transform.modelToViewDeltaX( r );
        }};
        body.createShape( circleDef );

        //Particle interface for coulomb updates
        particle = new Particle() {
            public Vec2 getBox2DPosition() {
                if ( origin ) {
                    return body.getPosition();
                }
                else {
                    ImmutableVector2D x = transform.modelToView( position.get() );
                    return new Vec2( (float) x.getX(), (float) x.getY() );
                }
            }

            public double getCharge() {
                return charge;
            }

            public ImmutableVector2D getModelPosition() {
                return transform.viewToModel( new ImmutableVector2D( getBox2DPosition().x, getBox2DPosition().y ) );
            }

            public void setModelPosition( ImmutableVector2D immutableVector2D ) {
                if ( origin ) {
                    ImmutableVector2D box2d = transform.modelToView( immutableVector2D );
                    body.setXForm( new Vec2( (float) box2d.getX(), (float) box2d.getY() ), 0 );
                }
            }
        };
    }
}