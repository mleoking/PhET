package edu.colorado.phet.circuitconstructionkit.model.mna2;

import java.text.DecimalFormat;

public class TestTheveninCapacitorRC {
    static class State {
        final double v;
        final double i;

        State(double v, double i) {
            this.v = v;
            this.i = i;
        }

        public double distance(State state) {
            return (Math.abs(state.v - v) + Math.abs(state.i - i))/2;//todo: when comparing entire circuits, important to divide by number of components
        }
    }

    public static void main(String[] args) {
        DecimalFormat f = new DecimalFormat("0.0000000000");
        double vBattery = 9;
        double rResistor = 1E-6;
        double c = 1E-1;

        State state = new State(0, vBattery / rResistor);

//        double dt = 0.03;
        System.out.println("iteration \t dt \t t \t v(t) \t i(t) \t vResistorAnalytical \t vResistorPredicted \t error");
//        double[] dtArray =new double[]{1E-6,1E-6,1E-5,1E-4,1E-3,1E-2,1E-2,1E-2};
        for (int j = 0; j < 10000; j++) {
//            double dt = dtArray[j];
            double dt = getTimestep(vBattery, rResistor, c, state, 0.03);
//            System.out.println("Chose dt = "+dt);
            double t = j * dt;
            double vResistorAnalytical = vBattery * Math.exp(-t / rResistor / c);
            double vResistorPredicted = vBattery - state.v;

            double error = Math.abs(vResistorAnalytical - vResistorPredicted);
            System.out.println(j + "\t" + f.format(dt)+"\t"+f.format(t) + "\t" + f.format(state.v) + "\t" + f.format(state.i) + "\t" + f.format(vResistorAnalytical) + "\t" + f.format(vResistorPredicted) + "\t" + f.format(error));

            state = newState(vBattery, rResistor, c, state, dt);
        }
    }

    private static double getTimestep(double vBattery, double rResistor, double c, State state, double dt) {
        State a = newState(vBattery, rResistor, c, state, dt);

        State b1 = newState(vBattery, rResistor, c, state, dt / 2);
        State b2 = newState(vBattery, rResistor, c, b1, dt / 2);
        double dist = a.distance(b2);
        if (dist < 1E-2) return dt;
        else return getTimestep(vBattery, rResistor, c, state, dt / 2);
    }

    private static State newState(double vBattery, double rResistor, double c, State state, double dt) {
        double vc = state.v + dt / 2 / c * state.i;
        double rc = dt / 2 / c;
        double newCurrent = (vBattery - vc) / (rc + rResistor);
        double newVoltage = vBattery - newCurrent * rResistor;//signs may be wrong here

        state = new State(newVoltage, newCurrent);
        return state;
    }

    private static double getTimestep(double v, double i) {
        return 0;
    }
}
