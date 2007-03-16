/*Copyright, University of Colorado, 2004.*/
package edu.colorado.phet.cck.elements.particles;

import edu.colorado.phet.cck.elements.branch.Branch;
import edu.colorado.phet.cck.elements.branch.BranchObserver;
import edu.colorado.phet.cck.elements.circuit.Junction;
import edu.colorado.phet.cck.elements.circuit.JunctionGroup;
import edu.colorado.phet.common.math.ImmutableVector2D;
import edu.colorado.phet.common.util.SimpleObservable;


/**
 * User: Sam Reid
 * Date: Sep 4, 2003
 * Time: 3:33:22 AM
 * Copyright (c) Sep 4, 2003 by Sam Reid
 */
public class BranchParticle extends SimpleObservable {
    double x;
    Branch branch;

    public Branch getBranch() {
        return branch;
    }

    public BranchParticle( Branch branch ) {
        this.branch = branch;
        branch.addObserver( new BranchObserver() {
            public void junctionMoved( Branch branch2, Junction j ) {
                notifyObservers();
            }

            public void currentOrVoltageChanged( Branch branch2 ) {
            }
        } );
    }

    public void setBranch( Branch branch, double x, JunctionGroup jg ) {
        this.branch = branch;
        setDistanceFromJunction( x, jg );
        notifyObservers();
    }

    public void setPosition( double x ) {
        if( x == this.x ) {
            return;
        }
        if( branch.containsScalarLocation( x ) ) {
            this.x = x;
            notifyObservers();
        }
        else {
            new RuntimeException( "No such location in wire." ).printStackTrace();
            this.x = 0;
        }
    }

    public double getPosition() {
        return x;
    }

    public ImmutableVector2D getPosition2D() {
        return branch.getPosition2D( x );
    }

    public void setDistanceFromJunction( double distanceOver, JunctionGroup jg ) {
        if( jg.contains( branch.getStartJunction() ) ) {
            setPosition( distanceOver );
        }
        else if( jg.contains( branch.getEndJunction() ) ) {
            setPosition( branch.getLength() - distanceOver );
        }
        else {
            throw new RuntimeException( "No such junction." );
        }
    }

    public double getDistanceFromClosestJunction() {
        double distFromStart = x;
        double distFromEnd = branch.getLength() - x;
        return Math.min( distFromStart, distFromEnd );
    }

}
