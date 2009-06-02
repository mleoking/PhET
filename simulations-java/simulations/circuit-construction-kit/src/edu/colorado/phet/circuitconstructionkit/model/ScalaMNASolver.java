package edu.colorado.phet.circuitconstructionkit.model;

import edu.colorado.phet.cckscala.tests.*;
import edu.colorado.phet.circuitconstructionkit.model.analysis.CircuitSolver;
import edu.colorado.phet.circuitconstructionkit.model.components.Wire;

import java.util.ArrayList;

public class ScalaMNASolver extends CircuitSolver {
    class BatteryAdapter extends Battery {
        private final edu.colorado.phet.circuitconstructionkit.model.components.Battery b;

        BatteryAdapter(Circuit circuit, edu.colorado.phet.circuitconstructionkit.model.components.Battery b) {
            super(circuit.indexOf(b.getStartJunction()), circuit.indexOf(b.getEndJunction()), b.getVoltageDrop());
            this.b = b;
        }
    }

    class WireAdapter extends Resistor {
        Wire wire;

        WireAdapter(Circuit circuit, Wire w) {
            super(circuit.indexOf(w.getStartJunction()), circuit.indexOf(w.getEndJunction()), w.getResistance());
            this.wire = w;
        }
    }

    class ResistorAdapter extends Resistor {
        edu.colorado.phet.circuitconstructionkit.model.components.Resistor resistor;

        ResistorAdapter(Circuit circuit, edu.colorado.phet.circuitconstructionkit.model.components.Resistor resistor) {
            super(circuit.indexOf(resistor.getStartJunction()), circuit.indexOf(resistor.getEndJunction()), resistor.getResistance());
            this.resistor = resistor;
        }
    }

    class CapacitorAdapter extends Capacitor {
        edu.colorado.phet.circuitconstructionkit.model.components.Capacitor capacitor;

        CapacitorAdapter(Circuit circuit, edu.colorado.phet.circuitconstructionkit.model.components.Capacitor capacitor) {
            super(circuit.indexOf(capacitor.getStartJunction()), circuit.indexOf(capacitor.getEndJunction()), capacitor.getCapacitance(),capacitor.getVoltageDrop(),capacitor.getCurrent());
            this.capacitor= capacitor;
        }
    }


    public void apply(Circuit circuit, double dt) {
        ArrayList<Battery> batteries = new ArrayList<Battery>();
        ArrayList<Resistor> resistors = new ArrayList<Resistor>();
        ArrayList<Capacitor> capacitors= new ArrayList<Capacitor>();
        for (int i = 0; i < circuit.numBranches(); i++) {
            if (circuit.getBranches()[i] instanceof edu.colorado.phet.circuitconstructionkit.model.components.Battery) {
                edu.colorado.phet.circuitconstructionkit.model.components.Battery b = (edu.colorado.phet.circuitconstructionkit.model.components.Battery) circuit.getBranches()[i];
                batteries.add(new BatteryAdapter(circuit, b));
            }
            if (circuit.getBranches()[i] instanceof Wire) {
                Wire w = (Wire) circuit.getBranches()[i];
                resistors.add(new WireAdapter(circuit, w));
            }
            if (circuit.getBranches()[i] instanceof edu.colorado.phet.circuitconstructionkit.model.components.Resistor) {
                edu.colorado.phet.circuitconstructionkit.model.components.Resistor b = (edu.colorado.phet.circuitconstructionkit.model.components.Resistor) circuit.getBranches()[i];
                resistors.add(new ResistorAdapter(circuit, b));
            }
            if (circuit.getBranches()[i] instanceof edu.colorado.phet.circuitconstructionkit.model.components.Capacitor){
                edu.colorado.phet.circuitconstructionkit.model.components.Capacitor capacitor=(edu.colorado.phet.circuitconstructionkit.model.components.Capacitor )circuit.getBranches()[i];
                capacitors.add(new CapacitorAdapter(circuit,capacitor));
            }
        }
        FullCircuit fullCircuit = new FullCircuit(batteries, resistors, capacitors, new ArrayList<Inductor>());
        CompanionSolution solution = fullCircuit.solve(dt);
        for (Battery battery : batteries) {
            ((BatteryAdapter) battery).b.setCurrent(solution.getCurrent(battery));
            ((BatteryAdapter) battery).b.setVoltageDrop(solution.getVoltage(battery));
        }
        for (Resistor resistor : resistors) {
            if (resistor instanceof WireAdapter) {
                ((WireAdapter) resistor).wire.setCurrent(solution.getCurrent(resistor));
                ((WireAdapter) resistor).wire.setVoltageDrop(solution.getVoltage(resistor));
            } else if (resistor instanceof ResistorAdapter) {
                ((ResistorAdapter) resistor).resistor.setCurrent(solution.getCurrent(resistor));
                ((ResistorAdapter) resistor).resistor.setVoltageDrop(solution.getVoltage(resistor));
            }
        }
        for(Capacitor cap:capacitors){
            ((CapacitorAdapter)cap).capacitor.setCurrent(solution.getCurrent(cap));
            ((CapacitorAdapter)cap).capacitor.setVoltageDrop(solution.getVoltage(cap));
        }

//        solution.get
//        Circuval scalaCircuit= new Circuit(battery :: Nil, resistor :: Nil)
        fireCircuitSolved();
    }
}
