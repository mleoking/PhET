package edu.colorado.phet.circuitconstructionkit.model.mna2;

import edu.colorado.phet.circuitconstructionkit.model.Circuit;
import edu.colorado.phet.circuitconstructionkit.model.analysis.CircuitSolver;
import edu.colorado.phet.circuitconstructionkit.model.components.*;
import edu.colorado.phet.common.phetcommon.math.MathUtil;

import java.util.ArrayList;

public class PureJavaSolver extends CircuitSolver {
    static interface Adapter {
        Branch getComponent();

        MNA.Element getElement();
    }

    public static class AdapterUtil {
        public static void applySolution(CompanionMNA.CompanionSolution sol, Adapter adapter) {
            adapter.getComponent().setCurrent(sol.getCurrent(adapter.getElement()));
            adapter.getComponent().setVoltageDrop(sol.getVoltage(adapter.getElement()));
        }

    }

    static class ResistiveBatteryAdapter extends CompanionMNA.ResistiveBattery implements Adapter {
        Circuit c;
        Battery b;

        ResistiveBatteryAdapter(Circuit c, Battery b) {
            super(c.indexOf(b.getStartJunction()), c.indexOf(b.getEndJunction()), b.getVoltageDrop(), b.getResistance());
            this.c = c;
            this.b = b;
        }

        public Branch getComponent() {
            return b;
        }

        public MNA.Element getElement() {
            return this;
        }

        //don't set voltage on the battery; that actually changes its nominal voltage
        void applySolution(CompanionMNA.CompanionSolution sol) {
            getComponent().setCurrent(sol.getCurrent(this));
        }
    }

    //doesn't appear in the mna physics engine; treated as a missing piece
    static class OpenAdapter implements Adapter {
        Circuit c;
        Branch b;

        OpenAdapter(Circuit c, Branch b) {
            this.c = c;
            this.b = b;
        }

        public Branch getComponent() {
            return b;
        }

        public MNA.Element getElement() {
            return null;
        }

        void applySolution(CompanionMNA.CompanionSolution sol) {
            getComponent().setCurrent(0.0);
            getComponent().setVoltageDrop(0.0);//todo: will this cause numerical problems?
        }
    }

    static class ResistorAdapter extends MNA.Resistor implements Adapter {
        Circuit c;
        Branch b;

        ResistorAdapter(Circuit c, Branch b) {
            super(c.indexOf(b.getStartJunction()), c.indexOf(b.getEndJunction()), b.getResistance());
            this.c = c;
            this.b = b;
        }

        public Branch getComponent() {
            return b;
        }

        public MNA.Element getElement() {
            return this;
        }

        void applySolution(CompanionMNA.CompanionSolution sol) {
            AdapterUtil.applySolution(sol, this);
        }
    }

    static class CapacitorAdapter extends CompanionMNA.Capacitor implements Adapter {
        Circuit c;
        Capacitor b;

        CapacitorAdapter(Circuit c, Capacitor b) {
            super(c.indexOf(b.getStartJunction()), c.indexOf(b.getEndJunction()), b.getCapacitance(), b.getVoltageDrop(), b.getCurrent());
            this.c = c;
            this.b = b;
        }

        public Branch getComponent() {
            return b;
        }

        public MNA.Element getElement() {
            return this;
        }

        static boolean signsMatch(double x, double y) {
            return MathUtil.getSign(x) == MathUtil.getSign(y);
        }

        static long lastTimeWorkaroundApplied = System.currentTimeMillis();

        //This workaround is to help improve behavior for situations such as a battery connected directly to a capacitor
        //See #1813 and TestTheveninCapacitorRC

        boolean useWorkaround = true;
        void applySolution(CompanionMNA.CompanionSolution sol) {
            long elapsedSinceWorkaround = System.currentTimeMillis() - lastTimeWorkaroundApplied;

            if (elapsedSinceWorkaround >= 0 && useWorkaround) {

                double oldCurrent = b.getCurrent();
                double newCurrent = sol.getCurrent(getElement());

                double oldVoltage = b.getVoltageDrop();
                double newVoltage = sol.getVoltage(getElement());

                double avgCurrent = (oldCurrent + newCurrent) / 2.0;
                double avgVoltage = (oldVoltage + newVoltage) / 2.0;

                if (!signsMatch(oldCurrent, newCurrent) || !signsMatch(oldVoltage, newVoltage)) {
//                    System.out.println("workaround at "+ System.currentTimeMillis());
                    getComponent().setCurrent(avgCurrent);
                    getComponent().setVoltageDrop(avgVoltage);
                } else {
                    getComponent().setCurrent(newCurrent);
                    getComponent().setVoltageDrop(newVoltage);
                }
                lastTimeWorkaroundApplied = System.currentTimeMillis();
            } else {
                AdapterUtil.applySolution(sol, this);
            }


        }
    }

    static class InductorAdapter extends CompanionMNA.Inductor implements Adapter {
        Circuit c;
        Inductor b;

