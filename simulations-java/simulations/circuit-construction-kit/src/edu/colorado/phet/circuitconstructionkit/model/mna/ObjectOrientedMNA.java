// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.circuitconstructionkit.model.mna;

import Jama.Matrix;

import java.util.*;

/**
 * This implementation of the LinearCircuitSolver is based on an object-oriented design that is easy to understand,
 * maintain and debug, but which performs slowly at runtime.
 *
 * @author Sam Reid
 */
public class ObjectOrientedMNA implements LinearCircuitSolver {
    public ISolution solve(Circuit circuit) {
        return new OOCircuit(circuit.batteries, circuit.resistors, circuit.currentSources).solve();
    }

    public static class OOCircuit extends Circuit {
        OOCircuit(List<Battery> batteries, List<Resistor> resistors) {
            super(batteries, resistors);
        }

        OOCircuit(List<Battery> batteries, List<Resistor> resistors, List<CurrentSource> currentSources) {
            super(batteries, resistors, currentSources);
        }

        int getNumVars() {
            return getNodeCount() + getCurrentCount();
        }

        public class Term {
            private final double coefficient;
            private final Unknown variable;

            public Term(double coefficient, Unknown variable) {
                this.coefficient = coefficient;
                this.variable = variable;
            }

            public String toTermString() {
                String prefix = coefficient == 1 ? "" : ((coefficient == -1) ? "-" : coefficient + "*");
                return prefix + variable.toTermName();
            }
        }

        static interface IndexMap {
            int getIndex(Unknown unknown);
        }

        class Equation {
            double rhs;
            Term[] terms;

            Equation(double rhs, Term... terms) {
                this.rhs = rhs;
                this.terms = terms;
            }

            void stamp(int row, Matrix A, Matrix z, IndexMap indexMap) {
                z.set(row, 0, rhs);
                for (Term a : terms) {
                    A.set(row, indexMap.getIndex(a.variable), a.coefficient + A.get(row, indexMap.getIndex(a.variable)));
                }
            }

            public String toString() {
                ArrayList<String> termList = new ArrayList<String>();
                for (Term a : terms) {
                    termList.add(a.toTermString());
                }
                String result = "" + Util.mkString(termList, "+") + "=" + rhs;
                return result.replaceAll("\\+\\-", "\\-");
            }
        }

        abstract class Unknown {
            abstract String toTermName();
        }

        class UnknownCurrent extends Unknown {
            Element element;

            UnknownCurrent(Element element) {
                this.element = element;
            }

            String toTermName() {
                return "I" + element.node0 + "_" + element.node1;
            }

            @Override
            public boolean equals(Object o) {
                if (this == o) {
                    return true;
                }
                if (o == null || getClass() != o.getClass()) {
                    return false;
                }

                UnknownCurrent that = (UnknownCurrent) o;

                if (!element.equals(that.element)) {
                    return false;
                }

                return true;
            }

            public int hashCode() {
                return element.hashCode();
            }

            public String toString() {
                return "UnknownCurrent{" + "element=" + element + '}';
            }
        }

        class UnknownVoltage extends Unknown {
            int node;

            UnknownVoltage(int node) {
                this.node = node;
            }

            String toTermName() {
                return "V" + node;
            }

            @Override
            public boolean equals(Object o) {
                if (this == o) {
                    return true;
                }
                if (o == null || getClass() != o.getClass()) {
                    return false;
                }

                UnknownVoltage that = (UnknownVoltage) o;

                if (node != that.node) {
                    return false;
                }

                return true;
            }

            @Override
            public int hashCode() {
                return node;
            }

            @Override
            public String toString() {
                return "UnknownVoltage{" + "node=" + node + '}';
            }
        }


        double getRHS(int node) {
            double sum = 0.0;
            for (CurrentSource c : currentSources) {
                if (c.node1 == node) {
                    sum = sum - c.current;//positive current is entering the node//TODO: these signs seem backwards, shouldn't incoming current add?
                }
                if (c.node0 == node) {
                    sum = sum + c.current;//positive current is leaving the node
                }
            }
            return sum;
        }

        //Todo: does this get the signs right in all cases?
        //TODO: maybe signs here should depend on component orientation?

        //incoming current is negative, outgoing is positive

