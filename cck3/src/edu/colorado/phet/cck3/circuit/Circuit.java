/** Sam Reid*/
package edu.colorado.phet.cck3.circuit;

import edu.colorado.phet.cck3.circuit.components.CircuitComponent;
import edu.colorado.phet.common.math.AbstractVector2D;
import edu.colorado.phet.common.math.ImmutableVector2D;
import edu.colorado.phet.common.math.Vector2D;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;

/**
 * User: Sam Reid
 * Date: May 24, 2004
 * Time: 1:31:24 AM
 * Copyright (c) May 24, 2004 by Sam Reid
 */
public class Circuit {
    ArrayList branches = new ArrayList();
    ArrayList junctions = new ArrayList();
    ArrayList listeners = new ArrayList();
    private KirkhoffListener kirkhoffListener;

    public Circuit( KirkhoffListener kirkhoffListener ) {
        this.kirkhoffListener = kirkhoffListener;
    }

    public void addCircuitListener( CircuitListener listener ) {
        listeners.add( listener );
    }

    public String toString() {
        return "Junctions=" + junctions + ", Branches=" + branches;
    }

    public Branch createBranch( double x1, double y1, double x2, double y2 ) {
        Junction startJunction = new Junction( x1, y1 );
        Junction endJunction = new Junction( x2, y2 );
        Branch branch = new Branch( kirkhoffListener, startJunction, endJunction );
        addBranch( branch );
        return branch;
    }

    public void addJunction( Junction startJunction ) {
        junctions.add( startJunction );
    }

    public Branch[] getAdjacentBranches( Junction junction ) {
        ArrayList out = new ArrayList();
        for( int i = 0; i < branches.size(); i++ ) {
            Branch branch = (Branch)branches.get( i );
            if( branch.hasJunction( junction ) ) {
                out.add( branch );
            }
        }
        return (Branch[])out.toArray( new Branch[0] );
    }

    public void updateNeighbors( Junction junction ) {
        for( int i = 0; i < branches.size(); i++ ) {
            Branch branch = (Branch)branches.get( i );
            if( branch.hasJunction( junction ) ) {
                branch.notifyObservers();
            }
        }
    }

    public void updateAll() {
        for( int i = 0; i < branches.size(); i++ ) {
            Branch branch = (Branch)branches.get( i );
            branch.notifyObservers();
        }
        for( int i = 0; i < junctions.size(); i++ ) {
            Junction junction = (Junction)junctions.get( i );
            junction.notifyObservers();
        }
    }

    public int numJunctions() {
        return junctions.size();
    }

    public Junction junctionAt( int i ) {
        return (Junction)junctions.get( i );
    }

    public boolean hasBranch( Junction a, Junction b ) {
        for( int i = 0; i < branches.size(); i++ ) {
            Branch branch = (Branch)branches.get( i );
            if( branch.hasJunction( a ) && branch.hasJunction( b ) ) {
                return true;
            }
        }
        return false;
    }

    public Junction[] getNeighbors( Junction a ) {
        ArrayList n = new ArrayList();
        for( int i = 0; i < branches.size(); i++ ) {
            Branch branch = (Branch)branches.get( i );
            if( branch.hasJunction( a ) ) {
                n.add( branch.opposite( a ) );
            }
        }
        return (Junction[])n.toArray( new Junction[0] );
    }

    public void replaceJunction( Junction old, Junction newJunction ) {
        junctions.remove( old );
        for( int i = 0; i < branches.size(); i++ ) {
            Branch branch = (Branch)branches.get( i );
            if( branch.getStartJunction() == old ) {
                branch.setStartJunction( newJunction );
            }
            if( branch.getEndJunction() == old ) {
                branch.setEndJunction( newJunction );
            }
        }
        fireJunctionRemoved( old );
    }

    private void fireJunctionRemoved( Junction junction ) {
        for( int i = 0; i < listeners.size(); i++ ) {
            CircuitListener circuitListener = (CircuitListener)listeners.get( i );
            circuitListener.junctionRemoved( junction );
        }
    }

    public boolean areNeighbors( Junction a, Junction b ) {
        if( a == b ) {
            return false;
        }
        Junction[] na = getNeighbors( a );
        return Arrays.asList( na ).contains( b );
    }

    public void addBranch( Branch component ) {
        addJunction( component.getStartJunction() );
        addJunction( component.getEndJunction() );
        branches.add( component );
    }

