//package edu.colorado.phet.circuitconstructionkit.model.mna2;
//
//import Jama.Matrix;
//
//import java.util.HashSet;
//import java.util.List;
//import java.util.ArrayList;
//import java.util.HashMap;
//
///**
// * Conventions:
// * Current is 'conventional current': in a battery positive current flows from the higher (+) potential
// */
//public class MNA {
//
//    abstract class ISolution {
//        abstract double getNodeVoltage(int node);
//
//        abstract double getCurrent(Element element);
//
//        public double getVoltageDifference(int node0, int node1) {
//            return getNodeVoltage(node1) - getNodeVoltage(node0);
//        }
//    }
//
//    //sparse solution containing only the solved unknowns in MNA
//    class Solution extends ISolution {
//        HashMap<Integer, Double> nodeVoltages = new HashMap<Integer, Double>();
//        HashMap<Element, Double> branchCurrents = new HashMap<Element, Double>();
//
//        Solution(HashMap<Integer, Double> nodeVoltages, HashMap<Element, Double> branchCurrents) {
//            this.nodeVoltages = nodeVoltages;
//            this.branchCurrents = branchCurrents;
//        }
//
//        double getNodeVoltage(int node) {
//            return nodeVoltages.get(node);
//        }
//
//        boolean approxEquals(Solution s) {
//            return approxEquals(s, 1E-6);
//        }
//
//        boolean approxEquals(Solution s, double delta) {
//            if (nodeVoltages.keySet() != s.nodeVoltages.keySet() || branchCurrents.keySet() != s.branchCurrents.keySet())
//                return false;
//            else {
//                boolean sameVoltages = true;
//                for (Integer key : nodeVoltages.keySet()) {
//                    if (nodeVoltages.get(key) != s.nodeVoltages.get(key))
//                        sameVoltages = false;
//                }
//                boolean sameCurrents = true;
//                for (Element key : branchCurrents.keySet()) {
//                    if (Math.abs(branchCurrents.get(key) - s.branchCurrents.get(key)) > delta) sameCurrents = false;
//                }
//
//                return sameVoltages && sameCurrents;
//            }
//        }
//
//        double getVoltage(Element e) {
//            return nodeVoltages.get(e.node1) - nodeVoltages.get(e.node0);
//        }
//
//        double getCurrent(Element e) {
//            //if it was a battery or resistor (of R=0), look up the answer
//            if (branchCurrents.containsKey(e)) return branchCurrents.get(e);
//                //else compute based on V=IR
//            else {
//                if (e instanceof Resistor) {
//                    Resistor r = (Resistor) e;
//                    return -getVoltage(r) / r.resistance;
//                } else {
//                    throw new RuntimeException("Solution does not contain current for element: " + e);
//                }
//            }
//
//        }
//    }
//
//    //Subclasses should have proper equals and hashcode for hashmapping
//    abstract class Element {
//        int node0;
//
//        int node1;
//
//        protected Element(int node0, int node1) {
//            this.node0 = node0;
//            this.node1 = node1;
//        }
//
//        boolean containsNode(int n) {
//            return n == node0 || n == node1;
//        }
//
//        int getOpposite(int node) {
//            if (node == node0) return node1;
//            else if (node == node1) return node0;
//            else throw new RuntimeException("node not found");
//        }
//    }
//
//    //todo: provide equals and hashcode for element subclasses
//    class Battery extends Element {
//        double voltage;
//
//        Battery(int node0, int node1, double voltage) {
//            super(node0, node1);
//            this.voltage = voltage;
//        }
//
//        int node0() {
//            return node0;
//        }
//
//        int node1() {
//            return node1;
//        }
//    }
//
//    class Resistor extends Element {
//        double resistance;
//
//        Resistor(int node0, int node1, double resistance) {
//            super(node0, node1);
//            this.resistance = resistance;
//        }
//    }
//
//    class CurrentSource extends Element {
//        double current;
//
//        CurrentSource(int node0, int node1, double current) {
//            super(node0, node1);
//            this.current = current;
//        }
//    }
//
//    static abstract class AbstractCircuit {
//        HashSet<Integer> getNodeSet() {
//            HashSet<Integer> set = new HashSet<Integer>();
//            for (Element element : getElements()) {
//                set.add(element.node0);
//                set.add(element.node1);
//            }
//            return set;
//        }
//
//        abstract List<Element> getElements();
//    }
//
//    static class Circuit extends AbstractCircuit {
//        //(batteries: Seq[Battery], resistors: Seq[Resistor], currentSources: Seq[CurrentSource])
//        List<Battery> batteries;
//        List<Resistor> resistors;
//        List<CurrentSource> currentSources;
//
//        Circuit(List<Battery> batteries, List<Resistor> resistors) {
//            this(batteries, resistors, new ArrayList<CurrentSource>());
//        }
//
//        Circuit(List<Battery> batteries, List<Resistor> resistors, List<CurrentSource> currentSources) {
//            this.batteries = batteries;
//            this.resistors = resistors;
//            this.currentSources = currentSources;
//        }
//
//        List<Element> getElements() {
//            List<Element> list = new ArrayList<Element>();
//            list.addAll(batteries);
//            list.addAll(resistors);
//            list.addAll(currentSources);
//            return list;
//        }
//
//        int getNodeCount() {
//            return getNodeSet().size();
//        }
//
//        int getCurrentCount() {//def getCurrentCount = batteries.length + resistors.filter(_.resistance == 0).size
//            int zeroResistors = 0;
//            for (Resistor resistor : resistors) {
//                if (resistor.resistance == 0) {
//                    zeroResistors++;
//                }
//            }
//            return batteries.size() + zeroResistors;
//        }
//
//        int getNumVars() {
//            return getNodeCount() + getCurrentCount();
//        }
//
//        class Term {
//            double coefficient;
//            Unknown variable;
//
//            Term(double coefficient, Unknown variable) {
//                this.coefficient = coefficient;
//                this.variable = variable;
//            }
//
//            String toTermString() {
//                String prefix = coefficient == 1 ? "" : ((coefficient == -1) ? "-" : coefficient + "*");
//                return prefix + variable.toTermName();
//            }
//        }
//
//        static interface IndexMap {
//            int getIndex(Unknown unknown);
//        }
//
//        class Equation {
//            //(rhs: Double, terms: Term*)
//            double rhs;
//            ArrayList<Term> terms;
//
//            Equation(double rhs, ArrayList<Term> terms) {
//                this.rhs = rhs;
//                this.terms = terms;
//            }
//
//            void stamp(int row, Matrix A, Matrix z, IndexMap indexMap) {
//                z.set(row, 0, rhs);
//                for (Term a : terms) {
//                    A.set(row, indexMap.getIndex(a.variable), a.coefficient + A.get(row, indexMap.getIndex(a.variable)));
//                }
//            }
//
//            public String toString() {
//                val termList =
//                for (a< -terms) yield a.toTermString
//                String result = "" + termList.mkString("+") + "=" + rhs;
//                return result.replaceAll("\\+\\-", "\\-");
//            }
//        }
//
//        abstract class Unknown {
//            abstract String toTermName();
//        }
//
//        class UnknownCurrent extends Unknown {
//            Element element;
//
//            UnknownCurrent(Element element) {
//                this.element = element;
//            }
//
//            String toTermName() {
//                return "I" + element.node0 + "_" + element.node1;
//            }
//
//        }
//
//        class UnknownVoltage extends Unknown {
//            int node;
//
//            UnknownVoltage(int node) {
//                this.node = node;
//            }
//
//            String toTermName() {
//                return "V" + node;
//            }
//        }
//
//
//        double getRHS(int node) {
//            double sum = 0.0;
//            for (CurrentSource c : currentSources) {
//                if (c.node1 == node)
//                    sum = sum - c.current;//current is entering the node//TODO: these signs seem backwards, shouldn't incoming current add?
//                if (c.node0 == node) sum = sum + c.current;//current is going away
//            }
//            return sum;
//        }
//
//
//        //Todo: does this get the signs right in all cases?
//  //TODO: maybe signs here should depend on component orientation?
//
//  //incoming current is negative, outgoing is positive
//  def getIncomingCurrentTerms(node: Int) = {
//    val nodeTerms = new ArrayBuffer[Term]
//    for (b <- batteries if b.node1 == node)
//      nodeTerms += Term(-1, UnknownCurrent(b))
//    for (r <- resistors; if r.node1 == node; if r.resistance == 0) //Treat resistors with R=0 as having unknown current and v1=v2
//      nodeTerms += Term(-1, UnknownCurrent(r))
//    for (r <- resistors; if r.node1 == node; if r.resistance != 0) {
//      nodeTerms += Term(1 / r.resistance, UnknownVoltage(r.node1))
//      nodeTerms += Term(-1 / r.resistance, UnknownVoltage(r.node0))
//    }
//    nodeTerms
//  }
//  //outgoing currents are negative so that incoming + outgoing = 0
//  def getOutgoingCurrentTerms(node: Int) = {
//    val nodeTerms = new ArrayBuffer[Term]
//    for (b <- batteries if b.node0 == node)
//      nodeTerms += Term(1, UnknownCurrent(b))
//    for (r <- resistors; if r.node0 == node; if r.resistance == 0) //Treat resistors with R=0 as having unknown current and v1=v2
//      nodeTerms += Term(1, UnknownCurrent(r))
//    for (r <- resistors; if r.node0 == node; if r.resistance != 0) {
//      nodeTerms += Term(-1 / r.resistance, UnknownVoltage(r.node1))
//      nodeTerms += Term(1 / r.resistance, UnknownVoltage(r.node0))
//    }
//    nodeTerms
//  }
//
//  def getCurrentConservationTerms(node: Int) = {
//    val nodeTerms = new ArrayBuffer[Term]
//    nodeTerms ++= getIncomingCurrentTerms(node)
//    nodeTerms ++= getOutgoingCurrentTerms(node)
//    nodeTerms
//  }
//
//  //obtain one node for each connected component to have the reference voltage of 0.0
//  def getReferenceNodes = {
//    val nodeSet: HashSet[Int] = getNodeSet
//    val remaining = new HashSet[Int]
//    remaining ++= nodeSet
//    val referenceNodes = new HashSet[Int]
//    while (remaining.size > 0) {
//      val sorted: Array[Int] = Sorting.stableSort(remaining.toArray)
//      referenceNodes += sorted(0)
//      val connected = getConnectedNodes(sorted(0))
//      remaining --= connected
//    }
//    referenceNodes
//  }
//
//  def getConnectedNodes(node: Int): HashSet[Int] = {
//    val nodeSet = getNodeSet
//    val visited = new HashSet[Int]
//    val toVisit = new HashSet[Int]
//    toVisit += node
//    getConnectedNodes(visited, toVisit)
//    visited
//  }
//
//  private def getConnectedNodes(visited: HashSet[Int], toVisit: HashSet[Int]): Unit = {
//    while (toVisit.size > 0) {
//      val n = toVisit.toArray(0)
//      visited += n
//      for (e <- getElements) {
//        if (e.containsNode(n) && !visited.contains(e.getOpposite(n))) {
//          toVisit += e.getOpposite(n)
//        }
//      }
//      toVisit -= n
//    }
//  }
//
//  def getEquations = {
//    val list = new ArrayBuffer[Equation]
//    //    println("nodeset=" + getNodeSet)
//
//    //reference node in each connected component has a voltage of 0.0
//    for (n <- getReferenceNodes) list += new Equation(0, Term(1, UnknownVoltage(n)))
//
//    //for each node, charge is conserved
//    for (node <- getNodeSet) list += new Equation(getRHS(node), getCurrentConservationTerms(node): _*) //see p. 155 scala book
//
//    //for each battery, voltage drop is given
//    for (battery <- batteries) list += new Equation(battery.voltage, Term(-1, UnknownVoltage(battery.node0)), Term(1, UnknownVoltage(battery.node1)))
//
//    //if resistor has no resistance, node0 and node1 should have same voltage
//    for (resistor <- resistors if resistor.resistance == 0) list += new Equation(0, Term(1, UnknownVoltage(resistor.node0)), Term(-1, UnknownVoltage(resistor.node1)))
//
//    list
//  }
//
//  def getUnknownVoltages = for (node <- getNodeSet) yield UnknownVoltage(node)
//
//  def getUnknownCurrents = {
//    val unknowns = new ArrayBuffer[UnknownCurrent]
//    for (battery <- batteries) unknowns += UnknownCurrent(battery)
//
//    //Treat resistors with R=0 as having unknown current and v1=v2
//    for (resistor <- resistors if resistor.resistance == 0) unknowns += UnknownCurrent(resistor)
//    unknowns
//  }
//
//  def getUnknowns = getUnknownCurrents.toList ::: getUnknownVoltages.toList
//
//  def solve = {
//    var equations = getEquations
//
//    val A = new Matrix(equations.size, getNumVars)
//    val z = new Matrix(equations.size, 1)
//    for (i <- 0 until equations.size) equations(i).stamp(i, A, z, getUnknowns.indexOf(_))
//    val x = A.solve(z)
//
//    val voltageMap = new HashMap[Int, Double]
//    for (nodeVoltage <- getUnknownVoltages) voltageMap(nodeVoltage.node) = x.get(getUnknowns.indexOf(nodeVoltage), 0)
//
//    val currentMap = new HashMap[Element, Double]
//    for (currentVar <- getUnknownCurrents) currentMap(currentVar.element) = x.get(getUnknowns.indexOf(currentVar), 0)
//
//    if (debug) {
//      println("Debugging circuit: " + toString)
//      println(equations.mkString("\n"))
//      println("a=")
//      A.print(4, 2)
//      println("z=")
//      z.print(4, 2)
//      println("unknowns=\n" + getUnknowns.mkString("\n"))
//      println("x=")
//      x.print(4, 2)
//    }
//
//    new Solution(voltageMap, currentMap)
//  }
//
//  var debug = false
//}
//        }
//
//class TestMNA {
//    public static void main(String[] args) {
//
//        Battery battery = new Battery(0, 1, 4.0);
//    Resistor resistor = new Resistor(1, 2, 4.0);
//    Resistor resistor2 = new Resistor(2, 0, 0.0);
//    Circuit circuit = new Circuit(battery :: Nil, resistor :: resistor2:: Nil)
//    Solution desiredSolution = new Solution(Map(0 -> 0.0, 1 -> 4.0), Map(battery -> 1.0))
//    circuit.debug=true
//    println("circuit.solve="+circuit.solve)
//    assert(circuit.solve.approxEquals(desiredSolution))
//}
//
//
//}
