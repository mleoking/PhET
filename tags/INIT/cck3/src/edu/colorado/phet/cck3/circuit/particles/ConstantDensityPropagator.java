/** Sam Reid*/
package edu.colorado.phet.cck3.circuit.particles;

import edu.colorado.phet.cck3.circuit.Branch;
import edu.colorado.phet.cck3.circuit.Circuit;
import edu.colorado.phet.cck3.circuit.Junction;
import edu.colorado.phet.cck3.circuit.kirkhoff.KirkhoffSolver;
import edu.colorado.phet.common.model.ModelElement;

import javax.swing.*;
import java.util.ArrayList;
import java.util.Collections;

/**
 * User: Sam Reid
 * Date: Jun 8, 2004
 * Time: 4:01:09 PM
 * Copyright (c) Jun 8, 2004 by Sam Reid
 */
public class ConstantDensityPropagator implements ModelElement {
    ParticleSet particleSet;
    Circuit circuit;
    private double speedScale = .01;
    private double MIN_CURRENT = Math.pow( 10, -10 );
    private double MAX_CURRENT = 1000;
    int numEqualize = 2;

    public ConstantDensityPropagator( ParticleSet particleSet, Circuit circuit ) {
        this.particleSet = particleSet;
        this.circuit = circuit;
    }

    public void stepInTime( double dt ) {
        for( int i = 0; i < particleSet.numParticles(); i++ ) {
            Electron e = particleSet.particleAt( i );
            propagate( e, dt );
        }
        //maybe this should be done in random order, otherwise we may get artefacts.

        for( int i = 0; i < numEqualize; i++ ) {
            equalize( dt );
        }
    }

    private void equalize( double dt ) {

        ArrayList indices = new ArrayList();
        for( int i = 0; i < particleSet.numParticles(); i++ ) {
            indices.add( new Integer( i ) );
        }
        Collections.shuffle( indices );
        for( int i = 0; i < particleSet.numParticles(); i++ ) {
            Integer index = (Integer)indices.get( i );
            int ind = index.intValue();
            Electron e = particleSet.particleAt( ind );
            equalize( e, dt );
        }
    }

    static double highestSoFar = 0;

    private void equalize( Electron e, double dt ) {
        //if it has a lower and upper neighbor, try to get the distance to each to be half of
        //CCK3Module.ELECTRON_DX
        Electron upper = particleSet.getUpperNeighborInBranch( e );
        Electron lower = particleSet.getLowerNeighborInBranch( e );
        if( upper == null || lower == null ) {
            return;
        }
        double sep = upper.getDistAlongWire() - lower.getDistAlongWire();
        double myloc = e.getDistAlongWire();
        double midpoint = lower.getDistAlongWire() + sep / 2;
        //move a bit toward the midpoint.
//        double correctionSpeed = .01;//gives a factor of 100 off the correct answer ^ish.
//        double correctionSpeed = .2;


        double dest = midpoint;
        double distMoving = Math.abs( dest - myloc );
        double vec = dest - myloc;
        boolean sameDirAsCurrent = vec > 0 && e.getBranch().getCurrent() > 0;
        double correctionSpeed = .055 / numEqualize;
        if( !sameDirAsCurrent ) {
            correctionSpeed = .01 / numEqualize;
        }
        double maxDX = Math.abs( correctionSpeed * dt );

        if( distMoving > highestSoFar ) {//For debugging.
//            System.out.println( "highestSoFar = " + highestSoFar );
            highestSoFar = distMoving;
        }

        if( distMoving > maxDX ) {
            //move in the appropriate direction maxDX
            if( dest < myloc ) {
                dest = myloc - maxDX;
            }
            else if( dest > myloc ) {
                dest = myloc + maxDX;
            }
        }
//        double vec = dest - myloc;
////        double newDist = Math.abs( dest - myloc );
////        System.out.println( "maxDX = " + maxDX + ", distMoving=" + distMoving + ", newDist=" + newDist );
//        boolean sameDirAsCurrent = vec > 0 && e.getBranch().getCurrent() > 0;
//        if( sameDirAsCurrent ) {
        e.setDistAlongWire( dest );
//        }
    }

    class CircuitLocation {
        Branch branch;
        double x;

        public CircuitLocation( Branch branch, double x ) {
            if( branch.containsScalarLocation( x ) ) {
                this.branch = branch;
                this.x = x;
            }
            else {
                throw new RuntimeException( "No such location in branch length=" + branch.getLength() + ", x=" + x );
            }
        }

