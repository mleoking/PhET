package edu.colorado.phet.cck.piccolo_cck;

import edu.colorado.phet.cck.model.BranchSet;
import edu.colorado.phet.cck.model.Circuit;
import edu.colorado.phet.cck.model.Junction;
import edu.colorado.phet.cck.model.components.Branch;
import edu.colorado.phet.cck.model.components.CircuitComponent;
import edu.colorado.phet.common.math.AbstractVector2D;
import edu.colorado.phet.common.math.ImmutableVector2D;
import edu.colorado.phet.common.math.Vector2D;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * User: Sam Reid
 * Date: Sep 17, 2006
 * Time: 8:43:39 PM
 * Copyright (c) Sep 17, 2006 by Sam Reid
 */

public class CircuitInteractionModel {
    private Circuit circuit;
    private Junction stickyTarget;
    private ImmutableVector2D.Double toStart;
    private boolean isDragging = false;
    private Circuit.DragMatch match;
    private ImmutableVector2D.Double toEnd;
    private Circuit.DragMatch startMatch;
    private Circuit.DragMatch endMatch;

    public CircuitInteractionModel( Circuit circuit ) {
        this.circuit = circuit;
    }

    public void translate( Branch branch, Point2D dragPt ) {
        if( !isDragging ) {
            isDragging = true;
            toStart = new ImmutableVector2D.Double( dragPt, branch.getStartJunction().getPosition() );
            toEnd = new ImmutableVector2D.Double( dragPt, branch.getEndJunction().getPosition() );
        }
        else {
            if( branch instanceof CircuitComponent ) {
                CircuitComponent cc = (CircuitComponent)branch;

                if( !isDragging ) {
                    isDragging = true;
                    Point2D startJ = cc.getStartJunction().getPosition();
                    toStart = new ImmutableVector2D.Double( dragPt, startJ );
                }
                Point2D newStartPosition = toStart.getDestination( dragPt );
                Vector2D dx = new Vector2D.Double( cc.getStartJunction().getPosition(), newStartPosition );
                Branch[] sc = circuit.getStrongConnections( cc.getStartJunction() );
                match = getCircuit().getBestDragMatch( sc, dx );
                if( match == null ) {
                    BranchSet branchSet = new BranchSet( circuit, sc );
                    branchSet.translate( dx );
                }
                else {
                    Vector2D vector = match.getVector();
                    BranchSet branchSet = new BranchSet( circuit, sc );
                    branchSet.translate( vector );
                }
            }
            else {//branch was just a wire
                Point2D newStartPosition = toStart.getDestination( dragPt );
                Point2D newEndPosition = toEnd.getDestination( dragPt );
                Branch[] scStart = circuit.getStrongConnections( branch.getStartJunction() );
                Branch[] scEnd = circuit.getStrongConnections( branch.getEndJunction() );
                Vector2D startDX = new Vector2D.Double( branch.getStartJunction().getPosition(), newStartPosition );
                Vector2D endDX = new Vector2D.Double( branch.getEndJunction().getPosition(), newEndPosition );
                Junction[] startSources = getSources( scStart, branch.getStartJunction() );
                Junction[] endSources = getSources( scEnd, branch.getEndJunction() );
                //how about removing any junctions in start and end that share a branch?
                //Is this sufficient to keep from dropping wires directly on other wires?

                startMatch = getCircuit().getBestDragMatch( startSources, startDX );
                endMatch = getCircuit().getBestDragMatch( endSources, endDX );

                if( startMatch != null && endMatch != null ) {
                    for( int i = 0; i < circuit.numBranches(); i++ ) {
                        Branch b = circuit.branchAt( i );
                        if( b.hasJunction( startMatch.getTarget() ) && branch.hasJunction( endMatch.getTarget() ) ) {
                            startMatch = null;
                            endMatch = null;
                            break;
                        }
                    }
                }
                apply( scStart, startDX, branch.getStartJunction(), startMatch );
                apply( scEnd, endDX, branch.getEndJunction(), endMatch );
            }
        }
    }

    public void dropBranch( Branch branch ) {
        if( branch instanceof CircuitComponent ) {
            isDragging = false;
            if( match != null ) {
                getCircuit().collapseJunctions( match.getSource(), match.getTarget() );
            }
            match = null;
            //todo, need bumpAway implementation
//        circuitGraphic.bumpAway( branchGraphic.getCircuitComponent() );
        }
        else {
            isDragging = false;
            if( startMatch != null ) {
                getCircuit().collapseJunctions( startMatch.getSource(), startMatch.getTarget() );
            }
            if( endMatch != null ) {
                getCircuit().collapseJunctions( endMatch.getSource(), endMatch.getTarget() );
            }
            //todo need bumpaway implementation
//            circuitGraphic.bumpAway( branch );
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

    public void dragJunction( Junction junction, Point2D target ) {
        junction.setPosition( getDestination( junction, target ) );
    }

    private Point2D getDestination( Junction junction, Point2D target ) {
        if( getSoleComponent( junction ) != null ) {
            CircuitComponent branch = getSoleComponent( junction );
            Junction opposite = branch.opposite( junction );
            AbstractVector2D vec = new ImmutableVector2D.Double( opposite.getPosition(), target );
            vec = vec.getInstanceOfMagnitude( branch.getComponentLength() );
            Point2D dst = vec.getDestination( opposite.getPosition() );
//            junction.setPosition( dst.getX(), dst.getY() );
            return dst;
//            branch.notifyObservers();
//            cg.getCircuit().fireJunctionsMoved();
//            CircuitComponent cc = getSoleComponent( junction );
//            Junction opposite = cc.opposite( junction );
//            AbstractVector2D vec = new ImmutableVector2D.Double( opposite.getPosition(), target );
//            vec = vec.getInstanceOfMagnitude( cc.getComponentLength() );
//            return vec.getDestination( opposite.getPosition() );
        }
        else {
            stickyTarget = getStickyTarget( junction, target.getX(), target.getY() );
            if( stickyTarget != null ) {
                target = stickyTarget.getPosition();
            }
            return target;
        }
    }

    private CircuitComponent getCircuitComponent( Junction junction ) {
        Branch[]b = circuit.getAdjacentBranches( junction );
        for( int i = 0; i < b.length; i++ ) {
            Branch branch = b[i];
            if( branch instanceof CircuitComponent ) {
                return (CircuitComponent)branch;
            }
        }
        return null;
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

    private Junction[] getSources( Branch[] sc, Junction j ) {
        ArrayList list = new ArrayList( Arrays.asList( Circuit.getJunctions( sc ) ) );
        if( !list.contains( j ) ) {
            list.add( j );
        }
        return (Junction[])list.toArray( new Junction[0] );
    }

    private void apply( Branch[] sc, Vector2D dx, Junction junction, Circuit.DragMatch match ) {
        if( match == null ) {
            BranchSet bs = new BranchSet( circuit, sc );
            bs.addJunction( junction );
            bs.translate( dx );
        }
        else {
            BranchSet bs = new BranchSet( circuit, sc );
            AbstractVector2D vec = match.getVector();
            bs.addJunction( junction );
            bs.translate( vec );
        }
//        System.out.println( "match = " + match );
    }


}
