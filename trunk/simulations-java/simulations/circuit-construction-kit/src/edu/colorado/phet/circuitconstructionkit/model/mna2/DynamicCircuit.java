package edu.colorado.phet.circuitconstructionkit.model.mna2;

import java.util.*;

///**This is a rewrite of companion mapping to make it simpler to construct and inspect companion models.*/
public class DynamicCircuit {
    private List<MNA.Battery> batteries;
    private List<MNA.Resistor> resistors;
    private List<MNA.CurrentSource> currents;
    private List<ResistiveBattery> resistiveBatteries;
    private List<DynamicCapacitor> capacitors;
    private List<DynamicInductor> inductors;

    @Override
    public String toString() {
        return "DynamicCircuit{" +
                "batteries=" + batteries +
                ", resistors=" + resistors +
                ", currents=" + currents +
                ", resistiveBatteries=" + resistiveBatteries +
                ", capacitors=" + capacitors +
                ", inductors=" + inductors +
                '}';
    }

    public DynamicCircuit(List<MNA.Battery> batteries, List<MNA.Resistor> resistors, List<MNA.CurrentSource> currents, List<ResistiveBattery> resistiveBatteries, List<DynamicCapacitor> capacitors, List<DynamicInductor> inductors) {
        this.batteries = batteries;
        this.capacitors = capacitors;
        this.currents = currents;
        this.inductors = inductors;
        this.resistiveBatteries = resistiveBatteries;
        this.resistors = resistors;
    }

    public static class DynamicCapacitor {
        Capacitor capacitor;
        CState state;

        public DynamicCapacitor(Capacitor capacitor, CState state) {
            this.capacitor = capacitor;
            this.state = state;
        }

        public double getCurrent() {
            return state.current;
        }

        @Override
        public String toString() {
            return "DynamicCapacitor{" +
                    "capacitor=" + capacitor +
                    ", state=" + state +
                    '}';
        }
    }

    public static class DynamicInductor {
        Inductor inductor;
        CState state;

        public DynamicInductor(Inductor inductor, CState state) {
            this.inductor = inductor;
            this.state = state;
        }

        @Override
        public String toString() {
            return "DynamicInductor{" +
                    "inductor=" + inductor +
                    ", state=" + state +
                    '}';
        }
    }

    public static class Capacitor extends MNA.Element {
        double capacitance;

        public Capacitor(int node0, int node1, double capacitance) {
            super(node0, node1);
            this.capacitance = capacitance;
        }
    }

    public static class Inductor extends MNA.Element {
        double inductance;

        public Inductor(int node0, int node1, double inductance) {
            super(node0, node1);
            this.inductance = inductance;
        }
    }

    public static class ResistiveBattery extends MNA.Element {
        private double voltage;
        private double resistance;

        public ResistiveBattery(int node0, int node1, double voltage, double resistance) {
            super(node0, node1);
            this.resistance = resistance;
            this.voltage = voltage;
        }

        @Override
        public String toString() {
            return "ResistiveBattery{" +
                    "resistance=" + resistance +
                    ", voltage=" + voltage +
                    '}';
        }
    }

    public static double euclideanDistance(double[] x, double[] y) {
        if (x.length != y.length) throw new RuntimeException("Vector length mismatch");
        double sumSqDiffs = 0.0;
        for (int i = 0; i < x.length; i++) {
            sumSqDiffs += Math.pow(x[i] - y[i], 2);
        }
        return Math.sqrt(sumSqDiffs);
    }

    public static class CState {
        double voltage;
        double current;

        public CState(double voltage, double current) {
            this.current = current;
            this.voltage = voltage;
        }

        @Override
        public String toString() {
            return "CState{" +
                    "voltage=" + voltage +
                    ", current=" + current +
                    '}';
        }
    }

    class Result {
        MNA.Circuit mnaCircuit;
        HashMap<MNA.Element, SolutionToDouble> currentCompanions;

        Result(MNA.Circuit mnaCircuit, HashMap<MNA.Element, SolutionToDouble> currentCompanions) {
            this.mnaCircuit = mnaCircuit;
            this.currentCompanions = currentCompanions;
        }
    }

    //Solving the companion model is the same as propagating forward in time by dt.
    public DynamicCircuitSolution solvePropagate(double dt) {
        Result result = toMNACircuit(dt);
        return new DynamicCircuitSolution(this, result.mnaCircuit.solve(), result.currentCompanions);
    }

    class DynamicState {
        DynamicCircuit circuit;
        DynamicCircuitSolution solution;

        DynamicState(DynamicCircuit circuit, DynamicCircuitSolution solution) {
            this.circuit = circuit;
            this.solution = solution;
        }

