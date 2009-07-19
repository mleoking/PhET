package edu.colorado.phet.circuitconstructionkit.model.mna2;

import Jama.Matrix;

import java.util.*;

/**
 * Conventions:
 * Current is 'conventional current': in a battery positive current flows from the higher (+) potential
 */
public class MNA {

    public static abstract class ISolution {
        abstract double getNodeVoltage(int node);

        abstract double getCurrent(Element element);

        public double getVoltageDifference(int node0, int node1) {
            return getNodeVoltage(node1) - getNodeVoltage(node0);
        }
    }

    //sparse solution containing only the solved unknowns in MNA
    public static class Solution extends ISolution {
        HashMap<Integer, Double> nodeVoltages = new HashMap<Integer, Double>();
        HashMap<Element, Double> branchCurrents = new HashMap<Element, Double>();

        Solution(HashMap<Integer, Double> nodeVoltages, HashMap<Element, Double> branchCurrents) {
            this.nodeVoltages = nodeVoltages;
            this.branchCurrents = branchCurrents;
        }

        double getNodeVoltage(int node) {
            return nodeVoltages.get(node);
        }

        boolean approxEquals(Solution s) {
            return approxEquals(s, 1E-6);
        }
        boolean approxEquals(double a,double b,double delta){
            return Math.abs(a-b)<delta;
        }

        boolean approxEquals(Solution s, double delta) {
            if (!nodeVoltages.keySet().equals(s.nodeVoltages.keySet()) || !branchCurrents.keySet().equals(s.branchCurrents.keySet()))
                return false;
            else {
                boolean sameVoltages = true;
                for (Integer key : nodeVoltages.keySet()) {
                    if (!approxEquals(nodeVoltages.get(key),s.nodeVoltages.get(key),delta))
                        sameVoltages = false;
                }
                boolean sameCurrents = true;
                for (Element key : branchCurrents.keySet()) {
                    if (Math.abs(branchCurrents.get(key) - s.branchCurrents.get(key)) > delta) sameCurrents = false;
                }

                return sameVoltages && sameCurrents;
            }
        }

        double getVoltage(Element e) {
            return nodeVoltages.get(e.node1) - nodeVoltages.get(e.node0);
        }

        double getCurrent(Element e) {
            //if it was a battery or resistor (of R=0), look up the answer
            if (branchCurrents.containsKey(e)) return branchCurrents.get(e);
                //else compute based on V=IR
            else {
                if (e instanceof Resistor) {
                    Resistor r = (Resistor) e;
                    return -getVoltage(r) / r.resistance;
                } else {
                    throw new RuntimeException("Solution does not contain current for element: " + e);
                }
            }

        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            Solution solution = (Solution) o;

            if (!branchCurrents.equals(solution.branchCurrents)) return false;
            if (!nodeVoltages.equals(solution.nodeVoltages)) return false;

            return true;
        }

        @Override
        public int hashCode() {
            int result = nodeVoltages.hashCode();
            result = 31 * result + branchCurrents.hashCode();
            return result;
        }

        @Override
        public String toString() {
            return "Solution{" +
                    "nodeVoltages=" + nodeVoltages +
                    ", branchCurrents=" + branchCurrents +
                    '}';
        }
    }

    //Subclasses should have proper equals and hashcode for hashmapping
    public static abstract class Element {
        int node0;

        int node1;

        protected Element(int node0, int node1) {
            this.node0 = node0;
            this.node1 = node1;
        }

        boolean containsNode(int n) {
            return n == node0 || n == node1;
        }

        int getOpposite(int node) {
            if (node == node0) return node1;
            else if (node == node1) return node0;
            else throw new RuntimeException("node not found");
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            Element element = (Element) o;

            if (node0 != element.node0) return false;
            if (node1 != element.node1) return false;

            return true;
        }

        @Override
        public int hashCode() {
            int result = node0;
            result = 31 * result + node1;
            return result;
        }

        @Override
        public String toString() {
            return "Element{" +
                    "node0=" + node0 +
                    ", node1=" + node1 +
                    '}';
        }
    }

    public static class Battery extends Element {
        double voltage;

        Battery(int node0, int node1, double voltage) {
            super(node0, node1);
            this.voltage = voltage;
        }

        int node0() {
            return node0;
        }

        int node1() {
            return node1;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            if (!super.equals(o)) return false;

            Battery battery = (Battery) o;

            if (Double.compare(battery.voltage, voltage) != 0) return false;

            return true;
        }

        @Override
        public int hashCode() {
            int result = super.hashCode();
            long temp;
            temp = voltage != +0.0d ? Double.doubleToLongBits(voltage) : 0L;
            result = 31 * result + (int) (temp ^ (temp >>> 32));
            return result;
        }

        @Override
        public String toString() {
            return "Battery{" +
                    "["+node0+"->"+node1+"], "+
                    "v=" + voltage +
                    '}';
        }
    }

    public static class Resistor extends Element {
        double resistance;

