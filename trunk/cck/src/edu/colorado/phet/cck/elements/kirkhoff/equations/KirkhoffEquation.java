package edu.colorado.phet.cck.elements.kirkhoff.equations;

/**
 * Represents numBranches values of current, then numBranches values of voltage, then the right-hand-side.
 * The matrix will be:
 * Ax=B
 * where A is a big matrix with the left side representing current coefficients and the right side representing voltage coefficients.
 * B is the right-hand-side vector (usually zero.)
 * and x is the row vector of currents first, then voltages.
 */
public class KirkhoffEquation {
    double[] data;
    int numBranches;

    public String toString() {
        String current = "<current coeffs=[";
        for (int i = 0; i < numBranches; i++) {
            current += "" + data[i];
            if (i < numBranches - 1)
                current += ", ";
        }
        current += "], voltage coeffs=[";
        for (int i = 0; i < numBranches; i++) {
            current += "" + data[i + numBranches];
            if (i < numBranches - 1)
                current += ", ";
        }
        current += "], RHS=" + data[data.length - 1] + " >";
        return current;
    }

    public double getRHS() {
        return data[data.length - 1];
    }

    public KirkhoffEquation(int numBranches) {
        this.data = new double[numBranches * 2 + 1];
        this.numBranches = numBranches;
    }

    public void setCurrentCoefficient(int index, double value) {
        setEntry(index, value);
    }

    public void setVoltageCoefficient(int index, double value) {
        setEntry(index + numBranches, value);
    }

    public void setRHS(double value) {
        setEntry(data.length - 1, value);
    }

    private void setEntry(int index, double value) {
        data[index] = value;
    }

    public void addRHS(double v) {
        data[data.length - 1] += v;
    }

    public int getColumnCount() {
        return data.length;
    }

    public static double[][] toMatrix(KirkhoffEquation[] r) {
        int rows = r.length;
        if (rows == 0)
            return new double[0][0];
        int cols = r[0].data.length;
        double[][] data = new double[rows][cols];
        for (int i = 0; i < data.length; i++) {
            for (int k = 0; k < data[0].length; k++) {
                data[i][k] = r[i].data[k];
            }
        }
        return data;
    }

    public static void print(KirkhoffEquation[] system) {

        System.out.println("Got " + system.length + " equations.");
        for (int i = 0; i < system.length; i++) {
            KirkhoffEquation kirkhoffEquation = system[i];
            System.out.println("kirkhoffEquation[" + i + "] = " + kirkhoffEquation);
        }
    }

    public static double[][] toJamaSquareMatrixPad(KirkhoffEquation[] r) {
        int rows = r.length;
        if (rows == 0)
            return new double[0][0];
        int cols = r[0].data.length - 1;
        double[][] data = new double[rows + 1][cols];
        for (int i = 0; i < rows; i++) {
            for (int k = 0; k < cols; k++) {
                data[i][k] = r[i].data[k];
            }
        }
        for (int i = 0; i < cols; i++) {
            data[rows][i] = 0;
        }
        return data;
    }

    public static double[][] toJamaSquareMatrix(KirkhoffEquation[] r) {
        int rows = r.length;
        if (rows == 0)
            return new double[0][0];
        int cols = r[0].data.length - 1;
        double[][] data = new double[rows][cols];
        for (int i = 0; i < data.length; i++) {
            for (int k = 0; k < cols; k++) {
                data[i][k] = r[i].data[k];
            }
        }
        return data;
    }

    public static double[][] toJamaRHSMatrix(KirkhoffEquation[] ke) {
        int rows = ke.length;
        double[][] data = new double[rows][1];
        for (int i = 0; i < rows; i++) {
            data[i][0] = ke[i].data[i];
        }
        return data;
    }

    public static double[][] toJamaRHSMatrixPad(KirkhoffEquation[] ke) {
        int rows = ke.length;
        double[][] data = new double[rows + 1][1];
        for (int i = 0; i < rows; i++) {
            data[i][0] = ke[i].data[i];
        }
        data[rows][0] = 0;
        return data;
    }
}
