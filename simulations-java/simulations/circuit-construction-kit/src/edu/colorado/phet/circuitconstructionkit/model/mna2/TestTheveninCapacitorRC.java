package edu.colorado.phet.circuitconstructionkit.model.mna2;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;

public class TestTheveninCapacitorRC {

    public static void main(String[] args) throws IOException {
        DecimalFormat f = new DecimalFormat("0.000000000000000");
        double vBattery = 9;
//        double rResistor = 1;
        double rResistor = 1E-6;
        double c = 0.1;
        double omega = 2 * Math.PI * 1;

        double t = 0;
        double dt = 0.03;

        State state = new State(0, vBattery / rResistor, dt);

        String headers = "iteration \t dt \t t \t v(t) \t i(t) \t vTrue \t vNumerical \t error";
        System.out.println(headers);

        BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(new File("C:/Users/Sam/Desktop/cck-out" + System.currentTimeMillis() + ".txt")));
        bufferedWriter.write(headers + "\n");

        for (int j = 0; j < 15; j++) {
            double vTrue = vBattery * Math.exp(-t / rResistor / c);
            double vNumeric = vBattery - state.v;

            double error = Math.abs(vTrue - vNumeric);
            String str = j + "\t" + f.format(dt) + "\t" + f.format(t) + "\t" + f.format(state.v) + "\t" + f.format(state.i) + "\t" + f.format(vTrue) + "\t" + f.format(vNumeric) + "\t" + f.format(error);
            System.out.println(str);
            bufferedWriter.write(str + "\n");

            state = updateWithSubdivisions(vBattery, rResistor, c, state, dt);
            t = t + dt;
        }
        bufferedWriter.close();
    }

    public static State updateWithSubdivisions(double vBattery, double rResistor, double c, State state, double totalDT) {
        double elapsed = 0.0;
        //run a number of dt's so that totalDT elapses in the end
        while (elapsed < totalDT) {
            double dt = getTimestep(vBattery, rResistor, c, state, state.dt);
            if (dt + elapsed > totalDT) dt = totalDT - elapsed;//don't overshoot the specified total
            state = update(vBattery, rResistor, c, state, dt);
            elapsed = elapsed + dt;
//            System.out.println("picked dt = "+dt);
        }
        return state;
    }

    //TODO: What about reusing the computations of newState, instead of recomputing them later once dt has been accepted?
    private static double getTimestep(double vBattery, double rResistor, double c, State state, double dt) {
        //store the previously used DT and try it first, then to increase it when possible.
        if (errorAcceptable(vBattery, rResistor, c, state, dt * 2)) return dt * 2;
        else if (errorAcceptable(vBattery, rResistor, c, state, dt)) return dt * 2;
        else return getTimestep(vBattery, rResistor, c, state, dt / 2);
    }

    private static boolean errorAcceptable(double vBattery, double rResistor, double c, State state, double dt) {
        State a = update(vBattery, rResistor, c, state, dt);
        State b1 = update(vBattery, rResistor, c, state, dt / 2);
        State b2 = update(vBattery, rResistor, c, b1, dt / 2);
        boolean errorAcceptable = a.distance(b2) < 1E-7;
        return errorAcceptable;
    }

    private static State update(double vBattery, double rResistor, double c, State state, double dt) {
        //TRAPEZOIDAL
//        double vc = state.v + dt / 2 / c * state.i;
//        double rc = dt / 2 / c;

        //BACKWARD EULER
        double vc = state.v;
        double rc = dt / c;

        double newCurrent = (vBattery - vc) / (rc + rResistor);
        double newVoltage = vBattery - newCurrent * rResistor;//signs may be wrong here

        return new State(newVoltage, newCurrent, dt);
    }

    static class State {
        final double v;
        final double i;
        final double dt;//last value used for dt

        State(double v, double i, double dt) {
            this.v = v;
            this.i = i;
            this.dt = dt;
        }

        public static double square(double x) {
            return x * x;
        }

        public double distance(State state) {
            return Math.sqrt(square(state.v - v) + square(state.i - i)) / 2;
        }
    }
}
