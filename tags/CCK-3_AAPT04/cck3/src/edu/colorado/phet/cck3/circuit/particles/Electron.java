/** Sam Reid*/
package edu.colorado.phet.cck3.circuit.particles;

import edu.colorado.phet.cck3.circuit.Branch;
import edu.colorado.phet.common.util.SimpleObservable;
import edu.colorado.phet.common.util.SimpleObserver;

import java.awt.geom.Point2D;

/**
 * User: Sam Reid
 * Date: May 29, 2004
 * Time: 12:19:48 AM
 * Copyright (c) May 29, 2004 by Sam Reid
 */
public class Electron extends SimpleObservable {
    private Branch branch;
    double distAlongWire;
    Point2D position;
    double radius = .1;
    private SimpleObserver observer;
    private boolean deleted = false;

    public Electron( Branch branch, double distAlongWire ) {
        if( Double.isNaN( distAlongWire ) ) {
            throw new RuntimeException( "Dist along wire is NaN." );
        }
        this.branch = branch;
        this.distAlongWire = distAlongWire;
        if( distAlongWire < 0 || distAlongWire > branch.getLength() ) {
            throw new RuntimeException( "Electron out of bounds." );
        }
        updatePosition();
        observer = new SimpleObserver() {
            public void update() {
                if( deleted ) {
                    throw new RuntimeException( "Update called on deleted electron." );
                }
                updatePosition();
            }
        };
        branch.addObserver( observer );
    }

    private void updatePosition() {
        Point2D pt = branch.getPosition( distAlongWire );
        if( isNaN( pt ) ) {
            int x = 3;
            pt = branch.getPosition( distAlongWire );
            throw new RuntimeException( "Point was NaN, pt=" + pt + ", dist=" + distAlongWire + ", wire length=" + branch.getLength() );
        }
        this.position = pt;
        notifyObservers();
    }

    private boolean isNaN( Point2D pt ) {
        return Double.isNaN( pt.getX() ) || Double.isNaN( pt.getY() );
    }

    public void setDistAlongWire( double dist ) {
        if( Double.isNaN( dist ) ) {
            throw new RuntimeException( "Dist along wire is NaN." );
        }
        if( getBranch().containsScalarLocation( dist ) ) {
            if( dist != distAlongWire ) {
                this.distAlongWire = dist;
                updatePosition();
            }
        }
        else {
            throw new RuntimeException( "Position not in wire, x=" + dist + ", length=" + getBranch().getLength() );
        }
    }

    public void delete() {
        branch.removeObserver( observer );
        this.deleted = true;
    }

    public Branch getBranch() {
        if( deleted ) {
            throw new RuntimeException( "Already deleted!" );
        }
        return branch;
    }

    public Point2D getPosition() {
        if( deleted ) {
            throw new RuntimeException( "Already deleted!" );
        }
        if( Double.isNaN( position.getX() ) || Double.isNaN( position.getY() ) ) {
            throw new RuntimeException( "Position is NaN." );
        }
        return position;
    }

    public double getRadius() {
        return radius;
    }

    public double getDistAlongWire() {
        return distAlongWire;
    }

    public void setLocation( Branch branch, double x ) {
        if( this.branch != branch ) {
            this.branch.removeObserver( observer );
            branch.addObserver( observer );
        }
        if( Double.isNaN( x ) ) {
            throw new RuntimeException( "x was NaN, for electron distance along branch." );
        }
        if( branch.containsScalarLocation( x ) ) {
            this.branch = branch;
            if( distAlongWire != x ) {
                this.distAlongWire = x;
                updatePosition();
            }
        }
        else {
            throw new RuntimeException( "No such location in branch, branch length=" + branch.getLength() + ", x=" + x );
        }
    }
}
