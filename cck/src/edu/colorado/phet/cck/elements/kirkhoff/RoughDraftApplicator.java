/*Copyright, Sam Reid, 2003.*/
package edu.colorado.phet.cck.elements.kirkhoff;


import Jama.Matrix;
import edu.colorado.phet.cck.elements.ErrorGraphic;
import edu.colorado.phet.cck.elements.branch.Branch;
import edu.colorado.phet.cck.elements.branch.components.Battery;
import edu.colorado.phet.cck.elements.branch.components.HasResistance;
import edu.colorado.phet.cck.elements.circuit.Circuit;
import edu.colorado.phet.cck.elements.kirkhoff.equations.Equation;
import edu.colorado.phet.cck.util.Histogram;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.logging.Logger;

/**
 * User: Sam Reid
 * Date: Sep 3, 2003
 * Time: 2:00:13 AM
 * Copyright (c) Sep 3, 2003 by Sam Reid
 */
public class RoughDraftApplicator implements KirkhoffSolutionApplicator {
    private static boolean debugKirkhoff = false;
    private Logger logger;
    private boolean recursing;
//    private static boolean recursing = false;

    public RoughDraftApplicator(Logger logger) {
        this.logger = logger;
    }

    public void clearCircuit(Circuit c) {
        for (int i = 0; i < c.numBranches(); i++) {
            c.branchAt(i).setCurrentNoUpdate(0);
            if (!(c.branchAt(i) instanceof Battery)) {
                c.branchAt(i).setVoltageDropNoUpdate(0);
            }
        }
    }

    interface Solver {
        Jama.Matrix solve(Jama.Matrix m, Jama.Matrix rhs);

        String getName();
    }

    class JamaSolver implements Solver {
        public Matrix solve(Matrix m, Matrix rhs) {
            return m.solve(rhs);
        }

        public String getName() {
            return "Jama.Matrix.solve()";
        }

    }

    JamaSolver jama = new JamaSolver();

    class QRSolver implements Solver {
        public Matrix solve(Matrix m, Matrix rhs) {
            return m.qr().solve(rhs);
        }

        public String getName() {
            return "Jama.QR.solve()";
        }

    }

    class LUSolver implements Solver {
        public Matrix solve(Matrix m, Matrix rhs) {
            return m.lu().solve(rhs);
        }

        public String getName() {
            return "Jama.LU.solve()";
        }

    }

    class CholeskySolver implements Solver {
        public Matrix solve(Matrix m, Matrix rhs) {
            return m.chol().solve(rhs);
        }

        public String getName() {
            return "Jama.Cholesky.solve()";
        }

    }

    class GaussianSolver implements Solver {
        edu.colorado.phet.cck.elements.kirkhoff.Matrix matrix;
        MatrixTable mt;

        public GaussianSolver(edu.colorado.phet.cck.elements.kirkhoff.Matrix matrix, MatrixTable mt) {
            this.matrix = matrix;
            this.mt = mt;
        }

        public Matrix solve(Matrix m, Matrix rhs) {
            edu.colorado.phet.cck.elements.kirkhoff.Matrix solution;
            solution = matrix.gaussJord();
            return toJamaStyleSolutionColumnMatrix(solution, mt);
        }

        public String getName() {
            return "GaussianSolver";
        }
    }

    class MatrixComparator implements Histogram.EqualsComparator {
        public boolean equals(Object a, Object b) {
            Matrix m1 = (Matrix) a;
            Matrix m2 = (Matrix) b;
            if (m1.getColumnDimension() != m2.getColumnDimension())
                return false;
            if (m1.getRowDimension() != m2.getRowDimension())
                return false;
            for (int i = 0; i < m1.getRowDimension(); i++) {
                for (int k = 0; k < m1.getColumnDimension(); k++) {
                    double aval = m1.get(i, k);
                    double bval = m2.get(i, k);
                    double abs = Math.abs(aval - bval);
                    double thresh = .0000001;
                    if (abs > thresh)
                        return false;
//                    if (aval != bval)
//                        return false;
                }
            }
            return true;
        }

    }

