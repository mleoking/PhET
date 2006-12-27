/** Sam Reid*/
package edu.colorado.phet.cck3.circuit.kirkhoff;

import Jama.Matrix;
import edu.colorado.phet.cck3.circuit.Branch;
import edu.colorado.phet.cck3.circuit.Circuit;
import edu.colorado.phet.cck3.circuit.Junction;
import edu.colorado.phet.cck3.circuit.components.Battery;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.DecimalFormat;
import java.util.*;

/**
 * User: Sam Reid
 * Date: Jun 1, 2004
 * Time: 4:14:19 PM
 * Copyright (c) Jun 1, 2004 by Sam Reid
 */
public class KirkhoffSolver {
    private ArrayList listeners = new ArrayList();
    public static boolean debugging = false;

    public void apply( Circuit circuit ) {
        //create a gauss jordan matrix.
        MatrixTable mt = new MatrixTable( circuit );

        EquationSet es = new EquationSet( mt );
        Equation[] junctionEquations = getJunctionEquations( circuit, mt );
        Equation[] loopEquations = getLoopEquations( circuit, mt );
        Equation[] ohmsLaws = getOhmsLaw( circuit, mt );

        es.addAll( junctionEquations );
        es.addAll( loopEquations );
        es.addAll( ohmsLaws );

        MatrixSystem ms = es.toMatrixSystem();
        Matrix solution = ms.solve();
        double rnorm = ms.getResidualNorm();
        if( debugging ) {
            System.out.println( "mt = " + mt );
            System.out.println( mt.describe( junctionEquations, "Junction Equations" ) );
            System.out.println( mt.describe( loopEquations, "Loop Equations" ) );
            System.out.println( mt.describe( ohmsLaws, "Ohms Law Equations" ) );
            System.out.println( "ms = " + ms );
            solution.print( new DecimalFormat( "#0.0####" ), 4 );
            System.out.println( "rnorm = " + rnorm );
        }

        //apply the solution.
        mt.applySolution( solution );
        for( int i = 0; i < listeners.size(); i++ ) {
            KirkhoffSolutionListener kirkhoffSolutionListener = (KirkhoffSolutionListener)listeners.get( i );
            kirkhoffSolutionListener.finishedKirkhoff();
        }
    }

    public static class MatrixSystem {
        Matrix a;
        Matrix b;

        public MatrixSystem( Matrix a, Matrix b ) {
            this.a = a;
            this.b = b;
        }

        public Matrix solve() {
            return a.solve( b );
        }

        public double getResidualNorm() {
            Matrix x = solve();
            Matrix r = a.times( x ).minus( b );
//            System.out.println( "R=" );
//            r.print( 4, 4 );
            double rnorm = r.normInf();
            return rnorm;
        }

        public String toString() {
            StringWriter swa = new StringWriter();
            a.print( new PrintWriter( swa ), new DecimalFormat( "##.####" ), 6 );
            StringWriter swb = new StringWriter();
            b.print( new PrintWriter( swb ), new DecimalFormat( "##.####" ), 6 );
            return "a=" + swa + "\nb=" + swb;
        }
    }

    //Ohm's law is -v=IR, because you lose voltage going in the direction of current.
    public Equation[] getOhmsLaw( Circuit circuit, MatrixTable table ) {
        ArrayList all = new ArrayList();
        for( int i = 0; i < circuit.numBranches(); i++ ) {
            Branch br = circuit.branchAt( i );
            if( table.isLoopElement( br ) ) {
                int iIndex = table.getCurrentColumn( br );
                if( iIndex != -1 ) {
                    double resistance = br.getResistance();
                    Equation eq = table.newEquation();
                    eq.setCoefficient( iIndex, -resistance );
                    if( br instanceof Battery ) {
                        eq.addValue( -br.getVoltageDrop() );
                    }
                    else {
                        int vIndex = table.getVoltageColumn( br );
                        if( vIndex != -1 ) {
                            eq.setCoefficient( vIndex, -1 );
                        }
                    }
                    if( !( br instanceof Battery ) ) {
                        all.add( eq );
                    }
                }
            }
        }
        return (Equation[])all.toArray( new Equation[0] );
    }