        ArrayList<Term> getIncomingCurrentTerms(int node) {
            ArrayList<Term> nodeTerms = new ArrayList<Term>();
            for (Battery b : batteries) {
                if (b.node1 == node) {
                    nodeTerms.add(new Term(-1, new UnknownCurrent(b)));
                }
            }
            for (Resistor r : resistors) {
                if (r.node1 == node && r.resistance == 0)//Treat resistors with R=0 as having unknown current and v1=v2
                {
                    nodeTerms.add(new Term(-1, new UnknownCurrent(r)));
                }
            }
            for (Resistor r : resistors) {
                if (r.node1 == node && r.resistance != 0) {
                    nodeTerms.add(new Term(1 / r.resistance, new UnknownVoltage(r.node1)));
                    nodeTerms.add(new Term(-1 / r.resistance, new UnknownVoltage(r.node0)));
                }
            }
            return nodeTerms;
        }

        //outgoing currents are negative so that incoming + outgoing = 0

        ArrayList<Term> getOutgoingCurrentTerms(int node) {
            ArrayList<Term> nodeTerms = new ArrayList<Term>();
            for (Battery b : batteries) {
                if (b.node0 == node) {
                    nodeTerms.add(new Term(1, new UnknownCurrent(b)));
                }
            }
            for (Resistor r : resistors) {
                //Treat resistors with R=0 as having unknown current and v1=v2
                if (r.node0 == node && r.resistance == 0) {
                    nodeTerms.add(new Term(1, new UnknownCurrent(r)));
                }
            }
            for (Resistor r : resistors) {
                if (r.node0 == node && r.resistance != 0) {
                    nodeTerms.add(new Term(-1 / r.resistance, new UnknownVoltage(r.node1)));
                    nodeTerms.add(new Term(1 / r.resistance, new UnknownVoltage(r.node0)));
                }
            }
            return nodeTerms;
        }

        ArrayList<Term> getCurrentConservationTerms(int node) {
            ArrayList<Term> nodeTerms = new ArrayList<Term>();
            nodeTerms.addAll(getIncomingCurrentTerms(node));
            nodeTerms.addAll(getOutgoingCurrentTerms(node));
            return nodeTerms;
        }

        //obtain one node for each connected component to have the reference voltage of 0.0

        HashSet<Integer> getReferenceNodes() {
            HashSet<Integer> nodeSet = getNodeSet();
            HashSet<Integer> remaining = new HashSet<Integer>();
            remaining.addAll(nodeSet);
            HashSet<Integer> referenceNodes = new HashSet<Integer>();
            while (remaining.size() > 0) {
                ArrayList<Integer> sorted = doSort(remaining.toArray(new Integer[remaining.size()]));
                referenceNodes.add(sorted.get(0));
                HashSet<Integer> connected = getConnectedNodes(sorted.get(0));
                remaining.removeAll(connected);
            }
            return referenceNodes;
        }

        private ArrayList<Integer> doSort(Integer[] objects) {
            ArrayList<Integer> copy = new ArrayList<Integer>(Arrays.asList(objects));
            Collections.sort(copy);
            return copy;
        }

        HashSet<Integer> getConnectedNodes(int node) {
            HashSet<Integer> visited = new HashSet<Integer>();
            HashSet<Integer> toVisit = new HashSet<Integer>();
            toVisit.add(node);
            getConnectedNodes(visited, toVisit);
            return visited;
        }

        private void getConnectedNodes(HashSet<Integer> visited, HashSet<Integer> toVisit) {
            while (toVisit.size() > 0) {
                Integer n = toVisit.toArray(new Integer[toVisit.size()])[0];
                visited.add(n);
                for (Element e : getElements()) {
                    if (e.containsNode(n) && !visited.contains(e.getOpposite(n))) {
                        toVisit.add(e.getOpposite(n));
                    }
                }
                toVisit.remove(n);
            }
        }

        ArrayList<Equation> getEquations() {
            ArrayList<Equation> list = new ArrayList<Equation>();
            //    println("nodeset=" + getNodeSet)

            //reference node in each connected component has a voltage of 0.0
            for (Integer n : getReferenceNodes()) {
                list.add(new Equation(0, new Term(1, new UnknownVoltage(n))));
            }

            //for each node, charge is conserved
            for (Integer node : getNodeSet()) {
                list.add(new Equation(getRHS(node), getCurrentConservationTerms(node).toArray(new Term[getCurrentConservationTerms(node).size()])));
            }

            //for each battery, voltage drop is given
            for (Battery battery : batteries) {
                list.add(new Equation(battery.voltage, new Term(-1, new UnknownVoltage(battery.node0)), new Term(1, new UnknownVoltage(battery.node1))));
            }

            //if resistor has no resistance, node0 and node1 should have same voltage
            for (Resistor resistor : resistors) {
                if (resistor.resistance == 0) {
                    list.add(new Equation(0, new Term(1, new UnknownVoltage(resistor.node0)), new Term(-1, new UnknownVoltage(resistor.node1))));
                }
            }

            return list;
        }

