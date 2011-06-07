// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.circuitconstructionkit.model;

import java.awt.geom.Point2D;
import java.util.Arrays;

import edu.colorado.phet.circuitconstructionkit.model.components.Branch;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;

/**
 * Model class for an Electron object, which moves along a Branch.
 */
public class Electron extends SimpleObservableDebug {
    private Branch branch;
    private double distAlongWire;
    private Point2D position;
    private double radius = .1;
    private SimpleObserver observer;
    private boolean deleted = false;

    public Electron( Branch branch, double distAlongWire ) {
        if ( Double.isNaN( distAlongWire ) ) {
            throw new RuntimeException( "Dist along wire is NaN." );
        }
        this.branch = branch;
        this.distAlongWire = distAlongWire;
        if ( distAlongWire < 0 || distAlongWire > branch.getLength() ) {
            throw new RuntimeException( "Electron out of bounds." );
        }
        updatePosition();
        observer = new Observer();
        branch.addObserver( observer );
    }

    public class Observer implements SimpleObserver {
        public void update() {
            if ( isDeleted() ) {
                new RuntimeException( "Update called on deleted electron." ).printStackTrace();
            }
            updatePosition();
        }

        public boolean isDeleted() {
            return Electron.this.isDeleted();
        }
    }

    private void updatePosition() {
        Point2D pt = branch.getPosition( distAlongWire );
        if ( isNaN( pt ) ) {
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
        if ( Double.isNaN( dist ) ) {
            throw new RuntimeException( "Dist along wire is NaN." );
        }
        if ( getBranch().containsScalarLocation( dist ) ) {
            if ( dist != distAlongWire ) {
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
        if ( deleted ) {
            throw new RuntimeException( "Already deleted!" );
        }
        return branch;
    }

    public Point2D getPosition() {
        if ( deleted ) {
            throw new RuntimeException( "Already deleted!" );
        }
        if ( Double.isNaN( position.getX() ) || Double.isNaN( position.getY() ) ) {
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
        if ( Double.isNaN( x ) ) {
            throw new RuntimeException( "x was NaN, for electron distance along branch." );
        }
        else if ( !branch.containsScalarLocation( x ) ) {
            throw new RuntimeException( "No location in branch." );
        }
        if ( this.branch != branch ) {
            this.branch = branch;
            this.branch.removeObserver( observer );
            branch.addObserver( observer );
        }
        if ( distAlongWire != x ) {
            this.distAlongWire = x;
            updatePosition();
        }
    }

    public String toString() {
        return "Electron, deleted=" + deleted + ", observers= " + Arrays.asList( getObserverList() );
    }

    //For use in debugging.
    public boolean isDeleted() {
        return deleted;
    }
}