// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.semiconductor.macro.circuit;

import java.util.ArrayList;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;


/**
 * User: Sam Reid
 * Date: Jan 15, 2004
 * Time: 1:04:42 PM
 */
public class CompositeLinearBranch {
    ArrayList branches = new ArrayList();

    public void addBranch( LinearBranch branch ) {
        branches.add( branch );
    }

    public double getLength() {
        double sum = 0;
        for ( int i = 0; i < branches.size(); i++ ) {
            LinearBranch linearBranch = (LinearBranch) branches.get( i );
            sum += linearBranch.getLength();
        }
        return sum;
    }

    public ImmutableVector2D getPosition( double dist ) {
        double start = 0;
        for ( int i = 0; i < branches.size(); i++ ) {
            LinearBranch linearBranch = (LinearBranch) branches.get( i );
            if ( linearBranch.getLength() + start >= dist ) {
                double distAlongBranch = dist - start;
                return linearBranch.getLocation( distAlongBranch );
            }
            start += linearBranch.getLength();
        }
        return null;
    }

    public LinearBranch branchAt( int i ) {
        return (LinearBranch) this.branches.get( i );
    }

    public int numBranches() {
        return branches.size();
    }
}
