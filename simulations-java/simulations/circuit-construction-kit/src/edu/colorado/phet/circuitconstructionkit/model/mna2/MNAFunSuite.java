package edu.colorado.phet.circuitconstructionkit.model.mna2;

import junit.framework.TestCase;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class MNAFunSuite extends TestCase {
    public void test_battery_resistor_circuit_should_have_correct_voltages_and_currents_for_a_simple_circuit() {
        MNA.Battery battery = new MNA.Battery(0, 1, 4.0);
        MNA.Circuit circuit = new MNA.Circuit(Arrays.asList(battery), Arrays.asList(new MNA.Resistor(1, 0, 4)));
        HashMap<Integer, Double> voltageMap = new HashMap<Integer, Double>();
        voltageMap.put(0, 0.0);
        voltageMap.put(1, 4.0);

        HashMap<MNA.Element, Double> currentMap = new HashMap<MNA.Element, Double>();
        currentMap.put(battery, 1.0);
        MNA.Solution desiredSolution = new MNA.Solution(voltageMap, currentMap);
        assertTrue(circuit.solve().approxEquals(desiredSolution));
    }

    boolean approxEquals(double a, double b) {
        return Math.abs(a - b) <= 1E-6;
    }

    public void test_battery_resistor_circuit_should_have_correct_voltages_and_currents_for_a_simple_circuit_ii() {
        MNA.Battery battery = new MNA.Battery(0, 1, 4.0);
        MNA.Circuit circuit = new MNA.Circuit(Arrays.asList(battery), Arrays.asList(new MNA.Resistor(1, 0, 2)));
        HashMap<Integer, Double> voltageMap = new HashMap<Integer, Double>();
        voltageMap.put(0, 0.0);
        voltageMap.put(1, 4.0);

        HashMap<MNA.Element, Double> currentMap = new HashMap<MNA.Element, Double>();
        currentMap.put(battery, 2.0);
        MNA.Solution desiredSolution = new MNA.Solution(voltageMap, currentMap);
        assertTrue(circuit.solve().approxEquals(desiredSolution));
    }

    public void test_should_be_able_to_obtain_current_for_a_resistor() {
        MNA.Battery battery = new MNA.Battery(0, 1, 4.0);
        MNA.Resistor resistor = new MNA.Resistor(1, 0, 2);
        MNA.Circuit circuit = new MNA.Circuit(Arrays.asList(battery), Arrays.asList(resistor));
        HashMap<Integer, Double> voltageMap = new HashMap<Integer, Double>();
        voltageMap.put(0, 0.0);
        voltageMap.put(1, 4.0);

        HashMap<MNA.Element, Double> currentMap = new HashMap<MNA.Element, Double>();
        currentMap.put(battery, 2.0);
        MNA.Solution desiredSolution = new MNA.Solution(voltageMap, currentMap);
        assertTrue(circuit.solve().approxEquals(desiredSolution));
        assertTrue(approxEquals(circuit.solve().getCurrent(resistor), 2));//current through resistor should be 2.0 Amps, same magnitude as battery: positive because current flows from node 1 to 0
    }
//
    //todo: works in IDE but fails in build process with error
//   found   : Double
//
// required: scala.reflect.Manifest[?]
//
//      circuit.solve.getCurrent(Battery(4, 1, 999))
//                    ^
//  test("should throw an exception when asking for current for unknown element") {
//    val circuit = new Circuit(Battery(0, 1, 4.0) :: Nil, Resistor(1, 0, 2.0) :: Nil)
//    intercept(classOf[RuntimeException]) {
//      circuit.solve.getCurrent(Battery(4, 1, 999))
//    }
//  }

    public void test_disjoint_circuits_should_be_solved_independently() {
        MNA.Battery battery = new MNA.Battery(0, 1, 4.0);
        MNA.Battery battery2 = new MNA.Battery(2, 3, 5.0);
        MNA.Circuit circuit = new MNA.Circuit(Arrays.asList(battery, battery2), Arrays.asList(new MNA.Resistor(1, 0, 4), new MNA.Resistor(3, 2, 2)));
        HashMap<Integer, Double> voltageMap = new HashMap<Integer, Double>();
        voltageMap.put(0, 0.0);
        voltageMap.put(1, 4.0);
        voltageMap.put(2, 0.0);
        voltageMap.put(3, 5.0);

        HashMap<MNA.Element, Double> currentMap = new HashMap<MNA.Element, Double>();
        currentMap.put(battery, 1.0);
        currentMap.put(battery2, 5.0 / 2);
        MNA.Solution desiredSolution = new MNA.Solution(voltageMap, currentMap);
        assertTrue(circuit.solve().approxEquals(desiredSolution));
    }

    public void test_current_source_should_provide_current() {
        MNA.Circuit circuit = new MNA.Circuit(new ArrayList<MNA.Battery>(), Arrays.asList(new MNA.Resistor(1, 0, 4)), Arrays.asList(new MNA.CurrentSource(0, 1, 10.0)));
        HashMap<Integer, Double> voltageMap = new HashMap<Integer, Double>();
        voltageMap.put(0, 0.0);
        voltageMap.put(1, 10.0 * 4.0);

        MNA.Solution desiredSolution = new MNA.Solution(voltageMap, new HashMap<MNA.Element, Double>());
        assertTrue(circuit.solve().approxEquals(desiredSolution));
    }

    public void test_current_should_be_reversed_when_voltage_is_reversed() {
        MNA.Battery battery = new MNA.Battery(0, 1, -4);
        MNA.Circuit circuit = new MNA.Circuit(Arrays.asList(battery), Arrays.asList(new MNA.Resistor(1, 0, 2.0)));
        HashMap<Integer, Double> voltageMap = new HashMap<Integer, Double>();
        voltageMap.put(0, 0.0);
        voltageMap.put(1, -4.0);

        HashMap<MNA.Element, Double> currentMap = new HashMap<MNA.Element, Double>();
        currentMap.put(battery, -2.0);
        MNA.Solution desiredSolution = new MNA.Solution(voltageMap, currentMap);
        assertTrue(circuit.solve().approxEquals(desiredSolution));
    }

    public void test_two_batteries_in_series_should_have_voltage_added() {
        MNA.Battery battery = new MNA.Battery(0, 1, -4);
        MNA.Battery battery2 = new MNA.Battery(1, 2, -4);
        MNA.Circuit circuit = new MNA.Circuit(Arrays.asList(battery, battery2), Arrays.asList(new MNA.Resistor(2, 0, 2.0)));
        HashMap<Integer, Double> voltageMap = new HashMap<Integer, Double>();
        voltageMap.put(0, 0.0);
        voltageMap.put(1, -4.0);
        voltageMap.put(2, -8.0);

        HashMap<MNA.Element, Double> currentMap = new HashMap<MNA.Element, Double>();
        currentMap.put(battery, -4.0);
        currentMap.put(battery2, -4.0);
        MNA.Solution desiredSolution = new MNA.Solution(voltageMap, currentMap);
        assertTrue(circuit.solve().approxEquals(desiredSolution));
    }

    public void test_two_resistors_in_series_should_have_resistance_added() {
        MNA.Battery battery = new MNA.Battery(0, 1, 5.0);
        MNA.Circuit circuit = new MNA.Circuit(Arrays.asList(battery), Arrays.asList(new MNA.Resistor(1, 2, 10.0), new MNA.Resistor(2, 0, 10.0)));
        HashMap<Integer, Double> voltageMap = new HashMap<Integer, Double>();
        voltageMap.put(0, 0.0);
        voltageMap.put(1, 5.0);
        voltageMap.put(2, 2.5);

        HashMap<MNA.Element, Double> currentMap = new HashMap<MNA.Element, Double>();
        currentMap.put(battery, 5 / 20.0);
        MNA.Solution desiredSolution = new MNA.Solution(voltageMap, currentMap);
        assertTrue(circuit.solve().approxEquals(desiredSolution));
    }

    public void test_A_resistor_with_one_node_unconnected_shouldnt_cause_problems() {
        MNA.Battery battery = new MNA.Battery(0, 1, 4.0);
        MNA.Circuit circuit = new MNA.Circuit(Arrays.asList(battery), Arrays.asList(new MNA.Resistor(1, 0, 4.0), new MNA.Resistor(0, 2, 100.0)));
        HashMap<Integer, Double> voltageMap = new HashMap<Integer, Double>();
        voltageMap.put(0, 0.0);
        voltageMap.put(1, 4.0);
        voltageMap.put(2, 0.0);

        HashMap<MNA.Element, Double> currentMap = new HashMap<MNA.Element, Double>();
        currentMap.put(battery, 1.0);
        MNA.Solution desiredSolution = new MNA.Solution(voltageMap, currentMap);
        assertTrue(circuit.solve().approxEquals(desiredSolution));
    }

    public void test_an_unconnected_resistor_shouldnt_cause_problems() {
        MNA.Battery battery = new MNA.Battery(0, 1, 4.0);
        MNA.Circuit circuit = new MNA.Circuit(Arrays.asList(battery), Arrays.asList(new MNA.Resistor(1, 0, 4.0), new MNA.Resistor(2, 3, 100.0)));
        HashMap<Integer, Double> voltageMap = new HashMap<Integer, Double>();
        voltageMap.put(0, 0.0);
        voltageMap.put(1, 4.0);
        voltageMap.put(2, 0.0);
        voltageMap.put(3, 0.0);

        HashMap<MNA.Element, Double> currentMap = new HashMap<MNA.Element, Double>();
        currentMap.put(battery, 1.0);
        MNA.Solution desiredSolution = new MNA.Solution(voltageMap, currentMap);
        assertTrue(circuit.solve().approxEquals(desiredSolution));
    }

    public void test_should_handle_resistors_with_no_resistance() {
        MNA.Battery battery = new MNA.Battery(0, 1, 5.0);
        MNA.Resistor resistor = new MNA.Resistor(2, 0, 0.0);
        MNA.Circuit circuit = new MNA.Circuit(Arrays.asList(battery), Arrays.asList(new MNA.Resistor(1, 2, 10.0), resistor));
        HashMap<Integer, Double> voltageMap = new HashMap<Integer, Double>();
        voltageMap.put(0, 0.0);
        voltageMap.put(1, 5.0);
        voltageMap.put(2, 0.0);

        HashMap<MNA.Element, Double> currentMap = new HashMap<MNA.Element, Double>();
        currentMap.put(battery, 5.0 / 10);
        currentMap.put(resistor, 5.0 / 10);
        MNA.Solution desiredSolution = new MNA.Solution(voltageMap, currentMap);
        assertTrue(circuit.solve().approxEquals(desiredSolution));
    }

    public void test_resistors_in_parallel_should_have_harmonic_mean_of_resistance() {
        double V = 9.0;
        double R1 = 5.0;
        double R2 = 5.0;
        double Req = 1 / (1 / R1 + 1 / R2);
        MNA.Battery battery = new MNA.Battery(0, 1, V);
        MNA.Circuit circuit = new MNA.Circuit(Arrays.asList(battery), Arrays.asList(new MNA.Resistor(1, 0, R1), new MNA.Resistor(1, 0, R2)));
        HashMap<Integer, Double> voltageMap = new HashMap<Integer, Double>();
        voltageMap.put(0, 0.0);
        voltageMap.put(1, V);

        HashMap<MNA.Element, Double> currentMap = new HashMap<MNA.Element, Double>();
        currentMap.put(battery, V / Req);
        MNA.Solution desiredSolution = new MNA.Solution(voltageMap, currentMap);
        assertTrue(circuit.solve().approxEquals(desiredSolution));
    }

    public void testVRCCircuit(double v, double r, double c) {
        MNA.Resistor resistor = new MNA.Resistor(1, 2, r);
        CompanionMNA.FullCircuit circuit = new CompanionMNA.FullCircuit(Arrays.asList(new CompanionMNA.ResistiveBattery(0, 1, v, 0)),
                Arrays.asList(resistor), Arrays.asList(new CompanionMNA.Capacitor(2, 0, c, 0.0, 0.0)), new ArrayList<CompanionMNA.Inductor>());

        double dt = 1E-4;
        CompanionMNA.FullCircuit dynamicCircuit = circuit.getInitializedCircuit();
        for (int i = 0; i < 1000; i++) {//takes 0.3 sec on my machine
            double t = i * dt;
            CompanionMNA.CompanionSolution solutionAtTPlusDT = dynamicCircuit.solve(dt);
            double voltage = solutionAtTPlusDT.getVoltage(resistor);
            double desiredVoltageAtTPlusDT = -v * Math.exp(-(t + dt) / r / c);
            double error = Math.abs(voltage - desiredVoltageAtTPlusDT);
            assertTrue(error < 1E-6); //sample run indicates largest error is 1.5328E-7, is this acceptable?  See TestRCCircuit
            dynamicCircuit = dynamicCircuit.stepInTime(dt);
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

    public void test_RL_Circuit_should_have_correct_behavior_for_V_7_R_13_L_1Eminus4() {//todo: currently fails
        testVRLCircuit(7, 13, 1E-4);
    }

    public void testVRLCircuit(double V, double R, double L) {
        MNA.Resistor resistor = new MNA.Resistor(1, 2, R);
        CompanionMNA.FullCircuit circuit = new CompanionMNA.FullCircuit(Arrays.asList(new CompanionMNA.ResistiveBattery(0, 1, V, 0)),
                Arrays.asList(resistor), new ArrayList<CompanionMNA.Capacitor>(), Arrays.asList(new CompanionMNA.Inductor(2, 0, L, 0, 0)));
        double dt = 1E-4;
        CompanionMNA.FullCircuit dynamicCircuit = circuit.getInitializedCircuit();
        for (int i = 0; i < 1000; i++) {
            double t = i * dt;
            CompanionMNA.CompanionSolution solution = dynamicCircuit.solve(dt);
            double voltage = solution.getVoltage(resistor);
            double desiredVoltage = -V * (1 - Math.exp(-t * R / L));
            double error = Math.abs(voltage - desiredVoltage);
            assertTrue(error < 1E-6);
            dynamicCircuit = dynamicCircuit.stepInTime(dt);
        }
    }
}