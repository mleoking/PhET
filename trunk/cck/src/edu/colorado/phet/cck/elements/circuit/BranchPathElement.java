/*Copyright, University of Colorado, 2004.*/
package edu.colorado.phet.cck.elements.circuit;

import edu.colorado.phet.cck.elements.branch.Branch;
import edu.colorado.phet.cck.elements.branch.components.Battery;

/**
 * User: Sam Reid
 * Date: Oct 26, 2003
 * Time: 11:13:56 PM
 * Copyright (c) Oct 26, 2003 by Sam Reid
 */
public class BranchPathElement {
    Junction startJunction;
    Branch branch;

    public BranchPathElement( Junction startJunction, Branch branch ) {
        this.startJunction = startJunction;
        this.branch = branch;
        if( !branch.containsJunction( startJunction ) ) {
            Junction candidate = branch.getEquivalentJunction( startJunction );
            if( candidate == null ) {
                throw new RuntimeException( "Mismatched branch/junction." );
            }
            else {
                this.startJunction = candidate;
            }
        }
    }

    public String toString() {
        return startJunction.toString() + " -> " + branch;
    }

    public Junction getEndJunction() {
        return branch.getOppositeJunction( startJunction );
    }

    public Junction getStartJunction() {
        return startJunction;
    }

    public Branch getBranch() {
        return branch;
    }

    public boolean isCorrectDirection() {
        return branch.getStartJunction().hasConnection( startJunction );
    }

    public double getVoltageDrop() {
        if( branch instanceof Battery ) {
            Battery b = (Battery)branch;
            if( b.getStartJunction().hasConnection( startJunction ) ) {
                //with the battery.
                return +b.getVoltageDrop();
            }
            else {
                return -b.getVoltageDrop();
            }
        }
        double current = branch.getCurrent();
        if( current == 0 ) {
            return 0;
        }
        if( isCorrectDirection() ) {
            //his start is my start.
            if( current > 0 ) {
                return -Math.abs( branch.getVoltageDrop() );
            }
            else {
                return Math.abs( branch.getVoltageDrop() );
            }
        }
        else {
            if( current > 0 ) {
                return Math.abs( branch.getVoltageDrop() );
            }
            return -Math.abs( branch.getVoltageDrop() );
        }
    }
}
