/*Copyright, Sam Reid, 2003.*/
package edu.colorado.phet.cck.elements.circuit;

import edu.colorado.phet.cck.elements.branch.Branch;
import edu.colorado.phet.cck.elements.branch.components.HasResistance;
import edu.colorado.phet.cck.elements.junction.Junction;
import edu.colorado.phet.cck.elements.particles.ObjectSelector;
import edu.colorado.phet.common.math.PhetVector;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;

/**
 * User: Sam Reid
 * Date: Aug 28, 2003
 * Time: 1:53:42 AM
 * Copyright (c) Aug 28, 2003 by Sam Reid
 */
public class Circuit {
    ArrayList branches = new ArrayList();
    ArrayList circuitObservers = new ArrayList();

    public Circuit() {
    }

    public String toString() {
        return branches.toString();
    }

    public ArrayList getBranches() {
        return branches;
    }

    public void setBranches( ArrayList branches ) {
        this.branches = branches;
    }

    public int numBranches() {
        return branches.size();
    }

    /**
     * Adds a disconnected branch to this circuit.
     */
    public void addCircuitObserver( CircuitObserver observer ) {
        circuitObservers.add( observer );
    }

    public void addBranch( final Branch branch ) {
        branches.add( branch );
        fireBranchAdded( branch );
    }

    private void fireBranchAdded( Branch branch ) {
        for( int i = 0; i < circuitObservers.size(); i++ ) {
            CircuitObserver observer2 = (CircuitObserver)circuitObservers.get( i );
            observer2.branchAdded( this, branch );
        }
    }

    public void removeBranch( Branch branch ) {
        branches.remove( branch );
        for( int i = 0; i < branches.size(); i++ ) {
            Branch branch2 = (Branch)branches.get( i );
            branch2.removeJunction( branch.getStartJunction() );
            branch2.removeJunction( branch.getEndJunction() );
        }
        fireBranchRemoved( branch );
    }

    private void fireBranchRemoved( Branch branch ) {
        for( int i = 0; i < circuitObservers.size(); i++ ) {
            CircuitObserver observer2 = (CircuitObserver)circuitObservers.get( i );
            observer2.branchRemoved( this, branch );
        }
    }

    public ArrayList getJunctions() {
        ArrayList j = new ArrayList( branches.size() * 2 );
        for( int i = 0; i < branches.size(); i++ ) {
            Branch branch2 = (Branch)branches.get( i );
            if( !j.contains( branch2.getStartJunction() ) ) {
                j.add( branch2.getStartJunction() );
            }
            if( !j.contains( branch2.getEndJunction() ) ) {
                j.add( branch2.getEndJunction() );
            }
        }
        return j;
    }

    public Junction getClosestJunction( Junction junction ) {
        return getClosestJunction( junction, junction.getX(), junction.getY() );
    }

    public Junction getClosestJunction( Junction junction, double x, double y ) {
        double best = Double.POSITIVE_INFINITY;
        ArrayList junctions = getJunctions();

        Junction bestValue = null;
        for( int i = 0; i < junctions.size(); i++ ) {
            Junction j2 = (Junction)junctions.get( i );
            if( okayMatch( j2, junction ) ) {//TODO broken//|| j2 != junction
                double dist = Point2D.Double.distance( j2.getX(), j2.getY(), x, y );
                if( dist < best ) {
                    best = dist;
                    bestValue = j2;
                }
            }
        }
        return bestValue;
    }

    private boolean okayMatch( Junction j2, Junction junction ) {
        for( int i = 0; i < branches.size(); i++ ) {
            Branch branch2 = (Branch)branches.get( i );
            if( branch2.connectsTo( j2 ) && branch2.connectsTo( junction ) ) {
                return false;
            }
        }
        if( j2 == junction ) {
            return false;
        }
        else if( j2.getBranch() == junction.getBranch() ) {
            return false;
        }
        else if( j2.hasConnection( junction ) ) {
            return false;
        }
        else if( junction.hasConnection( j2 ) ) {
            return false;
        }
        else if( j2.getBranch().containsJunction( junction ) ) {
            return false;
        }
        else if( junction.getBranch().containsJunction( j2 ) ) {
            return false;
        }
        else if( j2.getBranch().connectsTo( junction ) ) {
            return false;
        }
        else if( junction.getBranch().connectsTo( j2 ) ) {
            return false;
        }
        else if( itWouldMakeTwoSegmentsTotallyOverlap( j2, junction ) ) {
            return false;
        }
        return true;
    }

