package edu.colorado.phet.circuitconstructionkit.model.mna;

import edu.colorado.phet.circuitconstructionkit.model.Circuit;
import edu.colorado.phet.circuitconstructionkit.model.analysis.CircuitSolver;
import edu.colorado.phet.circuitconstructionkit.model.components.*;

import java.util.ArrayList;

public class PureJavaSolver extends CircuitSolver {

    static class ResistiveBatteryAdapter extends DynamicCircuit.ResistiveBattery {
        Circuit c;
        Battery b;

        ResistiveBatteryAdapter(Circuit c, Battery b) {
            super(c.indexOf(b.getStartJunction()), c.indexOf(b.getEndJunction()), b.getVoltageDrop(), b.getResistance());
            this.c = c;
            this.b = b;
        }

        //don't set voltage on the battery; that actually changes its nominal voltage
        void applySolution(DynamicCircuit.DynamicCircuitSolution solution) {
            b.setCurrent(solution.getCurrent(this));
        }
    }

    static class ResistorAdapter extends MNA.Resistor {
        Circuit c;
        Branch b;

        ResistorAdapter(Circuit c, Branch b) {
            super(c.indexOf(b.getStartJunction()), c.indexOf(b.getEndJunction()), b.getResistance());
            this.c = c;
            this.b = b;
        }

        void applySolution(DynamicCircuit.DynamicCircuitSolution sol) {
            b.setCurrent(sol.getCurrent(this));
            b.setVoltageDrop(sol.getVoltage(this));
        }
    }

    static class CapacitorAdapter extends DynamicCircuit.DynamicCapacitor {
        Circuit c;
        Capacitor b;

        CapacitorAdapter(Circuit c, Capacitor b) {
            super(new DynamicCircuit.Capacitor(c.indexOf(b.getStartJunction()), c.indexOf(b.getEndJunction()), b.getCapacitance()), new DynamicCircuit.DynamicElementState(b.getVoltageDrop(), b.getCurrent()));
            this.c = c;
            this.b = b;
        }

        void applySolution(DynamicCircuit.DynamicCircuitSolution sol) {
            b.setCurrent(sol.getCurrent(capacitor));
            b.setVoltageDrop(sol.getVoltage(capacitor));
        }
    }

    static class InductorAdapter extends DynamicCircuit.DynamicInductor {
        Circuit c;
        Inductor b;

        InductorAdapter(Circuit c, Inductor b) {
            super(new DynamicCircuit.Inductor(c.indexOf(b.getStartJunction()), c.indexOf(b.getEndJunction()), b.getInductance()), new DynamicCircuit.DynamicElementState(b.getVoltageDrop(), -b.getCurrent()));//todo: sign error
            this.c = c;
            this.b = b;
        }

        void applySolution(DynamicCircuit.DynamicCircuitSolution sol) {
            b.setCurrent(-sol.getCurrent(getInductor()));//todo: sign error
            b.setVoltageDrop(sol.getVoltage(getInductor()));
        }
    }

    public void apply(Circuit circuit, double dt) {
        ArrayList<ResistiveBatteryAdapter> batteries = new ArrayList<ResistiveBatteryAdapter>();
        ArrayList<ResistorAdapter> resistors = new ArrayList<ResistorAdapter>();
        ArrayList<CapacitorAdapter> capacitors = new ArrayList<CapacitorAdapter>();
        ArrayList<InductorAdapter> inductors = new ArrayList<InductorAdapter>();
        for (int i = 0; i < circuit.numBranches(); i++) {
            final Branch branch = circuit.getBranches()[i];
            if (branch instanceof Battery) {
                batteries.add(new ResistiveBatteryAdapter(circuit, (Battery) branch));
            } else if (branch instanceof Resistor) {
                resistors.add(new ResistorAdapter(circuit, branch));
            } else if (branch instanceof Wire) {
                resistors.add(new ResistorAdapter(circuit, branch));
            } else if (branch instanceof Filament) {
                resistors.add(new ResistorAdapter(circuit, branch));
            } else if (branch instanceof Switch) {
                Switch sw = (Switch) branch;
                if (sw.isClosed()) {
                    resistors.add(new ResistorAdapter(circuit, sw));
                } //else do nothing, since no closed circuit there; see below where current is zeroed out
            } else if (branch instanceof Bulb) {
                resistors.add(new ResistorAdapter(circuit, branch));
            } else if (branch instanceof SeriesAmmeter) {
                resistors.add(new ResistorAdapter(circuit, branch));
            } else if (branch instanceof Capacitor) {
                capacitors.add(new CapacitorAdapter(circuit, (Capacitor) branch));
            } else if (branch instanceof Inductor) {
                inductors.add(new InductorAdapter(circuit, (Inductor) branch));
            } else {
                new RuntimeException("Type not found: " + branch).printStackTrace();
            }
        }

        DynamicCircuit dynamicCircuit = new DynamicCircuit(new ArrayList<MNA.Battery>(), new ArrayList<MNA.Resistor>(resistors),
                new ArrayList<MNA.CurrentSource>(), new ArrayList<DynamicCircuit.ResistiveBattery>(batteries),
                new ArrayList<DynamicCircuit.DynamicCapacitor>(capacitors), new ArrayList<DynamicCircuit.DynamicInductor>(inductors));

        DynamicCircuit.DynamicCircuitSolution result = dynamicCircuit.solveItWithSubdivisions(dt);
        for (ResistiveBatteryAdapter batteryAdapter : batteries) batteryAdapter.applySolution(result);
        for (ResistorAdapter resistorAdapter : resistors) resistorAdapter.applySolution(result);
        for (CapacitorAdapter capacitorAdapter : capacitors) capacitorAdapter.applySolution(result);
        for (InductorAdapter inductorAdapter : inductors) inductorAdapter.applySolution(result);

        //zero out currents on open branches
        for (int i = 0; i < circuit.numBranches(); i++) {
            if (circuit.getBranches()[i] instanceof Switch) {
                Switch sw = (Switch) circuit.getBranches()[i];
                if (!sw.isClosed()) {
                    sw.setCurrent(0.0);
                    sw.setVoltageDrop(0.0);
                }
            }
        }
        circuit.setSolution(result);
        fireCircuitSolved();
    }
}