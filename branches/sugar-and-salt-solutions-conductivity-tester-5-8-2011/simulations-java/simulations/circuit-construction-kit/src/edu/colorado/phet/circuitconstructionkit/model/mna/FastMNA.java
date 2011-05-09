// Copyright 2002-2011, University of Colorado
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
 * <p/>
 * Note: The new implementation is revealing some difficulty. First, it looks like we will have to preprocess circuits
 * so that each is connected,  with one ground (instead of being able to solve multiple circuits simultaneously).
 * Second, there is a sign discrepancy between our implementation and the one at
 * http://www.swarthmore.edu/NatSci/echeeve1/Ref/mna/MNA3.html#Generating%20the%20MNA%20matrices
 * I'm not sure if it's our problem or theirs. Third, the speed savings for the passing tests (including inductor
 * tests which reach time step too small) are minimal, only about a 25% savings in either case.
 * Therefore, this may not solve the entire problem. However, the construction of the matrices is not by inspection,
 * it still involves extraneous iteration, but converting to construction by inspection may only yield a small speedup.
 * See #2272 for more details.
 *
 * @author Sam Reid
 */
public class FastMNA implements LinearCircuitSolver {
    public static void setSubmatrix(Matrix A, int row, int col, Matrix B) {
        for (int i = 0; i < B.getRowDimension(); i++) {
            for (int k = 0; k < B.getColumnDimension(); k++) {
                A.set(row + i, col + k, B.get(i, k));
            }
        }
    }

    public ISolution solve(Circuit circuit) {

        int n = circuit.getNodeCount() - 1;
        int m = circuit.getCurrentCount();//todo: make sure no 0.0 resistors; make them into 0.0 volt batteries in companion model

        Matrix A = new Matrix(n + m, n + m);
        Matrix z = new Matrix(n + m, 1);

        //See from http://www.swarthmore.edu/NatSci/echeeve1/Ref/mna/MNA3.html
        //G matrix
        Matrix G = getGMatrix(circuit);
        Matrix B = getBMatrix(circuit);
        Matrix C = B.transpose();

//        G.print(4, 4);
//        B.print(4, 4);

        //B matrix
        setSubmatrix(A, 0, 0, G);
        setSubmatrix(A, 0, n, B);
        setSubmatrix(A, n, 0, C);
        //no D matrix
//        A.setMatrix(0, n - 1, 0, n - 1, G);
//        A.setMatrix(n, n + m - 1, 0, n - 1, B);
//        A.setMatrix(0, n - 1, n, n + m - 1, C);

        for (int i = 1; i < n; i++) {
            z.set(i - 1, 0, circuit.sumIncomingCurrents(i));
        }
        for (int i = n; i < n + m; i++) {
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


//        System.out.println("A = ");
//        A.print(4, 4);
//        System.out.println("Z = ");
//        z.print(4, 4);
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
            currentMap.put(battery, -x.get(circuit.getNodeCount() + i - 1, 0));//todo: reconcile this sign error
        }
        return new LinearCircuitSolution(voltageMap, currentMap);
    }

    private Matrix getBMatrix(Circuit circuit) {
        int n = circuit.getNodeCount() - 1;
        int m = circuit.getBatteries().size();
        Matrix B = new Matrix(n, m);
        for (int i = 0; i < n; i++) {
            for (int k = 0; k < m; k++) {
                double value = 0.0;
                if (circuit.getBattery(k).getNode0() == i + 1) value = -1;
                else if (circuit.getBattery(k).getNode1() == i + 1) value = +1;//todo: check signs
//                System.out.println("i = " + i + ", k = " + k + ", value = " + value);
                B.set(i, k, value);
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
        //Swarthmore's example case 1:
        Resistor r1 = new Resistor(0, 1, 2);
        Resistor r2 = new Resistor(2, 3, 4);
        Resistor r3 = new Resistor(0, 2, 8);
        Battery v1 = new Battery(1, 2, 32);
        Battery v2 = new Battery(0, 3, 20);//todo: are these batteries oriented properly?
        Circuit circuit = new Circuit(new ArrayList<Battery>(Arrays.asList(v1, v2)), Arrays.asList(r1, r2, r3));
        ISolution solution = new FastMNA().solve(circuit);
        System.out.println("solution = " + solution);
    }
}