    public static class EquationSet {
        ArrayList equations = new ArrayList();
        MatrixTable table;

        public EquationSet( MatrixTable table ) {
            this.table = table;
        }

        public void addAll( Equation[] eq ) {
            for( int i = 0; i < eq.length; i++ ) {
                Equation equation = eq[i];
                if( !equations.contains( equation ) ) {
                    equations.add( equation );
                }
            }
        }

        public int numEquations() {
            return equations.size();
        }

        public MatrixSystem toMatrixSystem() {
            Matrix a = new Matrix( numEquations(), table.numVariables() );
            Matrix b = new Matrix( numEquations(), 1 );
            for( int i = 0; i < numEquations(); i++ ) {
                Equation eq = equationAt( i );
                for( int k = 0; k < eq.numCoefficients(); k++ ) {
                    a.set( i, k, eq.coefficientAt( k ) );
                }
                b.set( i, 0, eq.getValue() );
            }

            return new MatrixSystem( a, b );
        }

        private Equation equationAt( int i ) {
            return (Equation)equations.get( i );
        }

        public String toString() {
            return equations.toString();
        }

    }

    public Equation[] getLoopEquations( Circuit circuit, MatrixTable mt ) {
        ArrayList equations = new ArrayList();
        Path[] loops = mt.getLoops();
        for( int i = 0; i < loops.length; i++ ) {
            Path loop = loops[i];
            Path.DirectedBranch[] db = loop.getDirectedBranches();
            //the sum of the voltages around the directed loop is zero.
            Equation eq = mt.newEquation();
            for( int k = 0; k < db.length; k++ ) {
                Path.DirectedBranch directedBranch = db[k];
                Branch branch = directedBranch.getBranch();
                int value = 1;
                if( !directedBranch.isForward() ) {
                    value = -1;
                }
                if( branch instanceof Battery ) {
                    eq.addValue( -value * branch.getVoltageDrop() );//used to be -value
                }
                else {
                    int column = mt.getVoltageColumn( circuit.indexOf( directedBranch.getBranch() ) );
                    if( column != -1 ) {
                        eq.setCoefficient( column, value );
                    }
                }
            }
            equations.add( eq );
        }
        return (Equation[])equations.toArray( new Equation[0] );
    }

    public Equation[] getJunctionEquations( Circuit circuit, MatrixTable mt ) {
//        MatrixTable mt = new MatrixTable( circuit );
        ArrayList equations = new ArrayList();
        for( int i = 0; i < circuit.numJunctions(); i++ ) {
            Junction j = circuit.junctionAt( i );
            Equation eq = mt.newEquation();
            Branch[] b = circuit.getAdjacentBranches( j );
            for( int k = 0; k < b.length; k++ ) {
                Branch branch = b[k];
//                int branchIndex = circuit.indexOf( branch );
                int column = mt.getCurrentColumn( branch );
                if( column != -1 ) {
                    //doesn't matter which is + and which is -, just as long as we're consistent.
                    if( branch.getStartJunction() == j ) {
                        eq.setCoefficient( column, 1 );
                    }
                    else {
                        eq.setCoefficient( column, -1 );
                    }
                }
            }
            equations.add( eq );
        }
        return (Equation[])equations.toArray( new Equation[0] );
    }

    public void addSolutionListener( KirkhoffSolutionListener ksl ) {
        listeners.add( ksl );
    }

    public static class Equation {
        ArrayList coeffs = new ArrayList();
        double rhs = 0;

        public Equation( int numCoefficients ) {
            for( int i = 0; i < numCoefficients; i++ ) {
                coeffs.add( new Double( 0 ) );
            }
        }

        public void setCoefficient( int index, double value ) {
            coeffs.set( index, new Double( value ) );
        }

        public int numCoefficients() {
            return coeffs.size();
        }

        public void setValue( double value ) {
            this.rhs = value;
        }

        public String toString() {
            return coeffs + " : " + rhs;
        }

        public double coefficientAt( int k ) {
            Double co = (Double)coeffs.get( k );
            return co.doubleValue();
        }

