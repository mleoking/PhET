package edu.colorado.phet.cck.piccolo_cck;

import edu.colorado.phet.cck.model.Circuit;
import edu.colorado.phet.cck.model.Junction;
import edu.colorado.phet.cck.model.components.Branch;
import edu.colorado.phet.cck.model.components.CircuitComponent;
import edu.colorado.phet.cck.model.components.Wire;
import edu.colorado.phet.common.math.AbstractVector2D;
import edu.colorado.phet.common.math.ImmutableVector2D;

import java.awt.geom.Point2D;

/**
 * User: Sam Reid
 * Date: Sep 17, 2006
 * Time: 8:43:39 PM
 * Copyright (c) Sep 17, 2006 by Sam Reid
 */

public class CircuitInteractionModel {
    private Circuit circuit;
    private Junction stickyTarget;

    public CircuitInteractionModel( Circuit circuit ) {
        this.circuit = circuit;
    }

    public void translate( Branch branch, double dx, double dy ) {
        branch.translate( dx, dy );
    }

    public void translate( Junction junction, double dx, double dy ) {
        if( allWires( circuit.getAdjacentBranches( junction ) ) ) {
            junction.translate( dx, dy );
        }
        Branch[]b = circuit.getAdjacentBranches( junction );
        if( b.length == 1 && b[0]instanceof Wire ) {
            junction.translate( dx, dy );
        }
    }

    private CircuitComponent getSoleComponent( Junction j ) {
        if( circuit.getAdjacentBranches( j ).length == 1 && circuit.getAdjacentBranches( j )[0] instanceof CircuitComponent )
        {
            return (CircuitComponent)circuit.getAdjacentBranches( j )[0];
        }
        else {
            return null;
        }
    }

    private boolean allWires( Branch[] adjacentBranches ) {
        for( int i = 0; i < adjacentBranches.length; i++ ) {
            if( !( adjacentBranches[i] instanceof Wire ) ) {
                return false;
            }
        }
        return true;
    }

    public void dragJunction( Junction junction, Point2D target ) {
        junction.setPosition( getDestination( junction, target ) );
    }

    private Point2D getDestination( Junction junction, Point2D target ) {
        if( getSoleComponent( junction ) != null ) {
            CircuitComponent cc = getSoleComponent( junction );
            Junction opposite = cc.opposite( junction );
            AbstractVector2D vec = new ImmutableVector2D.Double( opposite.getPosition(), target );
            vec = vec.getInstanceOfMagnitude( cc.getComponentLength() );
            return vec.getDestination( opposite.getPosition() );
        }
        else {
            stickyTarget = getStickyTarget( junction, target.getX(), target.getY() );
            if( stickyTarget != null ) {
                target = stickyTarget.getPosition();
            }
            return target;
        }
    }

    public Junction getStickyTarget( Junction junction, double x, double y ) {
        double bestDist = Double.POSITIVE_INFINITY;
        double STICKY_DIST = 2;
        Junction best = null;

        for( int i = 0; i < circuit.getJunctions().length; i++ ) {
            //todo see circuitGraphic line 502 for handling strong components
            Junction j = circuit.getJunctions()[i];
            if( j != junction && !getCircuit().hasBranch( junction, j ) && !getCircuit().wouldConnectionCauseOverlappingBranches( junction, j ) )
            {
                double dist = new Point2D.Double( x, y ).distance( j.getX(), j.getY() );
                if( dist < STICKY_DIST && dist < bestDist ) {
                    best = j;
                    bestDist = dist;
                }
            }
        }
        return best;
    }

    private Circuit getCircuit() {
        return circuit;
    }

    public void dropJunction( Junction junction ) {
        if( stickyTarget != null ) {
            getCircuit().collapseJunctions( junction, stickyTarget );
        }
        stickyTarget = null;
    }
}