        DynamicState update(double dt) {
            DynamicCircuitSolution solution = circuit.solvePropagate(dt);
            DynamicCircuit newCircuit = circuit.updateCircuit(solution);
            return new DynamicState(newCircuit, solution);
        }

        public DynamicCircuit getCircuit() {
            return circuit;
        }

        public DynamicCircuitSolution getSolution() {
            return solution;
        }

        @Override
        public String toString() {
            return "DynamicState{" + "circuit=" + circuit + ", solution=" + solution + '}';
        }
    }

    //TODO: generalize distance criterion, will be simpler if solutions are incorporated
    public DynamicState solveWithSudbivisions(double dt) {
        TimestepSubdivisions.Steppable<DynamicState> steppable = new TimestepSubdivisions.Steppable<DynamicState>() {
            public DynamicState update(DynamicState a, double dt) {
                return a.update(dt);
            }

            public double distance(DynamicState a, DynamicState b) {
                double[] aCurrents = new double[a.circuit.capacitors.size()];
                for (int i = 0; i < aCurrents.length; i++) {
                    aCurrents[i] = a.circuit.capacitors.get(i).getCurrent();
                }

                double[] bCurrents = new double[b.circuit.capacitors.size()];
                for (int i = 0; i < bCurrents.length; i++) {
                    bCurrents[i] = b.circuit.capacitors.get(i).getCurrent();//todo: read from solution object
                }

                return euclideanDistance(aCurrents, bCurrents);
            }
        };
        return new TimestepSubdivisions<DynamicState>(1E-7).stepInTime(new DynamicState(this, null), steppable, dt);
    }

    public DynamicCircuit updateWithSubdivisions(double dt) {
        return solveWithSudbivisions(dt).getCircuit();
    }

    public DynamicCircuitSolution solveItWithSubdivisions(double dt) {
        return solveWithSudbivisions(dt).getSolution();
    }

    public DynamicCircuit update(double dt) {
        return updateCircuit(solvePropagate(dt));
    }

    //Applies the specified solution to the circuit.
    DynamicCircuit updateCircuit(DynamicCircuitSolution solution) {
        ArrayList<DynamicCapacitor> updatedCapacitors = new ArrayList<DynamicCapacitor>();
        for (DynamicCapacitor c : capacitors) {
            updatedCapacitors.add(new DynamicCapacitor(c.capacitor, new CState(solution.getNodeVoltage(c.capacitor.node1) - solution.getNodeVoltage(c.capacitor.node0), solution.getCurrent(c.capacitor))));
        }
        //todo: update inductors
        return new DynamicCircuit(batteries, resistors, currents, resistiveBatteries, updatedCapacitors, inductors);
    }

    public class DynamicCircuitSolution {
        DynamicCircuit circuit;
        MNA.Solution mnaSolution;
        HashMap<MNA.Element, SolutionToDouble> currentCompanions;

        public DynamicCircuitSolution(DynamicCircuit circuit, MNA.Solution mnaSolution, HashMap<MNA.Element, SolutionToDouble> currentCompanions) {
            this.circuit = circuit;
            this.mnaSolution = mnaSolution;
            this.currentCompanions = currentCompanions;
        }

        public double getNodeVoltage(int node) {
            return mnaSolution.getNodeVoltage(node);
        }

        public double getCurrent(MNA.Element element) {
            if (currentCompanions.containsKey(element))
                return currentCompanions.get(element).getValue(mnaSolution);
            else
                return mnaSolution.getCurrent(element);
        }

        @Override
        public String toString() {
            return "DynamicCircuitSolution{" +
                    "circuit=" + circuit +
                    ", mnaSolution=" + mnaSolution +
                    ", currentCompanions=" + currentCompanions +
                    '}';
        }

        public double getVoltage(MNA.Element element) {
            return getNodeVoltage(element.node1) - getNodeVoltage(element.node0);
        }
    }

    interface SolutionToDouble {
        double getValue(MNA.Solution solution);
    }

