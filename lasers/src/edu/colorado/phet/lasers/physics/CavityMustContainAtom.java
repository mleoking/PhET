/**
 * Class: CavityMustContainAtom
 * Package: edu.colorado.phet.lasers.physics
 * Author: Another Guy
 * Date: Apr 3, 2003
 */
package edu.colorado.phet.lasers.physics;

import edu.colorado.phet.physics.MustContain;
import edu.colorado.phet.physics.Constraint;
import edu.colorado.phet.physics.body.PhysicalEntity;
import edu.colorado.phet.lasers.physics.atom.Atom;

public class CavityMustContainAtom extends MustContain {

    public CavityMustContainAtom( ResonatingCavity container,
                                  Atom contained ) {
        super( container, contained );
    }

    public Object apply( Constraint.Spec spec ) {

        // Get references to the cavity and the atom
        MustContain.Spec mustContainSpec = null;
        if( spec instanceof MustContain.Spec ) {
            mustContainSpec = (MustContain.Spec)spec;
        }
        PhysicalEntity body = mustContainSpec.getContainer();
        ResonatingCavity cavity = null;
        if( body instanceof ResonatingCavity ) {
            cavity = (ResonatingCavity)body;
        } else {
            throw new RuntimeException( "Container not instance of ResonatingCavity" );
        }
        body = mustContainSpec.getContained();
        Atom atom = null;
        if( body instanceof Atom ) {
            atom = (Atom)body;
        } else {
            throw new RuntimeException( "Contained not instance of Atom" );
        }

        float x = atom.getPosition().getX();
        float newX = x;
        float y = atom.getPosition().getY();
        float newY = y;


        if( x < cavity.getPosition().getX() + atom.getRadius() ) {
            newX = cavity.getPosition().getX() + atom.getRadius();
            atom.getVelocity().setX( -atom.getVelocity().getX() );
        }
        else if ( x > cavity.getPosition().getX() + cavity.getWidth() - atom.getRadius() ) {
            newX = cavity.getPosition().getX() + cavity.getWidth() - atom.getRadius();
            atom.getVelocity().setX( -atom.getVelocity().getX() );
        }

        // Have we violated a the constraint with a horizontal wall?
        if( y < cavity.getPosition().getY() + atom.getRadius() ) {
            newY = cavity.getPosition().getY() + atom.getRadius();
            atom.getVelocity().setY( -atom.getVelocity().getY() );
        }
        else if ( y > cavity.getPosition().getY() + cavity.getHeight() - atom.getRadius() ) {
            newY = cavity.getPosition().getY() + cavity.getHeight() - atom.getRadius();
            atom.getVelocity().setY( -atom.getVelocity().getY() );
        }

        // Note that we must not call setPosition(), because it modifies
        // the previousPosition attribute of the atom. We call relocateBodyY()
        // to ensure conservation of energy if gravity it on
        if( newX != x ) {
            atom.getPosition().setX( newX );
        }
        if( newY != y ) {
            atom.getPosition().setY( newY );
        }
        return null;
    }
}
