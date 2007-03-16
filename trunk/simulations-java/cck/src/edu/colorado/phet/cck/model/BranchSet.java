/** Sam Reid*/
package edu.colorado.phet.cck.model;

import edu.colorado.phet.cck.model.components.Branch;
import edu.colorado.phet.common.math.AbstractVector2D;
import edu.colorado.phet.common.math.Vector2D;

import java.util.ArrayList;

/**
 * User: Sam Reid
 * Date: Jun 13, 2004
 * Time: 2:46:47 PM
 * Copyright (c) Jun 13, 2004 by Sam Reid
 */
public class BranchSet {
    private ArrayList branches = new ArrayList();
    private ArrayList junctions = new ArrayList();
    private Circuit circuit;

    public BranchSet( Circuit circuit, Branch[] branchs ) {
        this.circuit = circuit;
        addBranches( branchs );
    }

    public void addJunction( Junction junction ) {
        if( !junctions.contains( junction ) ) {
            junctions.add( junction );
        }
    }

    public void addBranches( Branch[] b ) {
        for( int i = 0; i < b.length; i++ ) {
            if( !branches.contains( b[i] ) ) {
                branches.add( b[i] );
            }
        }
    }

    /**
     * This makes sure the right components get the notification event.
     */
    public void translate( AbstractVector2D vector ) {
        ArrayList junctionSet = new ArrayList();
        junctionSet.addAll( junctions );
        for( int i = 0; i < branches.size(); i++ ) {
            Branch branch = (Branch)branches.get( i );
            if( !junctionSet.contains( branch.getStartJunction() ) ) {
                junctionSet.add( branch.getStartJunction() );
            }
            if( !junctionSet.contains( branch.getEndJunction() ) ) {
                junctionSet.add( branch.getEndJunction() );
            }
        }
        ArrayList branchesToNotify = new ArrayList();
        branchesToNotify.addAll( branches );
        for( int i = 0; i < junctionSet.size(); i++ ) {
            Junction junction = (Junction)junctionSet.get( i );
            junction.translateNoNotify( vector.getX(), vector.getY() );//can't do one-at-a-time, because intermediate notifications get inconsistent data.
            Branch[] neighbors = circuit.getAdjacentBranches( junction );
            for( int j = 0; j < neighbors.length; j++ ) {
                Branch neighbor = neighbors[j];
                if( !branchesToNotify.contains( neighbor ) ) {
                    branchesToNotify.add( neighbor );
                }
            }
        }
        for( int i = 0; i < junctionSet.size(); i++ ) {
            Junction junction = (Junction)junctionSet.get( i );
            junction.notifyChanged();
        }
        for( int i = 0; i < branchesToNotify.size(); i++ ) {
            Branch branch = (Branch)branchesToNotify.get( i );
            branch.notifyObservers();
        }
        Branch[] moved = (Branch[])branchesToNotify.toArray( new Branch[0] );
        circuit.fireJunctionsMoved();
        circuit.fireBranchesMoved( moved );
    }

    public void removeBranch( Branch b ) {
        branches.remove( b );
    }

    public void translate( double dx, double dy ) {
        translate( new Vector2D.Double( dx, dy ) );
    }

    public Branch[] getBranches() {
        return (Branch[])branches.toArray( new Branch[0] );
    }
}
