// edu.colorado.phet.idealgas.physics.body.HollowSphere
/*
 * User: Administrator
 * Date: Jan 5, 2003
 * Time: 8:11:48 AM
 */
package edu.colorado.phet.idealgas.model;

import edu.colorado.phet.collision.SphericalBody;
import edu.colorado.phet.common.math.Vector2D;
import edu.colorado.phet.mechanics.Body;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class HollowSphere extends SphericalBody {
    ArrayList containedBodies = new ArrayList();

    public HollowSphere( Point2D center,
                         Vector2D velocity,
                         Vector2D acceleration,
                         double mass,
                         double radius ) {
        super( center, velocity, acceleration, mass, radius );
    }

    // The following are used for debug purposes only. It allows the contact point in a
    // collison to be displayed on the screen.
    public Point2D contactPt;

    public void setContactPt( Point2D.Double contactPt ) {
        this.contactPt = contactPt;
        notifyObservers();
    }


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

    //----------------------------------------------------------------------------------------------
    // Methods for placing new molecules in the sphere
    //----------------------------------------------------------------------------------------------
    private Random random = new Random();

    public Point2D getNewMoleculeLocation() {
        double r = random.nextDouble() - GasMolecule.s_defaultRadius;
        double theta = random.nextDouble() * Math.PI * 2;
        Point2D.Double p = new Point2D.Double( this.getPosition().getX() + r * Math.cos( theta ),
                                               this.getPosition().getY() + r * Math.sin( theta ) );
        return p;
    }

    public Vector2D getNewMoleculeVelocity( Class species, IdealGasModel model ) {
        double s = 0;
        if( species == HeavySpecies.class ) {
            s = model.getHeavySpeciesAveSpeed();
            if( s == 0 ) {
                s = Math.sqrt( 2 * IdealGasModel.DEFAULT_ENERGY / HeavySpecies.getMoleculeMass() );
            }
        }
        if( species == LightSpecies.class ) {
            s = model.getLightSpeciesAveSpeed();
            if( s == 0 ) {
                s = Math.sqrt( 2 * IdealGasModel.DEFAULT_ENERGY / LightSpecies.getMoleculeMass() );
            }
        }
        double theta = random.nextDouble() * Math.PI * 2;
        return new Vector2D.Double( s * Math.cos( theta ), s * Math.sin( theta ) );
    }
}
