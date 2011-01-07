// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.circuitconstructionkit.model.mna;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;

public class TestPrototype {
    public static double euclideanDistance(double x, double y) {
        return Math.sqrt((x - y) * (x - y));
    }

    public static CapacitorState updateWithSubdivisions(final double voltage, final double resistance, final double capacitance, CapacitorState capacitorState, double dt) {
        TimestepSubdivisions.Steppable<CapacitorState> steppable = new TimestepSubdivisions.Steppable<CapacitorState>() {
            public double distance(CapacitorState a, CapacitorState b) {
                return euclideanDistance(a.current, b.current); //a.distance(b)//TODO: improve distance metric; just using current euclidean for comparison for TestCompanionModel
            }

            public CapacitorState update(CapacitorState a, double dt) {
                return TestPrototype.updateIt(voltage, resistance, capacitance, a, dt);
            }
        };
        return new TimestepSubdivisions<CapacitorState>(1E-7, 1E-8).stepInTime(capacitorState, steppable, dt);
    }

    public static CapacitorState updateIt(double voltage, double resistance, double capacitance, CapacitorState state, double dt) {
        //TRAPEZOIDAL
        double companionResistance = dt / 2 / capacitance;
        double companionBatteryVoltage = state.voltage + companionResistance * state.current;

        //    println("companion resistor resistance = "+companionResistorResistance+", companion battery voltage = "+companionBatteryVoltage)

        //BACKWARD EULER
        //    val companionBatteryVoltage = state.voltage
        //    val companionResistorResistance = dt / capacitance

        double newCurrent = (voltage - companionBatteryVoltage) / (companionResistance + resistance);
        double newVoltage = voltage - newCurrent * resistance; //signs may be wrong here

        return new CapacitorState(newVoltage, newCurrent);
    }

    //voltage and current across the capacitor
    static class CapacitorState {
        double voltage;
        double current;

        public CapacitorState(double voltage, double current) {
            this.voltage = voltage;
            this.current = current;
        }
    }

    public static void main(String[] args) throws IOException {
        DecimalFormat f = new DecimalFormat("0.000000000000000");
        double voltage = 9;
//        double resistance = 1;
        double resistance = 1E-6;
        double capacitance = 0.1;
        double time = 0.0;
        double dt = 0.03;
        CapacitorState state = new CapacitorState(0, voltage / resistance);

        String headers = "iteration \t dt \t t \t v(t) \t i(t) \t vTrue \t vNumerical \t error";
        System.out.println(headers);

        BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(new File("C:/Users/Owner/Desktop/cck-out" + System.currentTimeMillis() + ".txt")));
        bufferedWriter.write(headers + "\n");

        for (int j = 0; j < 15; j++) {
            double vTrue = voltage * Math.exp(-time / resistance / capacitance);
            double vNumeric = voltage - state.voltage;

            double error = Math.abs(vTrue - vNumeric);
            String str = j + "\t" + f.format(dt) + "\t" + f.format(time) + "\t" + f.format(state.voltage) + "\t" + f.format(state.current) + "\t" + f.format(vTrue) + "\t" + f.format(vNumeric) + "\t" + f.format(error);
            System.out.println(str);
            bufferedWriter.write(str + "\n");

            state = updateWithSubdivisions(voltage, resistance, capacitance, state, dt);
            time = time + dt;
        }
        bufferedWriter.close();
    }
}