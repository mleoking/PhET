/*Copyright, University of Colorado, 2004.*/
package edu.colorado.phet.cck.elements.branch;

import edu.colorado.phet.cck.elements.circuit.Circuit;
import edu.colorado.phet.common.math.PhetVector;

import java.util.ArrayList;

/**
 * User: Sam Reid
 * Date: Sep 8, 2003
 * Time: 3:25:11 AM
 * Copyright (c) Sep 8, 2003 by Sam Reid
 */
public class CompositeBranch {
    private ArrayList branches = new ArrayList();
    private Circuit parent;
    private double x;
    private double y;

    public CompositeBranch( Circuit parent, double x, double y ) {
        this.parent = parent;
        this.x = x;
        this.y = y;
    }

    public void lineTo( double x, double y ) {
        if( branches.size() == 0 ) {
            Branch firstBranch = new BareBranch( parent, this.x, this.y, x, y );
            branches.add( firstBranch );
        }
        else {
            Branch nextBranch = new BareBranch( parent, this.x, this.y, x, y );
            branches.add( nextBranch );
        }
        this.x = x;
        this.y = y;
    }

    public void addRelativePoint( PhetVector pt ) {
        addRelativePoint( pt.getX(), pt.getY() );
    }

    public void addRelativePoint( double x, double y ) {
        lineTo( this.x + x, this.y + y );
    }

    public PhetVector getPosition2D( double x ) {
        double totalDist = 0;
        for( int i = 0; i < branches.size(); i++ ) {
            Branch branch2 = (Branch)branches.get( i );
            double localDist = x - totalDist;
            if( branch2.containsScalarLocation( localDist ) ) {
                return branch2.getPosition2D( localDist );
            }
            totalDist += branch2.getLength();
        }
        return null;
    }

    public boolean containsScalarLocation( double x ) {
        return x >= 0 && x <= getLength();
    }

    public double getLength() {
        double dist = 0;
        for( int i = 0; i < branches.size(); i++ ) {
            Branch branch2 = (Branch)branches.get( i );
            dist += branch2.getLength();
        }
        return dist;
    }

    public PhetVector getEndPoint() {
        return new PhetVector( x, y );
    }


    public ArrayList getBranches() {
        return branches;
    }

    public void setBranches( ArrayList branches ) {
        this.branches = branches;
    }

    public Circuit getParent() {
        return parent;
    }

    public void setParent( Circuit parent ) {
        this.parent = parent;
    }

    public double getX() {
        return x;
    }

    public void setX( double x ) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY( double y ) {
        this.y = y;
    }

}
