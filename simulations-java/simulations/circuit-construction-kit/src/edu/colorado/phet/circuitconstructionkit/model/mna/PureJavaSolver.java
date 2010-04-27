package edu.colorado.phet.circuitconstructionkit.model.mna;

import edu.colorado.phet.circuitconstructionkit.model.Circuit;
import edu.colorado.phet.circuitconstructionkit.model.analysis.CircuitSolver;
import edu.colorado.phet.circuitconstructionkit.model.components.*;

import java.util.ArrayList;

public class PureJavaSolver extends CircuitSolver {

    static class ResistiveBatteryAdapter extends DynamicCircuit.ResistiveBattery {
        Battery battery;

        ResistiveBatteryAdapter(Circuit c, Battery battery) {
            super(c.indexOf(battery.getStartJunction()), c.indexOf(battery.getEndJunction()), battery.getVoltageDrop(), battery.getResistance());
            this.battery = battery;
        }

        //don't set voltage on the battery; that actually changes its nominal voltage
        void applySolution(DynamicCircuit.DynamicCircuitSolution solution) {
            battery.setCurrent(solution.getCurrent(this));
        }
    }

    static class ResistorAdapter extends LinearCircuitSolver.Resistor {
        Branch resistor;

        ResistorAdapter(Circuit c, Branch resistor) {
            super(c.indexOf(resistor.getStartJunction()), c.indexOf(resistor.getEndJunction()), resistor.getResistance());
            this.resistor = resistor;
        }

        void applySolution(DynamicCircuit.DynamicCircuitSolution sol) {
            resistor.setCurrent(sol.getCurrent(this));
            resistor.setVoltageDrop(sol.getVoltage(this));
        }
    }

    static class CapacitorAdapter extends DynamicCircuit.DynamicCapacitor {
        private Capacitor _capacitor;

        CapacitorAdapter(Circuit c, Capacitor capacitor) {
            super(new DynamicCircuit.Capacitor(c.indexOf(capacitor.getStartJunction()), c.indexOf(capacitor.getEndJunction()), capacitor.getCapacitance()), new DynamicCircuit.DynamicElementState(capacitor.getVoltageDrop(), capacitor.getCurrent()));
            this._capacitor = capacitor;
        }

        void applySolution(DynamicCircuit.DynamicCircuitSolution sol) {
            _capacitor.setCurrent(sol.getCurrent(capacitor));
            _capacitor.setVoltageDrop(sol.getVoltage(capacitor));
        }
    }

    static class InductorAdapter extends DynamicCircuit.DynamicInductor {
        Inductor inductor;

        InductorAdapter(Circuit c, Inductor inductor) {
            super(new DynamicCircuit.Inductor(c.indexOf(inductor.getStartJunction()), c.indexOf(inductor.getEndJunction()), inductor.getInductance()),
                    new DynamicCircuit.DynamicElementState(inductor.getVoltageDrop(), -inductor.getCurrent()));//todo: sign error
            this.inductor = inductor;
        }

        void applySolution(DynamicCircuit.DynamicCircuitSolution sol) {
            inductor.setCurrent(-sol.getCurrent(getInductor()));//todo: sign error
            inductor.setVoltageDrop(sol.getVoltage(getInductor()));
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

        DynamicCircuit dynamicCircuit = new DynamicCircuit(new ArrayList<LinearCircuitSolver.Battery>(), new ArrayList<LinearCircuitSolver.Resistor>(resistors),
                new ArrayList<LinearCircuitSolver.CurrentSource>(), new ArrayList<DynamicCircuit.ResistiveBattery>(batteries),
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