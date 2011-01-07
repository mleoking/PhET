// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.circuitconstructionkit.model.mna;

import edu.colorado.phet.circuitconstructionkit.model.Circuit;
import edu.colorado.phet.circuitconstructionkit.model.analysis.CircuitSolver;
import edu.colorado.phet.circuitconstructionkit.model.components.*;

import java.util.ArrayList;

/**
 * This adapter class converts the CCK branch classes to the corresponding MNA variants,
 * solves the MNA system and applies the solution back to the original CCK components.
 *
 * @author Sam Reid
 */
public class MNAAdapter extends CircuitSolver {
    private double errorThreshold = 1E-5;
    private double minDT = 1E-5;

    static class ResistiveBatteryAdapter extends DynamicCircuit.ResistiveBattery {
        Battery battery;

        ResistiveBatteryAdapter(Circuit c, Battery battery) {
            super(c.indexOf(battery.getStartJunction()), c.indexOf(battery.getEndJunction()), battery.getVoltageDrop(), battery.getResistance());
            this.battery = battery;
        }

        void applySolution(CircuitResult result) {
            //don't set voltage on the battery; that actually changes its nominal voltage
            battery.setMNACurrent(result.getInstantaneousCurrent(this));
            battery.setCurrent(result.getTimeAverageCurrent(this));
        }
    }

    static class ResistorAdapter extends LinearCircuitSolver.Resistor {
        Branch resistor;

        ResistorAdapter(Circuit c, Branch resistor) {
            super(c.indexOf(resistor.getStartJunction()), c.indexOf(resistor.getEndJunction()), resistor.getResistance());
            this.resistor = resistor;
        }

        void applySolution(CircuitResult solution) {
            resistor.setCurrent(solution.getTimeAverageCurrent(this));
            resistor.setVoltageDrop(solution.getTimeAverageVoltage(this));//use average since it doesn't feed back in to the MNA solution
            resistor.setMNACurrent(solution.getInstantaneousCurrent(this));
        }
    }

    static class CapacitorAdapter extends DynamicCircuit.DynamicCapacitor {
        private Capacitor _capacitor;

        CapacitorAdapter(Circuit c, Capacitor capacitor) {
            super(new DynamicCircuit.Capacitor(c.indexOf(capacitor.getStartJunction()), c.indexOf(capacitor.getEndJunction()), capacitor.getCapacitance()),
                    new DynamicCircuit.DynamicElementState(capacitor.getMNAVoltageDrop(), capacitor.getMNACurrent()));
            this._capacitor = capacitor;
        }

        void applySolution(CircuitResult solution) {
            _capacitor.setCurrent(solution.getTimeAverageCurrent(capacitor));
            _capacitor.setMNACurrent(solution.getInstantaneousCurrent(capacitor));
            _capacitor.setVoltageDrop(solution.getTimeAverageVoltage(capacitor));
            _capacitor.setMNAVoltageDrop(solution.getInstantaneousVoltage(capacitor));
        }
    }

    static class InductorAdapter extends DynamicCircuit.DynamicInductor {
        Inductor inductor;

        InductorAdapter(Circuit c, Inductor inductor) {
            super(new DynamicCircuit.Inductor(c.indexOf(inductor.getStartJunction()), c.indexOf(inductor.getEndJunction()), inductor.getInductance()),
                    new DynamicCircuit.DynamicElementState(inductor.getMNAVoltageDrop(), -inductor.getMNACurrent()));//todo: sign error
            this.inductor = inductor;
        }

        void applySolution(CircuitResult sol) {
            inductor.setCurrent(-sol.getTimeAverageCurrent(getInductor()));//todo: sign error
            inductor.setMNACurrent(-sol.getInstantaneousCurrent(getInductor()));
            inductor.setVoltageDrop(sol.getTimeAverageVoltage(getInductor()));
            inductor.setMNAVoltageDrop(sol.getInstantaneousVoltage(getInductor()));
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
                new ArrayList<DynamicCircuit.DynamicCapacitor>(capacitors), new ArrayList<DynamicCircuit.DynamicInductor>(inductors), new ObjectOrientedMNA());

        CircuitResult results = dynamicCircuit.solveWithSudbivisions(new TimestepSubdivisions<DynamicCircuit.DynamicState>(errorThreshold, minDT), dt);
        for (ResistiveBatteryAdapter batteryAdapter : batteries) batteryAdapter.applySolution(results);
        for (ResistorAdapter resistorAdapter : resistors) resistorAdapter.applySolution(results);
        for (CapacitorAdapter capacitorAdapter : capacitors) capacitorAdapter.applySolution(results);
        for (InductorAdapter inductorAdapter : inductors) inductorAdapter.applySolution(results);

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
        circuit.setSolution(results);
        fireCircuitSolved();
    }

    public double getErrorThreshold() {
        return errorThreshold;
    }

    public double getMinDT() {
        return minDT;
    }

    public void setErrorThreshold(double errorThreshold) {
        this.errorThreshold = errorThreshold;
    }

    public void setMinDT(double minDT) {
        this.minDT = minDT;
    }

    /**
     * This class represents the solution obtained by a timestep-subdivision-oriented MNA solve with companion models.
     * The distinction between instantaneous and average currents/voltages is made because we need to maintain the correct dynamics
     * (using instantantaneous solutions) but also to show intermediate states (using the average results), see #2270.
     */
    public static class CircuitResult {
        private ResultSet<DynamicCircuit.DynamicState> resultSet;

        public CircuitResult(ResultSet<DynamicCircuit.DynamicState> resultSet) {
            this.resultSet = resultSet;
        }

        public double getTimeAverageCurrent(LinearCircuitSolver.Element element) {
            double weightedSum = 0.0;
            for (ResultSet.State<DynamicCircuit.DynamicState> state : resultSet) {
                weightedSum += state.state.getSolution().getCurrent(element) * state.dt;//todo: make sure this is right
            }
            return weightedSum / resultSet.getTotalTime();
        }

        public double getInstantaneousCurrent(LinearCircuitSolver.Element element) {
            return getFinalState().getSolution().getCurrent(element);
        }

        public double getTimeAverageVoltage(LinearCircuitSolver.Element element) {
            double weightedSum = 0.0;
            for (ResultSet.State<DynamicCircuit.DynamicState> state : resultSet) {
                weightedSum += state.state.getSolution().getVoltage(element) * state.dt;//todo: make sure this is right
            }
            return weightedSum / resultSet.getTotalTime();
        }

        public double getInstantaneousVoltage(LinearCircuitSolver.Element element) {
            return getFinalState().getSolution().getVoltage(element);
        }

        public DynamicCircuit.DynamicState getFinalState() {
            return resultSet.getFinalState();
        }

        public double getInstantaneousNodeVoltage(int node) {
            return getFinalState().getSolution().getNodeVoltage(node);
        }

        public double getAverageNodeVoltage(int node) {
            double weightedSum = 0.0;
            for (ResultSet.State<DynamicCircuit.DynamicState> state : resultSet) {
                weightedSum += state.state.getSolution().getNodeVoltage(node) * state.dt;//todo: make sure this is right too
            }
            return weightedSum / resultSet.getTotalTime();
        }
    }
}