    public void notifyNeighbors( Branch b ) {
        ArrayList alreadyNotified = new ArrayList();
        Branch[] br1 = getAdjacentBranches( b.getStartJunction() );
        Branch[] br2 = getAdjacentBranches( b.getEndJunction() );
        ArrayList all = new ArrayList();
        all.addAll( Arrays.asList( br1 ) );
        all.addAll( Arrays.asList( br2 ) );
        for( int i = 0; i < all.size(); i++ ) {
            Branch branch = (Branch)all.get( i );
            if( !alreadyNotified.contains( branch ) ) {
                alreadyNotified.add( branch );
                branch.notifyObservers();
            }
        }
    }

    public Junction[] split( Junction junction ) {
        Branch[] b = getAdjacentBranches( junction );
        Junction[] newJunctions = new Junction[b.length];
        for( int i = 0; i < b.length; i++ ) {
            Branch branch = b[i];
            Junction opposite = branch.opposite( junction );
            AbstractVector2D vec = new Vector2D.Double( opposite.getPosition(), junction.getPosition() );
            double curLength = vec.getMagnitude();
            double newLength = Math.abs( curLength - CircuitGraphic.junctionRadius * 1.5 );
            vec = vec.getInstanceOfMagnitude( newLength );
            Point2D desiredDst = vec.getDestination( opposite.getPosition() );
            Point2D dst = desiredDst;
            if( branch instanceof CircuitComponent ) {
                dst = junction.getPosition();
            }

            Junction newJ = new Junction( dst.getX(), dst.getY() );
            branch.replaceJunction( junction, newJ );
            addJunction( newJ );
            newJunctions[i] = newJ;

            if( branch instanceof CircuitComponent ) {
                AbstractVector2D tx = new ImmutableVector2D.Double( junction.getPosition(), desiredDst );
                Branch[] stronglyConnected = getStrongConnections( newJ );
                BranchSet bs = new BranchSet( this, stronglyConnected );
                bs.translate( tx );
            }
            else {
                updateNeighbors( newJ );
            }
        }
        remove( junction );
        fireJunctionsMoved();
        kirkhoffListener.circuitChanged();
        return newJunctions;
    }

    public void remove( Junction junction ) {
        junctions.remove( junction );
        fireJunctionRemoved( junction );
    }

    public Branch[] getStrongConnections( Junction junction ) {
        ArrayList visited = new ArrayList();
        getStrongConnections( visited, junction );
        Branch[] b = (Branch[])visited.toArray( new Branch[0] );
        return b;
    }

    public Branch[] getStrongConnections( Branch wrongDir, Junction junction ) {
        ArrayList visited = new ArrayList();
        if( wrongDir != null ) {
            visited.add( wrongDir );
        }
        getStrongConnections( visited, junction );
        if( wrongDir != null ) {
            visited.remove( wrongDir );
        }
        Branch[] b = (Branch[])visited.toArray( new Branch[0] );
        return b;
    }

    private void getStrongConnections( ArrayList visited, Junction junction ) {
        Branch[] out = getAdjacentBranches( junction );
        for( int i = 0; i < out.length; i++ ) {
            Branch branch = out[i];
            Junction opposite = branch.opposite( junction );
            if( !visited.contains( branch ) ) {
                if( branch instanceof CircuitComponent ) {
                    visited.add( branch );
                    getStrongConnections( visited, opposite );
                }//Wires end the connectivity.
            }
        }
    }

    private void fireJunctionsMoved() {
        for( int i = 0; i < listeners.size(); i++ ) {
            CircuitListener circuitListener = (CircuitListener)listeners.get( i );
            circuitListener.junctionsMoved();
        }
    }

    public int numBranches() {
        return branches.size();
    }

    public int indexOf( Branch branch ) {
        return branches.indexOf( branch );
    }

    public int indexOf( Junction junction ) {
        return junctions.indexOf( junction );
    }

    public Branch branchAt( int i ) {
        return (Branch)branches.get( i );
    }

    public void remove( Branch branch ) {
        branches.remove( branch );
        fireBranchRemoved( branch );
        fireKirkhoffChanged();
    }

    private void fireBranchRemoved( Branch branch ) {
        for( int i = 0; i < listeners.size(); i++ ) {
            CircuitListener circuitListener = (CircuitListener)listeners.get( i );
            circuitListener.branchRemoved( branch );
        }
    }

    public void fireKirkhoffChanged() {
        kirkhoffListener.circuitChanged();
    }

    public Branch[] getBranches() {
        return (Branch[])branches.toArray( new Branch[0] );
    }

    private void translate( Junction[] j, AbstractVector2D vec ) {
        for( int i = 0; i < j.length; i++ ) {
            Junction junction = j[i];
            junction.translate( vec.getX(), vec.getY() );
        }
    }

