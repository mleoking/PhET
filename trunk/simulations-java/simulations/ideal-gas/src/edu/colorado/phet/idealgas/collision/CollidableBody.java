package edu.colorado.phet.idealgas.collision;

import edu.colorado.phet.common.mechanics.Body;
import edu.colorado.phet.common.phetcommon.math.Vector2D;
import edu.colorado.phet.idealgas.model.Constraint;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

/**
 * This abstract class represents physical bodies. It is abstract so that only
 * subclasses can be instantiated, forcing them to declare if they do or do not
 * have physical extent.
 */
public abstract class CollidableBody extends Body {

    private boolean collidable = true;
    private Vector2D velocityPrev;
    private Point2D positionPrev;
    ArrayList containedBodies = new ArrayList();

    // List of contraints that must be applied to the body's state
    // at the end of each doYourThing
    protected ArrayList constraints = new ArrayList();
    private Point2D positionBeforeTimeStep = new Point2D.Double();


    protected CollidableBody() {
    }

    protected CollidableBody( Point2D position, Vector2D velocity,
                              Vector2D acceleration, double mass, double charge ) {
        super( position, velocity, acceleration, mass, charge );
    }

    public boolean isCollidable() {
        return collidable;
    }

    public void stepInTime( double dt ) {
        // Save the velocity and position before they are updated. This information
        // is used in collision calculations
        if( velocityPrev == null ) {
            velocityPrev = new Vector2D.Double();
        }
        velocityPrev.setComponents( getVelocity().getX(), getVelocity().getY() );
        if( positionPrev == null ) {
            positionPrev = new Point2D.Double( getPosition().getX(), getPosition().getY() );
        }
        positionPrev.setLocation( getPosition() );
        positionBeforeTimeStep.setLocation( getPosition() );

        super.stepInTime( dt );
    }

    public void setCollidable( boolean collidable ) {
        this.collidable = collidable;
    }

    public Vector2D getVelocityPrev() {
        return velocityPrev;
    }

    public Point2D getPositionPrev() {
        return positionPrev;
    }

//    public abstract double getContactOffset( Body body );

    public List getContainedBodies() {
        return containedBodies;
    }

    public void addContainedBody( Body body ) {
        containedBodies.add( body );
    }

    public void removeContainedBody( Body body ) {
        containedBodies.remove( body );
    }

    public boolean containsBody( Body body ) {
        return containedBodies.contains( body );
    }

    public int numContainedBodies() {
        return containedBodies.size();
    }

    public Point2D getPositionBeforeTimeStep() {
        return positionBeforeTimeStep;
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
}