        public Branch getBranch() {
            return branch;
        }

        public double getX() {
            return x;
        }

    }

    private void propagate( Electron e, double dt ) {
        double x = e.getDistAlongWire();
        if( Double.isNaN( x ) ) {
            //TODO fix this
//            throw new RuntimeException( "X was nan.");
            return;
        }
        double current = e.getBranch().getCurrent();

        if( current == 0 || Math.abs( current ) < MIN_CURRENT ) {
            return;
        }
        if( Math.abs( current ) > MAX_CURRENT ) {
            System.out.println( "current = " + current + ", max current exceeded" );
            return;
        }
        double speed = current * speedScale;
        double newX = x + speed * dt;
        Branch branch = e.getBranch();
        if( branch.containsScalarLocation( newX ) ) {
            e.setDistAlongWire( newX );
        }
        else {
            //need a new branch.
            double overshoot = 0;
            boolean under = false;
            if( newX < 0 ) {
                overshoot = -newX;
                under = true;
            }
            else {
                overshoot = Math.abs( branch.getLength() - newX );
                under = false;
            }
            if( Double.isNaN( overshoot ) ) {
                throw new RuntimeException( "Overshoot is NaN" );
            }
//            System.out.println( "overshoot = " + overshoot+", under="+under );
            CircuitLocation[] loc = getLocations( e, dt, overshoot, under );
            if( loc.length == 0 ) {
                System.out.println( "No outgoing wires for current=" + current );
                new KirkhoffSolver().apply( circuit );
                RuntimeException re = new RuntimeException( "No outgoing wires, current=" + current );
                re.printStackTrace();
//                System.exit( 0 );
                JOptionPane.showMessageDialog( null, "No outgoing wires, current=" + current );
                return;
            }
//            CircuitLocation chosen = loc[0];

            //choose the branch with the furthest away electron, or the empty branch if no electron.
            CircuitLocation chosen = chooseDestinationBranch( loc );
            e.setLocation( chosen.getBranch(), Math.abs( chosen.getX() ) );
        }
    }

    private CircuitLocation chooseDestinationBranch( CircuitLocation[] loc ) {
        if( loc.length == 1 ) {
            return loc[0];
        }
        CircuitLocation bestYet = loc[0];
        double bestValue = particleSet.distanceToClosestElectron( bestYet.getBranch(), bestYet.getX() );

//        bestYet.distanceToClosestElectron( particleSet );
//        System.out.println( "bestValue = " + bestValue );
        for( int i = 1; i < loc.length; i++ ) {
            CircuitLocation circuitLocation = loc[i];
            double distToElectron = particleSet.distanceToClosestElectron( circuitLocation.getBranch(), circuitLocation.getX() );
//            circuitLocation.distanceToClosestElectron( particleSet );
//            System.out.println( "distToElectron = " + distToElectron );
            if( distToElectron > bestValue ) {
                bestYet = circuitLocation;
                bestValue = distToElectron;
//                System.out.println( "NEWbestValue = " + bestValue );
            }
        }
        return bestYet;
    }

    private CircuitLocation[] getLocations( Electron e, double dt, double overshoot, boolean under ) {
        Branch[] adj = null;
        Branch branch = e.getBranch();
        Junction jroot = null;
        if( under ) {
            jroot = branch.getStartJunction();
        }
        else {
            jroot = branch.getEndJunction();
        }
        adj = circuit.getAdjacentBranches( jroot );
        ArrayList all = new ArrayList();
        //keep only those with outgoing current.
        for( int i = 0; i < adj.length; i++ ) {
            Branch branch1 = adj[i];
            double current = branch1.getCurrent();
            double distAlongNew = Double.POSITIVE_INFINITY;
            if( current > 0 && branch1.getStartJunction() == jroot ) {//start near the beginning.
                distAlongNew = overshoot;
            }
            else if( current < 0 && branch1.getEndJunction() == jroot ) {
                distAlongNew = branch1.getLength() - overshoot;
            }
            if( !Double.isInfinite( distAlongNew ) ) {
                if( distAlongNew < branch1.getLength() ) {
                    CircuitLocation cl = new CircuitLocation( branch1, distAlongNew );
                    all.add( cl );
                }
                else {
                    System.out.println( "DistAlongNew too high, current=" + current );
                }
            }
        }
        return (CircuitLocation[])all.toArray( new CircuitLocation[0] );
    }

}
