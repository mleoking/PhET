package edu.colorado.phet.circuitconstructionkit.model.mna2;

import java.text.DecimalFormat;

public class TestTheveninCapacitorRC {
    public static void main(String[] args) {
        DecimalFormat f = new DecimalFormat("0.0000000000");
        double vBattery = 9;
        double rResistor = 9;
        double c = 1E-2;

        double v = 0;
        double i = vBattery / rResistor;

        double dt = 1E-4;
        System.out.println("i \t t \t vResistorAnalytical \t vResistorPredicted \t error");
        for (int j = 0; j < 1000; j++) {
            double t = j * dt;
            double vResistorAnalytical = vBattery * Math.exp(-t / rResistor / c);
            double vResistorPredicted = vBattery - v;

            double error = Math.abs(vResistorAnalytical - vResistorPredicted);
            System.out.println(j + "\t" + f.format(t) + "\t" + f.format(vResistorAnalytical) + "\t" + f.format(vResistorPredicted) + "\t" + f.format(error));

            double vc = v + dt / 2 / c * i;
            double rc = dt / 2 / c;
            double newCurrent = (vBattery - vc) / (rc + rResistor);
            double newVoltage = vBattery - newCurrent * rResistor;//signs may be wrong here

            i = newCurrent;
            v = newVoltage;
        }
    }
}
