package edu.colorado.phet.semiconductor.macro.circuit;

import edu.colorado.phet.common_semiconductor.math.PhetVector;

import java.util.ArrayList;

/**
 * User: Sam Reid
 * Date: Jan 15, 2004
 * Time: 1:04:42 PM
 * Copyright (c) Jan 15, 2004 by Sam Reid
 */
public class CompositeLinearBranch {
    ArrayList branches = new ArrayList();

    public void addBranch( LinearBranch branch ) {
        branches.add( branch );
    }

    public double getLength() {
        double sum = 0;
        for( int i = 0; i < branches.size(); i++ ) {
            LinearBranch linearBranch = (LinearBranch)branches.get( i );
            sum += linearBranch.getLength();
        }
        return sum;
    }

    public PhetVector getPosition( double dist ) {
        double start = 0;
        for( int i = 0; i < branches.size(); i++ ) {
            LinearBranch linearBranch = (LinearBranch)branches.get( i );
            if( linearBranch.getLength() + start >= dist ) {
                double distAlongBranch = dist - start;
                return linearBranch.getLocation( distAlongBranch );
            }
            start += linearBranch.getLength();
        }
        return null;
    }

    public LinearBranch branchAt( int i ) {
        return (LinearBranch)this.branches.get( i );
    }

    public int numBranches() {
        return branches.size();
    }
}
