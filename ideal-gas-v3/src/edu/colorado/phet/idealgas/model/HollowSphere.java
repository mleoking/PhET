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
import java.util.*;

public class HollowSphere extends SphericalBody {

    private ArrayList containedBodies = new ArrayList();
    private ArrayList listeners = new ArrayList();

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

    public void stepInTime( double dt ) {
        super.stepInTime( dt );
        System.out.println( "this.getAcceleration() = " + this.getAcceleration() );
//        System.out.println( "getVelocity() = " + getVelocity() );
    }

    public List getContainedBodies() {
        return containedBodies;
    }

    public void addContainedBody( Body body ) {
        containedBodies.add( body );
        if( body instanceof GasMolecule ) {
            GasMolecule molecule = (GasMolecule)body;
            MoleculeEvent event = new MoleculeEvent( this, molecule.getClass() );
            for( int i = 0; i < listeners.size(); i++ ) {
                Object o = listeners.get( i );
                if( o instanceof HollowSphereListener ) {
                    HollowSphereListener hollowSphereListener = (HollowSphereListener)o;
                    hollowSphereListener.moleculeAdded( event );
                }
            }
        }
    }

    public void removeContainedBody( Body body ) {
        containedBodies.remove( body );
        if( body instanceof GasMolecule ) {
            GasMolecule molecule = (GasMolecule)body;
            MoleculeEvent event = new MoleculeEvent( this, molecule.getClass() );
            for( int i = 0; i < listeners.size(); i++ ) {
                Object o = listeners.get( i );
                if( o instanceof HollowSphereListener ) {
                    HollowSphereListener hollowSphereListener = (HollowSphereListener)o;
                    hollowSphereListener.moleculeRemoved( event );
                }
            }
        }
    }

    public boolean containsBody( Body body ) {
        return containedBodies.contains( body );
    }

    public int numContainedBodies() {
        return containedBodies.size();
    }

    public void addHollowSphereListener( HollowSphereListener listener ) {
        listeners.add( listener );
    }

    public void removeHollowSphereListener( HollowSphereListener listener ) {
        listeners.remove( listener );
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

    public int getHeavySpeciesCnt() {
        return getSpeciesCnt( HeavySpecies.class );
    }

    public int getLightSpeciesCnt() {
        return getSpeciesCnt( LightSpecies.class );
    }

    private int getSpeciesCnt( Class species ) {
        int cnt = 0;
        for( int i = 0; i < containedBodies.size(); i++ ) {
            Body body = (Body)containedBodies.get( i );
            if( species.isInstance( body ) ) {
                cnt++;
            }
        }
        return cnt;
    }

    //----------------------------------------------------------------------------------------------
    // Inner classes
    //----------------------------------------------------------------------------------------------

    // TODO: is this interface actually used?
    public interface HollowSphereListener extends EventListener {
        void moleculeAdded( HollowSphere.MoleculeEvent event );

        void moleculeRemoved( HollowSphere.MoleculeEvent event );
    }

    public class MoleculeEvent extends EventObject {
        private Class moleculeType;

        public MoleculeEvent( Object source, Class moleculeType ) {
            super( source );
            this.moleculeType = moleculeType;
        }

        public Class getMoleculeType() {
            return moleculeType;
        }
    }
}
