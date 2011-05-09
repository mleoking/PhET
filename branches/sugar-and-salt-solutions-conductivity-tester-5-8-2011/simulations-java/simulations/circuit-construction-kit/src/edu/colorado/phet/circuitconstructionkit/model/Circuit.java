// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.circuitconstructionkit.model;

import edu.colorado.phet.circuitconstructionkit.model.analysis.KirkhoffSolver;
import edu.colorado.phet.circuitconstructionkit.model.analysis.Path;
import edu.colorado.phet.circuitconstructionkit.model.components.Branch;
import edu.colorado.phet.circuitconstructionkit.model.components.CircuitComponent;
import edu.colorado.phet.circuitconstructionkit.model.components.Switch;
import edu.colorado.phet.circuitconstructionkit.model.components.Wire;
import edu.colorado.phet.circuitconstructionkit.model.mna.MNAAdapter;
import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.math.Vector2D;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;

import java.awt.*;
import java.awt.geom.Area;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * This is the main class for the CCK model, providing a representation of all the branches and junctions, and a way of updating the physics.
 *
 * @author Sam Reid
 */
public class Circuit {
    private ArrayList<Branch> branches = new ArrayList<Branch>();
    private ArrayList<Junction> junctions = new ArrayList<Junction>();
    private ArrayList<CircuitListener> listeners = new ArrayList<CircuitListener>();
    private CircuitChangeListener circuitChangeListener;
    private MNAAdapter.CircuitResult solution;//solution from last update, used to look up dynamic circuit properties.

    public Circuit() {
        this(new CompositeCircuitChangeListener());
    }

    public Circuit(CircuitChangeListener circuitChangeListener) {
        this.circuitChangeListener = circuitChangeListener;
    }

    public void addCircuitListener(CircuitListener listener) {
        listeners.add(listener);
    }

    public void removeCircuitListener(CircuitListener listener) {
        listeners.remove(listener);
    }

    public String toString() {
        return "Junctions=" + junctions + ", Branches=" + branches;
    }

    public void addJunction(Junction junction) {
        if (!junctions.contains(junction)) {
            junctions.add(junction);
            fireJunctionAdded(junction);
        }
    }

    private void fireJunctionAdded(Junction junction) {
        for (int i = 0; i < listeners.size(); i++) {//using java 1.5 iterator causes concurrentmodification exception here, since some junnction added listeners add listeners to this list
            listeners.get(i).junctionAdded(junction);
        }
    }

    public void removeJunction(Junction junction) {
        junctions.remove(junction);
        junction.delete();
        fireJunctionRemoved(junction);
    }

    public Branch[] getAdjacentBranches(Junction junction) {
        ArrayList<Branch> out = new ArrayList<Branch>();
        for (Branch branch : branches) {
            if (branch.hasJunction(junction)) {
                out.add(branch);
            }
        }
        return out.toArray(new Branch[out.size()]);
    }

    public void updateNeighbors(Junction junction) {
        for (Branch branch : branches) {
            if (branch.hasJunction(junction)) {
                branch.notifyObservers();
            }
        }
    }

    public void updateAll() {
        for (Branch branch : branches) {
            branch.notifyObservers();
        }
        for (Junction junction : junctions) {
            junction.notifyObservers();
        }
    }

    public int numJunctions() {
        return junctions.size();
    }

    public Junction junctionAt(int i) {
        return junctions.get(i);
    }

    public boolean hasBranch(Junction a, Junction b) {
        for (Branch branch : branches) {
            if (branch.hasJunction(a) && branch.hasJunction(b)) {
                return true;
            }
        }
        return false;
    }

    public Junction[] getNeighbors(Junction a) {
        ArrayList<Junction> n = new ArrayList<Junction>();
        for (Branch branch : branches) {
            if (branch.hasJunction(a)) {
                n.add(branch.opposite(a));
            }
        }
        return n.toArray(new Junction[n.size()]);
    }

    public void replaceJunction(Junction old, Junction newJunction) {
        for (Branch branch : branches) {
            if (branch.getStartJunction() == old) {
                branch.setStartJunction(newJunction);
            }
            if (branch.getEndJunction() == old) {
                branch.setEndJunction(newJunction);
            }
        }
    }

