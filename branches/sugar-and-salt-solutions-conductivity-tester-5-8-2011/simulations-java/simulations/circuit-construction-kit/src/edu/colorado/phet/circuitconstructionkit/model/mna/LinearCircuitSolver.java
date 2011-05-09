// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.circuitconstructionkit.model.mna;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Conventions:
 * Current is 'conventional current': in a battery positive current flows from the higher (+) potential
 * When traversing a battery with voltage V, the potential increases by +V from node0 to node1 of the battery.
 * We are using the same conventions as the "flow of positive charge" in http://en.wikipedia.org/wiki/Electric_current
 */
public interface LinearCircuitSolver {

    ISolution solve(Circuit circuit);

    public static interface ISolution {
        double getNodeVoltage(int node);

        double getCurrent(Element element);

        boolean approxEquals(ISolution solution);//todo: convert to distance() computation instead of having built-in threshold for equality

        Set<Integer> getNodes();

        Set<Element> getBranches();
    }

    public static class Util {
        public static String mkString(List list, String separator) {
            String out = "";
            for (int i = 0; i < list.size(); i++) {
                out += list.get(i);
                if (i < list.size() - 1) {
                    out += separator;
                }
            }
            return out;
        }
    }

    public static class Circuit {
        List<Battery> batteries;
        List<Resistor> resistors;
        List<CurrentSource> currentSources;

        Circuit(List<Battery> batteries, List<Resistor> resistors) {
            this(batteries, resistors, new ArrayList<CurrentSource>());
        }

        public List<Battery> getBatteries() {
            return batteries;
        }

        public Battery getBattery(int i) {
            return batteries.get(i);
        }

        Circuit(List<Battery> batteries, List<Resistor> resistors, List<CurrentSource> currentSources) {
            this.batteries = batteries;
            this.resistors = resistors;
            this.currentSources = currentSources;
        }

        public String toString() {
            return "Circuit{" + "batteries=" + batteries + ", resistors=" + resistors + ", currentSources=" + currentSources + '}';
        }

        List<Element> getElements() {
            List<Element> list = new ArrayList<Element>();
            list.addAll(batteries);
            list.addAll(resistors);
            list.addAll(currentSources);
            return list;
        }

        int getNodeCount() {
            return getNodeSet().size();
        }

        int getCurrentCount() {
            int zeroResistors = 0;
            for (Resistor resistor : resistors) {
                if (resistor.resistance == 0) {
                    zeroResistors++;
                }
            }
            return batteries.size() + zeroResistors;
        }

        HashSet<Integer> getNodeSet() {
            HashSet<Integer> set = new HashSet<Integer>();
            for (Element element : getElements()) {
                set.add(element.node0);
                set.add(element.node1);
            }
            return set;
        }

        public double sumConductances(int nodeIndex) {
            double sum = 0.0;
            for (Resistor resistor : resistors) {
                if (resistor.containsNode(nodeIndex))
                    sum += resistor.conductance;
            }
            return sum;
        }

        public double getConductance(int node1, int node2) {
            //conductances sum:
            double sum = 0.0;
            for (Resistor resistor : resistors) {
                if (resistor.containsNode(node1) && resistor.containsNode(node2))
                    sum += resistor.conductance;
            }
            return sum;
        }

        public double sumIncomingCurrents(int nodeIndex) {
            double sum = 0.0;
            for (int i = 0; i < currentSources.size(); i++) {
                CurrentSource cs = currentSources.get(i);
                if (cs.node1 == nodeIndex) sum += cs.current;
            }
            return sum;
        }
    }


    /**
     * Battery model for the MNA circuit, with the convention that as you traverse from node0 to node1, the voltage increases by (voltage).
     */
    public static class Battery extends Element {
        double voltage;

        Battery(int node0, int node1, double voltage) {
            super(node0, node1);
            this.voltage = voltage;
        }

        public String toString() {
            return "Battery{" + "[" + node0 + "->" + node1 + "], " + "v=" + voltage + '}';
        }
    }

    public static class Resistor extends Element {
        final double resistance;
        final double conductance;

        Resistor(int node0, int node1, double resistance) {
            super(node0, node1);
            this.resistance = resistance;
            this.conductance = 1.0 / resistance;//todo: not all solvers use this
        }

        public String toString() {
            return "Resistor{" + "[" + node0 + "->" + node1 + "], " + "r=" + resistance + '}';
        }
    }

    public static class CurrentSource extends Element {
        double current;

        public CurrentSource(int node0, int node1, double current) {
            super(node0, node1);
            this.current = current;
        }

        public String toString() {
            return "CurrentSource{" + "current=" + current + '}';
        }
    }

    /**
     * This class represents an Element in a circuit, such as a Battery, Resistor, Capacitor, etc.
     * Comparisons must be made based on the identity of the object, not based on the content of the object, since, e.g.,
     * two identical resistors may connect the same nodes, and they should not be treated as the same resistor.
     */
    public static abstract class Element {
        public final int node0;

        public final int node1;

        protected Element(int node0, int node1) {
            this.node0 = node0;
            this.node1 = node1;
        }

        boolean containsNode(int n) {
            return n == node0 || n == node1;
        }

        public int getNode0() {
            return node0;
        }

        public int getNode1() {
            return node1;
        }

        int getOpposite(int node) {
            if (node == node0) {
                return node1;
            } else if (node == node1) {
                return node0;
            } else {
                throw new RuntimeException("node not found");
            }
        }

        public String toString() {
            return "Element{" + "node0=" + node0 + ", node1=" + node1 + '}';
        }
    }
}