        ArrayList<UnknownVoltage> getUnknownVoltages() {
            ArrayList<UnknownVoltage> v = new ArrayList<UnknownVoltage>();
            for (Integer node : getNodeSet()) {
                v.add(new UnknownVoltage(node));
            }
            return v;
        }

        ArrayList<UnknownCurrent> getUnknownCurrents() {
            ArrayList<UnknownCurrent> unknowns = new ArrayList<UnknownCurrent>();
            for (Battery battery : batteries) {
                unknowns.add(new UnknownCurrent(battery));
            }

            //Treat resistors with R=0 as having unknown current and v1=v2
            for (Resistor resistor : resistors) {
                if (resistor.resistance == 0) {
                    unknowns.add(new UnknownCurrent(resistor));
                }
            }
            return unknowns;
        }

        ArrayList<Unknown> getUnknowns() {
            ArrayList<Unknown> all = new ArrayList<Unknown>(getUnknownCurrents());
            all.addAll(getUnknownVoltages());
            return all;
        }

        public ISolution solve() {
            ArrayList<Equation> equations = getEquations();

            Matrix A = new Matrix(equations.size(), getNumVars());
            Matrix z = new Matrix(equations.size(), 1);
            final ArrayList<Unknown> unknowns = getUnknowns();//store the unknown list for index lookup
            for (int i = 0; i < equations.size(); i++) {
                equations.get(i).stamp(i, A, z, new IndexMap() {
                    public int getIndex(Unknown unknown) {
                        return unknowns.indexOf(unknown);//todo: this step could be sped up
                    }
                });
            }

            if (debug) {
                System.out.println("Debugging circuit: " + toString());
                System.out.println(Util.mkString(equations, "\n"));
                System.out.println("a=");
                A.print(4, 2);
                System.out.println("z=");
                z.print(4, 2);
                System.out.println("unknowns=\n" + Util.mkString(getUnknowns(), "\n"));
            }
            Matrix x = A.solve(z);

            HashMap<Integer, Double> voltageMap = new HashMap<Integer, Double>();
            for (UnknownVoltage nodeVoltage : getUnknownVoltages()) {
                voltageMap.put(nodeVoltage.node, x.get(getUnknowns().indexOf(nodeVoltage), 0));
            }

            HashMap<Element, Double> currentMap = new HashMap<Element, Double>();
            for (UnknownCurrent currentVar : getUnknownCurrents()) {
                currentMap.put(currentVar.element, x.get(getUnknowns().indexOf(currentVar), 0));
            }

            if (debug) {
                System.out.println("x=");
                x.print(4, 2);
            }

            return new LinearCircuitSolution(voltageMap, currentMap);
        }

        public boolean debug = false;
    }

    public static class TestMNA {
        public static void main(String[] args) {
            final ArrayList<Battery> batteryArrayList = new ArrayList<Battery>();
            Battery battery = new Battery(0, 1, 4.0);
            batteryArrayList.add(battery);
            final ArrayList<Resistor> resistorArrayList = new ArrayList<Resistor>();
            resistorArrayList.add(new Resistor(1, 2, 4.0));
            Resistor resistor2 = new Resistor(2, 0, 0.0);
            resistorArrayList.add(resistor2);
            OOCircuit circuit = new OOCircuit(batteryArrayList, resistorArrayList);
            HashMap<Integer, Double> voltageMap = new HashMap<Integer, Double>();
            voltageMap.put(0, 0.0);
            voltageMap.put(1, 4.0);
            voltageMap.put(2, 0.0);
            HashMap<Element, Double> currentMap = new HashMap<Element, Double>();
            currentMap.put(battery, 1.0);
            currentMap.put(resistor2, 1.0);
            LinearCircuitSolution desiredSolution = new LinearCircuitSolution(voltageMap, currentMap);
            circuit.debug = true;
            System.out.println("circuit.solve=" + circuit.solve());
            assert (circuit.solve().approxEquals(desiredSolution));
        }
    }
}