    /**
     * TODO this fails when you add a new branch to an existing branch with multiple connections.
     */
    private boolean itWouldMakeTwoSegmentsTotallyOverlap( Junction j1, Junction j2 ) {
        Junction opposite1 = j1.getBranch().getOppositeJunction( j1 );
        Junction opposite2 = j2.getBranch().getOppositeJunction( j2 );
        if( opposite1.hasConnection( opposite2 ) ) {
            return true;
        }
        if( opposite2.hasConnection( opposite1 ) ) {
            return true;
        }
        return false;
    }

    public void splitJunction( Junction junction ) {
        Junction[] j = junction.getConnections();
        junction.splitJunction();
        for( int i = 0; i < j.length; i++ ) {
            moveToNeighbor( j[i] );
        }
        moveToNeighbor( junction );
        fireConnectivityChanged();
    }

    private void moveToNeighbor( Junction junction ) {
        Junction other = junction.getBranch().getOppositeJunction( junction );
        PhetVector vector = new PhetVector( other.getX() - junction.getX(), other.getY() - junction.getY() );
        vector.setMagnitude( .4 );//TODO MAGIC
        junction.translate( vector.getX(), vector.getY() );
    }

    public Branch branchAt( int i ) {
        return (Branch)branches.get( i );
    }

    public void fireConnectivityChanged() {
        for( int i = 0; i < circuitObservers.size(); i++ ) {
            CircuitObserver circuitObserver2 = (CircuitObserver)circuitObservers.get( i );
            circuitObserver2.connectivityChanged( this );
        }
    }

    public JunctionGroup[] getJunctionGroups() {
        HashSet all = new HashSet();
        ArrayList junctions = getJunctions();
        for( int i = 0; i < junctions.size(); i++ ) {
            Junction junction2 = (Junction)junctions.get( i );
            JunctionGroup jg = new JunctionGroup( junction2 );
            all.add( jg );
        }
        return (JunctionGroup[])all.toArray( new JunctionGroup[0] );
    }

    public JunctionGroup getJunctionGroup( Junction junction ) {
        return new JunctionGroup( junction );
    }

    public Branch[] getBranches( JunctionGroup jg, ObjectSelector os ) {
        HashSet all = new HashSet();
        Junction[] j = jg.getJunctions();
        for( int i = 0; i < j.length; i++ ) {
            Junction junction2 = j[i];
            Branch branch = junction2.getBranch();
            if( os.isValid( branch ) ) {
                all.add( junction2.getBranch() );
            }
        }
        return (Branch[])all.toArray( new Branch[0] );
    }

    public Branch[] getBranches( Junction junction ) {
        ArrayList all = new ArrayList();
        for( int i = 0; i < branches.size(); i++ ) {
            Branch branch2 = (Branch)branches.get( i );
            if( branch2.containsJunction( junction ) || branch2.connectsTo( junction ) ) {
                all.add( branch2 );
            }
//            else {
//                Junction[] conn=junction.getConnections();
//                for (int j = 0; j < conn.length; j++) {
//                    Junction junction2 = conn[j];
//                    if (branch2.containsJunction(junction2))
//                    {
//                        all.add(branch2);
//                        break;
//                    }
//                }
//            }
//        }
        }
        return (Branch[])all.toArray( new Branch[0] );
    }

    public Branch[] getAdjacentBranches( Branch branch ) {
        ArrayList list = new ArrayList();
        Branch[] a = getBranches( branch.getStartJunction() );
        Branch[] b = getBranches( branch.getEndJunction() );
        for( int i = 0; i < a.length; i++ ) {
            Branch branch2 = a[i];
            if( !list.contains( branch2 ) ) {
                list.add( branch2 );
            }
        }
        for( int i = 0; i < b.length; i++ ) {
            Branch branch2 = b[i];
            if( !list.contains( branch2 ) ) {
                list.add( branch2 );
            }
        }
        return (Branch[])list.toArray( new Branch[0] );
    }

