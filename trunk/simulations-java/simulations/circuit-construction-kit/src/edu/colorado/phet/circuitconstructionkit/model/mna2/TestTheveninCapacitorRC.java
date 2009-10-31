package edu.colorado.phet.circuitconstructionkit.model.mna2;

import edu.colorado.phet.common.phetcommon.math.MathUtil;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
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
//            return Math.abs(state.i - i);//todo: when comparing entire circuits, important to divide by number of components
        }
    }

    public static void main(String[] args) throws IOException {
        DecimalFormat f = new DecimalFormat("0.000000000000000000");
        double vBattery = 9;
//        double rResistor = 1;
        double rResistor = 1E-6;
        double c = 0.1;
        double omega = 2 * Math.PI * 1;

        State state = new State(0, vBattery / rResistor);

//        double dt = 0.03;
        String headers = "iteration \t dt \t t \t v(t) \t i(t) \t vResistorAnalytical \t vResistorPredicted \t error";
        System.out.println(headers);

//        double[] dtArray =new double[]{1E-6,1E-6,1E-5,1E-4,1E-3,1E-2,1E-2,1E-2};
        BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(new File("C:/Users/Sam/Desktop/cck-out" + System.currentTimeMillis() + ".txt")));
        bufferedWriter.write(headers + "\n");
        double t = 0;
        double BASE_DT = 0.03;
        for (int j = 0; j <1000; j++) {

            if (j > 2000) {
//                System.out.println("");
                double x = 3;
                x = x*23;
            }
//            double dt = dtArray[j];
//            double dt = Math.min(0.03, Math.max(rResistor* c, 0.2 * state.v * c / state.i));
            double dt = getTimestep(vBattery, rResistor, c, state, BASE_DT);
//            System.out.println("Chose dt = "+dt);

//            double t = j * dt;
//            double voltageAsAFunctionOfTime = vBattery* Math.cos(t*omega);
            double voltageAsAFunctionOfTime = vBattery;

            double vResistorAnalytical = vBattery * Math.exp(-t / rResistor / c);
            double vResistorPredicted = vBattery - state.v;

            double error = Math.abs(vResistorAnalytical - vResistorPredicted);
            String str = j + "\t" + f.format(dt) + "\t" + f.format(t) + "\t" + f.format(state.v) + "\t" + f.format(state.i) + "\t" + f.format(vResistorAnalytical) + "\t" + f.format(vResistorPredicted) + "\t" + f.format(error);
            System.out.println(str);
            bufferedWriter.write(str + "\n");

            state = newState(voltageAsAFunctionOfTime, rResistor, c, state, dt);
            t = t + dt;
        }
        bufferedWriter.close();
    }

    private static double getTimestep(double vBattery, double rResistor, double c, State state, double dt) {
        return dt;
//        State a = newState(vBattery, rResistor, c, state, dt);
//
//        State b1 = newState(vBattery, rResistor, c, state, dt / 2);
//        State b2 = newState(vBattery, rResistor, c, b1, dt / 2);
//        double dist = a.distance(b2);
//        if (dist < 1E-6) return dt;
//        else return getTimestep(vBattery, rResistor, c, state, dt / 2);
    }

    static boolean signsMatch(double x,double y){
        return MathUtil.getSign(x) == MathUtil.getSign(y);
    }
    private static State newState(double vBattery, double rResistor, double c, State state, double dt) {
//        double vc = MathUtil.clamp(-9,state.v + dt / 2 / c * state.i,9);
        double vc = state.v + dt / 2 / c * state.i;
        double rc = dt / 2 / c;
//        System.out.println("vc = " + vc+", rc = "+rc);
        double newCurrent = (vBattery - vc) / (rc + rResistor);
        double newVoltage = vBattery - newCurrent * rResistor;//signs may be wrong here
        double avgCurrent = (newCurrent*0.9 + state.i*0.1);
        double avgVolts = (newVoltage*0.9 + state.v*0.1);

//        if (!signsMatch(newVoltage,state.v) || !signsMatch(newCurrent,state.i)){
            return new State(avgVolts, avgCurrent);
//        }
//        else
//        return new State(newVoltage, newCurrent);
    }

//    private static double getTimestep(double v, double i) {
//        return 0;
//    }
}
