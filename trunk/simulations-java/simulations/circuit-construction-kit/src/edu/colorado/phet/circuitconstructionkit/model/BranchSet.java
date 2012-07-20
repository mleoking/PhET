// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.circuitconstructionkit.model;

import java.util.ArrayList;

import edu.colorado.phet.circuitconstructionkit.model.components.Branch;
import edu.colorado.phet.common.phetcommon.math.AbstractVector2D;
import edu.colorado.phet.common.phetcommon.math.MutableVector2D;

/**
 * User: Sam Reid
 * Date: Jun 13, 2004
 * Time: 2:46:47 PM
 */
public class BranchSet {
    private ArrayList<Branch> branches = new ArrayList<Branch>();
    private ArrayList<Junction> junctions = new ArrayList<Junction>();
    private Circuit circuit;

    public BranchSet( Circuit circuit, Branch[] branchs ) {
        this.circuit = circuit;
        addBranches( branchs );
    }

    public void addJunction( Junction junction ) {
        if ( !junctions.contains( junction ) ) {
            junctions.add( junction );
        }
    }

    public void addBranches( Branch[] b ) {
        for ( int i = 0; i < b.length; i++ ) {
            if ( !branches.contains( b[i] ) ) {
                branches.add( b[i] );
            }
        }
    }

    /**
     * This makes sure the right components get the notification event.
     */
    public void translate( AbstractVector2D vector ) {
        ArrayList<Junction> junctionSet = new ArrayList<Junction>();
        junctionSet.addAll( junctions );
        for ( int i = 0; i < branches.size(); i++ ) {
            Branch branch = branches.get( i );
            if ( !junctionSet.contains( branch.getStartJunction() ) ) {
                junctionSet.add( branch.getStartJunction() );
            }
            if ( !junctionSet.contains( branch.getEndJunction() ) ) {
                junctionSet.add( branch.getEndJunction() );
            }
        }
        ArrayList<Branch> branchesToNotify = new ArrayList<Branch>();
        branchesToNotify.addAll( branches );
        for ( int i = 0; i < junctionSet.size(); i++ ) {
            Junction junction = junctionSet.get( i );
            junction.translateNoNotify( vector.getX(), vector.getY() );//can't do one-at-a-time, because intermediate notifications get inconsistent data.
            Branch[] neighbors = circuit.getAdjacentBranches( junction );
            for ( int j = 0; j < neighbors.length; j++ ) {
                Branch neighbor = neighbors[j];
                if ( !branchesToNotify.contains( neighbor ) ) {
                    branchesToNotify.add( neighbor );
                }
            }
        }
        for ( int i = 0; i < junctionSet.size(); i++ ) {
            Junction junction = junctionSet.get( i );
            junction.notifyChanged();
        }
        for ( int i = 0; i < branchesToNotify.size(); i++ ) {
            Branch branch = branchesToNotify.get( i );
            branch.notifyObservers();
        }
        Branch[] moved = branchesToNotify.toArray( new Branch[0] );
        circuit.fireJunctionsMoved();
        circuit.fireBranchesMoved( moved );
    }

    public void removeBranch( Branch b ) {
        branches.remove( b );
    }

    public void translate( double dx, double dy ) {
        translate( new MutableVector2D( dx, dy ) );
    }

    public Branch[] getBranches() {
        return branches.toArray( new Branch[0] );
    }
}
