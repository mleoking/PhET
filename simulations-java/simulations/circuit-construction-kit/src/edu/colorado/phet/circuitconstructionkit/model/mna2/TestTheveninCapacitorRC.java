package edu.colorado.phet.circuitconstructionkit.model.mna2;

import java.text.DecimalFormat;

public class TestTheveninCapacitorRC {
    public static void main(String[] args) {
        DecimalFormat f=new DecimalFormat("0.0000000000");
        double vBattery = 9;
        double rResistor = 9;
        double c = 1E-2;

        double v = 0;
        double i = vBattery / rResistor;

        double dt = 1E-4;
        System.out.println("i \t t \t vResistorAnalytical \t vResistorPredicted \t error");
        for (int j = 0; j < 1000; j++) {
            double t = j * dt;
            double vResistorAnalytical = -vBattery * Math.exp(-t / rResistor / c);
            double vResistorPredicted = v - vBattery;

            double error = Math.abs(vResistorAnalytical - vResistorPredicted);
//            System.out.println(i + "\t" + t + "\t" + vt + "\t" + it + "\t" + voltageDropAcrossResistor+"\t"+predictedVoltageDropAcrossResistor);
            System.out.println(j + "\t" + f.format(t) + "\t" + f.format(vResistorAnalytical) + "\t" + f.format(vResistorPredicted)+"\t"+f.format(error));

            double rTotal = dt / 2 / c + rResistor;//total equivalent resistance
            double vTotal = vBattery - v - dt / 2 / c * i;
            double iTotal = vTotal / rTotal;

            i = iTotal;
            v = vBattery - vTotal - dt / 2 / c * i + dt / 2 / c * i;
        }
    }
}
