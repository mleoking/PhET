package edu.colorado.phet.densityjava.model;

import Jama.Matrix;
import junit.framework.TestCase;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Arrays;

public class MatrixDynamics {
    private Element[] elements;
    private ContactGroup[] contactGroups;

    public MatrixDynamics(Element[] elements, ContactGroup[] contactGroups) {
        this.elements = elements;
        this.contactGroups = contactGroups;
    }

    public static class Element {
        private String name;//for debugging
        private double rightHandSideValue;
        private boolean isAtRest;

        public Element(String name) {
            this(name, 0, false);
        }

        public Element(String name, double rightHandSideValue, boolean atRest) {
            this.name = name;
            this.rightHandSideValue = rightHandSideValue;
            isAtRest = atRest;
        }

        public String getName() {
            return name;
        }

        public double getRightHandSideValue() {
            return rightHandSideValue;
        }

        public boolean isAtRest() {
            return isAtRest;
        }
    }

    class RowEntry {
        double[] x;
        private String description;
        double y;

        public RowEntry(String description, double y, Assignment... assignments) {
            this.description = description;
            this.y = y;
            this.x = new double[nCols()];
            for (Assignment assignment : assignments) {
                assignment.stamp(x);
            }
        }

        public int getColumnCount() {
            return x.length;
        }

        public double getMatrixValue(int k) {
            return x[k];
        }

        @Override
        public String toString() {
            String s = "";
            for (int i = 0; i < x.length; i++) {
                if (x[i] != 0) {
                    String valueString = "" + x[i] + "*";
                    if (x[i] == 1) valueString = "";
                    else if (x[i] == -1) valueString = "-";
                    s += valueString + "F[" + getSource(i) + "][" + getTarget(i) + "] +";
                }
            }
            return description + ": " + s.substring(0, s.length() - 1) + " = " + y;
        }
    }

    public double[][] solve() {
        ArrayList<RowEntry> rows = new ArrayList<RowEntry>();

        //F[i][i]=0
        for (int i = 0; i < elements.length; i++) {
            rows.add(new RowEntry("F[i][i]=0\t\t\t\t\t", 0, new Assignment(i, i, 1)));
        }
//        System.out.println("Added to " + rows.size() + " equations for F[i][i]=0");

        //F[i][k]=-F[k][i] if in contact
        for (int i = 0; i < elements.length; i++) {
            for (int k = 0; k < elements.length; k++) {
                if (isContact(i, k) && k != i) {
                    rows.add(new RowEntry("F[i][k]=-F[k][i]\t\t\t", 0, new Assignment(i, k, 1), new Assignment(k, i, 1)));
                }
            }
        }
//        System.out.println("Added to " + rows.size() + " equations for F[i][k]=-F[k][i] for contacting blocks");

        //If blocks aren't touching, both F[i][k] and F[k][i] forces should be zero
        for (int i = 0; i < elements.length; i++) {
            for (int k = 0; k < elements.length; k++) {
                if (!isContact(i, k) && k != i) {
                    rows.add(new RowEntry("F[i][k]=0\t\t\t\t", 0, new Assignment(i, k, 1)));
                    rows.add(new RowEntry("F[k][i]=0\t\t\t\t", 0, new Assignment(k, i, 1)));
                }
            }
        }
//        System.out.println("Added to " + rows.size() + " equations for F[i][k]=0 for noncontacting blocks");

        //If a block is at rest, it should have sum of forces = 0, i.e. the sum of contact forces is equal to negative sum of other forces
        for (int i = 0; i < elements.length; i++) {
            if (elements[i].isAtRest()) {
                Assignment[] assignments = new Assignment[elements.length];
                for (int j = 0; j < assignments.length; j++) {
                    assignments[j] = new Assignment(j, i, 1);
                }
                rows.add(new RowEntry("sum_k(F[k][i])=sum_ext(F)\t", -elements[i].getRightHandSideValue(), assignments));
            }
        }
//        System.out.println("Added to " + rows.size() + " equations for sum Forces = 0 for at-rest blocks");

        //Best block for debugging
//        System.out.println("#Set up system for " + elements.length + " particles:");
//        for (int i = 0; i < elements.length; i++) {
//            System.out.println(i + "=" + elements[i].getName());
//        }
//        System.out.println(toString(rows) + "\n#Finished System");

        MySystem system = toSystem(rows);

        final double[] solution = system.getSolution();
        double[][] matrixSolution = new double[elements.length][elements.length];
        for (int i = 0; i < elements.length; i++) {
            for (int k = 0; k < elements.length; k++) {
                matrixSolution[i][k] = solution[getColumn(i, k)];
            }
        }
        return matrixSolution;
    }

    private String toString(ArrayList<RowEntry> rows) {
        String s = "";
        for (int i = 0; i < rows.size(); i++) {
            RowEntry rowEntry = rows.get(i);
            s += rowEntry + "\n";
        }
        return s.substring(0, s.length() - 1);
    }

