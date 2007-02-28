/**
 * Class: Particle
 * Package: edu.colorado.phet.physics.body
 * User: Ron LeMaster
 * Date: Jan 21, 2003
 * Time: 3:28:24 PM
 */
package edu.colorado.phet.physics.body;

import edu.colorado.phet.physics.Constraint;
import edu.colorado.phet.physics.PhysicalSystem;
import edu.colorado.phet.physics.Vector2D;
import edu.colorado.phet.controller.PhetApplication;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Observable;
import java.util.Observer;

/**
 * Represents a body with Newtonian kinematics. It main responsibility it to implement
 * stepInTime(), which requires it to update its state for a specified time interval.
 * <p>
 * A body can contain other bodies. This is useful for defining certain constraints
 * <p>
 * A body keeps trakc of its current position, velocity and acceleration, and also
 * remembers its position, velocity and acceleration prior to it's last step in time.
 */
public abstract class Particle extends PhysicalEntity {

/*
    private PublicObservable o = new PublicObservable();
    public void notifyObservers() {
        o.notifyObservers();
    }

    public void setChanged() {
        o.setChanged();
    }

    public void addObserver( Observer co) {
        o.addObserver( new ParticleObserverAdapter( co ));
    }
*/

    // The physical system to which the body belongs;
    private PhysicalSystem physicalSystem;
    // List of bodies contained within this body
    private ArrayList containedBodies = new ArrayList();
    // Current state of the body
    protected Vector2D position;
    protected Vector2D velocity = new Vector2D();
    protected Vector2D acceleration;
    protected float  mass;
    protected float  charge;
    private Vector2D impartedForce;
    // Previous state of the body at end of last time step
    protected Vector2D positionPrev;
    protected Vector2D velocityPrev = new Vector2D();
    protected Vector2D accelerationPrev;
    protected float  massPrev;
    protected float  chargePrev;
    // Initial state of the body
    protected Vector2D positionInitial;
    protected Vector2D velocityInitial;
    protected Vector2D accelerationInitial;
    protected float  massInitial;
    protected float  chargeInitial;

    // List of contraints that must be applied to the Particle's state
    // at the end of each stepInTime
    protected ArrayList constraints = new ArrayList();
    // Working copy of constraint list used in case a constraint
    // needs to modify the real constraint list
    private ArrayList workingList = new ArrayList();


    /**
     * Constructors and initializers
     */
    protected Particle() {
        this( new Vector2D(), new Vector2D(), new Vector2D(), s_defaultMass );
    }

    protected Particle( Vector2D position, Vector2D velocity,
                    Vector2D acceleration, float  mass ) {
        this( position, velocity, acceleration, mass, s_defaultCharge );
    }

    protected Particle( Vector2D position, Vector2D velocity,
                    Vector2D acceleration, float  mass, float  charge ) {

        this.position = position;
        this.velocity = velocity;
        this.acceleration = acceleration;
        this.mass = mass;
        this.charge = charge;

        // Set previous position based on current position and velocity
        float  prevX = position.getX() - velocity.getX() * PhysicalSystem.instance().getDt();
        float  prevY = position.getY() - velocity.getY() * PhysicalSystem.instance().getDt();
        this.positionPrev = new Vector2D( prevX, prevY );

        this.velocityPrev = new Vector2D( velocity );
        this.accelerationPrev = new Vector2D( acceleration );
        this.massPrev = mass;
        this.chargePrev = charge;

        this.positionInitial = new Vector2D( position );
        this.velocityInitial = new Vector2D( velocity );
        this.accelerationInitial = new Vector2D( acceleration );
        this.massInitial = mass;
        this.chargeInitial = charge;
    }

    public void reInitialize() {
        position = new Vector2D( positionInitial );
        velocity = new Vector2D( velocityInitial );
        acceleration = new Vector2D( accelerationInitial );
        mass = massInitial;
        charge = chargeInitial;
    }