    private void fireJunctionRemoved(Junction junction) {
        for (CircuitListener circuitListener : listeners) {
            circuitListener.junctionRemoved(junction);
        }
    }

    public void addBranch(Branch component) {
        if (component == null) {
            throw new RuntimeException("Null component.");
        }

        branches.add(component);
        fireBranchAdded(component);

        addJunction(component.getStartJunction());
        addJunction(component.getEndJunction());
        component.addObserver(editingObserver);
        circuitChangeListener.circuitChanged();
    }

    EditingObserver editingObserver = new EditingObserver();

    public int numEditable() {
        int count = 0;
        for (Branch branch : branches) {
            if (branch.isEditable()) {
                count++;
            }
        }
        return count;
    }

    public Branch[] getNeighbors(Branch branch) {
        Branch[] n0 = getAdjacentBranches(branch.getStartJunction());
        Branch[] n1 = getAdjacentBranches(branch.getEndJunction());
        ArrayList<Branch> n = new ArrayList<Branch>();
        for (Branch aN0 : n0) {
            if (aN0 != branch) {
                n.add(aN0);
            }
        }
        for (Branch aN1 : n1) {
            if (aN1 != branch) {
                n.add(aN1);
            }
        }

        return n.toArray(new Branch[n.size()]);
    }

    public void setSolution(MNAAdapter.CircuitResult solution) {
        this.solution = solution;
    }

    public boolean hasProblematicConfiguration() {
        KirkhoffSolver.MatrixTable table = new KirkhoffSolver.MatrixTable(this);
        Path[] loops = table.getLoops();
        for (Path loop : loops) {
            if (isProblematicLoop(loop)) return true;
        }
        return false;
    }

    private boolean isProblematicLoop(Path loop) {
        if (loop.containsCapacitor() && loop.sumResistance() < 1) return true;
        else return false;
    }

    class EditingObserver implements SimpleObserver {
        public void update() {
            notifyEditingChanged();
        }
    }

    private void notifyEditingChanged() {
        for (CircuitListener circuitListener : listeners) {
            circuitListener.editingChanged();
        }
    }

    public Junction[] split(Junction junction) {
        Branch[] adjacentBranches = getAdjacentBranches(junction);
        Junction[] newJunctions = new Junction[adjacentBranches.length];
        for (int i = 0; i < adjacentBranches.length; i++) {
            Branch branch = adjacentBranches[i];
            Junction opposite = branch.opposite(junction);
            ImmutableVector2D vec = new Vector2D(opposite.getPosition(), junction.getPosition());
            double curLength = vec.getMagnitude();
            double newLength = Math.abs(curLength - CCKModel.JUNCTION_RADIUS * 1.5);
            vec = vec.getInstanceOfMagnitude(newLength);
            Point2D desiredDst = vec.getDestination(opposite.getPosition());
            Point2D destination = desiredDst;
            if (branch instanceof CircuitComponent) {
                destination = junction.getPosition();
            }

            Junction newJunction = new Junction(destination.getX(), destination.getY());
            branch.replaceJunction(junction, newJunction);
            addJunction(newJunction);
            newJunctions[i] = newJunction;

            if (branch instanceof CircuitComponent) {
                ImmutableVector2D tx = new ImmutableVector2D(junction.getPosition(), desiredDst);
                Branch[] stronglyConnected = getStrongConnections(newJunction);
                BranchSet bs = new BranchSet(this, stronglyConnected);
                bs.translate(tx);
            } else {
                updateNeighbors(newJunction);
            }
        }
        removeJunction(junction);
        fireJunctionsMoved();
        circuitChangeListener.circuitChanged();
        fireJunctionsSplit(junction, newJunctions);
        return newJunctions;
    }

    private void fireJunctionsSplit(Junction junction, Junction[] newJunctions) {
        for (CircuitListener circuitListener : listeners) {
            circuitListener.junctionsSplit(junction, newJunctions);
        }
    }

