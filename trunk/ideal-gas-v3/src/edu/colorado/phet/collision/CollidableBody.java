package edu.colorado.phet.collision;

import edu.colorado.phet.common.math.Vector2D;
import edu.colorado.phet.idealgas.model.Constraint;
import edu.colorado.phet.mechanics.Body;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Iterator;
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
    // Working copy of constraint list used in case a constraint
    // needs to modify the real constraint list
    private ArrayList workingList = new ArrayList();


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
            positionPrev = new Point2D.Double();
        }
        positionPrev.setLocation( getPosition() );

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

    public abstract double getContactOffset( Body body );

    public void stepInTimeNoNotify( double dt ) {
        this.stepInTime( dt );

        // any of the constraints need to add or remove constraints from the list
        workingList.clear();
        workingList.addAll( constraints );
        for( Iterator iterator = workingList.iterator(); iterator.hasNext(); ) {
            Constraint constraintSpec = (Constraint)iterator.next();
//            constraintSpec.apply();
        }
    }


    /**
     * Containment methods
     */
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
