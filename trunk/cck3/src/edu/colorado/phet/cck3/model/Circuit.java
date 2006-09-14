/** Sam Reid*/
package edu.colorado.phet.cck3.model;

import edu.colorado.phet.cck3.model.components.Branch;
import edu.colorado.phet.cck3.model.components.Capacitor;
import edu.colorado.phet.cck3.model.components.CircuitComponent;
import edu.colorado.phet.cck3.model.components.Inductor;
import edu.colorado.phet.cck3.phetgraphics_cck.circuit.CompositeCircuitChangeListener;
import edu.colorado.phet.cck3.phetgraphics_cck.circuit.VoltageCalculation;
import edu.colorado.phet.common_cck.math.AbstractVector2D;
import edu.colorado.phet.common_cck.math.ImmutableVector2D;
import edu.colorado.phet.common_cck.math.Vector2D;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * User: Sam Reid
 * Date: May 24, 2004
 * Time: 1:31:24 AM
 * Copyright (c) May 24, 2004 by Sam Reid
 */
public class Circuit {
    private ArrayList branches = new ArrayList();
    private ArrayList junctions = new ArrayList();
    private ArrayList listeners = new ArrayList();
    private CircuitChangeListener circuitChangeListener;
    private boolean fireKirkhoffChanges = true;

    public Circuit() {
        this( new CompositeCircuitChangeListener() );
    }

    public Circuit( CircuitChangeListener circuitChangeListener ) {
        this.circuitChangeListener = circuitChangeListener;
    }

    public void addCircuitListener( CircuitListener listener ) {
        listeners.add( listener );
        //        System.out.println( "Added " + listeners.size() + "th Circuit listener = " + listener );
    }

    public int numCircuitListeners() {
        return listeners.size();
    }

    public void removeCircuitListener( CircuitListener listener ) {
        listeners.remove( listener );
        //        System.out.println( "Removed  " + listeners.size() + "th Circuit listener = " + listener );
    }

    public String toString() {
        return "Junctions=" + junctions + ", Branches=" + branches;
    }

    public void addJunction( Junction junction ) {
        if( !junctions.contains( junction ) ) {
            junctions.add( junction );
            fireJunctionAdded( junction );
        }
        else {
            //            System.out.println( "Already contained junction." );
        }
    }

    private void fireJunctionAdded( Junction junction ) {
        for( int i = 0; i < listeners.size(); i++ ) {
            CircuitListener circuitListener = (CircuitListener)listeners.get( i );
            circuitListener.junctionAdded( junction );
        }
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
        old.delete();
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
        if( component == null ) {
            throw new RuntimeException( "Null component." );
        }
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
            double newLength = Math.abs( curLength - CCKModel.JUNCTION_RADIUS * 1.5 );
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
        circuitChangeListener.circuitChanged();
        fireJunctionsSplit( junction, newJunctions );
        return newJunctions;
    }

    private void fireJunctionsSplit( Junction junction, Junction[] newJunctions ) {
        for( int i = 0; i < listeners.size(); i++ ) {
            CircuitListener circuitListener = (CircuitListener)listeners.get( i );
            circuitListener.junctionsSplit( junction, newJunctions );
        }
    }