    public Branch[] getStrongConnections(Junction junction) {
        ArrayList<Branch> visited = new ArrayList<Branch>();
        getStrongConnections(visited, junction);
        return visited.toArray(new Branch[visited.size()]);
    }

    public Branch[] getStrongConnections(Branch wrongDir, Junction junction) {
        ArrayList<Branch> visited = new ArrayList<Branch>();
        if (wrongDir != null) {
            visited.add(wrongDir);
        }
        getStrongConnections(visited, junction);
        if (wrongDir != null) {
            visited.remove(wrongDir);
        }
        return visited.toArray(new Branch[visited.size()]);
    }

    private void getStrongConnections(ArrayList<Branch> visited, Junction junction) {
        Branch[] out = getAdjacentBranches(junction);
        for (Branch branch : out) {
            Junction opposite = branch.opposite(junction);
            if (!visited.contains(branch)) {
                if (branch instanceof CircuitComponent) {
                    visited.add(branch);
                    getStrongConnections(visited, opposite);
                }//Wires end the connectivity.
            }
        }
    }

    public Branch[] getConnectedSubgraph(Junction junction) {
        ArrayList<Branch> visited = new ArrayList<Branch>();
        getConnectedSubgraph(visited, junction);
        return visited.toArray(new Branch[visited.size()]);
    }

    private void getConnectedSubgraph(ArrayList<Branch> visited, Junction junction) {
        Branch[] adj = getAdjacentBranches(junction);
        for (Branch branch : adj) {
            if (branch instanceof Switch && !((Switch) branch).isClosed()) {
                // Skip this one.
            } else {
                Junction opposite = branch.opposite(junction);
                if (!visited.contains(branch)) {
                    visited.add(branch);
                    getConnectedSubgraph(visited, opposite);
                }
            }
        }
    }

    public void fireJunctionsMoved() {
        for (CircuitListener circuitListener : listeners) {
            circuitListener.junctionsMoved();
        }
    }

    public int numBranches() {
        return branches.size();
    }

    public int indexOf(Branch branch) {
        return branches.indexOf(branch);
    }

    public int indexOf(Junction junction) {
        return junctions.indexOf(junction);
    }

    public Branch branchAt(int i) {
        return branches.get(i);
    }

    public void removeBranch(Branch branch) {
        branch.removeObserver(editingObserver);
        branches.remove(branch);
        branch.delete();
        fireBranchRemoved(branch);
        fireKirkhoffChanged();
        removeIfOrphaned(branch.getStartJunction());
        removeIfOrphaned(branch.getEndJunction());
    }

    private void removeIfOrphaned(Junction junction) {
        if (getAdjacentBranches(junction).length == 0) {
            removeJunction(junction);
        }
    }

    private void fireBranchAdded(Branch branch) {
        for (CircuitListener circuitListener : listeners) {
            circuitListener.branchAdded(branch);
        }
    }

    private void fireBranchRemoved(Branch branch) {
        for (CircuitListener circuitListener : listeners) {
            circuitListener.branchRemoved(branch);
        }
    }

    public void fireKirkhoffChanged() {
        circuitChangeListener.circuitChanged();
    }

    public Branch[] getBranches() {
        return branches.toArray(new Branch[branches.size()]);
    }

    private void translate(Junction[] j, ImmutableVector2D vec) {
        for (Junction junction : j) {
            junction.translate(vec.getX(), vec.getY());
        }
    }

    public void translate(Branch[] branchs, ImmutableVector2D vec) {
        Junction[] j = getJunctions(branchs);
        translate(j, vec);
        for (Branch b : branchs) {
            b.notifyObservers();
        }
    }

    public Junction[] getJunctions() {
        return junctions.toArray(new Junction[junctions.size()]);
    }

    public static Junction[] getJunctions(Branch[] branchs) {
        ArrayList<Junction> list = new ArrayList<Junction>();
        for (Branch branch : branchs) {
            if (!list.contains(branch.getStartJunction())) {
                list.add(branch.getStartJunction());
            }
            if (!list.contains(branch.getEndJunction())) {
                list.add(branch.getEndJunction());
            }
        }
        return list.toArray(new Junction[list.size()]);
    }