        public double getValue() {
            return rhs;
        }

        public boolean equals( Object obj ) {
            if( !( obj instanceof Equation ) ) {
                return false;
            }
            Equation eq = (Equation)obj;
            return eq.coeffs.equals( coeffs ) && eq.rhs == rhs;
        }

        public void addValue( double val ) {
            rhs += val;
        }
    }

    public static class MatrixTable {
        Circuit circuit;
        Hashtable currentTable = new Hashtable();
        Hashtable voltageTable = new Hashtable();//key = branch index, value=column index.
        private int numFreeParameters;
        private Path[] loops;

        //This allows us to ignore batteries (which are not free parameters for voltage.)
        //and components not in loops (which cannot have current).
        public MatrixTable( Circuit circuit ) {
            this.circuit = circuit;
            int columnIndex = 0;
            this.loops = Path.getLoops( circuit );
            for( int i = 0; i < circuit.numBranches(); i++ ) {
                Branch br = circuit.branchAt( i );
                if( isLoopElement( br ) ) {
                    currentTable.put( new Integer( i ), new Integer( columnIndex++ ) );
//                    System.out.println( "column[" + ( columnIndex - 1 ) + "] = " + "I" + i );
                }
                else {
                    //make a note that this branch is not a free parameter.
                }
            }
            for( int i = 0; i < circuit.numBranches(); i++ ) {
                Branch b = circuit.branchAt( i );
                if( isLoopElement( b ) ) {
                    if( !( b instanceof Battery ) ) {
                        voltageTable.put( new Integer( i ), new Integer( columnIndex++ ) );
//                        System.out.println( "column[" + ( columnIndex - 1 ) + "] = " + "V" + i );
                    }
                }
                else {
                    //make a note that this branch is not a free parameter.
                }
            }
            this.numFreeParameters = columnIndex;
//            System.out.println( "numFreeParameters = " + numFreeParameters );
        }

        public String toString() {
            StringBuffer string = new StringBuffer();
//            Enumeration ce = currentTable.keys();

            List currentList = new ArrayList();
            currentList.addAll( currentTable.keySet() );
            Collections.sort( currentList );
            string.append( "MatrixTable: numFreeParameters=" + getNumFreeParameters() + ":\n" );
            for( int i = 0; i < currentList.size(); i++ ) {
                Integer key = (Integer)currentList.get( i );
                int value = getCurrentColumn( key.intValue() );
                string.append( "column[" + value + "]=I" + key + "\n" );
            }

            List voltList = new ArrayList();
            voltList.addAll( voltageTable.keySet() );
            Collections.sort( voltList );
            for( int i = 0; i < voltList.size(); i++ ) {
                Integer key = (Integer)voltList.get( i );
                int value = getVoltageColumn( key.intValue() );
                string.append( "column[" + value + "]=V" + key + "\n" );
            }
            return string.toString();
        }

        public Path[] getLoops() {
            return loops;
        }

        int getCurrentColumn( Branch branch ) {
            return getCurrentColumn( circuit.indexOf( branch ) );
        }

        int getCurrentColumn( int branch ) {
            Integer key = new Integer( branch );
            if( currentTable.containsKey( key ) ) {
                return ( (Integer)currentTable.get( key ) ).intValue();
            }
            else {
                return -1;
            }
        }

        int getVoltageColumn( Branch branch ) {
            return getVoltageColumn( circuit.indexOf( branch ) );
        }

        int getVoltageColumn( int branch ) {
            Integer key = new Integer( branch );
            if( voltageTable.containsKey( key ) ) {
                return ( (Integer)voltageTable.get( new Integer( branch ) ) ).intValue();
            }
            else {
                return -1;
            }
        }

        public Equation newEquation() {
            return new Equation( numVariables() );//last element is RHS.
        }

        public int numVariables() {
            return numFreeParameters;
        }

        public void applySolution( Matrix solution ) {
            applyCurrents( solution );
            applyVolts( solution );
        }

