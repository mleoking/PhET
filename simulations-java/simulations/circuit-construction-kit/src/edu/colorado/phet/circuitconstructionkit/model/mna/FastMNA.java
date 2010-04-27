package edu.colorado.phet.circuitconstructionkit.model.mna;

import Jama.Matrix;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

/**
 * This implementation of the LinearCircuitSolver is meant to run faster than the ObjectOrientedMNA
 * (at the expense of readability), while providing the same functionality.
 * <p/>
 * See Najm and http://www.swarthmore.edu/NatSci/echeeve1/Ref/mna/MNA3.html
 *
 * @author Sam Reid
 */
public class FastMNA implements LinearCircuitSolver {
    public ISolution solve(Circuit circuit) {

        int n = circuit.getNodeCount() - 1;
        int m = circuit.getCurrentCount();//todo: make sure no 0.0 resistors; make them into 0.0 volt batteries in companion model

        Matrix A = new Matrix(n + m, n + m);
        Matrix z = new Matrix(n + m, 1);

        //See from http://www.swarthmore.edu/NatSci/echeeve1/Ref/mna/MNA3.html
        //G matrix
        Matrix G = getGMatrix(circuit);
        G.print(4, 4);
        Matrix B = getBMatrix(circuit);
        Matrix C = B.transpose();

        //B matrix
        A.setMatrix(0, n, 0, n, G);
        A.setMatrix(n, n + m, 0, n, B);
        A.setMatrix(0, n, n, n + m, C);

        for (int i = 1; i < n; i++) {
            z.set(i - 1, 0, circuit.sumIncomingCurrents(i));
        }
        for (int i = n; i < m + 1; i++) {
            z.set(i, 0, circuit.getBattery(i - n).voltage);
        }
//        final ArrayList<Unknown> unknowns = getUnknowns();//store the unknown list for index lookup
//        for (int i = 0; i < equations.size(); i++) {
//            equations.get(i).stamp(i, A, z, new IndexMap() {
//                public int getIndex(Unknown unknown) {
//                    return unknowns.indexOf(unknown);//todo: this step could be sped up
//                }
//            });
//        }

        Matrix x = A.solve(z);

        //todo: could provide more efficient lookup
        HashMap<Integer, Double> voltageMap = new HashMap<Integer, Double>();
        voltageMap.put(0, 0.0);//ground
        for (int i = 1; i < circuit.getNodeCount(); i++) {
            voltageMap.put(i, x.get(i - 1, 0));
        }

        HashMap<Element, Double> currentMap = new HashMap<Element, Double>();
        for (int i = 0; i < circuit.getBatteries().size(); i++) {
            Battery battery = circuit.getBattery(i);
            currentMap.put(battery, x.get(circuit.getNodeCount() + i, 0));
        }
        return new LinearCircuitSolution(voltageMap, currentMap);
    }

    private Matrix getBMatrix(Circuit circuit) {
        int n = circuit.getNodeCount() - 1;
        int m = circuit.getCurrentCount();
        Matrix B = new Matrix(n, m);
        for (int i = 0; i < n; i++) {
            for (int k = 0; k < m; k++) {
                if (circuit.getBattery(i).getNode0() == k) B.set(i, k, +1);
                else if (circuit.getBattery(i).getNode1() == k) B.set(i, k, -1);//todo: check signs
            }
        }
        return B;
    }

    private Matrix getGMatrix(Circuit circuit) {
        int n = circuit.getNodeCount() - 1;
        Matrix G = new Matrix(n, n);
        for (int i = 1; i < circuit.getNodeCount(); i++) {
            G.set(i - 1, i - 1, circuit.sumConductances(i));
        }
        for (int i = 1; i < circuit.getNodeCount(); i++) {
            for (int k = 1; k < circuit.getNodeCount(); k++) {
                if (i != k) {
                    G.set(i - 1, k - 1, -circuit.getConductance(i, k));
                }
            }
        }
        return G;
    }

    public static void main(String[] args) {
        //Swarthmore's example:
        Resistor r1 = new Resistor(0, 1, 2);
        Resistor r2 = new Resistor(2, 3, 4);
        Resistor r3 = new Resistor(0, 2, 8);
        Battery v1 = new Battery(1, 2, 32);
        Battery v2 = new Battery(3, 0, 20);
        Circuit circuit = new Circuit(new ArrayList<Battery>(Arrays.asList(v1, v2)), Arrays.asList(r1, r2, r3));
        ISolution solution = new FastMNA().solve(circuit);
        System.out.println("solution = " + solution);
    }
}