// edu.colorado.phet.idealgas.physics.body.Sphere
/*
 * User: Administrator
 * Date: Jan 5, 2003
 * Time: 8:09:49 AM
 */
package edu.colorado.phet.idealgas.physics.body;

//import edu.colorado.phet.physics.collision.CollisionLaw;
import edu.colorado.phet.physics.Vector2D;
import edu.colorado.phet.physics.body.Body;
import edu.colorado.phet.physics.collision.CollidableBody;
import edu.colorado.phet.physics.collision.CollisionFactory;


public abstract class Sphere extends CollidableBody {

    private float radius;
    private Vector2D loa = new Vector2D();

    protected Sphere( Vector2D center,
                      Vector2D velocity,
                      Vector2D acceleration,
                      float mass,
                      float radius,
                      float charge ) {
        super( center, velocity, acceleration, mass, 0 );
        this.radius = radius;
    }

    public Vector2D getCenter() {
        return this.getPosition();
    }

    public float getRadius() {
        return radius;
    }

    public void setRadius( float radius ) {
        this.radius = radius;
    }

    /**
     *
     */
    public boolean isInContactWithParticle( IdealGasParticle particle ) {
        double sepSq = this.getPosition().distanceSquared( particle.getPosition() );
        double temp = this.getRadius() + particle.getRadius();
        return temp * temp >= sepSq;
    }

    public void collideWithParticle( IdealGasParticle particle ) {
//        CollisionLaw collisionLaw = CollisionLaw.instance();
        loa.setX( this.getPosition().getX() - particle.getPosition().getX() );
        loa.setY( this.getPosition().getY() - particle.getPosition().getY() );
//        Vector2D loa = new Vector2D( this.getPosition().getX() - particle.getPosition().getX(),
//                                    this.getPosition().getY() - particle.getPosition().getY() );
        CollisionFactory.create( this, particle ).collide();
//        collisionLaw.collide( this, particle, loa );
    }

    public boolean isInContactWithBody( Body body ) {
        throw new RuntimeException( "Not implemented");
    }

    public float getContactOffset( Body body ) {
        return this.radius;
    }
}
