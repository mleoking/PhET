/*Copyright, Sam Reid, 2003.*/
package edu.colorado.phet.cck.elements.kirkhoff;

import edu.colorado.phet.cck.elements.branch.Branch;
import edu.colorado.phet.cck.elements.branch.components.HasResistance;
import edu.colorado.phet.cck.elements.circuit.Circuit;
import edu.colorado.phet.cck.elements.circuit.JunctionGroup;
import edu.colorado.phet.cck.elements.junction.Junction;
import edu.colorado.phet.cck.graphtheory.DirectedDataEdge;
import edu.colorado.phet.cck.graphtheory.DirectedPathElement;
import edu.colorado.phet.cck.graphtheory.Graph;
import edu.colorado.phet.cck.graphtheory.Loop;

import javax.swing.*;
import java.util.ArrayList;

/**
 * User: Sam Reid
 * Date: Sep 3, 2003
 * Time: 1:10:06 AM
 * Copyright (c) Sep 3, 2003 by Sam Reid
 */
public class CircuitGraph {
    Circuit circuit;
    private Graph graph;
    Loop[] loops;
    private JunctionGroup[] junctionGroups;

    public CircuitGraph(Circuit circuit) {
        this.circuit = circuit;
        junctionGroups = circuit.getJunctionGroups();
        this.graph = toGraph(circuit);

//        this.loops = graph.getLoops();
        Loop[] myloops = graph.getLoops();

        ArrayList keepers = new ArrayList();
        for (int i = 0; i < myloops.length; i++) {
            Loop myloop = myloops[i];
            if (!loopContainsOpenSwitch(myloop)) {
                keepers.add(myloop);
            }
        }
        loops = (Loop[]) keepers.toArray(new Loop[0]);
//        System.out.println("loops.length = " + loops.length);
    }

    private boolean loopContainsOpenSwitch(Loop myloop) {
        for (int k = 0; k < myloop.numBranches(); k++) {
            DirectedPathElement elm = myloop.directedPathElementAt(k);
            Branch data = (Branch) elm.getEdge().getData();
            if (data instanceof HasResistance) {
                HasResistance hr = (HasResistance) data;
                if (Double.isInfinite(hr.getResistance()))
                    return true;
            }
        }
        return false;
    }

    public static boolean containsConnection(Junction[] j, Graph g) {
        for (int i = 0; i < j.length; i++) {
            Junction junction2 = j[i];
            if (g.containsVertex(junction2))
                return true;
        }
        return false;
    }

    public JunctionGroup getJunctionGroup(Junction junction) {
        for (int i = 0; i < junctionGroups.length; i++) {
            JunctionGroup junctionGroup = junctionGroups[i];
            if (junctionGroup.contains(junction))
                return junctionGroup;
        }
        return null;
    }

    private Graph toGraph(Circuit circuit) {
        Graph g = new Graph();
        for (int i = 0; i < this.junctionGroups.length; i++) {
            JunctionGroup junctionGroup = junctionGroups[i];
            g.addVertex(junctionGroup);
        }

        for (int i = 0; i < circuit.numBranches(); i++) {
            Branch branch = circuit.branchAt(i);
            JunctionGroup start = getJunctionGroup(branch.getStartJunction());
            JunctionGroup end = getJunctionGroup(branch.getEndJunction());
            /**Try throwing out the open switches.*/
//            boolean okayToAdd = true;
//            if (branch instanceof HasResistance) {
//                HasResistance hr = (HasResistance) branch;
//                if (Double.isInfinite(hr.getResistance())) {
//                    okayToAdd = false;
//                }
//            }
//            if (okayToAdd)
            g.addConnection(start, end, branch);
        }
        return g;
    }

    public Loop[] getLoops() {
        return loops;
    }

    public int numVertices() {
        return graph.numVertices();
    }

    public int numBranches() {
        return graph.numEdges();
    }

