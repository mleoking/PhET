// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.theramp.v2.model;

import edu.colorado.phet.common.phetcommon.math.vector.Vector2D;

public class RampObject {
    private double mass;
    private Vector2D position = new Vector2D();
    private Vector2D v = new Vector2D();
    private Vector2D a = new Vector2D();
    private double staticFrictionCoefficient;
    private double kineticFrictionCoefficient;
    private double angle;
    private boolean interacting;


    private static boolean started = false;

    public RampObject( double mass, Vector2D position, Vector2D v, Vector2D a, double staticFrictionCoefficient, double kineticFrictionCoefficient, double angle ) {
        this( mass, position, v, a, staticFrictionCoefficient, kineticFrictionCoefficient, angle, false );
    }

    public RampObject( double mass, Vector2D position, Vector2D v, Vector2D a, double staticFrictionCoefficient, double kineticFrictionCoefficient, double angle, boolean interacting ) {
        if ( position.magnitude() > 0 ) {
            started = true;
        }
        if ( started && position.magnitude() == 0 ) {
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

    public Vector2D getPosition() {
        return position;
    }

    public RampObject setPosition( double x, double y ) {
        return new RampObject( mass,
                               new Vector2D( x, y ),
                               new Vector2D( v.getX(), v.getY() ),
                               new Vector2D( a.getX(), a.getY() ),
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
                               new Vector2D( position.getX(), position.getY() ),
                               new Vector2D( v.getX(), v.getY() ),
                               new Vector2D( a.getX(), a.getY() ),
                               staticFrictionCoefficient, kineticFrictionCoefficient, angle, interacting );
    }

    public boolean isInteracting() {
        return interacting;
    }
}
