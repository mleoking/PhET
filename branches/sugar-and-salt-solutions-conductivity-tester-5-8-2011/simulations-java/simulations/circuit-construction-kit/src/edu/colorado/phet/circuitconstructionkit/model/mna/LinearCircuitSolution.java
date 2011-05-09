// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.circuitconstructionkit.model.mna;

import java.util.HashMap;
import java.util.Set;

/**
 * This class represents a sparse solution containing only the solved unknowns in MNA.
 */
public class LinearCircuitSolution implements LinearCircuitSolver.ISolution {
    HashMap<Integer, Double> nodeVoltages = new HashMap<Integer, Double>();
    HashMap<LinearCircuitSolver.Element, Double> branchCurrents = new HashMap<LinearCircuitSolver.Element, Double>();

    LinearCircuitSolution(HashMap<Integer, Double> nodeVoltages, HashMap<LinearCircuitSolver.Element, Double> branchCurrents) {
        this.nodeVoltages = nodeVoltages;
        this.branchCurrents = branchCurrents;
    }

    public double getNodeVoltage(int node) {
        return nodeVoltages.get(node);
    }

    public boolean approxEquals(LinearCircuitSolver.ISolution s) {
        return approxEquals(s, 1E-6);
    }

    boolean approxEquals(double a, double b, double delta) {
        return Math.abs(a - b) < delta;
    }

    public Set<Integer> getNodes() {
        return nodeVoltages.keySet();
    }

    public Set<LinearCircuitSolver.Element> getBranches() {
        return branchCurrents.keySet();
    }

    boolean approxEquals(LinearCircuitSolver.ISolution solution, double delta) {
        if (!getNodes().equals(solution.getNodes()) || !getBranches().equals(solution.getBranches())) {
            return false;
        } else {
            boolean sameVoltages = true;
            for (Integer key : nodeVoltages.keySet()) {
                if (!approxEquals(nodeVoltages.get(key), solution.getNodeVoltage(key), delta)) {
                    sameVoltages = false;
                }
            }
            boolean sameCurrents = true;
            for (LinearCircuitSolver.Element key : branchCurrents.keySet()) {
                if (Math.abs(branchCurrents.get(key) - solution.getCurrent(key)) > delta) {
                    sameCurrents = false;
                }
            }

            return sameVoltages && sameCurrents;
        }
    }

    double getVoltage(LinearCircuitSolver.Element e) {
        return nodeVoltages.get(e.node1) - nodeVoltages.get(e.node0);
    }

    public double getCurrent(LinearCircuitSolver.Element e) {
        //if it was a battery or resistor (of R=0), look up the answer
        if (branchCurrents.containsKey(e)) {
            return branchCurrents.get(e);
        }
        //else compute based on V=IR
        else {
            if (e instanceof LinearCircuitSolver.Resistor) {
                LinearCircuitSolver.Resistor r = (LinearCircuitSolver.Resistor) e;
                return -getVoltage(r) / r.resistance;
            } else {
                throw new RuntimeException("Solution does not contain current for element: " + e);
            }
        }

    }

    public String toString() {
        return "Solution{" +
                "nodeVoltages=" + nodeVoltages +
                ", branchCurrents=" + branchCurrents +
                '}';
    }

    public double distance(LinearCircuitSolution s) {
        double distanceVoltage = 0;
        for (Integer key : nodeVoltages.keySet()) {
            distanceVoltage = distanceVoltage + Math.abs(getNodeVoltage(key) - s.getNodeVoltage(key));
        }
        double averageVoltDist = distanceVoltage / nodeVoltages.size();
        if (nodeVoltages.size() == 0) {
            averageVoltDist = 0.0;
        }
        return averageVoltDist + Math.abs(getAverageCurrentMags() - s.getAverageCurrentMags());
    }

    private double getAverageCurrentMags() {
        double c = 0;
        for (Double cval : branchCurrents.values()) {
            c = c + Math.abs(cval);
        }
        return branchCurrents.size() > 0 ? c / branchCurrents.size() : 0.0;
    }
}