    public void apply(Equation[] ke, CircuitGraph circuit, MatrixTable mt) {
        Circuit c = circuit.getCircuit();//Clear the circuit.
        clearCircuit(c);
        edu.colorado.phet.cck.elements.kirkhoff.Matrix m = toMatrix(ke);
//        logger.fine("------------>Started Kirkhoff Solve<--------");
        if (m == null) {
//            logger.fine("No matrix system for circuit.");
            //need to fire update since we ignored in clearCircuit.
            //could use a ZeroInterpreter.
            for (int i = 0; i < c.numBranches(); i++) {
                Branch b = c.branchAt(i);
                //O.d("Examining branch["+i+"], type="+b.getClass());
                if (b instanceof HasResistance) {
                    HasResistance hr = (HasResistance) b;
                    double res = hr.getResistance();
                    double amps = 0;
                    double volts = 0;
                    if (res == 0)
                        volts = 0;
                    else
                        volts = 0;
                    b.setCurrentAndVoltage(amps, volts);
//                    b.setVoltageDrop(volts);
//                    logger.fine("Set values for i=" + i + ", current=" + amps + ", volts=" + volts);
                } else if (b instanceof Battery) {
                    Battery batt = (Battery) b;
                    double amps = 0;
                    batt.setCurrent(amps);
//                    logger.fine("Set value for battery i=" + i + ", current=" + amps);
                } else
                    throw new RuntimeException("Type not found: " + b.getClass());

            }

            return;
        }

        Jama.Matrix jmatrix = new Jama.Matrix(Equation.toJamaLHSMatrix(ke));
        Jama.Matrix jrhs = new Jama.Matrix(Equation.toJamaRHSMatrix(ke));

//        logger.fine("Here is the equation set string:\n" + mt.getEquationSetString(jmatrix, jrhs));
        //        logger.fine(Equation.printToString(ke));
        GaussianSolver gs = new GaussianSolver(toMatrix(ke), mt);
        JamaSolver js = new JamaSolver();
        QRSolver qs = new QRSolver();
        LUSolver ls = new LUSolver();
        CholeskySolver cs = new CholeskySolver();
        ArrayList solvers = new ArrayList();
        solvers.add(js);
        solvers.add(gs);
        solvers.add(qs);
        solvers.add(ls);
        solvers.add(cs);
        ArrayList solutions = new ArrayList();
        for (int i = 0; i < solvers.size(); i++) {
            Solver s = (Solver) solvers.get(i);
            Matrix solve = solve(jmatrix, jrhs, s);
            if (solve != null)
                solutions.add(solve);
        }
        if (solutions.size() == 0) {
//            logger.fine("Jama has failed me.  No solution for this system.");
            throw new RuntimeException("No solution for this system.");
        }
        //make sense of the solutions.
        Interpreter interpreter = null;
//        Hashtable map=new Hashtable();
        Histogram map = new Histogram(new MatrixComparator());
        for (int i = 0; i < solutions.size(); i++) {
            Matrix matrix = (Matrix) solutions.get(i);
            interpreter = new ColumnVectorInterpreter(matrix, mt);
            map.add(matrix);
        }
//        logger.fine("Number of different solution entries = " + map.numKeys());
//        logger.fine("Histogram=\n" + map.toString());

        if (!interpreter.isValidSolution()) {
            ErrorGraphic.errorTextIsVisible = true;
            return;
        } else
            ErrorGraphic.errorTextIsVisible = false;
        for (int i = 0; i < c.numBranches(); i++) {
            Branch b = c.branchAt(i);
            if (circuit.isLoopElement(b)) {
                //O.d("Examining branch["+i+"], type="+b.getClass());
                if (b instanceof HasResistance) {
                    HasResistance hr = (HasResistance) b;
                    double res = hr.getResistance();
                    double amps = interpreter.getCurrent(i);
//                    b.setCurrent(amps);
                    double volts = 0;
                    if (res == 0)
                        volts = 0;
                    else
                        volts = interpreter.getVoltage(i);
                    b.setCurrentAndVoltage(amps, volts);
//                    b.setVoltageDrop(volts);
//                    logger.fine("Set values for i=" + i + ", current=" + amps + ", volts=" + volts);
                } else if (b instanceof Battery) {
                    Battery batt = (Battery) b;
                    double amps = interpreter.getCurrent(i);
                    batt.setCurrent(amps);
//                    logger.fine("Set value for battery i=" + i + ", current=" + amps);
                } else
                    throw new RuntimeException("Type not found: " + b.getClass());
            }
        }
    }

    private Matrix solve(Matrix jmatrix, Matrix jrhs, Solver solver) {
        try {
            Matrix out = solver.solve(jmatrix, jrhs);
//            logger.fine(solver.getName() + " solved, result=\n" + toFormattedString(out));
            return out;
        } catch (Exception e) {
//            logger.fine(solver.getName() + " failed on Exception= " + e.toString());
            return null;
        }
    }

    private String toFormattedString(Matrix gj) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        gj.print(pw, 5, 3);
        return sw.toString();
    }

    public void setLogger(Logger logger) {
        this.logger = logger;
    }

    private Jama.Matrix toJamaStyleSolutionColumnMatrix(edu.colorado.phet.cck.elements.kirkhoff.Matrix solution, MatrixTable table) {
        double[][] out = new double[table.getFreeParameterCount()][1];
        for (int i = 0; i < table.getFreeParameterCount(); i++) {
            out[i][0] = solution.get(i, solution.numColumns() - 1);
        }
        return new Jama.Matrix(out);
    }

    private edu.colorado.phet.cck.elements.kirkhoff.Matrix toMatrix(Equation[] ke) {
        double[][] m = Equation.toMatrix(ke);
        if (m.length == 0)
            return null;
        edu.colorado.phet.cck.elements.kirkhoff.Matrix matrix = new edu.colorado.phet.cck.elements.kirkhoff.Matrix(m.length, m[0].length, m);
        return matrix;
    }
}