        private void applyVolts( Matrix solution ) {
            Set voltKeys = voltageTable.keySet();
            ArrayList remaining = new ArrayList();
            remaining.addAll( Arrays.asList( circuit.getBranches() ) );

            for( Iterator iterator = voltKeys.iterator(); iterator.hasNext(); ) {
                Integer key = (Integer)iterator.next();
                //key=branch, value=row.
                Branch br = circuit.branchAt( key.intValue() );
                Integer value = (Integer)voltageTable.get( key );
                int row = value.intValue();
                double volts = solution.get( row, 0 );
                if( !( br instanceof Battery ) ) {
                    br.setVoltageDrop( volts );
                }
                remaining.remove( br );
            }
            for( int i = 0; i < remaining.size(); i++ ) {
                Branch branch = (Branch)remaining.get( i );
                if( !( branch instanceof Battery ) ) {
                    branch.setVoltageDrop( 0 );
                }
            }
        }

        private void applyCurrents( Matrix solution ) {
            Set currentKeys = currentTable.keySet();
            ArrayList remaining = new ArrayList();
            remaining.addAll( Arrays.asList( circuit.getBranches() ) );

            for( Iterator iterator = currentKeys.iterator(); iterator.hasNext(); ) {
                Integer key = (Integer)iterator.next();
                //key=branch, value=row.
                Branch br = circuit.branchAt( key.intValue() );
                Integer value = (Integer)currentTable.get( key );
                int row = value.intValue();
                double current = solution.get( row, 0 );
                br.setCurrent( current );
                remaining.remove( br );
            }
            for( int i = 0; i < remaining.size(); i++ ) {
                Branch branch = (Branch)remaining.get( i );
                branch.setCurrent( 0 );
            }
        }

        public boolean isLoopElement( Branch br ) {
            for( int i = 0; i < loops.length; i++ ) {
                Path p = loops[i];
                if( p.containsBranch( br ) ) {
                    return true;
                }
            }
            return false;
        }

        public int getNumFreeParameters() {
            return numFreeParameters;
        }

        public String describe( Equation[] equations, String name ) {
            String str = "";
            str += ( "Equation Set for " + name + "\n" );
            for( int i = 0; i < equations.length; i++ ) {
                Equation equation = equations[i];
//                System.out.println( "equation = " + equation );
                String LHS = "";
                for( int k = 0; k < equation.numCoefficients(); k++ ) {
                    double coeff = equation.coefficientAt( k );
                    if( coeff != 0 ) {
                        String sign = "+";
                        if( coeff < 0 ) {
                            sign = "-";
                        }
                        String coeffVal = "" + Math.abs( coeff ) + "*";
                        if( coeff == 1 || coeff == -1 ) {
                            coeffVal = "";
                        }
                        String term = coeffVal + getVariableNameForColumn( k );

                        LHS += sign + term;
//                        LHS += coeffVal + getVariableNameForColumn( k ) + " ";
                    }
                }
                if( LHS.startsWith( "+" ) ) {
                    LHS = LHS.substring( 1 );
                }
                String RHS = "" + equation.getValue();
                String equ = LHS + " = " + RHS;

//                System.out.println( "" + i + ": " + equ );
                str += "" + i + ": " + equ + "\n";
            }
//            System.out.println( str );
            return str;
        }

        private String getVariableNameForColumn( int k ) {
            Integer col = new Integer( k );
            if( currentTable.values().contains( col ) ) {
                Set set = currentTable.keySet();
                for( Iterator iterator = set.iterator(); iterator.hasNext(); ) {
                    Integer key = (Integer)iterator.next();
                    if( getCurrentColumn( key.intValue() ) == k ) {
                        return "I" + key;
                    }
                }
                throw new RuntimeException( "Column not found in current table: " + k );
            }
            else if( voltageTable.values().contains( col ) ) {
                Set set = voltageTable.keySet();
                for( Iterator iterator = set.iterator(); iterator.hasNext(); ) {
                    Integer key = (Integer)iterator.next();
                    if( getVoltageColumn( key.intValue() ) == k ) {
                        return "V" + key;
                    }
                }
                throw new RuntimeException( "Column not found in voltage table: " + k );
            }
            else {
                throw new RuntimeException( "Column not found: " + k );
            }
        }
    }

}