    /**
     * Integrates the kinematics of the body over a specified time interval
     */
    public void stepInTime( float  dt ) {
        stepInTimeNoNotify( dt );

        // Tell our observers that we have changed
        this.setChanged();
        this.notifyObservers();
    }

    /**
     * Performs a step in time, but does not notify observers. This is provided so that an application
     * can compute where a body might be at a particular time without it being redisplayed
     */
    public void stepInTimeNoNotify( float  dt ) {

        // If the body is infinitely massive, that means it is fixed
        if( this.getMass() != Double.POSITIVE_INFINITY ) {
            this.setPosition( position.getX() + dt * velocity.getX() + dt * dt * acceleration.getX() / 2,
                              position.getY() + dt * velocity.getY() + dt * dt * acceleration.getY() / 2 );

            this.setVelocity( velocity.getX() + dt * acceleration.getX(),
                              velocity.getY() + dt * acceleration.getY() );
        }

        // Iterate the receiver's constraints. We iterate a copy of the list, in case
        // any of the constraints need to add or remove constraints from the list
        workingList.clear();
        workingList.addAll( constraints );
        for( Iterator iterator = workingList.iterator(); iterator.hasNext(); ) {
            Constraint constraintSpec = (Constraint)iterator.next();
            constraintSpec.apply();
        }
    }

    public String toString() {
        return "Position=" + position + ", Velocity=" + velocity + ", Acceleration=" + acceleration + ", Mass=" + mass + ", Charge=" + charge;
    }

    /**
     *
     * @return
     */
    public float  getKineticEnergy() {
        float  kineticEnergy = 0;
        if( mass != Double.POSITIVE_INFINITY ) {
            float  currSpeed = this.getSpeed();
            kineticEnergy = mass * currSpeed * currSpeed / 2;
        }
        return kineticEnergy;
    }

    /**
     * Sets the particle's kinetic energy to a specified value by scaling
     * its velocity. Note that this will fail if the particle's mass or
     * current speed is 0.
     * @param kineticEnergy
     */
    public void setKineticEnergy( float  kineticEnergy ) {
        float  currSpeed = this.getSpeed();
        float  newSpeed = (float)Math.sqrt( 2 * kineticEnergy / mass );
        float  scale = newSpeed / currSpeed;
        this.velocity.multiply( scale );
    }

    /**
     * Getters and setters
     */
    public void setCharge( float  charge ) {
        this.chargePrev = this.charge;
        this.charge = charge;
    }

    public float  getCharge() {
        return charge;
    }

    public void setMass( float  mass ) {
        this.massPrev = this.mass;
        this.mass = mass;
    }

    public float  getMass() {
        return mass;
    }

    public void clearAcceleration() {
        this.accelerationPrev.setX( this.acceleration.getX() );
        this.accelerationPrev.setY( this.acceleration.getY() );
        this.setAcceleration( 0, 0 );
    }

    // TODO: Determine if previous acceleration is needed. If not, get rid
    // of it
    public void setAcceleration( Vector2D acceleration ) {
        //this.accelerationPrev = this.acceleration;
        this.acceleration = acceleration;
    }

    public void setAcceleration( float  x, float  y ) {
        //        this.accelerationPrev.setX( this.acceleration.getX() );
        //        this.accelerationPrev.setY( this.acceleration.getY() );
        this.acceleration.setX( x );
        this.acceleration.setY( y );
    }

    public Vector2D getAcceleration() {
        return acceleration;
    }

    public Vector2D getVelocity() {
        return velocity;
    }

    public Vector2D getVelocityPrev() {
        return velocityPrev;
    }

    public float  getSpeed() {
        return velocity.getLength();
    }

    public void setVelocity( Vector2D velocity ) {
        this.velocityPrev.setX( this.velocity.getX() );
        this.velocityPrev.setY( this.velocity.getY() );
        this.velocity = velocity;
    }

    public void setVelocity( float  vX, float  vY ) {
        this.setVelocityX( vX );
        this.setVelocityY( vY );
    }