    public void fireBranchesMoved(Branch[] moved) {
        for (CircuitListener listener : listeners) {
            listener.branchesMoved(moved);
        }
    }

    public double getVoltage(Shape leftTip, Shape rightTip) {
        Area tipIntersection = new Area(leftTip);
        tipIntersection.intersect(new Area(rightTip));
        if (!tipIntersection.isEmpty()) {
            return 0;
        } else {
            Connection red = getConnection(leftTip);
            Connection black = getConnection(rightTip);

            if (red == null || black == null) {
                return Double.NaN;
            } else {
                return getVoltage(red, black);//dfs from one branch to the other, counting the voltage drop.
            }
        }
    }

    public double getVoltage(Connection a, Connection b) {
        if (a.equals(b) || !getSameComponent(a.getJunction(), b.getJunction())) {
            return 0;
        } else {
            double va = a.getVoltageAddon();
            double vb = -b.getVoltageAddon();//this has to be negative, because on the path VA->A->B->VB, the the VB computation is VB to B.
            //used for displaying values e.g. in voltmeter and charts, so use average node voltages instead of instantaneous, see #2270 
            double junctionAnswer = solution.getAverageNodeVoltage(indexOf(b.getJunction())) - solution.getAverageNodeVoltage(indexOf(a.getJunction()));
            return junctionAnswer + va + vb;
        }
    }

    private boolean getSameComponent(Junction a, Junction b) {
        Branch[] x = getConnectedSubgraph(a);//todo: this logic is duplicated in mna
        for (Branch branch : x) {
            if (branch.hasJunction(b)) {
                return true;
            }
        }
        return false;
    }

    public void setSelection(Branch branch) {
        clearSelection();
        branch.setSelected(true);
        notifySelectionChanged();
    }

    public void setSelection(Junction junction) {
        clearSelection();
        junction.setSelected(true);
        notifySelectionChanged();
    }

    private void notifySelectionChanged() {
        for (CircuitListener circuitListener : listeners) {
            circuitListener.selectionChanged();
        }
    }

    public void clearSelection() {
        for (Branch branch : branches) {
            branch.setSelected(false);
        }
        for (Junction junction : junctions) {
            junction.setSelected(false);
        }
        notifySelectionChanged();
    }

    public Branch[] getSelectedBranches() {
        ArrayList<Branch> sel = new ArrayList<Branch>();
        for (Branch branch : branches) {
            if (branch.isSelected()) {
                sel.add(branch);
            }
        }
        return sel.toArray(new Branch[sel.size()]);
    }

    public Junction[] getSelectedJunctions() {
        ArrayList<Junction> sel = new ArrayList<Junction>();
        for (Junction branch : junctions) {
            if (branch.isSelected()) {
                sel.add(branch);
            }
        }
        return sel.toArray(new Junction[sel.size()]);
    }

    public void selectAll() {
        for (Branch branch : branches) {
            branch.setSelected(true);
        }
        for (Junction junction : junctions) {
            junction.setSelected(true);
        }
    }

    public void fireJunctionsCollapsed(Junction j1, Junction j2, Junction replacement) {
        for (CircuitListener circuitListener : listeners) {
            circuitListener.junctionsConnected(j1, j2, replacement);
        }
    }

    public boolean isDynamic() {
        for (int i = 0; i < numBranches(); i++) {
            if (branchAt(i) instanceof DynamicBranch) {
                return true;
            }
        }
        return false;
    }

    public void stepInTime(double dt) {
        for (int i = 0; i < numBranches(); i++) {
            if (branchAt(i) instanceof DynamicBranch) {
                DynamicBranch b = (DynamicBranch) branchAt(i);
                b.stepInTime(dt);
            }
        }
    }

    public void resetDynamics() {
        for (int i = 0; i < numBranches(); i++) {
            if (branchAt(i) instanceof DynamicBranch) {
                DynamicBranch b = (DynamicBranch) branchAt(i);
                b.resetDynamics();
            }
        }
    }

    public void setTime(double time) {
        for (int i = 0; i < numBranches(); i++) {
            if (branchAt(i) instanceof DynamicBranch) {
                DynamicBranch b = (DynamicBranch) branchAt(i);
                b.setTime(time);
            }
        }
    }