    /**
     * Returns all paths from a to b.
     */
    public BranchPath[] getPaths( Junction a, Junction b ) {
        ArrayList list = new ArrayList();
        if( a.hasConnection( b ) ) {
//            System.out.println("Connected to start.");
            return new BranchPath[0];
        }
        if( a == b ) {
//            System.out.println("Same as start.");
            return new BranchPath[0];
        }
        Branch[] startbranches = getBranches( a );
        for( int i = 0; i < startbranches.length; i++ ) {
            Branch startbranch = startbranches[i];
            boolean legal = true;
            if( isInfiniteResistance( startbranch ) ) {
                legal = false;
            }
//            if (!startbranch.containsJunction(a)) {
//                throw new RuntimeException("Branch doesn't contain junction.");
//            }
//            a=startbranch.getEquivalentJunction(a);
            if( legal ) {
                BranchPath path = new BranchPath( startbranch.getEquivalentJunction( a ), startbranch );
                BranchPath[] paths = getPaths( path, b );
                list.addAll( Arrays.asList( paths ) );
            }
        }
        return (BranchPath[])list.toArray( new BranchPath[0] );
    }

    public boolean isInfiniteResistance( Branch b ) {
        if( b instanceof HasResistance ) {
            HasResistance hr = (HasResistance)b;
            if( Double.isInfinite( hr.getResistance() ) ) {
                return true;
            }
        }
        return false;
    }

    //The recursive one.
    private BranchPath[] getPaths( BranchPath startPath, Junction b ) {
        ArrayList all = new ArrayList();
        Junction end = startPath.getEndJunction();
        if( end.hasConnection( b ) ) {
//            System.out.println("end connected to start");
            return new BranchPath[]{startPath};
        }
        else if( end == b ) {
//            System.out.println("End equivalent to start");
            return new BranchPath[]{startPath};
        }
        else {
            //extend paths from end.
            Branch[] br = getBranches( end );
            for( int i = 0; i < br.length; i++ ) {
                Branch branch2 = br[i];
                if( !startPath.containsBranch( branch2 ) && !isInfiniteResistance( branch2 ) )//where no branch has gone before
                {
                    BranchPath newPath = startPath.getAppendedPath( branch2 );
                    BranchPath[] finishedProducts = getPaths( newPath, b );
                    all.addAll( Arrays.asList( finishedProducts ) );
                }
            }
            return (BranchPath[])all.toArray( new BranchPath[0] );
        }
    }

    public double getVoltageDrop( Junction start, Junction end ) {
        BranchPath[] bp = getPaths( start, end );
//        List list = Arrays.asList(bp);
//        System.out.println("start=" + start + ", end=" + end);
//        System.out.println("list = " + list);
        //Get the voltage over a path.
        if( bp.length == 0 ) {
            return 0;//TODO should this return Double.NaN?
        }
        BranchPath path = bp[0];
//        path.preappend(preStart);
//        path.append(postEnd);
        double volts = bp[0].getVoltageDrop();

        return volts;
    }

    public Branch[] getSelectedBranches() {
        ArrayList sel = new ArrayList();
        for( int i = 0; i < branches.size(); i++ ) {
            Branch branch2 = (Branch)branches.get( i );
            if( branch2.isSelected() ) {
                sel.add( branch2 );
            }
        }
        return (Branch[])sel.toArray( new Branch[0] );
    }

    public Junction[] getSelectedJunctions() {
        ArrayList sel = new ArrayList();
        for( int i = 0; i < branches.size(); i++ ) {
            Branch branch2 = (Branch)branches.get( i );
            Junction j2 = branch2.getStartJunction();
            if( j2.isSelected() ) {
                sel.add( j2 );
            }
            if( branch2.getEndJunction().isSelected() ) {
                sel.add( branch2.getEndJunction() );
            }
        }
        return (Junction[])sel.toArray( new Junction[0] );
    }

    /**
     * Adds the branch to this circuit, and connects any branches that are at the same location.
     */
    public void attachBranch( Branch b ) {
        addBranch( b );
        for( int i = 0; i < numBranches(); i++ ) {
            Branch other = branchAt( i );
            connect( b.getStartJunction(), other.getStartJunction() );
            connect( b.getEndJunction(), other.getEndJunction() );
            connect( b.getStartJunction(), other.getEndJunction() );
            connect( b.getEndJunction(), other.getStartJunction() );
        }
    }

    private void connect( Junction endJunction, Junction startJunction ) {
        if( endJunction.getLocation().equals( startJunction.getLocation() ) ) {
            endJunction.addConnection( startJunction );
            startJunction.addConnection( endJunction );
        }
    }


//    public void deleteCurrentDirectionGuesses() {
//        for (int i = 0; i < branches.size(); i++) {
//            branchAt(i).deleteCurrentDirectionGuesses();
//        }
//    }

}