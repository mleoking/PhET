package edu.colorado.phet.circuitconstructionkit.model.mna2;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.HashMap;

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
            ResultCache resultCache = new ResultCache(vBattery, rResistor, c);//to prevent recomputation of updates
            double dt = getTimestep(vBattery, rResistor, c, state, state.dt, resultCache);
            if (dt + elapsed > totalDT) dt = totalDT - elapsed;//don't overshoot the specified total
            state = resultCache.update(state, dt);
            elapsed = elapsed + dt;
//            System.out.println("picked dt = "+dt);
        }
        return state;
    }

    private static double getTimestep(double vBattery, double rResistor, double c, State state, double dt, ResultCache resultCache) {
        //store the previously used DT and try it first, then to increase it when possible.
        if (errorAcceptable(vBattery, rResistor, c, state, dt * 2, resultCache))
            return dt * 2;//only increase by one factor; if this exceeds the totalDT, it will be cropped later
        else if (errorAcceptable(vBattery, rResistor, c, state, dt, resultCache)) return dt * 2;
        else return getTimestep(vBattery, rResistor, c, state, dt / 2, resultCache);
    }

    private static boolean errorAcceptable(double vBattery, double rResistor, double c, State state, double dt, ResultCache cache) {
        if (dt<1E-6) return true;
        State a = cache.update(state, dt);
        State b1 = cache.update(state, dt / 2);
        State b2 = cache.update(b1, dt / 2);
        boolean errorAcceptable = a.distance(b2) < 1E-7;
        return errorAcceptable;
    }

    private static State update(double vBattery, double rResistor, double c, State state, double dt) {
        //TRAPEZOIDAL
        double vc = state.v + dt / 2 / c * state.i;
        double rc = dt / 2 / c;

        //BACKWARD EULER
//        double vc = state.v;
//        double rc = dt / c;

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

    static class Key {
        State state;
        double dt;

        Key(double dt, State state) {
            this.dt = dt;
            this.state = state;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            Key key = (Key) o;

            if (Double.compare(key.dt, dt) != 0) return false;
            if (state != null ? !state.equals(key.state) : key.state != null) return false;

            return true;
        }

        @Override
        public int hashCode() {
            int result;
            long temp;
            result = state != null ? state.hashCode() : 0;
            temp = dt != +0.0d ? Double.doubleToLongBits(dt) : 0L;
            result = 31 * result + (int) (temp ^ (temp >>> 32));
            return result;
        }
    }

    //TODO: Reuse the computations of update between error checks, and return one of the intermediate states instead of recomputing once dt has been accepted.
    static class ResultCache {
        private double vBattery;
        private double rResistor;
        private double c;
        private HashMap<Key, State> cache = new HashMap<Key, State>();

        public ResultCache(double vBattery, double rResistor, double c) {
            this.vBattery = vBattery;
            this.rResistor = rResistor;
            this.c = c;
        }

        public State update(State state, double dt) {
            Key key = new Key(dt, state);
            if (cache.containsKey(key)) {
//                System.out.println("Cache hit");
                return cache.get(key);
            } else {
//                System.out.println("Cache miss");
                State result = TestTheveninCapacitorRC.update(vBattery, rResistor, c, state, dt);
                cache.put(key, result);
                return result;
            }
        }
    }
}