    private boolean isContact(int i, int k) {
        for (ContactGroup contactGroup : contactGroups) {
            if (contactGroup.contains(elements[i]) && contactGroup.contains(elements[k])) {
                return true;
            }
        }
        return false;  //To change body of created methods use File | Settings | File Templates.
    }

    class MySystem {
        Matrix x;
        Matrix y;

        public MySystem(Matrix x, Matrix y) {
            this.x = x;
            this.y = y;
        }

        public double[] getSolution() {
            Matrix result = x.solve(y);
            assert result.getRowDimension() == y.getRowDimension();
            assert result.getColumnDimension() == 1;
            double[] res = new double[result.getRowDimension()];
            for (int i = 0; i < result.getRowDimension(); i++) {
                res[i] = result.get(i, 0);
            }
            return res;
        }

        @Override
        public String toString() {
            final StringWriter xwriter = new StringWriter();
            x.print(new PrintWriter(xwriter), 6, 6);
            StringWriter ywriter = new StringWriter();
            y.print(new PrintWriter(ywriter), 6, 6);
            return "x=\n" + xwriter.getBuffer().toString() + "\ny=\n" + ywriter.getBuffer().toString();
        }

    }

    private MySystem toSystem(ArrayList<RowEntry> rows) {
        Matrix x = new Matrix(rows.size(), rows.get(0).getColumnCount());
        Matrix y = new Matrix(rows.size(), 1);
        for (int i = 0; i < rows.size(); i++) {
            RowEntry rowEntry = rows.get(i);
            for (int k = 0; k < rowEntry.getColumnCount(); k++) {
                x.set(i, k, rowEntry.getMatrixValue(k));
            }
            y.set(i, 0, rowEntry.y);
        }
        return new MySystem(x, y);
    }

    public int getColumn(int source, int target) {
        return target * elements.length + source;
    }

    private int getSource(int columnIndex) {
        return columnIndex % elements.length;
    }

    private int getTarget(int columnIndex) {
        return columnIndex / elements.length;
    }

    private int nCols() {
        return elements.length * elements.length;
    }

    class Assignment {
        int index;
        double value;

        private Assignment(int source, int target, double value) {
            this(getColumn(source, target), value);
        }

        private Assignment(int index, double value) {
            this.index = index;
            this.value = value;
        }

        public void stamp(double[] x) {
            x[index] = value;
        }
    }

    public static class ContactGroup {
        private Element[] elements;

        public ContactGroup(Element[] elements) {
            this.elements = elements;
        }

        public boolean contains(Element element) {
            return Arrays.asList(elements).contains(element);
        }
    }

    public static void main(String[] args) {
    }

    public static class Tests extends TestCase {

        public void testFreeFallBlock() {
            double Fg1 = 9.8;
            Element element = new Element("block", Fg1, false);
            double[][] solution = new MatrixDynamics(new Element[]{element}, new ContactGroup[0]).solve();
            assertEquals("Solution should have one entry row", 1, solution.length);
            assertEquals("Solution should have one entry column", 1, solution[0].length);
            assertEquals("F00 should be zero", 0, solution[0][0], 1E-6);
        }

        public void testStandingBlock() {
            double Fg0 = 9.8;
            double Fg1 = 123;
            Element block0 = new Element("block0", Fg0, false);
            Element block1 = new Element("block1", Fg1, true);
            ContactGroup contactGroup = new ContactGroup(new Element[]{block0, block1});
            double[][] F = new MatrixDynamics(new Element[]{block0, block1}, new ContactGroup[]{contactGroup}).solve();
            assertEquals("Solution should have 2 entry row", 2, F.length);
            assertEquals("Solution should have 2 entry column", 2, F[0].length);
            assertEquals("F00 should be zero", 0, F[0][0], 1E-6);
            assertEquals("F11 should be zero", 0, F[1][1], 1E-6);
            assertEquals("F01 should equal -F10", F[0][1], -F[1][0], 1E-6);
            assertEquals("F01 should be -Fg1", -Fg1, F[0][1], 1E-6);
        }

        public void testTwoBlockTower() {
            double Fg1 = 123;
            double Fg2 = 76;
            Element earth = new Element("earth");
            Element block1 = new Element("block1", Fg1, true);
            Element block2 = new Element("block2", Fg2, true);
            ContactGroup group1 = new ContactGroup(new Element[]{earth, block1});
            ContactGroup group2 = new ContactGroup(new Element[]{block1, block2});
            double[][] F = new MatrixDynamics(new Element[]{earth, block1, block2}, new ContactGroup[]{group1, group2}).solve();
            assertEquals("Solution should have 3 entry row", 3, F.length);
            assertEquals("Solution should have 3 entry column", 3, F[0].length);
            assertEquals("F00 should be zero", 0, F[0][0], 1E-6);
            assertEquals("F11 should be zero", 0, F[1][1], 1E-6);
            assertEquals("F10 should be Fg1+Fg2", Fg1 + Fg2, F[1][0], 1E-6);
//            assertEquals("F01 should equal -F10", F[0][1], -F[1][0], 1E-6);
//            assertEquals("F01 should be -Fg1", -Fg1, F[0][1], 1E-6);
        }

    }
}