    public void remove( Junction junction ) {
        junctions.remove( junction );
        junction.delete();
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

    public Branch[] getConnectedSubgraph( Junction junction ) {
        ArrayList visited = new ArrayList();
        getConnectedSubgraph( visited, junction );
        Branch[] b = (Branch[])visited.toArray( new Branch[0] );
        return b;
    }

    private void getConnectedSubgraph( ArrayList visited, Junction junction ) {
        Branch[] adj = getAdjacentBranches( junction );
        for( int i = 0; i < adj.length; i++ ) {
            Branch branch = adj[i];
            Junction opposite = branch.opposite( junction );
            if( !visited.contains( branch ) ) {
                visited.add( branch );
                getConnectedSubgraph( visited, opposite );
            }
        }
    }

    public void fireJunctionsMoved() {
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
        branch.delete();
        fireBranchRemoved( branch );
        fireKirkhoffChanged();
    }

    public void setFireKirkhoffChanges( boolean fireKirkhoffChanges ) {
        this.fireKirkhoffChanges = fireKirkhoffChanges;
    }

    private void fireBranchRemoved( Branch branch ) {
        for( int i = 0; i < listeners.size(); i++ ) {
            CircuitListener circuitListener = (CircuitListener)listeners.get( i );
            circuitListener.branchRemoved( branch );
        }
    }

    public void fireKirkhoffChanged() {
        if( fireKirkhoffChanges ) {
            circuitChangeListener.circuitChanged();
        }
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

    public Junction[] getJunctions() {
        return (Junction[])junctions.toArray( new Junction[0] );
    }

    public static Junction[] getJunctions( Branch[] branchs ) {
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

    public double getVoltage( VoltageCalculation.Connection a, VoltageCalculation.Connection b ) {
        VoltageCalculation vc = new VoltageCalculation( this );
        return vc.getVoltage( a, b );
    }

    public void setSelection( Branch branch ) {
        clearSelection();
        branch.setSelected( true );
    }

    public void setSelection( Junction junction ) {
        clearSelection();
        junction.setSelected( true );
    }

    public void clearSelection() {
        for( int i = 0; i < branches.size(); i++ ) {
            Branch branch1 = (Branch)branches.get( i );
            branch1.setSelected( false );
        }
        for( int i = 0; i < junctions.size(); i++ ) {
            Junction junction1 = (Junction)junctions.get( i );
            junction1.setSelected( false );
        }
    }

    public Branch[] getSelectedBranches() {
        ArrayList sel = new ArrayList();
        for( int i = 0; i < branches.size(); i++ ) {
            Branch branch = (Branch)branches.get( i );
            if( branch.isSelected() ) {
                sel.add( branch );
            }
        }
        return (Branch[])sel.toArray( new Branch[0] );
    }

    public Junction[] getSelectedJunctions() {
        ArrayList sel = new ArrayList();
        for( int i = 0; i < junctions.size(); i++ ) {
            Junction branch = (Junction)junctions.get( i );
            if( branch.isSelected() ) {
                sel.add( branch );
            }
        }
        return (Junction[])sel.toArray( new Junction[0] );
    }

    public void selectAll() {
        for( int i = 0; i < branches.size(); i++ ) {
            Branch branch = (Branch)branches.get( i );
            branch.setSelected( true );
        }
        for( int i = 0; i < junctions.size(); i++ ) {
            Junction junction = (Junction)junctions.get( i );
            junction.setSelected( true );
        }
    }

    public void fireJunctionsCollapsed( Junction j1, Junction j2, Junction replacement ) {
        for( int i = 0; i < listeners.size(); i++ ) {
            CircuitListener circuitListener = (CircuitListener)listeners.get( i );
            circuitListener.junctionsConnected( j1, j2, replacement );
        }
    }

    public CircuitListener[] getCircuitListeners() {
        return (CircuitListener[])listeners.toArray( new CircuitListener[0] );
    }

    public void moveToFirst( Junction junction ) {
        junctions.remove( junction );
        junctions.add( 0, junction );
    }

    public CircuitChangeListener getKirkhoffListener() {
        return circuitChangeListener;
    }

    public boolean isDynamic() {
        for( int i = 0; i < numBranches(); i++ ) {
            if( branchAt( i ) instanceof DynamicBranch ) {
                return true;
            }
        }
        return false;
    }

    public void stepInTime( double dt ) {
        for( int i = 0; i < numBranches(); i++ ) {
            if( branchAt( i ) instanceof DynamicBranch ) {
                DynamicBranch b = (DynamicBranch)branchAt( i );
                b.stepInTime( dt );
            }
        }
    }

    public void resetDynamics() {
        for( int i = 0; i < numBranches(); i++ ) {
            if( branchAt( i ) instanceof DynamicBranch ) {
                DynamicBranch b = (DynamicBranch)branchAt( i );
                b.resetDynamics();
            }
        }
    }

    public void setTime( double time ) {
        for( int i = 0; i < numBranches(); i++ ) {
            if( branchAt( i ) instanceof DynamicBranch ) {
                DynamicBranch b = (DynamicBranch)branchAt( i );
                b.setTime( time );
            }
        }
    }

    public int getInductorCount() {
        int sum = 0;
        for( int i = 0; i < branches.size(); i++ ) {
            Branch branch = (Branch)branches.get( i );
            if( branch instanceof Inductor ) {
                sum++;
            }
        }
        return sum;
    }

    public Inductor getInductor( int index ) {
        ArrayList inductors = new ArrayList();
        for( int i = 0; i < branches.size(); i++ ) {
            Branch branch = (Branch)branches.get( i );
            if( branch instanceof Inductor ) {
                inductors.add( branch );
            }
        }
        return (Inductor)inductors.get( index );
    }

    public int getCapacitorCount() {
        int sum = 0;
        for( int i = 0; i < numBranches(); i++ ) {
            if( branchAt( i ) instanceof Capacitor ) {
                sum++;
            }
        }
        return sum;
    }
}