        Resistor(int node0, int node1, double resistance) {
            super(node0, node1);
            this.resistance = resistance;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            if (!super.equals(o)) return false;

            Resistor resistor = (Resistor) o;

            if (Double.compare(resistor.resistance, resistance) != 0) return false;

            return true;
        }

        @Override
        public int hashCode() {
            int result = super.hashCode();
            long temp;
            temp = resistance != +0.0d ? Double.doubleToLongBits(resistance) : 0L;
            result = 31 * result + (int) (temp ^ (temp >>> 32));
            return result;
        }

        @Override
        public String toString() {
            return "Resistor{" +
                    "["+node0+"->"+node1+"], "+
                    "r=" + resistance +
                    '}';
        }
    }

    public static class CurrentSource extends Element {
        double current;

        public CurrentSource(int node0, int node1, double current) {
            super(node0, node1);
            this.current = current;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            if (!super.equals(o)) return false;

            CurrentSource that = (CurrentSource) o;

            if (Double.compare(that.current, current) != 0) return false;

            return true;
        }

        @Override
        public int hashCode() {
            int result = super.hashCode();
            long temp;
            temp = current != +0.0d ? Double.doubleToLongBits(current) : 0L;
            result = 31 * result + (int) (temp ^ (temp >>> 32));
            return result;
        }

        @Override
        public String toString() {
            return "CurrentSource{" +
                    "current=" + current +
                    '}';
        }
    }

    public static abstract class AbstractCircuit {
        HashSet<Integer> getNodeSet() {
            HashSet<Integer> set = new HashSet<Integer>();
            for (Element element : getElements()) {
                set.add(element.node0);
                set.add(element.node1);
            }
            return set;
        }

        abstract List<Element> getElements();
    }

    public static class Circuit extends AbstractCircuit {
        List<Battery> batteries;
        List<Resistor> resistors;
        List<CurrentSource> currentSources;

        Circuit(List<Battery> batteries, List<Resistor> resistors) {
            this(batteries, resistors, new ArrayList<CurrentSource>());
        }

        Circuit(List<Battery> batteries, List<Resistor> resistors, List<CurrentSource> currentSources) {
            this.batteries = batteries;
            this.resistors = resistors;
            this.currentSources = currentSources;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            Circuit circuit = (Circuit) o;

            if (debug != circuit.debug) return false;
            if (!batteries.equals(circuit.batteries)) return false;
            if (!currentSources.equals(circuit.currentSources)) return false;
            if (!resistors.equals(circuit.resistors)) return false;

            return true;
        }

        @Override
        public int hashCode() {
            int result = batteries.hashCode();
            result = 31 * result + resistors.hashCode();
            result = 31 * result + currentSources.hashCode();
            result = 31 * result + (debug ? 1 : 0);
            return result;
        }

        @Override
        public String toString() {
            return "Circuit{" +
                    "batteries=" + batteries +
                    ", resistors=" + resistors +
                    ", currentSources=" + currentSources +
                    ", debug=" + debug +
                    '}';
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

        int getNumVars() {
            return getNodeCount() + getCurrentCount();
        }

        class Term {
            double coefficient;
            Unknown variable;

            Term(double coefficient, Unknown variable) {
                this.coefficient = coefficient;
                this.variable = variable;
            }

            String toTermString() {
                String prefix = coefficient == 1 ? "" : ((coefficient == -1) ? "-" : coefficient + "*");
                return prefix + variable.toTermName();
            }

            @Override
            public boolean equals(Object o) {
                if (this == o) return true;
                if (o == null || getClass() != o.getClass()) return false;

                Term term = (Term) o;

                if (Double.compare(term.coefficient, coefficient) != 0) return false;
                if (!variable.equals(term.variable)) return false;

                return true;
            }

            @Override
            public int hashCode() {
                int result;
                long temp;
                temp = coefficient != +0.0d ? Double.doubleToLongBits(coefficient) : 0L;
                result = (int) (temp ^ (temp >>> 32));
                result = 31 * result + variable.hashCode();
                return result;
            }

            @Override
            public String toString() {
                return super.toString();
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
                for (Term a : terms) termList.add(a.toTermString());
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
                if (this == o) return true;
                if (o == null || getClass() != o.getClass()) return false;

                UnknownCurrent that = (UnknownCurrent) o;

                if (!element.equals(that.element)) return false;

                return true;
            }

            @Override
            public int hashCode() {
                return element.hashCode();
            }

            @Override
            public String toString() {
                return "UnknownCurrent{" +
                        "element=" + element +
                        '}';
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
                if (this == o) return true;
                if (o == null || getClass() != o.getClass()) return false;

                UnknownVoltage that = (UnknownVoltage) o;

                if (node != that.node) return false;

                return true;
            }

            @Override
            public int hashCode() {
                return node;
            }

            @Override
            public String toString() {
                return "UnknownVoltage{" +
                        "node=" + node +
                        '}';
            }
        }


        double getRHS(int node) {
            double sum = 0.0;
            for (CurrentSource c : currentSources) {
                if (c.node1 == node)
                    sum = sum - c.current;//current is entering the node//TODO: these signs seem backwards, shouldn't incoming current add?
                if (c.node0 == node) sum = sum + c.current;//current is going away
            }
            return sum;
        }


        //Todo: does this get the signs right in all cases?
        //TODO: maybe signs here should depend on component orientation?

        //incoming current is negative, outgoing is positive

        ArrayList<Term> getIncomingCurrentTerms(int node) {
            ArrayList<Term> nodeTerms = new ArrayList<Term>();
            for (Battery b : batteries) {
                if (b.node1 == node) nodeTerms.add(new Term(-1, new UnknownCurrent(b)));
            }
            for (Resistor r : resistors) {
                if (r.node1 == node && r.resistance == 0)//Treat resistors with R=0 as having unknown current and v1=v2
                    nodeTerms.add(new Term(-1, new UnknownCurrent(r)));
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
                if (b.node0 == node) nodeTerms.add(new Term(1, new UnknownCurrent(b)));
            }
            for (Resistor r : resistors) {
                if (r.node0 == node && r.resistance == 0)//Treat resistors with R=0 as having unknown current and v1=v2
                    nodeTerms.add(new Term(1, new UnknownCurrent(r)));
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
                    if (e.containsNode(n) && !visited.contains(e.getOpposite(n)))
                        toVisit.add(e.getOpposite(n));
                }
                toVisit.remove(n);
            }
        }

