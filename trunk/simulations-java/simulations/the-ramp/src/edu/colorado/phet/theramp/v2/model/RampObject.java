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
    private boolean interacting;


    private static boolean started=false;
    public RampObject( double mass, ImmutableVector2D.Double position, ImmutableVector2D.Double v, ImmutableVector2D.Double a, double staticFrictionCoefficient, double kineticFrictionCoefficient, double angle ) {
        this( mass, position, v, a, staticFrictionCoefficient, kineticFrictionCoefficient, angle, false );
    }

    public RampObject( double mass, ImmutableVector2D.Double position, ImmutableVector2D.Double v, ImmutableVector2D.Double a, double staticFrictionCoefficient, double kineticFrictionCoefficient, double angle, boolean interacting ) {
        if (position.getMagnitude()>0){
            started=true;
        }
        if (started&&position.getMagnitude()==0){
            System.out.println( "" );
        }
        this.mass = mass;
        this.position = position;
        this.v = v;
        this.a = a;
        this.staticFrictionCoefficient = staticFrictionCoefficient;
        this.kineticFrictionCoefficient = kineticFrictionCoefficient;
        this.angle = angle;
        this.interacting = interacting;
    }

    public RampObject translate( double dx, double dy ) {
        return setPosition( position.getX() + dx, position.getY() + dy );
    }

    public ImmutableVector2D.Double getPosition() {
        return position;
    }

    public RampObject setPosition( double x, double y ) {
        return new RampObject( mass,
                               new ImmutableVector2D.Double( x, y ),
                               new ImmutableVector2D.Double( v.getX(), v.getY() ),
                               new ImmutableVector2D.Double( a.getX(), a.getY() ),
                               staticFrictionCoefficient, kineticFrictionCoefficient, angle, interacting );
    }

    public String toString() {
        return "RampObject{" +
               "mass=" + mass +
               ", position=" + position +
               ", v=" + v +
               ", a=" + a +
               ", staticFrictionCoefficient=" + staticFrictionCoefficient +
               ", kineticFrictionCoefficient=" + kineticFrictionCoefficient +
               ", angle=" + angle +
               '}';
    }

    public RampObject setInteracting( boolean interacting ) {
        return new RampObject( mass,
                               new ImmutableVector2D.Double( position.getX(), position.getY() ),
                               new ImmutableVector2D.Double( v.getX(), v.getY() ),
                               new ImmutableVector2D.Double( a.getX(), a.getY() ),
                               staticFrictionCoefficient, kineticFrictionCoefficient, angle, interacting );
    }

    public boolean isInteracting() {
        return interacting;
    }
}
