// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.circuitconstructionkit.view.piccolo;

import edu.colorado.phet.circuitconstructionkit.model.BranchSet;
import edu.colorado.phet.circuitconstructionkit.model.CCKModel;
import edu.colorado.phet.circuitconstructionkit.model.Circuit;
import edu.colorado.phet.circuitconstructionkit.model.Junction;
import edu.colorado.phet.circuitconstructionkit.model.components.Branch;
import edu.colorado.phet.circuitconstructionkit.model.components.CircuitComponent;
import edu.colorado.phet.circuitconstructionkit.model.components.Wire;
import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.math.Vector2D;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * User: Sam Reid
 * Date: Sep 17, 2006
 * Time: 8:43:39 PM
 */

public class CircuitInteractionModel {
    private CCKModel model;
    private Circuit circuit;

    private JunctionInteractionModel junctionInteractionModel;
    private BranchInteractionModel branchInteractionModel;

    public CircuitInteractionModel(CCKModel model) {
        this.circuit = model.getCircuit();
        this.model = model;
        junctionInteractionModel = new JunctionInteractionModel();
        branchInteractionModel = new BranchInteractionModel();
    }

    public void dragJunction(Junction junction, Point2D target) {
        junctionInteractionModel.dragJunction(junction, target);
    }

    public void dropJunction(Junction junction) {
        junctionInteractionModel.dropJunction(junction);
    }

    public void translate(Wire wire, Point2D pt) {
        branchInteractionModel.translate(wire, pt);
    }

    public void dropBranch(Wire wire) {
        branchInteractionModel.dropBranch(wire);
    }

    public void translate(Branch branch, Point2D location) {
        branchInteractionModel.translate(branch, location);
    }

    public void dropBranch(Branch branch) {
        branchInteractionModel.dropBranch(branch);
    }

    class BranchInteractionModel {

        private ImmutableVector2D toStart;
        private ImmutableVector2D toEnd;

        private boolean draggingBranch = false;
        private Circuit.DragMatch branchDragMatch;

        private Circuit.DragMatch startMatch;
        private Circuit.DragMatch endMatch;

        public void translate(Branch branch, Point2D dragPt) {
            if (!draggingBranch) {
                draggingBranch = true;
                toStart = new ImmutableVector2D(dragPt, branch.getStartJunction().getPosition());
                toEnd = new ImmutableVector2D(dragPt, branch.getEndJunction().getPosition());
            } else {
                if (branch instanceof CircuitComponent) {
                    translateCircuitComponent((CircuitComponent) branch, dragPt);
                } else if (branch instanceof Wire) {//branch was just a wire
                    translateWire((Wire) branch, dragPt);
                } else {
                    throw new RuntimeException("Unknown wire type: " + branch.getClass());
                }
            }
        }

        private void translateWire(Wire wire, Point2D dragPt) {
            Point2D newStartPosition = toStart.getDestination(dragPt);
            Point2D newEndPosition = toEnd.getDestination(dragPt);
            Branch[] scStart = circuit.getStrongConnections(wire.getStartJunction());
            Branch[] scEnd = circuit.getStrongConnections(wire.getEndJunction());
            Vector2D startDX = new Vector2D(wire.getStartJunction().getPosition(), newStartPosition);
            Vector2D endDX = new Vector2D(wire.getEndJunction().getPosition(), newEndPosition);
            Junction[] startSources = getSources(scStart, wire.getStartJunction());
            Junction[] endSources = getSources(scEnd, wire.getEndJunction());
            //how about removing any junctions in start and end that share a branch?
            //Is this sufficient to keep from dropping wires directly on other wires?

            startMatch = getCircuit().getBestDragMatch(startSources, startDX);
            endMatch = getCircuit().getBestDragMatch(endSources, endDX);

            if (endMatch != null && startMatch != null && endMatch.getTarget() == startMatch.getTarget()) {
                endMatch = null;
            }
            if (endMatch != null && startMatch != null && wouldCauseOverlap(wire, startMatch, endMatch)) {
                endMatch = null;
            }

            if (startMatch != null && endMatch != null) {
                for (int i = 0; i < circuit.numBranches(); i++) {
                    Branch b = circuit.branchAt(i);
                    if (b.hasJunction(startMatch.getTarget()) && wire.hasJunction(endMatch.getTarget())) {
                        startMatch = null;
                        endMatch = null;
                        break;
                    }
                }
            }
            apply(scStart, startDX, wire.getStartJunction(), startMatch);
            apply(scEnd, endDX, wire.getEndJunction(), endMatch);
        }

        private boolean wouldCauseOverlap(Wire wire, Circuit.DragMatch startMatch, Circuit.DragMatch endMatch) {
            Junction[] neighbors = circuit.getNeighbors(startMatch.getTarget());
            ArrayList list = new ArrayList();
            list.addAll(Arrays.asList(neighbors));
            list.remove(wire.getStartJunction());
            list.remove(wire.getEndJunction());
            if (list.contains(endMatch.getTarget())) {
                return true;
            } else {
                return false;
            }
        }

