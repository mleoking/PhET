package edu.colorado.phet.cck.elements.kirkhoff;

/**
 * Created by IntelliJ IDEA.
 * User: Sam Reid
 * Date: Mar 15, 2003
 * Time: 10:56:25 AM
 * To change this template use Options | File Templates.
 */
public class GaussJordanMatrixInterpreter implements Interpreter {
    Matrix m;
    int numBranches;
    private MatrixTable mt;

    public GaussJordanMatrixInterpreter(Matrix m, int numBranches, MatrixTable mt) {
        this.m = m;
        this.numBranches = numBranches;
        this.mt = mt;
    }

    public double getCurrent(int componentIndex) {
        int index = mt.getCurrentColumn(componentIndex);
        return m.get(index, m.numColumns() - 1);
    }

    public double getVoltage(int componentIndex) {
        int index = mt.getVoltageColumn(componentIndex);
        if (index >= m.numRows())
            throw new RuntimeException("Row index out of bounds, index=" + index + ", rowCount=" + m.numRows());
        return m.get(index, m.numColumns() - 1);
    }

    public int countEntries(int row, double threshold) {
        int entryCount = 0;
        for (int i = 0; i < m.numColumns(); i++) {
            double val = m.get(row, i);
            if (Math.abs(val) > threshold) {
                entryCount++;
            }
        }
        return entryCount;
    }

    public boolean isValidSolution() {
        for (int i = 0; i < m.numRows(); i++) {
            if (countEntries(i, .001) > 2) {
                System.out.println("Invalid row=row[" + i + "]:\n" + m.rowToString(i));
                return false;
            }
        }
        return true;
    }
}
