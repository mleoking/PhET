package edu.colorado.phet.theramp.v2.model;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;

public class RampObject {
    private double mass;
    private ImmutableVector2D.Double position = new ImmutableVector2D.Double();
    private ImmutableVector2D.Double v = new ImmutableVector2D.Double();
    private ImmutableVector2D.Double a = new ImmutableVector2D.Double();
    private double staticFrictionCoefficient;
    private double kineticFrictionCoefficient;
    private double angle;

    public RampObject( double mass, ImmutableVector2D.Double position, ImmutableVector2D.Double v, ImmutableVector2D.Double a, double staticFrictionCoefficient, double kineticFrictionCoefficient, double angle ) {
        this.mass = mass;
        this.position = position;
        this.v = v;
        this.a = a;
        this.staticFrictionCoefficient = staticFrictionCoefficient;
        this.kineticFrictionCoefficient = kineticFrictionCoefficient;
        this.angle = angle;
    }

    public RampObject translate( double dx, double dy ) {
        return new RampObject( mass,
                               new ImmutableVector2D.Double( position.getX() + dx, position.getY() + dy ),
                               new ImmutableVector2D.Double( v.getX(), v.getY() ),
                               new ImmutableVector2D.Double( a.getX(), a.getY() ),
                               staticFrictionCoefficient, kineticFrictionCoefficient, angle );
    }

    public ImmutableVector2D.Double getPosition() {
        return position;
    }
}