        private void translateCircuitComponent(CircuitComponent cc, Point2D dragPt) {
            if (!draggingBranch) {
                draggingBranch = true;
                Point2D startJ = cc.getStartJunction().getPosition();
                toStart = new ImmutableVector2D(dragPt, startJ);
            }
            Point2D newStartPosition = toStart.getDestination(dragPt);
            Vector2D dx = new Vector2D(cc.getStartJunction().getPosition(), newStartPosition);
            Branch[] strongComponent = circuit.getStrongConnections(cc.getStartJunction());
            branchDragMatch = getCircuit().getBestDragMatch(strongComponent, dx);
            BranchSet branchSet = new BranchSet(circuit, strongComponent);
            branchSet.translate(branchDragMatch == null ? dx : branchDragMatch.getVector());
        }

        public void dropBranch(Branch branch) {
            if (branch instanceof CircuitComponent) {
                if (branchDragMatch != null) {
                    getCircuit().collapseJunctions(branchDragMatch.getSource(), branchDragMatch.getTarget());
                }
                branchDragMatch = null;
                draggingBranch = false;
                //todo, need bumpAway implementation
//        circuitGraphic.bumpAway( branchGraphic.getCircuitComponent() );
            } else {
                draggingBranch = false;
                if (startMatch != null) {
                    getCircuit().collapseJunctions(startMatch.getSource(), startMatch.getTarget());
                }
                if (endMatch != null) {
                    getCircuit().collapseJunctions(endMatch.getSource(), endMatch.getTarget());
                }
                //todo need bumpaway implementation
//            circuitGraphic.bumpAway( branch );
            }
            if (branch != null) {
                circuit.bumpAway(branch.getStartJunction());
                circuit.bumpAway(branch.getEndJunction());
            }
        }

        private Junction[] getSources(Branch[] sc, Junction j) {
            ArrayList list = new ArrayList(Arrays.asList(Circuit.getJunctions(sc)));
            if (!list.contains(j)) {
                list.add(j);
            }
            return (Junction[]) list.toArray(new Junction[0]);
        }

        private void apply(Branch[] sc, Vector2D dx, Junction junction, Circuit.DragMatch match) {
            if (match == null) {
                BranchSet bs = new BranchSet(circuit, sc);
                bs.addJunction(junction);
                bs.translate(dx);
            } else {
                BranchSet bs = new BranchSet(circuit, sc);
                ImmutableVector2D vec = match.getVector();
                bs.addJunction(junction);
                bs.translate(vec);
            }
        }
    }

    class JunctionInteractionModel {
        private Circuit.DragMatch junctionDragMatch;

        private CircuitComponent getSoleComponent(Junction j) {
            if (circuit.getAdjacentBranches(j).length == 1 && circuit.getAdjacentBranches(j)[0] instanceof CircuitComponent) {
                return (CircuitComponent) circuit.getAdjacentBranches(j)[0];
            } else {
                return null;
            }
        }

        public void dragJunction(Junction junction, Point2D target) {
            drag(junction, target);
        }

        private void drag(Junction junction, Point2D target) {
            if (getSoleComponent(junction) != null) {
                rotateComponent(junction, target);
            } else {
                translateJunction(junction, target);
            }
        }

        private void translateJunction(Junction junction, Point2D target) {
            Branch[] sc = circuit.getStrongConnections(junction);
            Junction[] j = Circuit.getJunctions(sc);
            ArrayList ju = new ArrayList(Arrays.asList(j));
            if (!ju.contains(junction)) {
                ju.add(junction);
            }

            Junction[] jx = (Junction[]) ju.toArray(new Junction[0]);
            Vector2D dx = new Vector2D(junction.getPosition(), target);
            junctionDragMatch = getCircuit().getBestDragMatch(jx, dx);
            if (junctionDragMatch != null) {
                dx = junctionDragMatch.getVector();
            }

            BranchSet bs = new BranchSet(circuit, sc);
            bs.addJunction(junction);
            bs.translate(dx);

            Branch[] subgraph = circuit.getConnectedSubgraph(junction);
            model.layoutElectrons(subgraph);
        }

        private void rotateComponent(Junction junction, Point2D target) {
            CircuitComponent branch = getSoleComponent(junction);
            Junction opposite = branch.opposite(junction);
            ImmutableVector2D vec = new ImmutableVector2D(opposite.getPosition(), target);
            vec = vec.getInstanceOfMagnitude(branch.getComponentLength());
            Point2D dst = vec.getDestination(opposite.getPosition());
            junction.setPosition(dst.getX(), dst.getY());
        }

        public void dropJunction(Junction junction) {
            if (junctionDragMatch != null) {
                getCircuit().collapseJunctions(junction, junctionDragMatch.getTarget());
            }
            junctionDragMatch = null;
            circuit.bumpAway(junction);
        }
    }

    private Circuit getCircuit() {
        return circuit;
    }

}