        InductorAdapter(Circuit c, Inductor b) {
            super(c.indexOf(b.getStartJunction()), c.indexOf(b.getEndJunction()), b.getInductance(), b.getVoltageDrop(), b.getCurrent());
            this.c = c;
            this.b = b;
        }

        public Branch getComponent() {
            return b;
        }

        public MNA.Element getElement() {
            return this;
        }

        void applySolution(CompanionMNA.CompanionSolution sol) {
            AdapterUtil.applySolution(sol, this);
        }
    }

    public void apply(Circuit circuit, double dt) {
        ArrayList<ResistiveBatteryAdapter> batteries = new ArrayList<ResistiveBatteryAdapter>();
        ArrayList<ResistorAdapter> resistors = new ArrayList<ResistorAdapter>();
        ArrayList<OpenAdapter> openBranches = new ArrayList<OpenAdapter>();
        ArrayList<CapacitorAdapter> capacitors = new ArrayList<CapacitorAdapter>();
        ArrayList<InductorAdapter> inductors = new ArrayList<InductorAdapter>();
        for (int i = 0; i < circuit.numBranches(); i++) {
            if (circuit.getBranches()[i] instanceof Battery) {
                batteries.add(new ResistiveBatteryAdapter(circuit, (Battery) circuit.getBranches()[i]));
            } else if (circuit.getBranches()[i] instanceof Resistor) {
                resistors.add(new ResistorAdapter(circuit, circuit.getBranches()[i]));
            } else if (circuit.getBranches()[i] instanceof Wire) {
                resistors.add(new ResistorAdapter(circuit, circuit.getBranches()[i]));
            } else if (circuit.getBranches()[i] instanceof Filament) {
                resistors.add(new ResistorAdapter(circuit, circuit.getBranches()[i]));
            } else if (circuit.getBranches()[i] instanceof Switch) {//todo: how to handle switch here.
                //todo: perhaps if it is open; don't add it at all, and just make sure we make its current zero afterwards
                //todo:
                Switch sw = (Switch) circuit.getBranches()[i];
                if (sw.isClosed()) {
                    resistors.add(new ResistorAdapter(circuit, circuit.getBranches()[i]));
                } else {
                    openBranches.add(new OpenAdapter(circuit, circuit.getBranches()[i]));
                }
            } else if (circuit.getBranches()[i] instanceof Bulb) {
                resistors.add(new ResistorAdapter(circuit, circuit.getBranches()[i]));
            } else if (circuit.getBranches()[i] instanceof SeriesAmmeter) {
                resistors.add(new ResistorAdapter(circuit, circuit.getBranches()[i]));
            } else if (circuit.getBranches()[i] instanceof Capacitor) {
                capacitors.add(new CapacitorAdapter(circuit, (Capacitor) circuit.getBranches()[i]));
            } else if (circuit.getBranches()[i] instanceof Inductor) {
                inductors.add(new InductorAdapter(circuit, (Inductor) circuit.getBranches()[i]));
            } else {
                new RuntimeException("Type not found: " + circuit.getBranches()[i]).printStackTrace();
            }

        }
        CompanionMNA.FullCircuit circ = new CompanionMNA.FullCircuit(batteries, resistors, capacitors, inductors);

//        double dynamicDT = getDT(circ,dt);
        double dynamicDT = dt;
//        System.out.println("dynamicDT = " + dynamicDT);
//        int numTimeSteps = (int) (dt/dynamicDT);
//        System.out.println("numTimeSteps = " + numTimeSteps);
        CompanionMNA.CompanionSolution solution = circ.solve(dynamicDT);
//        for (int i = 0; i < numTimeSteps; i++) {
//            solution = solution.fullCircuit.solve(dynamicDT);
//        }
        for (ResistiveBatteryAdapter batteryAdapter : batteries) {
            batteryAdapter.applySolution(solution);
        }
        for (ResistorAdapter resistorAdapter : resistors) {
            resistorAdapter.applySolution(solution);
        }
        for (CapacitorAdapter capacitorAdapter : capacitors) {
            capacitorAdapter.applySolution(solution);
        }
        for (InductorAdapter inductorAdapter : inductors) {
            inductorAdapter.applySolution(solution);
        }
        for (OpenAdapter openAdapter : openBranches) {
            openAdapter.applySolution(solution);
        }
        circuit.setSolution(solution);
        fireCircuitSolved();
    }

    private double getDT(CompanionMNA.FullCircuit circ, double dt) {
        CompanionMNA.CompanionSolution a = circ.solve(dt);
        CompanionMNA.CompanionSolution b1 = circ.solve(dt / 2);
        CompanionMNA.CompanionSolution b2 = b1.fullCircuit.solve(dt / 2);

        double distance = b2.distance(a);
        System.out.println("dt = " + dt + " => " + distance);
        if (distance < 1E-2) return dt;
        else return getDT(circ, dt / 2);
    }
}