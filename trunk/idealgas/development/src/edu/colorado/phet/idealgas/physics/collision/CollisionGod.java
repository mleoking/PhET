/**
 * Class: CollisionGod
 * Package: edu.colorado.phet.idealgas.physics.collision
 * Author: Another Guy
 * Date: Jan 20, 2004
 */
package edu.colorado.phet.idealgas.physics.collision;

import edu.colorado.phet.idealgas.physics.GasMolecule;
import edu.colorado.phet.physics.Law;
import edu.colorado.phet.physics.PhysicalSystem;
import edu.colorado.phet.physics.body.Body;
import edu.colorado.phet.physics.collision.Collision;
import edu.colorado.phet.physics.collision.CollisionFactory;
import edu.colorado.phet.physics.collision.ContactDetector;

import java.awt.geom.Rectangle2D;
import java.util.*;

/**
 * This class takes care of detecting and computing all the collisions in
 * the system. To avoid combinatorial explosions in collision detection,
 * the area in which collisions are to be handled is divided up into regions,
 * and particle-particle collisions are only searched for within each
 * region and those adjacent to it.
 */
public class CollisionGod implements Law {
    private int numRegionsX;
    private int numRegionsY;
    private Region[][] regions;
    private HashMap elementToRegionMap = new HashMap();
    // List to track bodies that are to be removed from the system at the
    // end of the apply() method, to avoid concurrentModificationExceptions
    private ArrayList removalList = new ArrayList();

    public CollisionGod( Rectangle2D.Double bounds, int numRegionsX, int numRegionsY ) {
        this.numRegionsX = numRegionsX;
        this.numRegionsY = numRegionsY;
        regions = new Region[numRegionsX][numRegionsY];
        double dx = bounds.getWidth() / numRegionsX;
        double dy = bounds.getHeight() / numRegionsY;
        for( int i = 0; i < numRegionsX; i++ ) {
            for( int j = 0; j < numRegionsY; j++ ) {
                regions[i][j] = new Region( bounds.getX() + i * dx, bounds.getX() + ( ( i + 1 ) * dx ) - Double.MIN_VALUE,
                                            bounds.getY() + j * dy, bounds.getY() + ( ( j + 1 ) * dy ) - Double.MIN_VALUE );
            }
        }
    }

    public void apply( float time, PhysicalSystem system ) {
        List bodies = system.getBodies();
        adjustRegionMembership( bodies );
        doMiscCollisions( bodies );
        doGasToGasCollisions();
    }

    /**
     * Makes sure all gas molecules are in the correct regions.
     * @param bodies
     */
    private void adjustRegionMembership( List bodies ) {

        // Put all the gas molecules in the model in the right regions
        for( int i = 0; i < bodies.size(); i++ ) {
            Body body = (Body)bodies.get( i );
            if( body instanceof GasMolecule ) {
                if( elementToRegionMap.containsKey( body ) ) {
                    this.placeBody( body );
                }
                else {
                    this.addBody( body );
                }
            }
        }

        // Remove any gas molecules from our internal structures that
        // are no longer in the physical system
        removalList.clear();
        Set placedBodies = elementToRegionMap.keySet();
        for( Iterator iterator = placedBodies.iterator(); iterator.hasNext(); ) {
            Object o = iterator.next();
            if( o instanceof GasMolecule ) {
                if( !bodies.contains( o ) ) {
                    removalList.add( o );
                }
            }
        }
        while( !removalList.isEmpty() ) {
            Body body = (Body)removalList.remove( 0 );
            removeBody( body );
        }
    }

    /**
     * Detects and performs collisions in which at least one of
     * the bodies is not a gas molecule
     * @param bodies
     */
    private void doMiscCollisions( List bodies ) {
        ArrayList nonGasBodies = new ArrayList();
        // Find all the bodies that aren't gas molecules
        for( int i = 0; i < bodies.size(); i++ ) {
            Object o = bodies.get( i );
            if( !( o instanceof GasMolecule ) ) {
                nonGasBodies.add( o );
            }
        }
        // Find all collisions between non-gas molecules and other
        // bodies
        for( int i = 0; i < nonGasBodies.size(); i++ ) {
            Body body1 = (Body)nonGasBodies.get( i );
            for( int j = 0; j < bodies.size(); j++ ) {
                Body body2 = (Body)bodies.get( j );
                detectAndDoCollision( body1, body2 );
            }
        }
    }

    private void doGasToGasCollisions() {

        // Do particle-particle collisions. Each region collides with
        // itself and the regions to the right and below.
        for( int i = 0; i < numRegionsX; i++ ) {
            for( int j = 0; j < numRegionsY; j++ ) {

//                System.out.println( "num: " + regions[i][j].size() );

                // collide region within itself
                doRegionToRegionCollision( regions[i][j], regions[i][j] );

                // collide region with regions[i+1][j]
                if( i < numRegionsX - 1 ) {
                    doRegionToRegionCollision( regions[i][j], regions[i + 1][j] );
                }
                // collide region with regions[i][j+1]
                if( j < numRegionsY - 1 ) {
                    doRegionToRegionCollision( regions[i][j], regions[i][j + 1] );
                }
                // collide region with regions[i+1][j+1]
                if( i < numRegionsX - 1 && j < numRegionsY - 1 ) {
                    doRegionToRegionCollision( regions[i][j], regions[i + 1][j + 1] );
                }
            }
        }
    }

    private void doRegionToRegionCollision( Region region1, Region region2 ) {
        for( Iterator it1 = region1.iterator(); it1.hasNext(); ) {
            Body body1 = (Body)it1.next();
            for( Iterator it2 = region2.iterator(); it2.hasNext(); ) {
                Body body2 = (Body)it2.next();
                detectAndDoCollision( body1, body2 );
            }
        }
    }

    private void detectAndDoCollision( Body body1, Body body2 ) {
        if( body1 != body2 && ContactDetector.areContacting( body1, body2 ) ) {
            Collision collision = CollisionFactory.create( body1, body2 );
            if( collision != null ) {
                collision.collide();
            }
        }
    }

    private void addBody( Body body ) {
        Region region = findRegionFor( body );
        elementToRegionMap.put( body, region );
        region.add( body );

    }

    private void removeBody( Body body ) {
        ( (Region)elementToRegionMap.get( body ) ).remove( body );
        elementToRegionMap.remove( body );
    }

    private Region findRegionFor( Body body ) {
        Region region = null;
        for( int i = 0; region == null && i < numRegionsX; i++ ) {
            for( int j = 0; region == null && j < numRegionsY; j++ ) {
                if( regions[i][j].belongsIn( body ) ) {
                    region = regions[i][j];
                }
            }
        }
        return region;
    }

    private void placeBody( Body body ) {
        Region currRegion = (Region)elementToRegionMap.get( body );
        if( currRegion == null ) {
            addBody( body );
        }
        else if( !currRegion.belongsIn( body ) ) {
            currRegion.remove( body );
            addBody( body );
        }
    }


    /**
     * A region within the physical system
     */
    private class Region extends HashSet {
        double xMin;
        double xMax;
        double yMin;
        double yMax;

        Region( double xMin, double xMax, double yMin, double yMax ) {
            this.xMin = xMin;
            this.xMax = xMax;
            this.yMin = yMin;
            this.yMax = yMax;
        }

        boolean belongsIn( Body body ) {
            boolean result = body.getPosition().getX() >= xMin
                    && body.getPosition().getX() <= xMax
                    && body.getPosition().getY() >= yMin
                    && body.getPosition().getY() <= yMax;
            return result;
        }
    }
}
