/*Copyright, Sam Reid, 2003.*/
package edu.colorado.phet.cck.elements.particles;

import edu.colorado.phet.cck.elements.branch.Branch;
import edu.colorado.phet.cck.elements.circuit.Circuit;
import edu.colorado.phet.cck.elements.circuit.CircuitObserver;
import edu.colorado.phet.common.model.ModelElement;

import java.util.ArrayList;
import java.util.Random;

/**
 * User: Sam Reid
 * Date: Sep 4, 2003
 * Time: 3:48:35 AM
 * Copyright (c) Sep 4, 2003 by Sam Reid
 */
public class ParticleSet implements ModelElement {
    ArrayList particles = new ArrayList();
    Circuit circuit;
    ArrayList observers = new ArrayList();
    Random random = new Random();
    Propagator propagator;

    public ParticleSet( Circuit circuit ) {
        this.circuit = circuit;
        this.propagator = new ConstantDensityPropagator( circuit, this );
//        this.propagator = new DefaultPropagator(circuit, this);
        circuit.addCircuitObserver( new CircuitObserver() {
            public void branchAdded( Circuit circuit2, Branch branch ) {
            }

            public void branchRemoved( Circuit circuit2, Branch branch ) {
                ArrayList toRemove = new ArrayList();
                for( int i = 0; i < particles.size(); i++ ) {
                    BranchParticle branchParticle = (BranchParticle)particles.get( i );
                    if( branchParticle.getBranch() == branch ) {
                        toRemove.add( branchParticle );
                    }
                }
                for( int i = 0; i < toRemove.size(); i++ ) {
                    BranchParticle branchParticle = (BranchParticle)toRemove.get( i );
                    removeParticle( branchParticle );
                }
            }

            public void connectivityChanged( Circuit circuit2 ) {
            }
        } );
    }

    public void addParticleSetObserver( ParticleSetObserver pso ) {
        observers.add( pso );
    }

    public void removeParticle( BranchParticle particle ) {
        particles.remove( particle );
        fireParticleRemoved( particle );
    }

    public String toString() {
        return particles.toString();
    }

    private void fireParticleRemoved( BranchParticle bp ) {
        for( int i = 0; i < observers.size(); i++ ) {
            ParticleSetObserver particleSetObserver = (ParticleSetObserver)observers.get( i );
            particleSetObserver.particleRemoved( bp );
        }
    }

    public void addParticle( BranchParticle bp ) {
        particles.add( bp );
        fireParticleAdded( bp );
    }

    private void fireParticleAdded( BranchParticle bp ) {
        for( int i = 0; i < observers.size(); i++ ) {
            ParticleSetObserver particleSetObserver = (ParticleSetObserver)observers.get( i );
            particleSetObserver.particleAdded( bp );
        }
    }

    public BranchParticle particleAt( int i ) {
        return (BranchParticle)particles.get( i );
    }

    public BranchParticle[] getBranchParticles( Branch branch ) {
        ArrayList all = new ArrayList();
        for( int i = 0; i < particles.size(); i++ ) {
            if( particleAt( i ).getBranch() == branch ) {
                all.add( particleAt( i ) );
            }
        }
        return (BranchParticle[])all.toArray( new BranchParticle[0] );
    }

    public void stepInTime( double dt ) {
        propagator.stepInTime( dt );
        for( int i = 0; i < particles.size(); i++ ) {
            BranchParticle branchParticle = (BranchParticle)particles.get( i );
            propagator.propagate( branchParticle, dt );
        }
    }

    public int numParticles() {
        return this.particles.size();
    }

    public void removeParticlesForBranch( Branch branch ) {
        for( int i = 0; i < particles.size(); i++ ) {
            BranchParticle branchParticle = (BranchParticle)particles.get( i );
            if( branchParticle.getBranch() == branch ) {
                removeParticle( branchParticle );
                i--;
            }
        }
    }

    public Circuit getCircuit() {
        return circuit;
    }

}
