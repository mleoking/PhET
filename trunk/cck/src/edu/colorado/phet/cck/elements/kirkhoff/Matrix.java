package edu.colorado.phet.cck.elements.kirkhoff;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

// for testing only

/**
 * *************************************************************************************
 */
public class Matrix  // encapsulates a matrix
        /*****************************************************************************************/ {
    private int rows, cols;
    private double M[][];
    //Info for gj.
    private int rank[];

    /**
     * *************************************************************************************
     */
    public Matrix(int tRows, int tCols, double T[][]) //requires dims and 2D array
            /*****************************************************************************************/ {
        M = new double[tRows][tCols];
        rows = tRows;
        cols = tCols;
        rank = new int[rows];
        for (int i = 0; i < rows; i++)
            for (int j = 0; j < cols; j++) {
                M[i][j] = T[i][j];
            }
//        cleanZeros();
    }

    private void cleanZeros() {
        for (int i = 0; i < rows; i++)
            for (int j = 0; j < cols; j++) {

                if (M[i][j] == -0.0)
                    M[i][j] = 0;
            }
    }

    /**
     * *************************************************************************************
     */
    public Matrix swapRow(int r1, int r2)
            /*****************************************************************************************/ {
        double tempRow[] = new double[rows];
        Matrix returnMatrix = new Matrix(rows, cols, M);

        if ((r1 >= rows) | (r2 >= rows) | (r1 < 0) | (r2 < 0))
            throw new ArithmeticException("srr.Matrix.swapRow: r1 or r2 not within matrix: " + r1 + "," + r2);

        tempRow = returnMatrix.M[r1];
        returnMatrix.M[r1] = returnMatrix.M[r2];
        returnMatrix.M[r2] = tempRow;

        return returnMatrix;
    }


    /**
     * *************************************************************************************
     */
    public Matrix mulRow(int r1, double scalar)
            /*****************************************************************************************/ {
        Matrix returnMatrix = new Matrix(rows, cols, M);

        if ((r1 >= rows) || (r1 < 0))
            throw new ArithmeticException("srr.Matrix.mulRow: r1 not within matrix: " + r1);

        for (int i = 0; i < cols; i++)
            returnMatrix.M[r1][i] = M[r1][i] * scalar;

        return returnMatrix;
    }


    /**
     * *************************************************************************************
     */
    public Matrix addMulRow(int r1, int r2, double scalar)
            /*****************************************************************************************/ {
        Matrix returnMatrix = new Matrix(rows, cols, M);
        Matrix tempMatrix = new Matrix(rows, cols, M);

        if ((r1 >= rows) | (r2 >= rows) | (r1 < 0) | (r2 < 0))
            throw new ArithmeticException("srr.Matrix.addMulRow: r1 or r2 not within matrix: " + r1 + "," + r2);

        tempMatrix = tempMatrix.mulRow(r1, scalar);

        for (int i = 0; i < cols; i++)
            returnMatrix.M[r2][i] = returnMatrix.M[r2][i] + tempMatrix.M[r1][i];

        return returnMatrix;
    }

    private boolean isPrettyCloseToZero(double a) {
        return Math.abs(a) < .00000000001;
//        return a==0;
    }

    double MIN = .000001;
    double MAX = 10000;

    private void clamp() {
        for (int i = 0; i < rows; i++) {
            for (int k = 0; k < cols; k++) {
                if (Math.abs(M[i][k]) < MIN)
                    M[i][k] = 0;
                if (Math.abs(M[i][k]) > MAX) {
                    if (M[i][k] < 0)
                        M[i][k] = -MAX;
                    else
                        M[i][k] = MAX;
                }
            }
        }
    }

    /**
     * *************************************************************************************
     */
    public Matrix gaussJord() {
        clamp();
        Matrix ret = new Matrix(rows, cols, M);

        for (int i = 0; i < rows; i++) {

            // first find first non-zero coefficient
            int j = 0;
            while (isPrettyCloseToZero(ret.M[i][j])) {
//                    ret.M[i][j] == (double) 0) {
                j++;
                if (j == cols)
                    break;
            }

            // if this row is all zeros just skip on to next row
            if (j == cols)
                continue;

            // get leading one
            ret = ret.mulRow(i, 1.0 / ret.M[i][j]);
//            ret.clamp();

            //get zeros above leading one
            if (i != 0)
                for (int k = i - 1; k >= 0; k--) {
                    ret = ret.addMulRow(i, k, -1.0 * ret.M[k][j]);
//                    ret.clamp();
                }

            //get zeros below leading one

            for (int k = i + 1; k < rows; k++) {
                ret = ret.addMulRow(i, k, -1.0 * ret.M[k][j]);
//                ret.clamp();
            }

        } // end for loop

        // swap rows until all-zero rows are at bottom and all
        // leading one's descend to the right

        //first assign rank according to leading ones
//        int rank[] = new int[rows];
        ret.clamp();
        for (int i = 0; i < rows; i++) {
            int j = 0;
//            while (ret.M[i][j] == (double) 0) {
            while (isPrettyCloseToZero(ret.M[i][j])) {
                j++;
                if (j == cols)
                    break;
            }
            rank[i] = j;
        }

        // then use bubble sort to put them in descending order
        int temp;
        for (int i = 0; i < rows - 1; i++) {
            for (int j = i + 1; j < rows; j++) {
                if (rank[i] > rank[j]) {
                    ret = ret.swapRow(j, i);
                    temp = rank[i];
                    rank[i] = rank[j];
                    rank[j] = temp;
                }
            }
        }
//        cleanZeros();
        //and finally
        return ret;
    }


    public String toString()
            /*****************************************************************************************/ {
        cleanZeros();
        String string = "";

        for (int i = 0; i < rows; i++) {
            for (int k = 0; k < cols; k++) {
                string += M[i][k] + "   ";
            }
            //string += M[i][cols-1] + "   ";
            /* for(int j=0; j<cols; j++){
                string += M[i][j] + "   ";
            }*/
            string += "\n";
        }

        return string;
    }

    /**
     * *************************************************************************************
     */
    public String toStringRightColumn()
            /*****************************************************************************************/ {

        String string = "";

        for (int i = 0; i < rows; i++) {
            string += M[i][cols - 1] + "   ";
            /* for(int j=0; j<cols; j++){
                string += M[i][j] + "   ";
            }*/
            string += "\n";
        }

        return string;
    }

    public static void main(String args[]) throws IOException {

        int rows;
        int cols;

        BufferedReader in = new BufferedReader(new InputStreamReader(System.in));//InputStream in=new BufferedInputStream(System.in);
        //DataInputStream in = new DataInputStream(System.in);

        System.out.print("\nPlease enter number of matrix rows: ");
        System.out.flush();
        rows = Integer.parseInt(in.readLine());
        System.out.print("\nPlease enter number of matrix cols: ");
        System.out.flush();
        cols = Integer.parseInt(in.readLine());

        double M[][] = new double[rows][cols];

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                System.out.print("Element " + i + ", " + j + "> ");
                System.out.flush();
                M[i][j] = Double.parseDouble(in.readLine());
            }
        }

        Matrix matrix = new Matrix(rows, cols, M);
        System.out.println("srr.Matrix before row reduction:\n" + matrix);
        System.out.println("srr.Matrix after row reduction:\n" + matrix.gaussJord());

    }

    public int numColumns() {
        return cols;
    }

    public int numRows() {

        return rows;
    }

    public double get(int row, int i) {
        return M[row][i];
    }

    public String rowToString(int row) {
        String string = "";
        for (int k = 0; k < cols; k++) {
            string += M[row][k] + "   ";
        }
        return string;
    }
}