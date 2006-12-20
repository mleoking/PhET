package edu.colorado.phet.cck.piccolo_cck;

import edu.colorado.phet.cck.model.BranchSet;
import edu.colorado.phet.cck.model.CCKModel;
import edu.colorado.phet.cck.model.Circuit;
import edu.colorado.phet.cck.model.Junction;
import edu.colorado.phet.cck.model.components.Branch;
import edu.colorado.phet.cck.model.components.CircuitComponent;
import edu.colorado.phet.cck.model.components.Wire;
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
    private CCKModel model;
    private Circuit circuit;

    private JunctionInteractionModel junctionInteractionModel;
    private BranchInteractionModel branchInteractionModel;

    public CircuitInteractionModel( CCKModel model ) {
        this.circuit = model.getCircuit();
        this.model = model;
        junctionInteractionModel = new JunctionInteractionModel();
        branchInteractionModel = new BranchInteractionModel();
    }

    public void dragJunction( Junction junction, Point2D target ) {
        junctionInteractionModel.dragJunction( junction, target );
    }

    public void dropJunction( Junction junction ) {
        junctionInteractionModel.dropJunction( junction );
    }

    public void translate( Wire wire, Point2D pt ) {
        branchInteractionModel.translate( wire, pt );
    }

    public void dropBranch( Wire wire ) {
        branchInteractionModel.dropBranch( wire );
    }

    public void translate( Branch branch, Point2D location ) {
        branchInteractionModel.translate( branch, location );
    }

    public void dropBranch( Branch branch ) {
        branchInteractionModel.dropBranch( branch );
    }

    class BranchInteractionModel {

        private ImmutableVector2D.Double toStart;
        private ImmutableVector2D.Double toEnd;

        private boolean draggingBranch = false;
        private Circuit.DragMatch branchDragMatch;

        private Circuit.DragMatch startMatch;
        private Circuit.DragMatch endMatch;

        public void translate( Branch branch, Point2D dragPt ) {
            if( !draggingBranch ) {
                draggingBranch = true;
                toStart = new ImmutableVector2D.Double( dragPt, branch.getStartJunction().getPosition() );
                toEnd = new ImmutableVector2D.Double( dragPt, branch.getEndJunction().getPosition() );
            }
            else {
                if( branch instanceof CircuitComponent ) {
                    translateCircuitComponent( (CircuitComponent)branch, dragPt );
                }
                else if( branch instanceof Wire ) {//branch was just a wire
                    translateWire( (Wire)branch, dragPt );
                }
                else {
                    throw new RuntimeException( "Unknown wire type: " + branch.getClass() );
                }
            }
        }

        private void translateWire( Wire wire, Point2D dragPt ) {
            Point2D newStartPosition = toStart.getDestination( dragPt );
            Point2D newEndPosition = toEnd.getDestination( dragPt );
            Branch[] scStart = circuit.getStrongConnections( wire.getStartJunction() );
            Branch[] scEnd = circuit.getStrongConnections( wire.getEndJunction() );
            Vector2D startDX = new Vector2D.Double( wire.getStartJunction().getPosition(), newStartPosition );
            Vector2D endDX = new Vector2D.Double( wire.getEndJunction().getPosition(), newEndPosition );
            Junction[] startSources = getSources( scStart, wire.getStartJunction() );
            Junction[] endSources = getSources( scEnd, wire.getEndJunction() );
            //how about removing any junctions in start and end that share a branch?
            //Is this sufficient to keep from dropping wires directly on other wires?

            startMatch = getCircuit().getBestDragMatch( startSources, startDX );
            endMatch = getCircuit().getBestDragMatch( endSources, endDX );

            if( endMatch != null && startMatch != null && endMatch.getTarget() == startMatch.getTarget() ) {
                endMatch = null;
            }
            if( endMatch != null && startMatch != null && wouldCauseOverlap( wire, startMatch, endMatch ) ) {
                endMatch = null;
            }

            if( startMatch != null && endMatch != null ) {
                for( int i = 0; i < circuit.numBranches(); i++ ) {
                    Branch b = circuit.branchAt( i );
                    if( b.hasJunction( startMatch.getTarget() ) && wire.hasJunction( endMatch.getTarget() ) ) {
                        startMatch = null;
                        endMatch = null;
                        break;
                    }
                }
            }
            apply( scStart, startDX, wire.getStartJunction(), startMatch );
            apply( scEnd, endDX, wire.getEndJunction(), endMatch );
        }

        private boolean wouldCauseOverlap( Wire wire, Circuit.DragMatch startMatch, Circuit.DragMatch endMatch ) {
            Junction[] neighbors = circuit.getNeighbors( startMatch.getTarget() );
            ArrayList list = new ArrayList();
            list.addAll( Arrays.asList( neighbors ) );
            list.remove( wire.getStartJunction() );
            list.remove( wire.getEndJunction() );
            if( list.contains( endMatch.getTarget() ) ) {
                return true;
            }
            else {
                return false;
            }
        }

        private void translateCircuitComponent( CircuitComponent cc, Point2D dragPt ) {
            if( !draggingBranch ) {
                draggingBranch = true;
                Point2D startJ = cc.getStartJunction().getPosition();
                toStart = new ImmutableVector2D.Double( dragPt, startJ );
            }
            Point2D newStartPosition = toStart.getDestination( dragPt );
            Vector2D dx = new Vector2D.Double( cc.getStartJunction().getPosition(), newStartPosition );
            Branch[] strongComponent = circuit.getStrongConnections( cc.getStartJunction() );
            branchDragMatch = getCircuit().getBestDragMatch( strongComponent, dx );
            BranchSet branchSet = new BranchSet( circuit, strongComponent );
            branchSet.translate( branchDragMatch == null ? dx : branchDragMatch.getVector() );
        }

        public void dropBranch( Branch branch ) {
            if( branch instanceof CircuitComponent ) {
                if( branchDragMatch != null ) {
                    getCircuit().collapseJunctions( branchDragMatch.getSource(), branchDragMatch.getTarget() );
                }
                branchDragMatch = null;
                draggingBranch = false;
                //todo, need bumpAway implementation
//        circuitGraphic.bumpAway( branchGraphic.getCircuitComponent() );
            }
            else {
                draggingBranch = false;
                if( startMatch != null ) {
                    getCircuit().collapseJunctions( startMatch.getSource(), startMatch.getTarget() );
                }
                if( endMatch != null ) {
                    getCircuit().collapseJunctions( endMatch.getSource(), endMatch.getTarget() );
                }
                //todo need bumpaway implementation
//            circuitGraphic.bumpAway( branch );
            }
            circuit.bumpAway( branch.getStartJunction() );
            circuit.bumpAway( branch.getEndJunction() );
        }

//        public Junction getStickyTarget( Junction junction, double x, double y ) {
//            double bestDist = Double.POSITIVE_INFINITY;
//            double STICKY_DIST = 2;
//            Junction best = null;
//
//            for( int i = 0; i < circuit.getJunctions().length; i++ ) {
//                //todo see circuitGraphic line 502 for handling strong components
//                Junction j = circuit.getJunctions()[i];
//                if( j != junction && !getCircuit().hasBranch( junction, j ) && !getCircuit().wouldConnectionCauseOverlappingBranches( junction, j ) )
//                {
//                    double dist = new Point2D.Double( x, y ).distance( j.getX(), j.getY() );
//                    if( dist < STICKY_DIST && dist < bestDist ) {
//                        best = j;
//                        bestDist = dist;
//                    }
//                }
//            }
//            return best;
//        }


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
        }
    }

    class JunctionInteractionModel {
        private Circuit.DragMatch junctionDragMatch;

        private CircuitComponent getSoleComponent( Junction j ) {
            if( circuit.getAdjacentBranches( j ).length == 1 && circuit.getAdjacentBranches( j )[0] instanceof CircuitComponent ) {
                return (CircuitComponent)circuit.getAdjacentBranches( j )[0];
            }
            else {
                return null;
            }
        }

        public void dragJunction( Junction junction, Point2D target ) {
            drag( junction, target );
        }

        private void drag( Junction junction, Point2D target ) {
            if( getSoleComponent( junction ) != null ) {
                rotateComponent( junction, target );
            }
            else {
                translateJunction( junction, target );
            }
        }

        private void translateJunction( Junction junction, Point2D target ) {
            Branch[] sc = circuit.getStrongConnections( junction );
            Junction[] j = Circuit.getJunctions( sc );
            ArrayList ju = new ArrayList( Arrays.asList( j ) );
            if( !ju.contains( junction ) ) {
                ju.add( junction );
            }

            Junction[] jx = (Junction[])ju.toArray( new Junction[0] );
            Vector2D dx = new Vector2D.Double( junction.getPosition(), target );
            junctionDragMatch = getCircuit().getBestDragMatch( jx, dx );
            if( junctionDragMatch != null ) {
                dx = junctionDragMatch.getVector();
            }

            BranchSet bs = new BranchSet( circuit, sc );
            bs.addJunction( junction );
            bs.translate( dx );

            Branch[] subgraph = circuit.getConnectedSubgraph( junction );
            model.layoutElectrons( subgraph );
        }

        private void rotateComponent( Junction junction, Point2D target ) {
            CircuitComponent branch = getSoleComponent( junction );
            Junction opposite = branch.opposite( junction );
            AbstractVector2D vec = new ImmutableVector2D.Double( opposite.getPosition(), target );
            vec = vec.getInstanceOfMagnitude( branch.getComponentLength() );
            Point2D dst = vec.getDestination( opposite.getPosition() );
            junction.setPosition( dst.getX(), dst.getY() );
        }

        public void dropJunction( Junction junction ) {
            if( junctionDragMatch != null ) {
                getCircuit().collapseJunctions( junction, junctionDragMatch.getTarget() );
            }
            junctionDragMatch = null;
            circuit.bumpAway( junction );
        }
    }

    private Circuit getCircuit() {
        return circuit;
    }

}