    public void setState(Circuit newCircuit) {
        clear();
        for (int i = 0; i < newCircuit.numJunctions(); i++) {
            addJunction(newCircuit.junctionAt(i));
        }
        for (int i = 0; i < newCircuit.numBranches(); i++) {
            addBranch(newCircuit.branchAt(i));
        }
    }

    public void clear() {
        while (numBranches() > 0) {
            Branch br = branchAt(0);
            removeBranch(br);
        }
        while (numJunctions() > 0) {
            removeJunction(junctionAt(0));
        }
    }

    public boolean wouldConnectionCauseOverlappingBranches(Junction a, Junction b) {
        Junction[] neighborsOfA = getNeighbors(a);
        Junction[] neighborsOfB = getNeighbors(b);
        for (Junction na : neighborsOfA) {
            for (Junction nb : neighborsOfB) {
                if (na == nb) {
                    return true;
                }
            }
        }
        return false;
    }


    public void collapseJunctions(Junction j1, Junction j2) {
        if (!j1.getPosition().equals(j2.getPosition())) {
            throw new RuntimeException("Juncitons Not at same coordinates.");
        }
        removeJunction(j1);
        removeJunction(j2);
        Junction replacement = new Junction(j1.getX(), j1.getY());
        addJunction(replacement);
        replaceJunction(j1, replacement);
        replaceJunction(j2, replacement);

        fireKirkhoffChanged();
        fireJunctionsCollapsed(j1, j2, replacement);
    }

    public DragMatch getBestDragMatch(Branch[] sc, Vector2D dx) {
        return getBestDragMatch(Circuit.getJunctions(sc), dx);
    }

    public void removedUnusedJunctions(Junction st) {
        Branch[] out = getAdjacentBranches(st);
        if (out.length == 0) {
            removeJunction(st);
        }
    }

    public void deleteSelectedBranches() {
        for (int i = 0; i < branches.size(); i++) {
            Branch branch = branches.get(i);
            if (branch.isSelected()) {
                removeBranch(branch);
                i--;
            }
        }
    }

    public void desolderSelectedJunctions() {
        ArrayList<Junction> alreadySplit = new ArrayList<Junction>();
        for (int i = 0; i < junctions.size(); i++) {
            Junction junction = junctions.get(i);
            if (!alreadySplit.contains(junction) && junction.isSelected() && getNeighbors(junction).length > 1) {
                split(junction);
                alreadySplit.add(junction);
                i = -1;
            }
        }
    }

    public void setAllComponentsEditing(boolean editing) {
        for (Branch branch : branches) {
            branch.setEditing(editing);
        }
    }

    public int getNumEditing() {
        int count = 0;
        for (Branch branch : branches) {
            if (branch.isEditing()) {
                count++;
            }
        }
        return count;
    }

    public static class DragMatch {
        private Junction source;
        private Junction target;

        public DragMatch(Junction source, Junction target) {
            this.source = source;
            this.target = target;
        }

        public Junction getSource() {
            return source;
        }

        public Junction getTarget() {
            return target;
        }

        public double getDistance() {
            return source.getDistance(target);
        }

        public String toString() {
            return "match, source=" + source + ", dest=" + target + ", dist=" + getDistance();
        }

        public Vector2D getVector() {
            return new Vector2D(source.getPosition(), target.getPosition());
        }
    }

    public DragMatch getBestDragMatch(Junction[] draggedJunctions, Vector2D dx) {
        Junction[] all = getJunctions();
        ArrayList<Junction> potentialMatches = new ArrayList<Junction>();
        potentialMatches.addAll(Arrays.asList(all));
        potentialMatches.removeAll(Arrays.asList(draggedJunctions));
        //now we have all the junctions that are moving,
        //and all the junctions that aren't moving, so we can look for a best match.
        Junction[] remaining = potentialMatches.toArray(new Junction[potentialMatches.size()]);
        DragMatch best = null;
        for (Junction draggedJunction : draggedJunctions) {
            Point2D loc = dx.getDestination(draggedJunction.getPosition());
            Junction bestForJunction = getBestDragMatch(draggedJunction, loc, remaining);
            if (bestForJunction != null) {
                DragMatch dm = new DragMatch(draggedJunction, bestForJunction);
                if (best == null || dm.getDistance() < best.getDistance()) {
                    best = dm;
                }
            }
        }
        return best;
    }

