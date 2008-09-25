// Decompiled by Jad v1.5.8f. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 

package edu.colorado.phet.conductivity.macro.circuit;

import java.util.ArrayList;

import edu.colorado.phet.common.phetcommon.math.AbstractVector2D;

// Referenced classes of package edu.colorado.phet.semiconductor.macro.circuit:
//            LinearBranch

public class CompositeLinearBranch {

    public CompositeLinearBranch() {
        branches = new ArrayList();
    }

    public void addBranch( LinearBranch linearbranch ) {
        branches.add( linearbranch );
    }

    public double getLength() {
        double d = 0.0D;
        for ( int i = 0; i < branches.size(); i++ ) {
            LinearBranch linearbranch = (LinearBranch) branches.get( i );
            d += linearbranch.getLength();
        }

        return d;
    }

    public AbstractVector2D getPosition( double d ) {
        double d1 = 0.0D;
        for ( int i = 0; i < branches.size(); i++ ) {
            LinearBranch linearbranch = (LinearBranch) branches.get( i );
            if ( linearbranch.getLength() + d1 >= d ) {
                double d2 = d - d1;
                return linearbranch.getLocation( d2 );
            }
            d1 += linearbranch.getLength();
        }

        return null;
    }

    public LinearBranch branchAt( int i ) {
        return (LinearBranch) branches.get( i );
    }

    public int numBranches() {
        return branches.size();
    }

    ArrayList branches;
}