        ArrayList<Equation> getEquations() {
            ArrayList<Equation> list = new ArrayList<Equation>();
            //    println("nodeset=" + getNodeSet)

            //reference node in each connected component has a voltage of 0.0
            for (Integer n : getReferenceNodes()) list.add(new Equation(0, new Term(1, new UnknownVoltage(n))));

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
                if (resistor.resistance == 0)
                    list.add(new Equation(0, new Term(1, new UnknownVoltage(resistor.node0)), new Term(-1, new UnknownVoltage(resistor.node1))));
            }

            return list;
        }

        ArrayList<UnknownVoltage> getUnknownVoltages() {
            ArrayList<UnknownVoltage> v = new ArrayList<UnknownVoltage>();
            for (Integer node : getNodeSet()) v.add(new UnknownVoltage(node));
            return v;
        }

        ArrayList<UnknownCurrent> getUnknownCurrents() {
            ArrayList<UnknownCurrent> unknowns = new ArrayList<UnknownCurrent>();
            for (Battery battery : batteries) unknowns.add(new UnknownCurrent(battery));

            //Treat resistors with R=0 as having unknown current and v1=v2
            for (Resistor resistor : resistors) {
                if (resistor.resistance == 0) unknowns.add(new UnknownCurrent(resistor));
            }
            return unknowns;
        }

        ArrayList<Unknown> getUnknowns() {
            ArrayList<Unknown> all = new ArrayList<Unknown>(getUnknownCurrents());
            all.addAll(getUnknownVoltages());
            return all;
        }

        Solution solve() {
            ArrayList<Equation> equations = getEquations();

            Matrix A = new Matrix(equations.size(), getNumVars());
            Matrix z = new Matrix(equations.size(), 1);
            for (int i = 0; i < equations.size(); i++)
                equations.get(i).stamp(i, A, z, new IndexMap() {
                    public int getIndex(Unknown unknown) {
                        return getUnknowns().indexOf(unknown);
                    }
                });

            if (debug){
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
            for (UnknownVoltage nodeVoltage : getUnknownVoltages())
                voltageMap.put(nodeVoltage.node, x.get(getUnknowns().indexOf(nodeVoltage), 0));

            HashMap<Element, Double> currentMap = new HashMap<Element, Double>();
            for (UnknownCurrent currentVar : getUnknownCurrents())
                currentMap.put(currentVar.element, x.get(getUnknowns().indexOf(currentVar), 0));

            if (debug) {
                System.out.println("x=");
                x.print(4, 2);
            }

            return new Solution(voltageMap, currentMap);
        }

        boolean debug = true;

    }

    public static class Util {
        public static String mkString(List list, String separator) {
            String out = "";
            for (int i = 0; i < list.size(); i++) {
                out += list.get(i);
                if (i < list.size() - 1) out += separator;
            }
            return out;
        }
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
            Circuit circuit = new Circuit(batteryArrayList, resistorArrayList);
            HashMap<Integer, Double> voltageMap = new HashMap<Integer, Double>();
            voltageMap.put(0, 0.0);
            voltageMap.put(1, 4.0);
            voltageMap.put(2, 0.0);
            HashMap<Element, Double> currentMap = new HashMap<Element, Double>();
            currentMap.put(battery, 1.0);
            currentMap.put(resistor2, 1.0);
            Solution desiredSolution = new Solution(voltageMap, currentMap);
            circuit.debug = true;
            System.out.println("circuit.solve=" + circuit.solve());
            assert (circuit.solve().approxEquals(desiredSolution));
        }
    }
}