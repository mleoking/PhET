/** Sam Reid*/
package edu.colorado.phet.cck3.circuit.particles;

import edu.colorado.phet.cck3.circuit.Branch;
import edu.colorado.phet.cck3.circuit.Circuit;
import edu.colorado.phet.common.model.ModelElement;

import java.util.ArrayList;

/**
 * User: Sam Reid
 * Date: Jun 1, 2004
 * Time: 4:07:51 PM
 * Copyright (c) Jun 1, 2004 by Sam Reid
 */
public class ParticleSet implements ModelElement {
    private ArrayList particles = new ArrayList();
    private ConstantDensityPropagator propagator;

    public ParticleSet( Circuit circuit ) {
        propagator = new ConstantDensityPropagator( this, circuit );
    }

    public void addParticle( Electron e ) {
        particles.add( e );
    }

    public void clear() {
        particles.clear();
    }

    public Electron[] getParticles( Branch branch ) {
        ArrayList all = new ArrayList();
        for( int i = 0; i < particles.size(); i++ ) {
            Electron electron = (Electron)particles.get( i );
            if( electron.getBranch() == branch ) {
                all.add( electron );
            }
        }
        return (Electron[])all.toArray( new Electron[0] );
    }

    public Electron[] removeParticles( Branch branch ) {
        Electron[] p = getParticles( branch );
        for( int i = 0; i < p.length; i++ ) {
            Electron electron = p[i];
            particles.remove( electron );
            electron.delete();
        }
        return p;
    }

    public int numParticles() {
        return particles.size();
    }

    public Electron particleAt( int i ) {
        return (Electron)particles.get( i );
    }

    public void stepInTime( double dt ) {
        propagator.stepInTime( dt );
    }

    public double distanceToClosestElectron( Branch branch, double x ) {
        Electron[] e = getParticles( branch );
        double bestMatch = Double.POSITIVE_INFINITY;
        for( int i = 0; i < e.length; i++ ) {
            Electron electron = e[i];
            double dist = Math.abs( x - electron.getDistAlongWire() );
            if( dist < bestMatch ) {
                bestMatch = dist;
            }
        }
        return bestMatch;
    }

    public Electron getUpperNeighborInBranch( Electron myelectron ) {
        Electron[] e = getParticles( myelectron.getBranch() );
        Electron upper = null;
        double dist = Double.POSITIVE_INFINITY;
        for( int i = 0; i < e.length; i++ ) {
            Electron electron = e[i];
            if( electron != myelectron ) {
                double yourDist = electron.getDistAlongWire();
                double myDist = myelectron.getDistAlongWire();
                if( yourDist > myDist ) {
                    double distance = yourDist - myDist;
                    if( distance < dist ) {
                        dist = distance;
                        upper = electron;
                    }
                }
            }
        }
        return upper;
    }

    public Electron getLowerNeighborInBranch( Electron myelectron ) {
        Electron[] e = getParticles( myelectron.getBranch() );
        Electron lower = null;
        double dist = Double.POSITIVE_INFINITY;
        for( int i = 0; i < e.length; i++ ) {
            Electron electron = e[i];
            if( electron != myelectron ) {
                double yourDist = electron.getDistAlongWire();
                double myDist = myelectron.getDistAlongWire();
                if( yourDist < myDist ) {
                    double distance = myDist - yourDist;
                    if( distance < dist ) {
                        dist = distance;
                        lower = electron;
                    }
                }
            }
        }
        return lower;
    }

}