    public void translate( Branch[] branchs, AbstractVector2D vec ) {
        Junction[] j = getJunctions( branchs );
        translate( j, vec );
        for( int i = 0; i < branchs.length; i++ ) {
            Branch b = branchs[i];
            b.notifyObservers();
        }
    }

    private static Junction[] getJunctions( Branch[] branchs ) {
        ArrayList list = new ArrayList();
        for( int i = 0; i < branchs.length; i++ ) {
            Branch branch = branchs[i];
            if( !list.contains( branch.getStartJunction() ) ) {
                list.add( branch.getStartJunction() );
            }
            if( !list.contains( branch.getEndJunction() ) ) {
                list.add( branch.getEndJunction() );
            }
        }
        return (Junction[])list.toArray( new Junction[0] );
    }

    public void fireBranchesMoved( Branch[] moved ) {
        for( int i = 0; i < listeners.size(); i++ ) {
            CircuitListener circuitListener = (CircuitListener)listeners.get( i );
            circuitListener.branchesMoved( moved );
        }
    }

    public boolean hasJunction( Junction junction ) {
        return junctions.contains( junction );
    }

    public double getVoltage( Branch branch1, Branch branch2 ) {
        if( branch1 == branch2 ) {
            return 0;
        }
        else {
//            ArrayList visited = new ArrayList();
//            visited.add( branch1 );
            double v1 = getVoltage( branch1, branch1.getStartJunction(), branch2.getStartJunction(), 0 );
            double v2 = getVoltage( branch1, branch1.getEndJunction(), branch2.getStartJunction(), 0 );
            double v3 = getVoltage( branch1, branch1.getStartJunction(), branch2.getEndJunction(), 0 );
            double v4 = getVoltage( branch1, branch1.getEndJunction(), branch2.getEndJunction(), 0 );
            ArrayList list = new ArrayList();
            if( !Double.isInfinite( v1 ) ) {
                list.add( new Double( v1 ) );
            }
            if( !Double.isInfinite( v2 ) ) {
                list.add( new Double( v2 ) );
            }
            if( !Double.isInfinite( v3 ) ) {
                list.add( new Double( v3 ) );
            }
            if( !Double.isInfinite( v4 ) ) {
                list.add( new Double( v4 ) );
            }
//            System.out.println( "list = " + list );
            Collections.sort( list, new Comparator() {
                public int compare( Object o1, Object o2 ) {
                    Double a = (Double)o1;
                    Double b = (Double)o2;
                    double diff = ( -Math.abs( a.doubleValue() ) + Math.abs( b.doubleValue() ) );
                    if (diff==0){
                        return 0;
                    }
                    else if (diff>0){
                        return -1;
                    }
                    else if (diff<0){
                        return 1;
                    }
//                    return diff;
                    else{
                        return -(int)diff;
                    }
                }
            } );
            System.out.println( "list = " + list );
            Double lowest = (Double)list.get( 0 );
            return lowest.doubleValue();
        }
    }

    private double getVoltage( Branch branch1, Junction at, Junction target, double volts ) {
        ArrayList visited = new ArrayList();
        visited.add( branch1 );
        return getVoltage( visited, at, target, volts );
    }

    private double getVoltage( ArrayList visited, Junction at, Junction target, double volts ) {
        if( at == target ) {
            return volts;
        }
        Branch[] out = getAdjacentBranches( at );
        for( int i = 0; i < out.length; i++ ) {
            Branch branch = out[i];
            Junction opposite = branch.opposite( at );
            if( !visited.contains( branch ) ) {  //don't cross the same bridge twice.
                visited.add( branch );
                double dv = branch.getVoltageDrop();
                if( branch.getEndJunction() == opposite ) {
                    dv *= -1;
                }
                return getVoltage( visited, opposite, target, volts + dv );
//                    getStrongConnections( visited, opposite );
            }
        }
        return Double.POSITIVE_INFINITY;
    }
//
//    private Branch[] getAdjacentBranches( Branch target ) {
//        ArrayList all = new ArrayList();
//        Branch[] a = getAdjacentBranches( target.getStartJunction() );
//        Branch[] b = getAdjacentBranches( target.getEndJunction() );
//        all.addAll( Arrays.asList( a ) );
//        for( int i = 0; i < b.length; i++ ) {
//            Branch branch = b[i];
//            if( !all.contains( branch ) ) {
//                all.add( branch );
//            }
//        }
//        return (Branch[])all.toArray( new Branch[0] );
//    }

}