    //TODO: why not give every component a companion in the MNACircuit?
    Result toMNACircuit(double dt) {

        ArrayList<MNA.Battery> companionBatteries = new ArrayList<MNA.Battery>();
        ArrayList<MNA.Resistor> companionResistors = new ArrayList<MNA.Resistor>();
        ArrayList<MNA.CurrentSource> companionCurrents = new ArrayList<MNA.CurrentSource>();

        HashMap<MNA.Element, SolutionToDouble> currentCompanions = new HashMap<MNA.Element, SolutionToDouble>();
        HashSet<Integer> usedNodes = new HashSet<Integer>();
        ArrayList<MNA.Element> elements = new ArrayList<MNA.Element>();
        elements.addAll(batteries);
        elements.addAll(resistors);
        elements.addAll(resistiveBatteries);
        elements.addAll(currents);
        for (DynamicCapacitor c : capacitors) elements.add(c.capacitor);
        for (DynamicInductor i : inductors) elements.add(i.inductor);
        for (MNA.Element e : elements) {
            usedNodes.add(e.node0);
            usedNodes.add(e.node1);
        }

        //each resistive battery is a resistor in series with a battery
        for (ResistiveBattery b : resistiveBatteries) {
            int newNode = Collections.max(usedNodes) + 1;
            usedNodes.add(newNode);
            final MNA.Battery idealBattery = new MNA.Battery(b.node0, newNode, b.voltage);
            MNA.Resistor idealResistor = new MNA.Resistor(newNode, b.node1, b.resistance);
            companionBatteries.add(idealBattery);
            companionResistors.add(idealResistor);
            //we need to be able to get the current for this component
            currentCompanions.put(b, new SolutionToDouble() {
                public double getValue(MNA.Solution solution) {
                    return solution.getCurrent(idealBattery);
                }
            });
        }


        //add companion models for capacitor

        //TRAPEZOIDAL
        //        double vc = state.v + dt / 2 / c * state.i;
        //        double rc = dt / 2 / c;

        //BACKWARD EULER
        //        double vc = state.v;
        //        double rc = dt / c;
        for (DynamicCapacitor c : capacitors) {
            Capacitor capacitor = c.capacitor;
            CState cstate = c.state;
            //in series
            int newNode = Collections.max(usedNodes) + 1;
            usedNodes.add(newNode);

            double companionResistance = dt / 2.0 / capacitor.capacitance;
            double companionVoltage = cstate.voltage - companionResistance * cstate.current; //TODO: explain the difference between this sign and the one in TestTheveninCapacitorRC
            //      println("companion resistance = "+companionResistance+", companion voltage = "+companionVoltage)

            final MNA.Battery battery = new MNA.Battery(capacitor.node0, newNode, companionVoltage);
            MNA.Resistor resistor = new MNA.Resistor(newNode, capacitor.node1, companionResistance);
            companionBatteries.add(battery);
            companionResistors.add(resistor);

            //we need to be able to get the current for this component
            currentCompanions.put(capacitor, new SolutionToDouble() {
                public double getValue(MNA.Solution solution) {
                    return solution.getCurrent(battery);//in series, so current is same through both companion components
                }
            });
        }
        //        println("currentCompanions = " + currentCompanions)
        //    for (i <- inductors) {
        //      mnaBatteries += new Battery
        //      mnaCurrents += new CurrentSource
        //    }
        ArrayList<MNA.Battery> newBatteryList = new ArrayList<MNA.Battery>(batteries);
        newBatteryList.addAll(companionBatteries);
        ArrayList<MNA.Resistor> newResistorList = new ArrayList<MNA.Resistor>(resistors);
        newResistorList.addAll(companionResistors);
        ArrayList<MNA.CurrentSource> newCurrentList = new ArrayList<MNA.CurrentSource>(currents);
        newCurrentList.addAll(companionCurrents);

        return new Result(new MNA.Circuit(newBatteryList, newResistorList, newCurrentList), currentCompanions);
    }

    public static void main(String[] args) {
        double voltage = 9.0;
        //    double resistance = 1E-6
        double resistance = 1;
        Capacitor c = new Capacitor(2, 0, 0.1);
        MNA.Battery battery = new MNA.Battery(0, 1, voltage);

        ArrayList<MNA.Battery> batteries = new ArrayList<MNA.Battery>();
        batteries.add(battery);

        ArrayList<MNA.Resistor> resistors = new ArrayList<MNA.Resistor>();
        resistors.add(new MNA.Resistor(1, 2, resistance));

        ArrayList<DynamicCapacitor> capacitors = new ArrayList<DynamicCapacitor>();
        capacitors.add(new DynamicCapacitor(c, new CState(0.0, voltage / resistance)));
        DynamicCircuit circuit = new DynamicCircuit(batteries, resistors, new ArrayList<MNA.CurrentSource>(), new ArrayList<ResistiveBattery>(), capacitors, new ArrayList<DynamicInductor>());
//    //    var circuit = new CompanionCircuit(battery :: Nil, new Resistor(1, 0, resistance) :: Nil, Nil, Nil, Nil)
        System.out.println("current through capacitor");
        for (int i = 0; i < 10; i++) {
            DynamicCircuitSolution solution = circuit.solveItWithSubdivisions(0.03);
            long startTime = System.currentTimeMillis();
            circuit = circuit.updateWithSubdivisions(0.03);
            long endTime = System.currentTimeMillis();
            System.out.println("time = " + (endTime - startTime));
            //      println("Circuit: "+circuit)
            System.out.println("companions = " + solution.currentCompanions);
            System.out.println(circuit.capacitors.get(0).state.current + "\t" + solution.getCurrent(c));
        }
    }
//}
}