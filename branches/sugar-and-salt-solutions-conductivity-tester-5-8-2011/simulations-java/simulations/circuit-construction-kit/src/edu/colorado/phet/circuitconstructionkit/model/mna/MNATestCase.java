// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.circuitconstructionkit.model.mna;

import junit.framework.TestCase;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class MNATestCase extends TestCase {

    public LinearCircuitSolver.ISolution solve(LinearCircuitSolver.Circuit circuit) {
        return getSolver().solve(circuit);
    }

    //Use OOMNA by default, but allow override for testing other solvers.
    public LinearCircuitSolver getSolver() {
        return new ObjectOrientedMNA();
    }

    public void test_battery_resistor_circuit_should_have_correct_voltages_and_currents_for_a_simple_circuit() {
        LinearCircuitSolver.Battery battery = new LinearCircuitSolver.Battery(0, 1, 4.0);
        LinearCircuitSolver.Resistor resistor = new LinearCircuitSolver.Resistor(1, 0, 4);
        LinearCircuitSolver.Circuit circuit = new ObjectOrientedMNA.OOCircuit(Arrays.asList(battery), Arrays.asList(resistor));
        HashMap<Integer, Double> voltageMap = new HashMap<Integer, Double>();
        voltageMap.put(0, 0.0);
        voltageMap.put(1, 4.0);

        HashMap<LinearCircuitSolver.Element, Double> currentMap = new HashMap<LinearCircuitSolver.Element, Double>();
        currentMap.put(battery, 1.0);
        LinearCircuitSolver.ISolution desiredSolution = new LinearCircuitSolution(voltageMap, currentMap);
        LinearCircuitSolver.ISolution solution = solve(circuit);
        System.out.println("solution = " + solution);
        System.out.println("desiredSolution = " + desiredSolution);
        assertTrue(solution.approxEquals(desiredSolution));

        double currentThroughResistor = solution.getCurrent(resistor);
        assertTrue(currentThroughResistor == 1.0);//should be flowing forward through resistor
    }

    boolean approxEquals(double a, double b) {
        return Math.abs(a - b) <= 1E-6;
    }

    public void test_battery_resistor_circuit_should_have_correct_voltages_and_currents_for_a_simple_circuit_ii() {
        LinearCircuitSolver.Battery battery = new LinearCircuitSolver.Battery(0, 1, 4.0);
        LinearCircuitSolver.Circuit circuit = new ObjectOrientedMNA.OOCircuit(Arrays.asList(battery), Arrays.asList(new LinearCircuitSolver.Resistor(1, 0, 2)));
        HashMap<Integer, Double> voltageMap = new HashMap<Integer, Double>();
        voltageMap.put(0, 0.0);
        voltageMap.put(1, 4.0);

        HashMap<LinearCircuitSolver.Element, Double> currentMap = new HashMap<LinearCircuitSolver.Element, Double>();
        currentMap.put(battery, 2.0);
        LinearCircuitSolver.ISolution desiredSolution = new LinearCircuitSolution(voltageMap, currentMap);
        assertTrue(solve(circuit).approxEquals(desiredSolution));
    }

    public void test_should_be_able_to_obtain_current_for_a_resistor() {
        LinearCircuitSolver.Battery battery = new LinearCircuitSolver.Battery(0, 1, 4.0);
        LinearCircuitSolver.Resistor resistor = new LinearCircuitSolver.Resistor(1, 0, 2);
        LinearCircuitSolver.Circuit circuit = new ObjectOrientedMNA.OOCircuit(Arrays.asList(battery), Arrays.asList(resistor));
        HashMap<Integer, Double> voltageMap = new HashMap<Integer, Double>();
        voltageMap.put(0, 0.0);
        voltageMap.put(1, 4.0);

        HashMap<LinearCircuitSolver.Element, Double> currentMap = new HashMap<LinearCircuitSolver.Element, Double>();
        currentMap.put(battery, 2.0);
        LinearCircuitSolver.ISolution desiredSolution = new LinearCircuitSolution(voltageMap, currentMap);
        assertTrue(solve(circuit).approxEquals(desiredSolution));
        assertTrue(approxEquals(solve(circuit).getCurrent(resistor), 2));//current through resistor should be 2.0 Amps, same magnitude as battery: positive because current flows from node 1 to 0
    }

    public void test_disjoint_circuits_should_be_solved_independently() {
        LinearCircuitSolver.Battery battery = new LinearCircuitSolver.Battery(0, 1, 4.0);
        LinearCircuitSolver.Battery battery2 = new LinearCircuitSolver.Battery(2, 3, 5.0);
        LinearCircuitSolver.Circuit circuit = new ObjectOrientedMNA.OOCircuit(Arrays.asList(battery, battery2), Arrays.asList(new LinearCircuitSolver.Resistor(1, 0, 4), new LinearCircuitSolver.Resistor(3, 2, 2)));
        HashMap<Integer, Double> voltageMap = new HashMap<Integer, Double>();
        voltageMap.put(0, 0.0);
        voltageMap.put(1, 4.0);
        voltageMap.put(2, 0.0);
        voltageMap.put(3, 5.0);

        HashMap<LinearCircuitSolver.Element, Double> currentMap = new HashMap<LinearCircuitSolver.Element, Double>();
        currentMap.put(battery, 1.0);
        currentMap.put(battery2, 5.0 / 2);
        LinearCircuitSolver.ISolution desiredSolution = new LinearCircuitSolution(voltageMap, currentMap);
        assertTrue(solve(circuit).approxEquals(desiredSolution));
    }

    public void test_current_source_should_provide_current() {
        LinearCircuitSolver.CurrentSource current = new LinearCircuitSolver.CurrentSource(0, 1, 10.0);
        LinearCircuitSolver.Resistor resistor = new LinearCircuitSolver.Resistor(1, 0, 4);
        LinearCircuitSolver.Circuit circuit = new ObjectOrientedMNA.OOCircuit(new ArrayList<LinearCircuitSolver.Battery>(), Arrays.asList(resistor), Arrays.asList(current));
        HashMap<Integer, Double> voltageMap = new HashMap<Integer, Double>();
        voltageMap.put(0, 0.0);
        voltageMap.put(1, -10.0 * 4.0);//This is negative since traversing across the resistor should yield a negative voltage, see http://en.wikipedia.org/wiki/Current_source 

        LinearCircuitSolver.ISolution desiredSolution = new LinearCircuitSolution(voltageMap, new HashMap<LinearCircuitSolver.Element, Double>());
        System.out.println("solve(circuit) = " + solve(circuit));
        assertTrue(solve(circuit).approxEquals(desiredSolution));
    }

    public void test_current_should_be_reversed_when_voltage_is_reversed() {
        LinearCircuitSolver.Battery battery = new LinearCircuitSolver.Battery(0, 1, -4);
        LinearCircuitSolver.Circuit circuit = new ObjectOrientedMNA.OOCircuit(Arrays.asList(battery), Arrays.asList(new LinearCircuitSolver.Resistor(1, 0, 2.0)));
        HashMap<Integer, Double> voltageMap = new HashMap<Integer, Double>();
        voltageMap.put(0, 0.0);
        voltageMap.put(1, -4.0);

        HashMap<LinearCircuitSolver.Element, Double> currentMap = new HashMap<LinearCircuitSolver.Element, Double>();
        currentMap.put(battery, -2.0);
        LinearCircuitSolver.ISolution desiredSolution = new LinearCircuitSolution(voltageMap, currentMap);
        assertTrue(solve(circuit).approxEquals(desiredSolution));
    }

    public void test_two_batteries_in_series_should_have_voltage_added() {
        LinearCircuitSolver.Battery battery = new LinearCircuitSolver.Battery(0, 1, -4);
        LinearCircuitSolver.Battery battery2 = new LinearCircuitSolver.Battery(1, 2, -4);
        LinearCircuitSolver.Circuit circuit = new ObjectOrientedMNA.OOCircuit(Arrays.asList(battery, battery2), Arrays.asList(new LinearCircuitSolver.Resistor(2, 0, 2.0)));
        HashMap<Integer, Double> voltageMap = new HashMap<Integer, Double>();
        voltageMap.put(0, 0.0);
        voltageMap.put(1, -4.0);
        voltageMap.put(2, -8.0);

        HashMap<LinearCircuitSolver.Element, Double> currentMap = new HashMap<LinearCircuitSolver.Element, Double>();
        currentMap.put(battery, -4.0);
        currentMap.put(battery2, -4.0);
        LinearCircuitSolver.ISolution desiredSolution = new LinearCircuitSolution(voltageMap, currentMap);
        assertTrue(solve(circuit).approxEquals(desiredSolution));
    }

    public void test_two_resistors_in_series_should_have_resistance_added() {
        LinearCircuitSolver.Battery battery = new LinearCircuitSolver.Battery(0, 1, 5.0);
        LinearCircuitSolver.Circuit circuit = new ObjectOrientedMNA.OOCircuit(Arrays.asList(battery), Arrays.asList(new LinearCircuitSolver.Resistor(1, 2, 10.0), new LinearCircuitSolver.Resistor(2, 0, 10.0)));
        HashMap<Integer, Double> voltageMap = new HashMap<Integer, Double>();
        voltageMap.put(0, 0.0);
        voltageMap.put(1, 5.0);
        voltageMap.put(2, 2.5);

        HashMap<LinearCircuitSolver.Element, Double> currentMap = new HashMap<LinearCircuitSolver.Element, Double>();
        currentMap.put(battery, 5 / 20.0);
        LinearCircuitSolver.ISolution desiredSolution = new LinearCircuitSolution(voltageMap, currentMap);
        assertTrue(solve(circuit).approxEquals(desiredSolution));
    }

    public void test_A_resistor_with_one_node_unconnected_shouldnt_cause_problems() {
        LinearCircuitSolver.Battery battery = new LinearCircuitSolver.Battery(0, 1, 4.0);
        LinearCircuitSolver.Circuit circuit = new ObjectOrientedMNA.OOCircuit(Arrays.asList(battery), Arrays.asList(new LinearCircuitSolver.Resistor(1, 0, 4.0), new LinearCircuitSolver.Resistor(0, 2, 100.0)));
        HashMap<Integer, Double> voltageMap = new HashMap<Integer, Double>();
        voltageMap.put(0, 0.0);
        voltageMap.put(1, 4.0);
        voltageMap.put(2, 0.0);

        HashMap<LinearCircuitSolver.Element, Double> currentMap = new HashMap<LinearCircuitSolver.Element, Double>();
        currentMap.put(battery, 1.0);
        LinearCircuitSolver.ISolution desiredSolution = new LinearCircuitSolution(voltageMap, currentMap);
        assertTrue(solve(circuit).approxEquals(desiredSolution));
    }

    public void test_an_unconnected_resistor_shouldnt_cause_problems() {
        LinearCircuitSolver.Battery battery = new LinearCircuitSolver.Battery(0, 1, 4.0);
        LinearCircuitSolver.Circuit circuit = new ObjectOrientedMNA.OOCircuit(Arrays.asList(battery), Arrays.asList(new LinearCircuitSolver.Resistor(1, 0, 4.0), new LinearCircuitSolver.Resistor(2, 3, 100.0)));
        HashMap<Integer, Double> voltageMap = new HashMap<Integer, Double>();
        voltageMap.put(0, 0.0);
        voltageMap.put(1, 4.0);
        voltageMap.put(2, 0.0);
        voltageMap.put(3, 0.0);

        HashMap<LinearCircuitSolver.Element, Double> currentMap = new HashMap<LinearCircuitSolver.Element, Double>();
        currentMap.put(battery, 1.0);
        LinearCircuitSolver.ISolution desiredSolution = new LinearCircuitSolution(voltageMap, currentMap);
        assertTrue(solve(circuit).approxEquals(desiredSolution));
    }

    public void test_should_handle_resistors_with_no_resistance() {
        LinearCircuitSolver.Battery battery = new LinearCircuitSolver.Battery(0, 1, 5.0);
        LinearCircuitSolver.Resistor resistor = new LinearCircuitSolver.Resistor(2, 0, 0.0);
        LinearCircuitSolver.Circuit circuit = new ObjectOrientedMNA.OOCircuit(Arrays.asList(battery), Arrays.asList(new LinearCircuitSolver.Resistor(1, 2, 10.0), resistor));
        HashMap<Integer, Double> voltageMap = new HashMap<Integer, Double>();
        voltageMap.put(0, 0.0);
        voltageMap.put(1, 5.0);
        voltageMap.put(2, 0.0);

        HashMap<LinearCircuitSolver.Element, Double> currentMap = new HashMap<LinearCircuitSolver.Element, Double>();
        currentMap.put(battery, 5.0 / 10);
        currentMap.put(resistor, 5.0 / 10);
        LinearCircuitSolver.ISolution desiredSolution = new LinearCircuitSolution(voltageMap, currentMap);
        assertTrue(solve(circuit).approxEquals(desiredSolution));
    }

    public void test_resistors_in_parallel_should_have_harmonic_mean_of_resistance() {
        double V = 9.0;
        double R1 = 5.0;
        double R2 = 5.0;
        double Req = 1 / (1 / R1 + 1 / R2);
        LinearCircuitSolver.Battery battery = new LinearCircuitSolver.Battery(0, 1, V);
        LinearCircuitSolver.Circuit circuit = new ObjectOrientedMNA.OOCircuit(Arrays.asList(battery), Arrays.asList(new LinearCircuitSolver.Resistor(1, 0, R1), new LinearCircuitSolver.Resistor(1, 0, R2)));
        HashMap<Integer, Double> voltageMap = new HashMap<Integer, Double>();
        voltageMap.put(0, 0.0);
        voltageMap.put(1, V);

        HashMap<LinearCircuitSolver.Element, Double> currentMap = new HashMap<LinearCircuitSolver.Element, Double>();
        currentMap.put(battery, V / Req);
        LinearCircuitSolver.ISolution desiredSolution = new LinearCircuitSolution(voltageMap, currentMap);
        assertTrue(solve(circuit).approxEquals(desiredSolution));
    }

    //this is for comparison with TestTheveninCapacitorRC
    public void testVRC991Eminus2() {
        testVRCCircuit(9, 9, 1E-2);
    }

    public void testVRCCircuit(double v, double r, double c) {
        LinearCircuitSolver.Resistor resistor = new LinearCircuitSolver.Resistor(1, 2, r);
        DynamicCircuit circuit = new DynamicCircuit(new ArrayList<LinearCircuitSolver.Battery>(), Arrays.asList(resistor),
                new ArrayList<LinearCircuitSolver.CurrentSource>(), Arrays.asList(new DynamicCircuit.ResistiveBattery(0, 1, v, 0)), Arrays.asList(new DynamicCircuit.DynamicCapacitor(new DynamicCircuit.Capacitor(2, 0, c), new DynamicCircuit.DynamicElementState(0.0, v / r))),
                new ArrayList<DynamicCircuit.DynamicInductor>(), getSolver());

        double dt = 1E-4;
        System.out.println("voltage");
        System.out.println("");
        for (int i = 0; i < 1000; i++) {//takes 0.3 sec on my machine
            double t = i * dt;

            DynamicCircuit.DynamicCircuitSolution companionSolution = circuit.solveItWithSubdivisions(dt);
            double voltage = companionSolution.getVoltage(resistor);
            double desiredVoltageAtTPlusDT = -v * Math.exp(-(t + dt) / r / c);
            double error = Math.abs(voltage - desiredVoltageAtTPlusDT);
            System.out.println(-voltage);
            assertTrue(error < 1E-6); //sample run indicates largest error is 1.5328E-7, is this acceptable?  See TestRCCircuit
            circuit = circuit.updateWithSubdivisions(dt);
        }
    }

    public void test_RC_Circuit_should_have_voltage_exponentially_decay_with_T_RC_for_v_5_r_10_c_1E_minus2() {
        testVRCCircuit(5.0, 10.0, 1.0E-2);
    }

    public void test_RC_Circuit_should_have_voltage_exponentially_decay_with_T_RC_for_v_10_r_10_c_1E_minus2() {
        testVRCCircuit(10.0, 10.0, 1.0E-2);
    }

    public void test_RC_Circuit_should_have_voltage_exponentially_decay_with_T_RC_for_v_3__r_7__c_1Eminus1() {
        testVRCCircuit(3, 7, 1E-1);
    }

    public void test_RC_Circuit_should_have_voltage_exponentially_decay_with_T_RC_for_v_3__r_7__c_100() {
        testVRCCircuit(3, 7, 100);
    }

    public void test_RL_Circuit_should_have_correct_behavior_for_V_5_R_10_L_1() {
        testVRLCircuit(5, 10, 1);
    }

    public void test_RL_Circuit_should_have_correct_behavior_for_V_3_R_11_L_2_5() {
        testVRLCircuit(3, 11, 2.5);
    }

    public void test_RL_Circuit_should_have_correct_behavior_for_V_7_R_13_L_1E4() {
        testVRLCircuit(7, 13, 1E4);
    }

    public void test_RL_Circuit_should_have_correct_behavior_for_V_7_R_13_L_1Eminus4() {
        testVRLCircuit(7, 13, 1E-4);
    }

    public void testVRLCircuit(double V, double R, double L) {
        long start = System.currentTimeMillis();
        LinearCircuitSolver.Resistor resistor = new LinearCircuitSolver.Resistor(1, 2, R);
        LinearCircuitSolver.Battery battery = new LinearCircuitSolver.Battery(0, 1, V);
        DynamicCircuit circuit = new DynamicCircuit(Arrays.asList(battery), Arrays.asList(resistor),
                new ArrayList<LinearCircuitSolver.CurrentSource>(), new ArrayList<DynamicCircuit.ResistiveBattery>(), new ArrayList<DynamicCircuit.DynamicCapacitor>(),
                Arrays.asList(new DynamicCircuit.DynamicInductor(new DynamicCircuit.Inductor(2, 0, L), new DynamicCircuit.DynamicElementState(V, 0.0))), getSolver());

        double dt = 1E-4;
        for (int i = 0; i < 1000; i++) {
            double t = i * dt;
            DynamicCircuit.DynamicCircuitSolution solution = circuit.solveItWithSubdivisions(dt);
            double current = solution.getCurrent(battery);
            double expectedCurrent = V / R * (1 - Math.exp(-(t + dt) * R / L));//positive, by definition of MNA.Battery
            double error = Math.abs(current - expectedCurrent);
            assertTrue(error < 1E-4);
            circuit = circuit.updateWithSubdivisions(dt);
        }
        long end = System.currentTimeMillis();
        double elapsed = (end - start) / 1000.0;
        System.out.println("elapsed = " + elapsed);
    }
}