    public Branch branchAt(int i) {
        return (Branch) graph.edgeAt(i).getData();
    }

    public JunctionGroup junctionGroupAt(int i) {
        return (JunctionGroup) graph.vertexAt(i);
    }

    public boolean isLoopElement(Branch branch) {
        for (int i = 0; i < loops.length; i++) {
            Loop loop = loops[i];
            if (loop.containsEdgeData(branch))
                return true;
        }
        return false;
    }

    public Branch[] getConnectionsWithLoopElements(JunctionGroup junction) {
        DirectedDataEdge[] edges = graph.getEdges(junction);
        ArrayList all = new ArrayList();
        for (int i = 0; i < edges.length; i++) {
            DirectedDataEdge edge = edges[i];
            Branch branch = (Branch) edge.getData();
            if (isLoopElement(branch))
                all.add(branch);
        }
        return (Branch[]) all.toArray(new Branch[0]);
    }

    public int indexOfBranch(Branch branch) {
        return graph.indexOfEdgeForData(branch);
    }

    public int indexOf(Branch branch) {
        return graph.indexOfEdgeForData(branch);
    }

    public Circuit getCircuit() {
        return circuit;
    }

//    public void guessCurrentDirections() {
//        circuit.deleteCurrentDirectionGuesses();//clear the circuit currents.
//        if (loops.length == 0)
//            return;
//        debug("Cleared Circuit");
//        Branch seed = (Branch) loops[0].directedPathElementAt(0).getEdge().getData();
//        seed.setGuessedCurrentDirectionSameAsOrientation(Branch.SAME_AS_ORIENTATION);
//        ArrayList remainingLoops = new ArrayList();
//        remainingLoops.addAll(Arrays.asList(loops));
//        //assign to the first loop,
//        fillLoopCurrentGuesses(loops[0]);
//        debug("Inited first branch" + getLoopIndexString(loops[0]));
//        //see if any remaining loops connect to loops we've done.
//        ArrayList doneLoops = new ArrayList();
//        doneLoops.add(loops[0]);
//        remainingLoops.remove(loops[0]);
//        while (remainingLoops.size() > 0) {
//            checkAndFillPartials(doneLoops, remainingLoops);//keeps going 'till we are sure we have another degree of freedom.
//            if (remainingLoops.size() == 0)
//                break;
//            Loop freeChoice = (Loop) remainingLoops.get(0);
//            Branch init = (Branch) freeChoice.directedPathElementAt(0).getEdge().getData();
//            init.setGuessedCurrentDirectionSameAsOrientation(Branch.SAME_AS_ORIENTATION);
//            debug("Set free guess of branch " + init.getId());
//            //loop takes care of the best.
//        }
//    }

    public String getLoopIndexString(Loop loop) {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < loop.numBranches(); i++) {
            Branch b = (Branch) loop.directedPathElementAt(i).getEdge().getData();
            sb.append(b.getId() + ", ");
        }
        return sb.toString();
    }

    int NONE = 0;
    int PARTIAL = 1;
    int FULL = 2;

//    private void checkAndFillPartials(ArrayList doneLoops, ArrayList remainingLoops) {
//        for (int i = 0; i < remainingLoops.size(); i++) {
//            Loop rem = (Loop) remainingLoops.get(i);
//            int fillState = getFillState(rem);
//            if (fillState == FULL) {
//                debug("Loop full, removing: " + getLoopIndexString(rem));
//                doneLoops.add(rem);
//                remainingLoops.remove(rem);
//                i = -1;
//            }
//            if (fillState == PARTIAL) {
////            if (isPartiallyFilled(rem)) {
////                JOptionPane.showMessageDialog(null, "Found partially filled loop=loop[" + i + "]");
//                debug("Partial loop=" + getLoopIndexString(rem) + ", gonna fill.");
//                fillLoopCurrentGuesses(rem);
//                debug("Partial loop now filled: " + getLoopIndexString(rem));
//                doneLoops.add(rem);
//                remainingLoops.remove(rem);
//                i = -1;
//            }
//        }
//    }

