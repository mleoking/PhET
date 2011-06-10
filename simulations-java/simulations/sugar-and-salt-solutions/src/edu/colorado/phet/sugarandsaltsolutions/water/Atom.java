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

    public Atom( double x, double y, final ModelViewTransform transform, final double r, final Body body ) {
        position = new Property<ImmutableVector2D>( new ImmutableVector2D( x, y ) );

        //Make it a bouncy circle
        circleDef = new CircleDef() {{
            restitution = 0.4f;
            density = 1;
            radius = (float) transform.modelToViewDeltaX( r );
        }};
        body.createShape( circleDef );

        //Particle interface for coulomb updates
        particle = new Particle() {
            public Vec2 getBox2DPosition() {
                return body.getPosition();
            }

            public double getCharge() {
                return -2;
            }

            public ImmutableVector2D getModelPosition() {
                return transform.viewToModel( new ImmutableVector2D( getBox2DPosition().x, getBox2DPosition().y ) );
            }

            public void setModelPosition( ImmutableVector2D immutableVector2D ) {
                ImmutableVector2D box2d = transform.modelToView( immutableVector2D );
                body.setXForm( new Vec2( (float) box2d.getX(), (float) box2d.getY() ), 0 );
            }
        };
    }
}