    public void setVelocityX( float  vX ) {
//        if( Double.isNaN( vX ) ) {
//            System.out.println( "Particle.setVelocityX : x is NaN" );
//        }
        this.velocityPrev.setX( this.velocity.getX() );
        this.velocity.setX( vX );
    }

    public void setVelocityY( float  vY ) {
//        if( Double.isNaN( vY ) ) {
//            System.out.println( "Particle.setVelocityY : y is NaN" );
//        }
        this.velocityPrev.setY( this.velocity.getY() );
        this.velocity.setY( vY );
    }

    public Vector2D getPosition() {
        return position;
    }

    public Vector2D getPositionPrev() {
        return positionPrev;
    }

    public void setPosition( float  x, float  y ) {
        setPositionX( x );
        setPositionY( y );
    }

    public void setPositionX( float  x ) {

//        if( Double.isNaN( x ) ) {
//            System.out.println( "Particle.setPositionX : x is NaN" );
//        }
        this.positionPrev.setX( this.position.getX() );
        this.position.setX( x );
        setChanged();
        notifyObservers();
    }

    public void setPositionY( float  y ) {

//        if( Double.isNaN( y ) ) {
//            System.out.println( "Particle.setPositionY : y is NaN" );
//        }
        this.positionPrev.setY( this.position.getY() );
        this.position.setY( y );
        setChanged();
        notifyObservers();
    }

    public void setPosition( Vector2D position ) {
        this.positionPrev = this.position;
        this.position = position;
        setChanged();
        notifyObservers();
    }

    public void setPosition( Point2D position ) {
        this.positionPrev = this.position;
        this.position = new Vector2D( (float)position.getX(), (float)position.getY() ) ;
        setChanged();
        notifyObservers();
    }

    public void setPhysicalSystem( PhysicalSystem physicalSystem ) {
        this.physicalSystem = physicalSystem;
    }

    // TODO: This method is probably unnecessary
    public PhysicalSystem getPhysicalSystem() {

        return PhetApplication.instance().getPhysicalSystem();
        // ???? 4/29/03
//        return physicalSystem;
    }

    /**
     *
     */
    public void removeFromSystem() {
        Particle.removeParticle( this );
        setChanged();
        notifyObservers( Particle.S_REMOVE_BODY );
    }


    //
    // Containment related methods
    //

    /**
     *
     */
    public ArrayList getContainedBodies() {
        return containedBodies;
    }

    /**
     * Containment methods
     */
    public void addContainedBody( PhysicalEntity body ) {
        containedBodies.add( body );
    }

    public void removeContainedBody( PhysicalEntity body ) {
        containedBodies.remove( body );
    }

    public boolean containsBody( PhysicalEntity body ) {
        return containedBodies.contains( body );
    }

    public int numContainedBodies() {
        return containedBodies.size();
    }

    //
    // Constraint related methods
    //
    public void addConstraint( Constraint constraintSpec ) {
        constraints.add( constraintSpec );
    }

    public void removeConstraint( Constraint constraintSpec ) {
        constraints.remove( constraintSpec );
    }


    //
    // Static fields and methods
    //
    protected static float  s_defaultMass = Float.POSITIVE_INFINITY;
    protected static float  s_defaultCharge = 0;
    public final static Integer S_REMOVE_BODY = new Integer( 0 );


    //
    // Static fields and methods
    //

    // The number of particles in the system
    private static int s_numParticles = 0;
    // The default radius for a particle
    private static double s_defaultRadius = 5.0;

    public static int getNumParticles() {
        return s_numParticles;
    }

    public static void removeParticle( Particle particle ) {
        particle.getPhysicalSystem().removeBody( particle );
        s_numParticles--;
    }


    //
    // Inner Classes
    //
    private class ParticleObserverAdapter implements Observer {
        Observer po;
//        ParticleObserver po;
        public ParticleObserverAdapter( Observer po ) {
//        public ParticleObserverAdapter( ParticleObserver po ) {
            this.po = po;
        }

        public void update(Observable o, Object arg) {
            if( po == null ) {
                System.out.println( "$$$" );
            }
            po.update(  Particle.this, null );
        }
    }

}