    public Junction getBestDragMatch(Junction dragging, Point2D loc, Junction[] targets) {
        Branch[] strong = getStrongConnections(dragging);
        Junction closestJunction = null;
        double closestValue = Double.POSITIVE_INFINITY;
        for (Junction target : targets) {
            double dist = loc.distance(target.getPosition());
            if (target != dragging && !hasBranch(dragging, target) && !wouldConnectionCauseOverlappingBranches(dragging, target)) {
                if (closestJunction == null || dist < closestValue) {
                    boolean legal = !contains(strong, target);
                    double STICKY_THRESHOLD = 1;
                    if (dist <= STICKY_THRESHOLD && legal) {
                        closestValue = dist;
                        closestJunction = target;
                    }
                }
            }
        }
        return closestJunction;
    }

    private boolean contains(Branch[] strong, Junction j) {
        for (Branch branch : strong) {
            if (branch.hasJunction(j)) {
                return true;
            }
        }
        return false;
    }

    public Branch getBranch(Point2D pt) {
        return detectBranch(new Rectangle2D.Double(pt.getX(), pt.getY(), 0.001, 0.001));
    }

    public Connection getConnection(Shape tipShape) {
        Branch branch = detectBranch(tipShape);
        Junction junction = detectJunction(tipShape);
        Connection result = null;
        if (junction != null) {
            result = new Connection.JunctionConnection(junction);
        } else if (branch != null) {
            //could choose the closest junction
            //but we want a potentiometer.
            Point2D tipCenterModel = new Point2D.Double(tipShape.getBounds2D().getCenterX(), tipShape.getBounds2D().getCenterY());
            Point2D.Double branchStartModel = branch.getStartJunction().getPosition();
            Vector2D vec = new Vector2D(branchStartModel, tipCenterModel);
            double dist = vec.getMagnitude();
            result = new Connection.BranchConnection(branch, dist);
        }
        return result;
    }

    private Branch detectBranch(Shape tipShape) {
        Wire[] wires = getWires();
        Wire connection = null;
        for (Wire wire : wires) {
            Shape wireShape = wire.getShape();
            Area area = new Area(tipShape);
            area.intersect(new Area(wireShape));
            if (!area.isEmpty()) {
                connection = wire;
            }
        }
        return connection;
    }

    private Wire[] getWires() {
        ArrayList<Wire> list = new ArrayList<Wire>();
        for (Branch branch : branches) {
            if (branch instanceof Wire) {
                list.add((Wire) branch);
            }
        }
        return list.toArray(new Wire[list.size()]);
    }

    private Junction detectJunction(Shape tipShape) {
        Junction[] junctions = getJunctions();
        Junction detectedJunction = null;
        for (Junction junction : junctions) {
            Area area = new Area(junction.getShape());
            area.intersect(new Area(tipShape));
            if (!area.isEmpty()) {
                detectedJunction = junction;
            }
        }
        return detectedJunction;
    }

    public void bumpAway(Junction junction) {
        for (int i = 0; i < 2; i++) {
            bumpOnce(junction);
        }
    }

    public void bumpOnce(Junction junction) {
        Branch[] branches = getBranches();
        Branch[] strongConnections = getStrongConnections(junction);
        for (Branch branch : branches) {
            if (!branch.hasJunction(junction)) {
                if (branch.getShape().intersects(junction.getShape().getBounds2D())) {
                    ImmutableVector2D vec = branch.getDirectionVector();
                    vec = vec.getNormalVector();
                    vec = vec.getNormalizedInstance().getScaledInstance(junction.getShape().getBounds2D().getWidth());
                    BranchSet bs = new BranchSet(this, strongConnections);
                    bs.addJunction(junction);
                    bs.translate(vec);
                    break;
                }
            }
        }
    }

}