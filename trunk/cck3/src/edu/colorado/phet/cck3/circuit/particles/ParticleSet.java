/** Sam Reid*/
package edu.colorado.phet.cck3.circuit.particles;

import edu.colorado.phet.cck3.CCKModule;
import edu.colorado.phet.cck3.circuit.Branch;
import edu.colorado.phet.cck3.circuit.Circuit;
import edu.colorado.phet.common_cck.model.ModelElement;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;

/**
 * User: Sam Reid
 * Date: Jun 1, 2004
 * Time: 4:07:51 PM
 * Copyright (c) Jun 1, 2004 by Sam Reid
 */
public class ParticleSet implements ModelElement {
    private ArrayList particles = new ArrayList();
    private ConstantDensityPropagator propagator;
    private double time = 0;
    private Storage storage = new Storage();
    private Circuit circuit;

    public double getDensity( Branch branch ) {
        Electron[] e = getParticles( branch );
        double density = e.length / branch.getLength();
        return density;
    }

    public ParticleSet( CCKModule module, Circuit circuit ) {
        propagator = new ConstantDensityPropagator( module, this, circuit );
        this.circuit = circuit;
    }

    public void addParticle( Electron e ) {
        particles.add( e );
    }

    public void clear() {
        particles.clear();
    }

    public Electron[] getParticles( Branch branch ) {
        return getParticlesSlowly( branch );
//        storage.synchronize();
////        if( storage.time != time || storage.getParticles( branch ) == null ) {
////            storage.synchronize();
////        }
//        Electron[] result = storage.getParticles( branch );
//        if( result == null ) {
////            new RuntimeException( "No partilces for branch: " + branch ).printStackTrace();
//            result = new Electron[0];
//        }
//        return result;
    }

    private Electron[] getParticlesSlowly( Branch branch ) {
//        new Exception("Debugging calls to getParticles.").printStackTrace( );
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
        storage.removeParticles( branch );
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
        time += dt;
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

    class Storage {
        double time;
        Hashtable listtable = new Hashtable();
        Hashtable arraytable = new Hashtable();

        public Storage() {
        }

        public void reset() {
            this.time = ParticleSet.this.time;
        }

        public double getTime() {
            return time;
        }

        public Electron[] getParticles( Branch branch ) {
            Electron[] e = (Electron[])arraytable.get( branch );
            return e;
        }

        public void synchronize() {
            this.time = ParticleSet.this.time;
            listtable.clear();
            arraytable.clear();
            for( int i = 0; i < circuit.numBranches(); i++ ) {
                listtable.put( circuit.branchAt( i ), new ArrayList() );
            }
            for( int i = 0; i < particles.size(); i++ ) {
                Electron particle = (Electron)particles.get( i );
                Branch branch = particle.getBranch();
                ArrayList list = (ArrayList)listtable.get( branch );
                list.add( particle );
            }
            Enumeration k = listtable.keys();
            while( k.hasMoreElements() ) {
                Branch branch = (Branch)k.nextElement();
                ArrayList value = (ArrayList)listtable.get( branch );
                arraytable.put( branch, value.toArray( new Electron[0] ) );
            }
        }

        public void removeParticles( Branch branch ) {
            arraytable.put( branch, new Electron[0] );
        }
    }

}
