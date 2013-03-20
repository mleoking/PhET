// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.energyformsandchanges.energysystems.model;

import java.util.List;

import edu.colorado.phet.common.phetcommon.math.vector.Vector2D;
import edu.colorado.phet.energyformsandchanges.common.model.EnergyChunk;

/**
 * Class that is used to move an energy chunk along a path.
 *
 * @author John Blanco
 */
public class EnergyChunkPathMover {
    public final EnergyChunk energyChunk;
    private final List<Vector2D> path;
    private final double velocity;
    private Vector2D nextPoint;
    private boolean pathFullyTraversed = false;

    public EnergyChunkPathMover( EnergyChunk energyChunk, List<Vector2D> path, double velocity ) {
        this.energyChunk = energyChunk;
        this.path = path;
        this.velocity = velocity;

        if ( path.isEmpty() ) {
            throw new IllegalArgumentException( "Path must have at least one point." );
        }
        nextPoint = path.get( 0 );
    }

    public void moveAlongPath( double time ) {
        double distanceToTravel = time * velocity;
        while ( distanceToTravel > 0 && !pathFullyTraversed ) {
            if ( distanceToTravel < energyChunk.position.get().distance( nextPoint ) ) {
                // Not arriving at destination next point yet, so just move towards it.
                Vector2D velocityVector = new Vector2D( distanceToTravel, 0 ).getRotatedInstance( nextPoint.minus( energyChunk.position.get() ).getAngle() );
                energyChunk.position.set( energyChunk.position.get().plus( velocityVector ) );
                distanceToTravel = 0; // No remaining distance.
            }
            else {
                // Arrived at next destination point.
                distanceToTravel = distanceToTravel - energyChunk.position.get().distance( nextPoint );
                energyChunk.position.set( nextPoint );
                if ( nextPoint == path.get( path.size() - 1 ) ) {
                    // At the end.
                    pathFullyTraversed = true;
                }
                else {
                    // Set the next destination point.
                    nextPoint = path.get( path.indexOf( nextPoint ) + 1 );
                }
            }
        }
    }

    public Vector2D getFinalDestination(){
        return path.get( path.size() - 1 );
    }

    public boolean isPathFullyTraversed() {
        return pathFullyTraversed;
    }
}