//    private int getFillState(Loop loop) {
//        int numUnknown = 0;
//        int numSame = 0;
//        int numOpp = 0;
//        for (int i = 0; i < loop.numBranches(); i++) {
//            Branch b = (Branch) loop.directedPathElementAt(i).getEdge().getData();
//            if (b.isCurrentDirectionGuessed()) {
//                if (b.isCurrentDirectionSameAsOrientation()) {
//                    numSame++;
//                } else
//                    numOpp++;
//            } else
//                numUnknown++;
//        }
//        int numGuessed = numSame + numOpp;
//        if (numGuessed == loop.numBranches())
//            return FULL;
//        else if (numGuessed == 0)
//            return NONE;
//        else
//            return PARTIAL;
//    }

//    private void fillLoopCurrentGuesses(Loop loop) {
//        fillLoopCurrentGuessesForward(loop);
//        fillLoopCurrentGuessesBackward(loop);
//    }
//
//    private void fillLoopCurrentGuessesBackward(Loop loop) {
//        int startIndex = -1;
//        for (int i = loop.numBranches() - 1; i >= 0; i--) {
//            Branch b = (Branch) loop.directedPathElementAt(i).getEdge().getData();
//            if (b.isCurrentDirectionGuessed()) {
//                startIndex = i;
//                break;
//            }
//        }
//        if (startIndex == -1)
//            throw new RuntimeException("Couldn't find labeled branch.");
//        Branch lastBranch = (Branch) loop.directedPathElementAt(startIndex).getEdge().getData();
//        for (int k = startIndex - 1; k >= 0; k--) {
//            Branch nextBranch = (Branch) loop.directedPathElementAt(k).getEdge().getData();
//            //if they are the same orientation, give the same current guess,
//            if (nextBranch.getStartJunction().hasConnection(lastBranch.getEndJunction()) || nextBranch.getEndJunction().hasConnection(lastBranch.getStartJunction())) {
//                nextBranch.setGuessedCurrentDirectionSameAsOrientation(lastBranch.getGuessedCurrentDirSameAsOrientation());
//            } else {//must have been opposite current guesses.
//                nextBranch.setGuessedCurrentDirectionSameAsOrientation(lastBranch.getOppositeCurrentGuess());
//            }
//            lastBranch = nextBranch;
//        }
//    }
//
//    private void fillLoopCurrentGuessesForward(Loop loop) {
//        int startIndex = -1;
//        for (int i = 0; i < loop.numBranches(); i++) {
//            Branch b = (Branch) loop.directedPathElementAt(i).getEdge().getData();
//            if (b.isCurrentDirectionGuessed()) {
//                startIndex = i;
//                break;
//            }
//        }
//        if (startIndex == -1)
//            throw new RuntimeException("Couldn't find labeled branch.");
//        Branch lastBranch = (Branch) loop.directedPathElementAt(startIndex).getEdge().getData();
//        for (int k = startIndex + 1; k < loop.numBranches(); k++) {
//            Branch nextBranch = (Branch) loop.directedPathElementAt(k).getEdge().getData();
//            //if they are the same orientation, give the same current guess,
//            if (nextBranch.getStartJunction().hasConnection(lastBranch.getEndJunction()) || nextBranch.getEndJunction().hasConnection(lastBranch.getStartJunction())) {
//                nextBranch.setGuessedCurrentDirectionSameAsOrientation(lastBranch.getGuessedCurrentDirSameAsOrientation());
//            } else {//must have been opposite current guesses.
//                nextBranch.setGuessedCurrentDirectionSameAsOrientation(lastBranch.getOppositeCurrentGuess());
//            }
//            lastBranch = nextBranch;
//        }
//    }

    public void debug(String msg) {
        JOptionPane.showMessageDialog(null, msg);
    }
}
