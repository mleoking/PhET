/*Copyright, Sam Reid, 2003.*/
package edu.colorado.phet.cck.elements.kirkhoff;

import Jama.Matrix;
import edu.colorado.phet.cck.elements.branch.Branch;
import edu.colorado.phet.cck.elements.branch.components.Battery;
import edu.colorado.phet.cck.elements.branch.components.HasResistance;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Set;

/**
 * User: Sam Reid
 * Date: Nov 12, 2003
 * Time: 10:28:24 AM
 * Copyright (c) Nov 12, 2003 by Sam Reid
 */

public class MatrixTable {
    Hashtable table = new Hashtable();//key=branch index (Integer), value= table entry
    private CircuitGraph cg;
    private int freeParameterCount;
    String toString;

    public MatrixTable( CircuitGraph cg ) {
        this.cg = cg;
        table = new Hashtable();
        int index = 0;//variable index.
        toString = "";

        for( int i = 0; i < cg.numBranches(); i++ ) {
//            int currentIndex = index;
            Branch b = cg.branchAt( i );
            if( cg.isLoopElement( b ) ) {
                if( b instanceof Battery ) {
                    TableEntry te = new TableEntry( index, -1 );
                    toString += "[batt index=" + i + ", I=" + index + "], ";
                    table.put( new Integer( i ), te );
                    index++;
                }
                else {
                    HasResistance hr = (HasResistance)b;
                    double resistance = hr.getResistance();
                    if( resistance == 0 ) {
                        TableEntry te = new TableEntry( index, -1 );
                        toString += "[index=" + i + ", I=" + index + "]";
                        table.put( new Integer( i ), te );
                        index += 1;
                    }
                    else {
                        int voltageIndex = index + 1;
                        TableEntry te = new TableEntry( index, index + 1 );
                        toString += "[index=" + i + ", I=" + index + ", V=" + voltageIndex + "], ";
                        table.put( new Integer( i ), te );
                        index += 2;
                    }
                }
            }
        }
        this.freeParameterCount = index;
//        O.d("MT.this = " + this);
    }

    public String toString() {
        return toString;
    }

    public int getFreeParameterCount() {
        return freeParameterCount;
    }

    public int getCurrentColumn( int componentIndex ) {
        TableEntry te = (TableEntry)table.get( new Integer( componentIndex ) );
        return te.currentColumn;
    }

    public int getVoltageColumn( int componentIndex ) {
//        System.out.println("cg.branchAt(componentIndex) = " + cg.branchAt(componentIndex));
        if( cg.branchAt( componentIndex ) instanceof Battery ) {
            throw new RuntimeException( "Battery voltage is not a parameter." );
        }
        TableEntry te = (TableEntry)table.get( new Integer( componentIndex ) );
        return te.voltageColumn;
    }

    public String getEquationSetString( Matrix jmatrix, Matrix solutionColumn ) {
        StringBuffer sb = new StringBuffer();
        for( int i = 0; i < jmatrix.getRowDimension(); i++ ) {
            String eq = getEquationString( i, jmatrix, solutionColumn );
            sb.append( "eq[" + i + "] = " + eq );
//            System.out.println("eq[" + i + "] = " + eq);
            if( i < jmatrix.getRowDimension() - 1 ) {
                sb.append( "\n" );
            }
        }
        return sb.toString();
    }

//    public void printEquations(Matrix jmatrix, Matrix solutionColumn) {
//        System.out.println(getEquationSetString(jmatrix, solutionColumn));
//    }

    private String getEquationString( int row, Matrix jmatrix, Matrix rhsMatrix ) {
//        StringBuffer sb = new StringBuffer();
        ArrayList terms = new ArrayList();
        for( int k = 0; k < jmatrix.getColumnDimension(); k++ ) {
            double value = jmatrix.get( row, k );
            if( value != 0 ) {
                String variableName = getVariableNameForColumn( k );
//                sb.append(variableName);
                String term = value + variableName;
                terms.add( term );
            }
        }
        StringBuffer out = new StringBuffer();
        for( int i = 0; i < terms.size(); i++ ) {
            String s = (String)terms.get( i );
            out.append( s );
            if( i < terms.size() - 1 ) {
                out.append( " + " );
            }
        }
        out.append( " = " + rhsMatrix.get( row, 0 ) );
        return out.toString();
    }

    private String getVariableNameForColumn( int column ) {
        Set keys = table.keySet();
        for( Iterator iterator = keys.iterator(); iterator.hasNext(); ) {
            Integer integer = (Integer)iterator.next();
            TableEntry value = (TableEntry)table.get( integer );
            Branch branch = cg.getCircuit().branchAt( integer.intValue() );
            if( value.currentColumn == column ) {
                return "I" + branch.getId();
            }
            else if( value.voltageColumn == column ) {
                return "V" + branch.getId();
            }
//            if (value.currentColumn == column) {
//                return "I" + integer.intValue();
//            } else if (value.voltageColumn == column)
//                return "V" + integer.intValue();
        }
        throw new RuntimeException( "No variable name for column: " + column );
    }

    private class TableEntry {

        int currentColumn;
        int voltageColumn;


        public TableEntry( int currentColumn, int voltageColumn ) {
            this.currentColumn = currentColumn;
            this.voltageColumn = voltageColumn;
        }

        public int getCurrentColumn() {
            return currentColumn;
        }

        public int getVoltageColumn() {
            return voltageColumn;
        }
    }
}

