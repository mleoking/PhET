// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.selfdrivenparticlemodel.model;

import java.util.ArrayList;

public class ClusterAssignment {
    public int[] assignClusters( ParticleModel model ) {
        int clusterIndex = 0;
        int[] result = new int[model.numParticles()];
        for ( int i = 0; i < result.length; i++ ) {
            result[i] = -1;//unseen
        }
        for ( int i = 0; i < result.length; i++ ) {
            //find all in same cluster
            if ( result[i] == -1 ) {
                Particle particle = model.particleAt( i );
                Particle[] cluster = getCluster( model, particle );
                for ( int j = 0; j < cluster.length; j++ ) {
                    Particle particle1 = cluster[j];
                    result[model.indexOf( particle1 )] = clusterIndex;
                }
                clusterIndex++;
            }
        }
        return result;
    }

    private Particle[] getCluster( ParticleModel model, Particle particle ) {
        ArrayList visited = new ArrayList();
        ArrayList toVisit = new ArrayList();
        toVisit.add( particle );
        bfs( model, toVisit, visited );
        return (Particle[]) visited.toArray( new Particle[0] );
    }

    private void bfs( ParticleModel model, ArrayList toVisit, ArrayList visited ) {
        for ( int i = 0; i < toVisit.size(); i++ ) {
            Particle tv = (Particle) toVisit.get( i );
            if ( !visited.contains( tv ) ) {
                visited.add( tv );
                Particle[] inRange = model.getNeighborsInRadius( tv );
                for ( int j = 0; j < inRange.length; j++ ) {
                    if ( !visited.contains( inRange[j] ) && !toVisit.contains( inRange[j] ) ) {
                        toVisit.add( inRange[j] );
                        bfs( model, toVisit, visited );
                    }
                }
            }
        }
    }
}
